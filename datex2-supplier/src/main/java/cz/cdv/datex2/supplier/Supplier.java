package cz.cdv.datex2.supplier;

import java.util.logging.Logger;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;

import cz.cdv.datex2.Datex2;
import cz.cdv.datex2.Datex2Supplier;
import eu.datex2.schema._2._2_0.CountryEnum;

public class Supplier implements InitializingBean {

	private static final Logger log = Logger.getLogger(Supplier.class
			.getSimpleName());

	@Autowired
	private Datex2 datex2;
	@Autowired
	private TaskScheduler scheduler;

	@Override
	public void afterPropertiesSet() throws Exception {
		log.info("Initializing JAX-WS client ...");
		Datex2Supplier supplier = datex2.createSupplier("cdv", true);
		RandomSpacesProvider provider = new RandomSpacesProvider(supplier,
				scheduler, CountryEnum.CZ, "CDV", "cs");
		supplier.addProvider(provider);
		log.info("Supplier initialized.");
	}

}
