package cz.cdv.datex2;

import java.net.URL;

import javax.xml.ws.Endpoint;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import cz.cdv.datex2.internal.ClientPullImpl;
import cz.cdv.datex2.internal.ClientSubscriptionImpl;
import cz.cdv.datex2.internal.Datex2ClientImpl;
import cz.cdv.datex2.internal.Datex2SupplierImpl;
import cz.cdv.datex2.internal.SupplierPushImpl;
import cz.cdv.datex2.wsdl.clientsubscribe.ClientSubscribeInterface;
import cz.cdv.datex2.wsdl.clientsubscribe.ClientSubscribeService;
import eu.datex2.wsdl.clientpull._2_0.ClientPullInterface;
import eu.datex2.wsdl.clientpull._2_0.ClientPullService;

public class Datex2 implements ApplicationContextAware {

	private ApplicationContext applicationContext;

	private String subscribePrefix = "subscribe";

	public Datex2Client createClient(URL supplierWsdlLocation,
			URL supplierSubscriptionWsdlLocation, String clientPath) {

		ClientPullService pullService = new ClientPullService(
				supplierWsdlLocation);
		ClientPullInterface pullEndPoint = pullService
				.getClientPullSoapEndPoint();

		ClientSubscribeInterface subscriptionEndPoint = null;
		if (supplierSubscriptionWsdlLocation != null) {
			ClientSubscribeService subscriptionService = new ClientSubscribeService(
					supplierSubscriptionWsdlLocation);
			subscriptionEndPoint = subscriptionService
					.getClientSubscribeSoapEndPoint();
		}

		Datex2ClientImpl client = new Datex2ClientImpl(pullEndPoint,
				subscriptionEndPoint);
		autowire(client);

		if (clientPath != null) {
			SupplierPushImpl push = new SupplierPushImpl(client);
			autowire(push);
			Endpoint.publish(clientPath, push);
		}

		return client;
	}

	public Datex2Supplier createSupplier(String supplierPath,
			boolean withSubscriptions) {

		return createSupplier(
				supplierPath,
				withSubscriptions ? joinPaths(getSubscribePrefix(),
						supplierPath) : null);
	}

	public Datex2Supplier createSupplier(String supplierPath,
			String subscriptionPath) {

		if (subscriptionPath != null) {
			ClientSubscriptionImpl subscription = new ClientSubscriptionImpl(
					supplierPath, subscriptionPath);
			autowire(subscription);
			Endpoint.publish(subscriptionPath, subscription);
		}

		Datex2SupplierImpl supplier = new Datex2SupplierImpl(supplierPath,
				subscriptionPath);
		autowire(supplier);

		ClientPullImpl pull = new ClientPullImpl(supplier);
		autowire(pull);
		Endpoint.publish(supplierPath, pull);

		return supplier;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {

		this.applicationContext = applicationContext;
	}

	private String joinPaths(String... paths) {
		if (paths == null || paths.length == 0)
			return null;

		StringBuilder sb = new StringBuilder();
		for (String path : paths) {
			if (path == null)
				continue;
			path = path.trim();
			if (path.length() == 0)
				continue;

			if (sb.length() > 0) {
				while (path.startsWith("/")) {
					path = path.substring(1);
				}
			}
			while (path.endsWith("/"))
				path = path.substring(0, path.length() - 1);

			path = path.trim();
			if (path == null || path.length() == 0)
				continue;

			if (sb.length() > 0)
				sb.append("/");

			sb.append(path);
		}

		return sb.toString();
	}

	private void autowire(Object bean) {
		applicationContext.getAutowireCapableBeanFactory().autowireBean(bean);
		if (bean instanceof InitializingBean) {
			try {
				((InitializingBean) bean).afterPropertiesSet();
			} catch (RuntimeException e) {
				throw e;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	public String getSubscribePrefix() {
		return subscribePrefix;
	}

	public void setSubscribePrefix(String subscribePrefix) {
		this.subscribePrefix = subscribePrefix;
	}

}
