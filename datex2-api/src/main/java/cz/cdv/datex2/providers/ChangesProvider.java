package cz.cdv.datex2.providers;

import eu.datex2.schema._2._2_0.D2LogicalModel;
import eu.datex2.schema._2._2_0.UpdateMethodEnum;

public interface ChangesProvider extends Datex2Provider {

	D2LogicalModel getChanges(UpdateMethodEnum updateMethod, String... changes);

}
