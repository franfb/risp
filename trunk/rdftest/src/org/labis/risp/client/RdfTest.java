package org.labis.risp.client;

import java.util.ArrayList;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
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
import com.google.gwt.maps.client.geocode.Geocoder;
import com.google.gwt.maps.client.geocode.LocationCallback;
import com.google.gwt.maps.client.geocode.Placemark;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.overlay.Marker;
import com.google.gwt.maps.client.overlay.MarkerOptions;
import com.google.gwt.maps.client.overlay.PolyStyleOptions;
import com.google.gwt.maps.client.overlay.Polygon;
import com.google.gwt.maps.client.overlay.Polyline;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class RdfTest implements EntryPoint, MapClickHandler, ClickHandler,
		PolygonMouseOverHandler, PolygonMouseOutHandler {
	private MapWidget map;
	private Button button;
	private ArrayList<Area> areas;
	private InfoWindow info;

	private Geocoder geo;
	
	private VerticalPanel mainPanel;


	private final GreetingServiceAsync greetingService = GWT
			.create(GreetingService.class);

	public void onModuleLoad() {
		
		greetingService.initialize(new AsyncCallback<Boolean>(){

			public void onFailure(Throwable caught) {
			}

			public void onSuccess(Boolean result) {
				if (!result){
					System.out.println("No se pudo cargar el modelo RDF");
				}
				else{
					System.out.println("Modelo RDF cargado correctamente");
				}
			}
			
		});
		
		geo = new Geocoder();
		areas = new ArrayList<Area>();

		
		mainPanel = new VerticalPanel();

		
		


		// LatLng tenerife =
		// LatLng.newInstance(28.4682385853027,-16.2546157836914);

		//RDF con Google Maps
		
		map = new MapWidget();
		map.setSize("700px", "500px");
		map.setUIToDefault();
		mainPanel.add(map);
		
		button = new Button("new area");
		mainPanel.add(button);
		
		button.addClickHandler(this);
		map.addMapClickHandler(this);
		
//		HorizontalPanel h = new HorizontalPanel();
//		h.add(new Image("http://www.ull.es/Public/images/wull/logo.gif"));
//		h.add(new Image("http://www.gerenciaurbanismo.com/gerencia/GERENCIA/published/DEFAULT/img/layout_common/gerencia.gif"));
//		h.add(new Image("http://www.gerenciaurbanismo.com/gerencia/GERENCIA/published/DEFAULT/img/layout_common/la_laguna.gif"));
//
//		mainPanel.add(h);
		
		RootPanel.get().add(mainPanel);

		
		
		
		
	}

	private Marker createMarker(final Street street) {
		MarkerOptions markerOpt = MarkerOptions.newInstance();
		markerOpt.setClickable(true);
		final Marker marker = new Marker(LatLng.newInstance(street.getCoord()
				.getLatitude(), street.getCoord().getLongitude()));
		marker.addMarkerClickHandler(new MarkerClickHandler() {
			public void onClick(MarkerClickEvent event) {
				InfoWindow info = map.getInfoWindow();
				info.open(marker, new InfoWindowContent("<b>"
						+ street.getName() + "</b>" + "<br>Code: "
						+ street.getCode() + "<br>Population: "
						+ street.getPopulation() + "<br>Registers: "
						+ street.getRegisters() + "<br>Type: "
						+ street.getKind()));
			}
		});
		return marker;
	}

	private void createPolyline() {
		final RdfTest This = this;
		String color = "#FF0000";
		double opacity = 1.0;
		int weight = 1;

		PolyStyleOptions style = PolyStyleOptions.newInstance(color, weight,
				opacity);

		final Polyline poly = new Polyline(new LatLng[0]);
		map.addOverlay(poly);
		poly.setDrawingEnabled();
		poly.setStrokeStyle(style);
		// poly.addPolylineLineUpdatedHandler(new PolylineLineUpdatedHandler() {
		//
		// public void onUpdate(PolylineLineUpdatedEvent event) {
		// }
		// });

		// poly.addPolylineCancelLineHandler(new PolylineCancelLineHandler() {
		//
		// public void onCancel(PolylineCancelLineEvent event) {
		// message2.setText(message2.getText() + " : Line Canceled");
		// }
		// });

		poly.addPolylineEndLineHandler(new PolylineEndLineHandler() {

			public void onEnd(PolylineEndLineEvent event) {
				System.out.println("POLILINEA CREADA");
				newArea(event.getSender());
				button.setEnabled(true);
				map.addMapClickHandler(This);
			}
		});
	}

	public void onClick(MapClickEvent event) {
		if (button.isEnabled()) {
			if (event.getLatLng() == null) {
				return;
			}
			showStreet(event.getLatLng());
			return;
		}
		if (event.getLatLng() == null) {
			return;
		}
	}

	public void onClick(ClickEvent event) {
		if (event.getSource() == button) {
			button.setEnabled(false);
			map.removeMapClickHandler(this);
			createPolyline();
		}
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

	private void newArea(Polyline pline) {
		final RdfTest This = this;

		LatLng[] coord = new LatLng[pline.getVertexCount()];
		for (int i = 0; i < pline.getVertexCount(); i++) {
			coord[i] = pline.getVertex(i);
		}
		final Polygon p = new Polygon(coord, "#f33f00", 5, 1, "#ff0000", 0.2);

		try {
			greetingService.getStreets(
					new LatLong(p.getBounds().getNorthEast()), new LatLong(p
							.getBounds().getSouthWest()),
					new AsyncCallback<ArrayList<Street>>() {
						public void onFailure(Throwable caught) {
							System.out.println("Error.");
						}

						public void onSuccess(ArrayList<Street> result) {
							int count = 0;
							for (int i = 0; i < result.size(); i++) {
								LatLng l = LatLng.newInstance(result.get(i)
										.getCoord().getLatitude(), result.get(i)
										.getCoord().getLongitude());
								if (contains(p, l)) {
									count++;
								}
							}

							final Street[] contained = new Street[count];

							int index = 0;
							for (int i = 0; i < result.size(); i++) {
								LatLng l = LatLng.newInstance(result.get(i)
										.getCoord().getLatitude(), result.get(i)
										.getCoord().getLongitude());
								if (contains(p, l)) {
									contained[index++] = result.get(i);

									final int j = index - 1;

									geo.getLocations(l, new LocationCallback() {
										public void onFailure(int statusCode) {
											contained[j].setName(".....");
											map.addOverlay(createMarker(contained[j]));
										}

										public void onSuccess(
												JsArray<Placemark> locations) {
											for (int i = 0; i < locations
													.length(); i++) {
												if (locations.get(i)
														.getStreet() != null) {
													contained[j]
															.setName(locations
																	.get(i)
																	.getStreet());
													map.addOverlay(createMarker(contained[j]));
													return;
												}
											}
											contained[j].setName(".....");
											map.addOverlay(createMarker(contained[j]));
										}
									});

								}
							}

							map.addOverlay(p);
							p.addPolygonMouseOverHandler(This);
							p.addPolygonMouseOutHandler(This);
							areas.add(new Area(p, contained));
							for (int i = 0; i < contained.length; i++) {
								map.addOverlay(createMarker(contained[i]));
							}

						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void showStreet(final LatLng point) {
		try {
			greetingService.getStreet(new LatLong(point),
					new AsyncCallback<Street>() {
						public void onFailure(Throwable caught) {
							System.out.println("Error.");
						}

						public void onSuccess(final Street result) {
							LatLng p = LatLng.newInstance(result.getCoord()
									.getLatitude(), result.getCoord()
									.getLongitude());

							geo.getLocations(p, new LocationCallback() {
								public void onFailure(int statusCode) {
									showInfo(".....");
								}

								public void onSuccess(
										JsArray<Placemark> locations) {
									for (int i = 0; i < locations.length(); i++) {
										if (locations.get(i).getStreet() != null) {
											showInfo(locations.get(i)
													.getStreet());
											return;
										}
									}
									showInfo(".....");
								}

								public void showInfo(String street) {
									InfoWindow info = map.getInfoWindow();
									info.open(point, new InfoWindowContent(
											"<b>" + street + "</b>"
													+ "<br>Code: "
													+ result.getCode()
													+ "<br>Population: "
													+ result.getPopulation()
													+ "<br>Registers: "
													+ result.getRegisters()
													+ "<br>Type: "
													+ result.getKind()));
								}
							});
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void onMouseOver(PolygonMouseOverEvent event) {
		for (Area area : areas) {
			if (area.getPoly() == event.getSource()) {
				info = map.getInfoWindow();
				double sizeArea = area.getPoly().getArea();
				String unit = "m";
//				if (sizeArea > 1000000){
//					sizeArea /= 1000000;
//					unit = "km";
//				}

				info.open(area.getPoly().getBounds().getCenter(),
						new InfoWindowContent("Area: "
								+ (int)sizeArea + " " + unit + "<sup>2</sup>"
								+ "<br>Population: " + area.getPopulation()
								+ "<br>Registers: " + area.getRegisters()));
			}
		}
	}

	public void onMouseOut(PolygonMouseOutEvent event) {
		if (info != null) {
			info.close();
		}
	}

}
