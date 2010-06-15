package org.labis.risp.client;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("greet")
public interface GreetingService extends RemoteService {
	Portal getPortal(MyLatLng point);
	Via getVia(MyLatLng point);
	ArrayList<Portal> getPortales(MyPolygon poly, MyLatLng topRight, MyLatLng BottomLeft);
	ArrayList<Portal> getPortales(Via via);
	ArrayList<Via> getVias(MyPolygon poly, MyLatLng topRight, MyLatLng BottomLeft);
}