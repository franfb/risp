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
import com.google.gwt.maps.client.event.PolylineEndLineHandler;
import com.google.gwt.maps.client.event.MapClickHandler.MapClickEvent;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.overlay.Marker;
import com.google.gwt.maps.client.overlay.MarkerOptions;
import com.google.gwt.maps.client.overlay.PolyStyleOptions;
import com.google.gwt.maps.client.overlay.Polygon;
import com.google.gwt.maps.client.overlay.Polyline;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class RdfTest implements EntryPoint{
	private MapWidget map;
	private Button zonaButton;
	private Button viaButton;
	private Button portalButton;
	private ArrayList<Zona> areas;
	private InfoWindow info;

	private VerticalPanel mainPanel;
	
	private final GreetingServiceAsync greetingService = GWT
			.create(GreetingService.class);

	public void onModuleLoad() {
		
		areas = new ArrayList<Zona>();
		
		mainPanel = new VerticalPanel();


		//RDF con Google Maps
		
		map = new MapWidget();
		map.setSize("700px", "500px");
		map.setUIToDefault();
		LatLng tenerife = LatLng.newInstance(28.4860,-16.3161);
		map.setCenter(tenerife);
		map.setZoomLevel(13);
		
		mainPanel.add(map);
		
		zonaButton = new Button("nueva zona");
		mainPanel.add(zonaButton);
		zonaButton.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event) {
				zonaButton.setEnabled(false);
				viaButton.setEnabled(false);
				portalButton.setEnabled(false);
				createPolyline();
			}
		});

		viaButton = new Button("nueva via");
		mainPanel.add(viaButton);
		viaButton.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event) {
				zonaButton.setEnabled(false);
				viaButton.setEnabled(false);
				portalButton.setEnabled(false);
				map.addMapClickHandler(new MapClickHandler(){
					public void onClick(MapClickEvent event) {
						map.removeMapClickHandler(this);
						nuevaVia(event.getLatLng());
						zonaButton.setEnabled(true);
						viaButton.setEnabled(true);
						portalButton.setEnabled(true);
					}
				});
			}
		});
		
		portalButton = new Button("nuevo portal");
		mainPanel.add(portalButton);
		portalButton.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event) {
				zonaButton.setEnabled(false);
				viaButton.setEnabled(false);
				portalButton.setEnabled(false);
				map.addMapClickHandler(new MapClickHandler(){
					public void onClick(MapClickEvent event) {
						map.removeMapClickHandler(this);
						nuevoPortal(event.getLatLng());
						zonaButton.setEnabled(true);
						viaButton.setEnabled(true);
						portalButton.setEnabled(true);
					}
				});
			}
		});
		
//		HorizontalPanel h = new HorizontalPanel();
//		h.add(new Image("http://www.ull.es/Public/images/wull/logo.gif"));
//		h.add(new Image("http://www.gerenciaurbanismo.com/gerencia/GERENCIA/published/DEFAULT/img/layout_common/gerencia.gif"));
//		h.add(new Image("http://www.gerenciaurbanismo.com/gerencia/GERENCIA/published/DEFAULT/img/layout_common/la_laguna.gif"));
//
//		mainPanel.add(h);
		
		RootPanel.get().add(mainPanel);

		
		
		
		
	}

	private Marker createMarkerPortal(final Portal portal) {
		MarkerOptions markerOpt = MarkerOptions.newInstance();
		markerOpt.setClickable(true);
		final Marker marker = new Marker(LatLng.newInstance(portal.getCoordenadas()
				.getLatitude(), portal.getCoordenadas().getLongitude()));
		marker.addMarkerClickHandler(new MarkerClickHandler() {
			public void onClick(MarkerClickEvent event) {
				InfoWindow info = map.getInfoWindow();
				String nombreCalle = "Desconocido";
				if (portal.getVia() != null){
					nombreCalle = "<b>" +
					portal.getVia().getTipo() +
					" " + 
					portal.getVia().getNombre() + 
					", " + 
					portal.getNumero() + 
					"</b>";
				}
				info.open(marker, new InfoWindowContent(nombreCalle
						+ "<br>Habitantes: "
						+ portal.getHabitantes() + "<br>Hojas padronales: "
						+ portal.getHojas()));
			}
		});
		return marker;
	}
	
	

	private void createPolyline() {
		String color = "#FF0000";
		double opacity = 1.0;
		int weight = 1;

		PolyStyleOptions style = PolyStyleOptions.newInstance(color, weight,
				opacity);

		final Polyline poly = new Polyline(new LatLng[0]);
		map.addOverlay(poly);
		poly.setDrawingEnabled();
		poly.setStrokeStyle(style);

		poly.addPolylineEndLineHandler(new PolylineEndLineHandler() {

			public void onEnd(PolylineEndLineEvent event) {
				nuevaZona(event.getSender());
				zonaButton.setEnabled(true);
				viaButton.setEnabled(true);
				portalButton.setEnabled(true);
			}
		});
	}

	public boolean contains(Polygon p, LatLng latLng) {
		int j = 0;
		boolean oddNodes = false;
		double x = latLng.getLongitude();
		double y = latLng.getLatitude();
		for (int i = 0; i < p.getVertexCount(); i++) {
			j++;
			if (j == p.getVertexCount()) {
				j = 0;
			}
			if (((p.getVertex(i).getLatitude() < y) && (p.getVertex(j)
					.getLatitude() >= y))
					|| ((p.getVertex(j).getLatitude() < y) && (p.getVertex(i)
							.getLatitude() >= y))) {
				if (p.getVertex(i).getLongitude()
						+ (y - p.getVertex(i).getLatitude())
						/ (p.getVertex(j).getLatitude() - p.getVertex(i)
								.getLatitude())
						* (p.getVertex(j).getLongitude() - p.getVertex(i)
								.getLongitude()) < x) {
					oddNodes = !oddNodes;
				}
			}
		}
		return oddNodes;
	}

	private void nuevaZona(Polyline pline) {
		LatLng[] coord = new LatLng[pline.getVertexCount()];
		for (int i = 0; i < pline.getVertexCount(); i++) {
			coord[i] = pline.getVertex(i);
		}
		final Polygon p = new Polygon(coord, "#f33f00", 5, 1, "#ff0000", 0.2);

		try {
			greetingService.getPortales(
					new LatLong(p.getBounds().getNorthEast()), new LatLong(p
							.getBounds().getSouthWest()),
					new AsyncCallback<ArrayList<Portal>>() {
						public void onFailure(Throwable caught) {
							System.out.println("Error.");
						}

						public void onSuccess(ArrayList<Portal> result) {
							int count = 0;
							for (int i = 0; i < result.size(); i++) {
								
								LatLng l = LatLng.newInstance(result.get(i)
										.getCoordenadas().getLatitude(), result.get(i)
										.getCoordenadas().getLongitude());
								if (contains(p, l)) {
									count++;
								}
							}

							final Portal[] contained = new Portal[count];

							int index = 0;
							for (int i = 0; i < result.size(); i++) {
								LatLng l = LatLng.newInstance(result.get(i)
										.getCoordenadas().getLatitude(), result.get(i)
										.getCoordenadas().getLongitude());
								if (contains(p, l)) {
									contained[index++] = result.get(i);
									map.addOverlay(createMarkerPortal(contained[index - 1]));
								}
							}
							map.addOverlay(p);
							p.addPolygonMouseOverHandler(new PolygonMouseOverHandler(){
								public void onMouseOver(PolygonMouseOverEvent event) {
									for (Zona area : areas) {
										if (area.getPoly() == event.getSource()) {
											info = map.getInfoWindow();
											double sizeArea = area.getPoly().getArea();
											String unit = "m";
//											if (sizeArea > 1000000){
//												sizeArea /= 1000000;
//												unit = "km";
//											}

											info.open(area.getPoly().getBounds().getCenter(),
													new InfoWindowContent("Area: "
															+ (int)sizeArea + " " + unit + "<sup>2</sup>"
															+ "<br>Population: " + area.getHabitantes()
															+ "<br>Registers: " + area.getHojas()));
										}
									}
								}

							});
							p.addPolygonMouseOutHandler(new PolygonMouseOutHandler(){
								public void onMouseOut(PolygonMouseOutEvent event) {
									if (info != null) {
										info.close();
									}
								}
							});
							areas.add(new Zona(p, contained));
							for (int i = 0; i < contained.length; i++) {
								map.addOverlay(createMarkerPortal(contained[i]));
							}

						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void nuevoPortal(final LatLng point) {
		try {
			greetingService.getPortal(new LatLong(point),
					new AsyncCallback<Portal>() {
						public void onFailure(Throwable caught) {
							System.out.println("Error.");
						}

						public void onSuccess(final Portal portal) {
							if (portal == null){
								InfoWindow info = map.getInfoWindow();
								info.open(point, new InfoWindowContent("no hay ningún edificio poblado en las cercanías"));
							}
							else{
								map.addOverlay(createMarkerPortal(portal));
							}
						}
						});
					}
			catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void nuevaVia(final LatLng point) {
		try {
			greetingService.getVia(new LatLong(point),
					new AsyncCallback<Via>() {
						public void onFailure(Throwable caught) {
							System.out.println("Error.");
						}

						public void onSuccess(final Via via) {
							if (via == null){
								InfoWindow info = map.getInfoWindow();
								info.open(point, new InfoWindowContent("no hay ninguna vía en las cercanías"));
							}
							else{
								for (Portal portal: via.getPortales()){
									map.addOverlay(createMarkerPortal(portal));
								}	
							}
						}
						});
					}
			catch (Exception e) {
			e.printStackTrace();
		}
	}
}
