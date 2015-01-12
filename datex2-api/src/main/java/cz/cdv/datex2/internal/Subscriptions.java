package cz.cdv.datex2.internal;

import java.io.File;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.mapdb.Bind;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Fun;
import org.mapdb.Fun.Tuple2;
import org.mapdb.HTreeMap;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;

import cz.cdv.datex2.Datex2Supplier;
import eu.datex2.schema._2._2_0.D2LogicalModel;
import eu.datex2.schema._2._2_0.UpdateMethodEnum;

public class Subscriptions implements InitializingBean, DisposableBean {

	private static Logger log = Logger.getLogger(Subscriptions.class
			.getSimpleName());

	@Autowired
	private TaskScheduler scheduler;
	@Autowired
	private Pusher pusher;

	private DB db = null;
	private HTreeMap<String, Entry> entries;
	private NavigableSet<Tuple2<String, String>> supplierIndex;
	private HTreeMap<String, List<String>> changesHistory;
	private HTreeMap<String, String> lastChanges;

	private Map<String, ScheduledFuture<?>> scheduled = new ConcurrentHashMap<>();

	private Map<String, Datex2Supplier> suppliers = new ConcurrentHashMap<>();

	public void registerSupplier(String supplierPath, Datex2Supplier supplier) {
		suppliers.put(supplierPath, supplier);
	}

	public void push(String supplier, String... changes) {
		try {
			addChanges(supplier, changes);

			for (String reference : getReferences(supplier)) {
				Entry entry = entries.get(reference);

				// run push only for on-occurrence subscriptions
				if (entry.getPeriodSeconds() != null)
					execute(reference);
			}
		} finally {
			db.commit();
		}
	}

	public String addPeriodic(String supplier, Calendar startTime,
			Calendar stopTime, Float periodSeconds,
			UpdateMethodEnum updateMethod, List<PushTarget> pushTargets) {

		try {
			String reference = createReference();
			entries.put(
					reference,
					createEntry(supplier, updateMethod, pushTargets, startTime,
							stopTime, periodSeconds));

			schedule(reference, startTime, stopTime, periodSeconds);

			return reference;
		} finally {
			db.commit();
		}
	}

	public String updatePeriodic(String supplier, String reference,
			Calendar startTime, Calendar stopTime, Float periodSeconds,
			UpdateMethodEnum updateMethod, List<PushTarget> pushTargets) {

		try {
			entries.put(
					reference,
					createEntry(supplier, updateMethod, pushTargets, startTime,
							stopTime, periodSeconds));

			schedule(reference, startTime, stopTime, periodSeconds);

			return reference;
		} finally {
			db.commit();
		}
	}

	public String add(String supplier, UpdateMethodEnum updateMethod,
			List<PushTarget> pushTargets) {

		try {
			String reference = createReference();
			entries.put(reference,
					createEntry(supplier, updateMethod, pushTargets));
			return reference;
		} finally {
			db.commit();
		}
	}

	public String update(String supplier, String reference,
			UpdateMethodEnum updateMethod, List<PushTarget> pushTargets) {

		try {
			entries.put(reference,
					createEntry(supplier, updateMethod, pushTargets));
			return reference;
		} finally {
			db.commit();
		}
	}

	public void delete(String supplier, String reference) {
		try {
			Entry entry = entries.get(reference);
			if (entry != null) {
				if (supplier != null && entry.getSupplier() != null
						&& supplier.equals(entry.getSupplier()))
					entries.remove(reference);
				else
					return;
			}

			cancelSchedule(reference);
		} finally {
			db.commit();
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		String dbFileName = System.getProperty("user.dir") + "/"
				+ "subscriptions";
		File dbFile = new File(dbFileName);
		log.info("Subscriptions will be stored in: " + dbFile.getAbsolutePath());

		if (!dbFile.exists())
			dbFile.getParentFile().mkdirs();

		db = DBMaker.newFileDB(dbFile).asyncWriteEnable().checksumEnable()
				.mmapFileEnableIfSupported().cacheSize(50).cacheLRUEnable()
				.transactionDisable().make();

		entries = db.createHashMap("Entry").makeOrGet();

		supplierIndex = new TreeSet<Fun.Tuple2<String, String>>();
		Bind.secondaryKey(entries, supplierIndex,
				new Fun.Function2<String, String, Entry>() {
					@Override
					public String run(String reference, Entry entry) {
						try {
							if (entry == null)
								return null;
							return entry.getSupplier();
						} catch (RuntimeException e) {
							throw e;
						} catch (Exception e) {
							throw new RuntimeException(e);
						}
					}
				});

		changesHistory = db.createHashMap("ChangesSet").makeOrGet();
		lastChanges = db.createHashMap("LastChanges").makeOrGet();

		// reschedule
		for (String reference : entries.keySet()) {
			Entry entry = entries.get(reference);
			if (entry.getPeriodSeconds() != null)
				schedule(reference, entry.getStartTime(), entry.getStopTime(),
						entry.getPeriodSeconds());
		}

		db.commit();
	}

	@Override
	public void destroy() throws Exception {
		if (db == null)
			return;

		db.close();
		db = null;
	}

	private String createReference() {
		return UUID.randomUUID().toString();
	}

	private Entry createEntry(String supplier, UpdateMethodEnum updateMethod,
			List<PushTarget> pushTargets) {

		return createEntry(supplier, updateMethod, pushTargets, null, null,
				null);
	}

	private Entry createEntry(String supplier, UpdateMethodEnum updateMethod,
			List<PushTarget> pushTargets, Calendar startTime,
			Calendar stopTime, Float periodSeconds) {

		if (supplier == null)
			throw new IllegalArgumentException("'supplier' is null");

		return new Entry(supplier, updateMethod, pushTargets, startTime,
				stopTime, periodSeconds);
	}

	private Iterable<String> getReferences(String supplier) {
		return Fun.filter(supplierIndex, supplier);
	}

	private void schedule(String reference, Calendar startTime,
			Calendar stopTime, Float periodSeconds) {

		cancelSchedule(reference);

		ScheduledFuture<?> schedule = scheduler.schedule(createTask(reference),
				createTrigger(startTime, stopTime, periodSeconds));

		scheduled.put(reference, schedule);
	}

	private void cancelSchedule(String reference) {
		ScheduledFuture<?> schedule = scheduled.get(reference);
		if (schedule != null)
			schedule.cancel(false);
	}

	private Trigger createTrigger(Calendar startTime, Calendar stopTime,
			Float periodSeconds) {

		long periodMillis = Math.round(periodSeconds * 1000);
		return new BoundedPeriodicTrigger(startTime.getTime(),
				stopTime.getTime(), periodMillis);
	}

	private Runnable createTask(String reference) {
		return new Task(reference);
	}

	private void execute(String reference) {
		Entry entry = entries.get(reference);
		if (entry == null)
			return;

		String supplierId = entry.getSupplier();
		Datex2Supplier supplier = suppliers.get(supplierId);

		for (PushTarget target : entry.getPushTargets()) {
			String[] changes = getNewChanges(reference, target);
			if (changes == null || changes.length == 0)
				continue;

			D2LogicalModel model = supplier.getChanges(entry.getUpdateMethod(),
					changes);
			if (model == null)
				continue;

			try {
				supplierPush(model, target);
				setPushedChanges(reference, target, changes);
			} catch (Exception e) {
				log.log(Level.SEVERE, "Can't push to " + target, e);
			}
		}
	}

	private void supplierPush(D2LogicalModel model, PushTarget target) {
		pusher.push(target, model);
	}

	private void addChanges(String supplier, String... changes) {
		if (changes == null || changes.length == 0)
			return;

		List<String> supplierHistory = changesHistory.get(supplier);
		if (supplierHistory == null)
			supplierHistory = new CopyOnWriteArrayList<>();
		for (String change : changes) {
			if (!supplier.contains(change))
				supplierHistory.add(change);
		}
	}

	private String[] getNewChanges(String reference, PushTarget target) {
		Entry entry = entries.get(reference);
		if (entry == null)
			return null;

		List<String> supplierHistory = changesHistory.get(entry.getSupplier());
		if (supplierHistory == null || supplierHistory.size() == 0)
			return null;

		String change = lastChanges.get(getLastChangeKey(reference, target));
		int lastIndex = supplierHistory.lastIndexOf(change);

		List<String> newChanges = supplierHistory.subList(lastIndex,
				supplierHistory.size());
		return newChanges.toArray(new String[0]);
	}

	private void setPushedChanges(String reference, PushTarget target,
			String[] changes) {

		if (changes == null || changes.length == 0)
			return;

		Entry entry = entries.get(reference);
		if (entry == null)
			return;

		List<String> supplierHistory = changesHistory.get(entry.getSupplier());
		if (supplierHistory == null || supplierHistory.size() == 0)
			throw new IllegalStateException("No changes history");

		lastChanges.put(getLastChangeKey(reference, target),
				changes[changes.length - 1]);
	}

	private String getLastChangeKey(String reference, PushTarget target) {
		return reference + "-" + target.toString();
	}

	private class Task implements Runnable {

		private final String reference;

		public Task(String reference) {
			this.reference = reference;
		}

		@Override
		public void run() {
			try {
				execute(reference);
			} finally {
				db.commit();
			}
		}

	}

	@SuppressWarnings("serial")
	private static class Entry implements Serializable {

		private final String supplier;
		private final UpdateMethodEnum updateMethod;
		private final List<PushTarget> pushTargets;
		private final Calendar startTime;
		private final Calendar stopTime;
		private final Float periodSeconds;

		public Entry(String supplier, UpdateMethodEnum updateMethod,
				List<PushTarget> pushTargets, Calendar startTime,
				Calendar stopTime, Float periodSeconds) {

			this.supplier = supplier;
			this.updateMethod = updateMethod;
			this.pushTargets = Collections.unmodifiableList(pushTargets);
			this.startTime = startTime;
			this.stopTime = stopTime;
			this.periodSeconds = periodSeconds;
		}

		public String getSupplier() {
			return supplier;
		}

		public UpdateMethodEnum getUpdateMethod() {
			return updateMethod;
		}

		public List<PushTarget> getPushTargets() {
			return pushTargets;
		}

		public Calendar getStartTime() {
			return startTime;
		}

		public Calendar getStopTime() {
			return stopTime;
		}

		public Float getPeriodSeconds() {
			return periodSeconds;
		}

	}

}
