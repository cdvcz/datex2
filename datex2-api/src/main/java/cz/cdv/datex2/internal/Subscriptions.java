package cz.cdv.datex2.internal;

import java.net.URL;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.support.PeriodicTrigger;

import eu.datex2.schema._2._2_0.UpdateMethodEnum;

public class Subscriptions {

	@Autowired
	private TaskScheduler scheduler;

	// TODO: make persistent, re-schedule on startup (use MapDB???)
	private Map<String, Entry> entries = new ConcurrentHashMap<>();

	private Map<String, ScheduledFuture<?>> scheduled = new ConcurrentHashMap<>();

	public String addPeriodic(Calendar startTime, Calendar stopTime,
			Float periodSeconds, UpdateMethodEnum updateMethod,
			List<PushTarget> pushTargets) {

		String reference = createReference();
		entries.put(
				reference,
				createEntry(updateMethod, pushTargets, startTime, stopTime,
						periodSeconds));

		ScheduledFuture<?> schedule = scheduler.schedule(createTask(reference),
				createTrigger(startTime, stopTime, periodSeconds));
		scheduled.put(reference, schedule);

		return reference;
	}

	public String updatePeriodic(String reference, Calendar startTime,
			Calendar stopTime, Float periodSeconds,
			UpdateMethodEnum updateMethod, List<PushTarget> pushTargets) {

		entries.put(
				reference,
				createEntry(updateMethod, pushTargets, startTime, stopTime,
						periodSeconds));

		ScheduledFuture<?> schedule = scheduled.get(reference);
		if (schedule != null)
			schedule.cancel(false);
		schedule = scheduler.schedule(createTask(reference),
				createTrigger(startTime, stopTime, periodSeconds));
		scheduled.put(reference, schedule);

		return reference;
	}

	public String add(UpdateMethodEnum updateMethod,
			List<PushTarget> pushTargets) {

		String reference = createReference();
		entries.put(reference, createEntry(updateMethod, pushTargets));
		return reference;
	}

	public String update(String reference, UpdateMethodEnum updateMethod,
			List<PushTarget> pushTargets) {

		entries.put(reference, createEntry(updateMethod, pushTargets));
		return reference;
	}

	public void delete(String reference) {
		entries.remove(reference);

		ScheduledFuture<?> schedule = scheduled.get(reference);
		if (schedule != null)
			schedule.cancel(false);
	}

	private String createReference() {
		return UUID.randomUUID().toString();
	}

	private Entry createEntry(UpdateMethodEnum updateMethod,
			List<PushTarget> pushTargets) {

		return createEntry(updateMethod, pushTargets, null, null, null);
	}

	private Entry createEntry(UpdateMethodEnum updateMethod,
			List<PushTarget> pushTargets, Calendar startTime,
			Calendar stopTime, Float periodSeconds) {

		return new Entry(updateMethod, pushTargets, startTime, stopTime,
				periodSeconds);
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

		// FIXME: obtain data to push

		for (PushTarget target : entry.getPushTargets()) {
			URL url = target.getUrl();
			// FIXME: get username and password
			// FIXME: execute supplier push on target
		}
	}

	private class Task implements Runnable {

		private final String reference;

		public Task(String reference) {
			this.reference = reference;
		}

		@Override
		public void run() {
			execute(reference);
		}

	}

	private static class Entry {

		private final UpdateMethodEnum updateMethod;
		private final List<PushTarget> pushTargets;
		private final Calendar startTime;
		private final Calendar stopTime;
		private final Float periodSeconds;

		public Entry(UpdateMethodEnum updateMethod,
				List<PushTarget> pushTargets, Calendar startTime,
				Calendar stopTime, Float periodSeconds) {

			this.updateMethod = updateMethod;
			this.pushTargets = Collections.unmodifiableList(pushTargets);
			this.startTime = startTime;
			this.stopTime = stopTime;
			this.periodSeconds = periodSeconds;
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

	private static class BoundedPeriodicTrigger extends PeriodicTrigger {

		private final Date stop;

		public BoundedPeriodicTrigger(Date start, Date stop, long period) {
			super(period);
			this.stop = stop;
			this.setFixedRate(true);
			long initialDelay = start.getTime() - System.currentTimeMillis();
			this.setInitialDelay(initialDelay);
		}

		@Override
		public Date nextExecutionTime(TriggerContext triggerContext) {
			Date next = super.nextExecutionTime(triggerContext);
			if (next.after(stop))
				return null;
			return next;
		}

	}

}
