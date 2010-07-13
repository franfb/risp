package org.labis.risp.client;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("greet")
public interface GreetingService extends RemoteService {
	Portal getPortal(MyLatLng point);
	ArrayList<Portal> getPortales(String[] nombre, int numero);
	Via getVia(MyLatLng point);
	ArrayList<Portal> getPortales(Via via);
	ArrayList<Portal> getPortales(MyPolygon poly);
	Zona getVias(MyPolygon poly);
	Zona getZonass(MyPolygon poly);
	Via getVia(Portal portal);
	ArrayList<Via> getVias(String[] nombre);
}