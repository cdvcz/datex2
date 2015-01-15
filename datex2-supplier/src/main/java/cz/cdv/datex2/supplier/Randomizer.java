package cz.cdv.datex2.supplier;

import java.util.Date;
import java.util.Random;

import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;

@SuppressWarnings("serial")
public class Randomizer extends Random {

	private static Randomizer instance = null;

	public static Randomizer getInstance() {
		if (instance == null)
			instance = new Randomizer();
		return instance;
	}

	public Trigger createTrigger(final int minSeconds, final int maxSeconds) {
		return new Trigger() {
			@Override
			public Date nextExecutionTime(TriggerContext context) {
				Date last = context.lastCompletionTime();
				if (last == null)
					last = new Date();
				int seconds = nextInt(maxSeconds - minSeconds + 1);
				seconds += minSeconds;

				Date next = new Date(last.getTime() + (seconds * 1000));
				return next;
			}
		};
	}

}
