package cz.cdv.datex2;

import java.util.List;

import cz.cdv.datex2.handlers.Datex2Handler;
import eu.datex2.schema._2._2_0.D2LogicalModel;

public interface Datex2Client {

	D2LogicalModel get();

	void pull();

	String subscribe(Datex2Subscription subscription);

	void addHandler(Datex2Handler handler);

	void removeHandler(Datex2Handler handler);

	List<Datex2Handler> getHandlers();

}
