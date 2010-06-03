package org.labis.risp.client;



import java.util.ArrayList;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("greet")
public interface GreetingService extends RemoteService {
	boolean initialize();
	
	ArrayList<Street> getStreets(LatLong topRight, LatLong BottonLeft);
	Street getStreet(LatLong place);
}
