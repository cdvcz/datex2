package cz.cdv.datex2.providers;

import eu.datex2.schema._2._2_0.D2LogicalModel;

public interface SnapshotProvider extends Datex2Provider {

	D2LogicalModel getSnapshot();

}
