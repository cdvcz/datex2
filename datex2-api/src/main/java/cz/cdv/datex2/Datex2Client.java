package cz.cdv.datex2;

import java.util.List;

import cz.cdv.datex2.handlers.Datex2Handler;

public interface Datex2Client {

	void pull();

	void addHandler(Datex2Handler handler);

	void removeHandler(Datex2Handler handler);

	List<Datex2Handler> getHandlers();

}
