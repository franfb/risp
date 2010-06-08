package org.labis.risp.client;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("greet")
public interface GreetingService extends RemoteService {
	//ArrayList<Portal> getPortales(LatLong topRight, LatLong BottomLeft);
	Portal getPortal(LatLong place);
	Via getVia(LatLong place);
	ArrayList<Portal> getPortales(LatLong topRight, LatLong BottomLeft);
}