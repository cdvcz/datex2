package cz.cdv.datex2;

import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cz.cdv.datex2.internal.PushTarget;
import eu.datex2.schema._2._2_0.D2LogicalModel;
import eu.datex2.schema._2._2_0.Exchange;
import eu.datex2.schema._2._2_0.OperatingModeEnum;
import eu.datex2.schema._2._2_0.Subscription;
import eu.datex2.schema._2._2_0.Target;
import eu.datex2.schema._2._2_0.UpdateMethodEnum;

public class Datex2Subscription {

	private final String reference;
	private final boolean delete;
	private final Calendar start;
	private final Calendar stop;
	private final Float seconds;
	private final UpdateMethodEnum updateMethod;
	private final List<Target> targets;

	private Datex2Subscription(String reference, boolean delete,
			Calendar start, Calendar stop, Float seconds,
			UpdateMethodEnum updateMethod, List<Target> targets) {

		this.reference = reference;
		this.delete = delete;
		this.start = start;
		this.stop = stop;
		this.seconds = seconds;
		this.updateMethod = updateMethod;
		this.targets = targets;
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public D2LogicalModel getModel() {
		D2LogicalModel model = new D2LogicalModel();
		updateModel(model);
		return model;
	}

	public void updateModel(D2LogicalModel model) {
		Exchange e = model.getExchange();
		if (e == null) {
			e = new Exchange();
			model.setExchange(e);
		}

		if (reference != null)
			e.setSubscriptionReference(reference);

		Subscription s = e.getSubscription();
		if (s == null) {
			s = new Subscription();
			e.setSubscription(s);
		}

		UpdateMethodEnum update = updateMethod;
		if (delete) { // delete
			s.setOperatingMode(OperatingModeEnum.OPERATING_MODE_0);
			s.setDeleteSubscription(true);
		}

		else if (seconds != null) { // periodic push
			s.setOperatingMode(OperatingModeEnum.OPERATING_MODE_2);
			s.setDeliveryInterval(seconds);
			if (update == null)
				update = UpdateMethodEnum.SNAPSHOT;
		}

		else { // push on occurrence
			s.setOperatingMode(OperatingModeEnum.OPERATING_MODE_1);
			if (update == null)
				update = UpdateMethodEnum.SINGLE_ELEMENT_UPDATE;
		}

		s.setUpdateMethod(update);
		if (start != null)
			s.setSubscriptionStartTime(start);
		if (stop != null)
			s.setSubscriptionStopTime(stop);

		if (targets != null && targets.size() > 0) {
			List<Target> subscriptionTargets = s.getTarget();
			subscriptionTargets.addAll(targets);
		}
	}

	public static class Builder {

		private String reference = null;
		private boolean delete = false;
		private Float seconds = null;
		private Calendar start = null;
		private Calendar stop = null;
		private UpdateMethodEnum updateMethod = null;
		private List<Target> targets = new ArrayList<>();

		private Builder() {
		}

		public Builder reference(String reference) {
			this.reference = reference;
			return this;
		}

		public Builder delete() {
			this.delete = true;
			return this;
		}

		public Builder periodic(float seconds) {
			this.seconds = seconds;
			return this;
		}

		public Builder start(Calendar start) {
			this.start = start;
			return this;
		}

		public Builder start(Date start) {
			this.start = Calendar.getInstance();
			this.start.setTime(start);
			return this;
		}

		public Builder stop(Calendar stop) {
			this.stop = stop;
			return this;
		}

		public Builder stop(Date stop) {
			this.stop = Calendar.getInstance();
			this.stop.setTime(stop);
			return this;
		}

		public Builder snapshot() {
			this.updateMethod = UpdateMethodEnum.SNAPSHOT;
			return this;
		}

		public Builder singleElement() {
			this.updateMethod = UpdateMethodEnum.SINGLE_ELEMENT_UPDATE;
			return this;
		}

		public Builder allElement() {
			this.updateMethod = UpdateMethodEnum.ALL_ELEMENT_UPDATE;
			return this;
		}

		public Builder update(UpdateMethodEnum updateMethod) {
			this.updateMethod = updateMethod;
			return this;
		}

		public Builder target(Target target) {
			this.targets.add(target);
			return this;
		}

		public Builder target(PushTarget target) {
			this.targets.add(target.toTarget());
			return this;
		}

		public Builder target(URL url) {
			return target(url, null, null);
		}

		public Builder target(URL url, String username, String password) {
			this.targets
					.add(new PushTarget(url, username, password).toTarget());
			return this;
		}

		public Builder target(List<PushTarget> pushTargets) {
			if (pushTargets == null)
				targets.clear();
			for (PushTarget t : pushTargets)
				target(t);
			return this;
		}

		public Datex2Subscription build() {
			return new Datex2Subscription(reference, delete, start, stop,
					seconds, updateMethod, targets);
		}

	}

}
