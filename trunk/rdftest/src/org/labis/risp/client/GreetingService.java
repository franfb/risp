package org.labis.risp.client;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("greet")
public interface GreetingService extends RemoteService {
	Portal getPortal(LatLong point);
	Via getVia(LatLong point);
	ArrayList<Portal> getPortales(LatLong topRight, LatLong BottomLeft);
	ArrayList<Via> getVias(LatLong topRight, LatLong BottomLeft);
}