package cz.cdv.datex2.internal;

import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import eu.datex2.schema._2._2_0.UpdateMethodEnum;

public class Subscriptions {

	// TODO: make persistent
	private Map<String, Entry> entries = new HashMap<>();

	public String addPeriodic(Calendar startTime, Calendar stopTime,
			Float periodSeconds, UpdateMethodEnum updateMethod,
			List<PushTarget> pushTargets) {

		String reference = createReference();
		entries.put(reference, createEntry(updateMethod, pushTargets));
		// FIXME: add scheduling
		return reference;
	}

	public void updatePeriodic(String subscriptionReference,
			Calendar startTime, Calendar stopTime, Float periodSeconds,
			UpdateMethodEnum updateMethod, List<PushTarget> pushTargets) {

		entries.put(subscriptionReference,
				createEntry(updateMethod, pushTargets));
		// FIXME: remove old and add new scheduling
	}

	public String add(UpdateMethodEnum updateMethod,
			List<PushTarget> pushTargets) {

		String reference = createReference();
		entries.put(reference, createEntry(updateMethod, pushTargets));
		return reference;
	}

	public void update(String subscriptionReference,
			UpdateMethodEnum updateMethod, List<PushTarget> pushTargets) {

		entries.put(subscriptionReference,
				createEntry(updateMethod, pushTargets));
	}

	public void delete(String subscriptionReference) {
		entries.remove(subscriptionReference);
	}

	private String createReference() {
		return UUID.randomUUID().toString();
	}

	private Entry createEntry(UpdateMethodEnum updateMethod,
			List<PushTarget> pushTargets) {

		return new Entry(updateMethod, pushTargets);
	}

	private static class Entry {

		private final UpdateMethodEnum updateMethod;
		private final List<PushTarget> pushTargets;

		public Entry(UpdateMethodEnum updateMethod, List<PushTarget> pushTargets) {
			this.updateMethod = updateMethod;
			this.pushTargets = Collections.unmodifiableList(pushTargets);
		}

		public UpdateMethodEnum getUpdateMethod() {
			return updateMethod;
		}

		public List<PushTarget> getPushTargets() {
			return pushTargets;
		}

	}

}
