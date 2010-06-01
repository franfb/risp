package org.labis.risp.client;





import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface GreetingServiceAsync {
	void greetServer(String input, AsyncCallback<String> callback)
			throws Exception;

	void getStreets(LatLong topRight, LatLong BottonLeft,
			AsyncCallback<Street[]> callback);

	void getStreet(LatLong place, AsyncCallback<Street> callback);
}
