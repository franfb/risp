package org.labis.risp.client;

import java.util.ArrayList;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import com.google.gwt.maps.client.InfoWindow;
import com.google.gwt.maps.client.InfoWindowContent;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.event.MapClickHandler;
import com.google.gwt.maps.client.event.MarkerClickHandler;
import com.google.gwt.maps.client.event.PolygonMouseOutHandler;
import com.google.gwt.maps.client.event.PolygonMouseOverHandler;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.overlay.Marker;
import com.google.gwt.maps.client.overlay.MarkerOptions;
import com.google.gwt.maps.client.overlay.Polygon;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.RootPanel;

public class RdfTest implements EntryPoint, MapClickHandler, ClickHandler, PolygonMouseOverHandler, PolygonMouseOutHandler {
	private MapWidget map;
	private Button button;
	private LatLng first, second;
	private ArrayList<Area> areas;
	private InfoWindow info;
	
	private ArrayList<Marker> path;
	
	private final GreetingServiceAsync greetingService = GWT
			.create(GreetingService.class);

	public void onModuleLoad() {
		button = new Button("new area");
		RootPanel.get().add(button);
		
		
		//LatLng tenerife = LatLng.newInstance(28.4682385853027,-16.2546157836914);

	    map = new MapWidget();
	    map.setSize("700px", "500px");
	    map.setUIToDefault();
	    RootPanel.get("mapContainer").add(map);
	    button.addClickHandler(this);
		map.addMapClickHandler(this);
		areas = new ArrayList<Area>();
	}
	
    private Marker createMarker(final Street street) {
        MarkerOptions markerOpt = MarkerOptions.newInstance();
        markerOpt.setClickable(true);
        final Marker marker = new Marker(LatLng.newInstance(street.getCoord().getLatitude(), street.getCoord().getLongitude()));
        marker.addMarkerClickHandler(new MarkerClickHandler() {
            public void onClick(MarkerClickEvent event) {
                InfoWindow info = map.getInfoWindow();
                info.open(marker,
                        new InfoWindowContent("<b>" + street.getName() + "</b>" + 
                        		"<br>Code: " + street.getCode() +
                        		"<br>Population: " + street.getPopulation() +
                        		"<br>Registers: " + street.getRegisters() +
                        		"<br>Type: " + street.getKind()
                        )
                );
            }
        });
        return marker;
    }

	public void onClick(MapClickEvent event) {
		if (event.getLatLng() == null){
			return;
		}
		if (button.isEnabled()){
			showStreet(event.getLatLng());
			return;
		}
		if (first == null){
			first = event.getLatLng();
		}
		else{
			second = event.getLatLng();
			map.removeMapClickHandler(this);
			
			LatLng left = first;
			LatLng right = second;
			if (second.getLongitude() < first.getLongitude()){
				left = second;
				right = first;
			}

			LatLng topRight;
			LatLng bottomLeft;
			LatLng topLeft;
			LatLng bottomRight;
			
			if (left.getLatitude() < right.getLatitude()){
				topRight = right;
				bottomLeft = left;
				topLeft = LatLng.newInstance(topRight.getLatitude(), bottomLeft.getLongitude());
				bottomRight = LatLng.newInstance(bottomLeft.getLatitude(), topRight.getLongitude());
			}
			else{
				bottomRight = right;
				topLeft = left;
				topRight = LatLng.newInstance(topLeft.getLatitude(), bottomRight.getLongitude());
				bottomLeft = LatLng.newInstance(bottomRight.getLatitude(), topLeft.getLongitude());
			}
			
			newArea(topRight, bottomRight, bottomLeft, topLeft);
			button.setEnabled(true);
			map.addMapClickHandler(this);
		}
	}

	public void onClick(ClickEvent event) {
		if (event.getSource() == button){
			button.setEnabled(false);
			first = null;
			second = null;
		}
	}
	
	private void newArea(final LatLng topRight, final LatLng bottomRight, final LatLng bottomLeft, final LatLng topLeft){
		final RdfTest This = this;
		try {
			greetingService.getStreets(
					new LatLong(topRight),
					new LatLong(bottomLeft),
					new AsyncCallback<Street[]>() {
						public void onFailure(Throwable caught) {
							System.out.println("Error.");
						}
						public void onSuccess(Street[] result) {
							LatLng[] coord = {topRight, bottomRight, bottomLeft, topLeft, topRight};
							Polygon p = new Polygon(coord, "#f33f00", 5, 1, "#ff0000", 0.2);
							//p.getBounds().get
							map.addOverlay(p);
							p.addPolygonMouseOverHandler(This);
							p.addPolygonMouseOutHandler(This);
							areas.add(new Area(p, result));
							for (int i = 0; i < result.length; i++){
								map.addOverlay(createMarker(result[i]));
							}
							
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void showStreet(final LatLng point){
		try {
			greetingService.getStreet(
					new LatLong(point),
					new AsyncCallback<Street>() {
						public void onFailure(Throwable caught) {
							System.out.println("Error.");
						}
						public void onSuccess(Street result) {
							InfoWindow info = map.getInfoWindow();
				            info.open(point,
				                    new InfoWindowContent("<b>" + result.getName() + "</b>" + 
				                    		"<br>Code: " + result.getCode() +
				                    		"<br>Population: " + result.getPopulation() +
				                    		"<br>Registers: " + result.getRegisters() +
				                    		"<br>Type: " + result.getKind()
				                    )
				            );
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void onMouseOver(PolygonMouseOverEvent event) {
		for (Area area: areas){
			if (area.getPoly() == event.getSource()){
				info = map.getInfoWindow();
		        info.open(area.getPoly().getBounds().getCenter(),
		                new InfoWindowContent( 
		                		"<br>Area: " + area.getPoly().getArea() + " m^2" + 
		                		"<br>Population: " + area.getPopulation() +
		                		"<br>Registers: " + area.getRegisters()
		                )
		        );
			}
		}
	}

	public void onMouseOut(PolygonMouseOutEvent event) {
		if (info != null){
			info.close();
		}
	}

}
