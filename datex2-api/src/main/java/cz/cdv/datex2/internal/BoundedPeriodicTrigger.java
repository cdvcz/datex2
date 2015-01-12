package cz.cdv.datex2.internal;

import java.util.Date;

import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.support.PeriodicTrigger;

public class BoundedPeriodicTrigger extends PeriodicTrigger {

	private final Date stop;

	public BoundedPeriodicTrigger(Date start, Date stop, long period) {
		super(period);
		this.stop = stop;
		this.setFixedRate(true);
		if (start != null) {
			long initialDelay = start.getTime() - System.currentTimeMillis();
			this.setInitialDelay(initialDelay);
		}
	}

	@Override
	public Date nextExecutionTime(TriggerContext triggerContext) {
		Date next = super.nextExecutionTime(triggerContext);
		if (stop != null && next != null && next.after(stop))
			return null;
		return next;
	}

}
