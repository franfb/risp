package org.labis.risp.client;

import java.util.ArrayList;
import java.util.LinkedList;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.LocaleInfo;

import com.google.gwt.maps.client.InfoWindow;
import com.google.gwt.maps.client.InfoWindowContent;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.event.MapClickHandler;
import com.google.gwt.maps.client.event.MarkerClickHandler;
import com.google.gwt.maps.client.event.MarkerInfoWindowCloseHandler;
import com.google.gwt.maps.client.event.MarkerMouseOutHandler;
import com.google.gwt.maps.client.event.MarkerMouseOverHandler;
import com.google.gwt.maps.client.event.PolygonClickHandler;
import com.google.gwt.maps.client.event.PolylineEndLineHandler;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.geom.Point;
import com.google.gwt.maps.client.overlay.Icon;
import com.google.gwt.maps.client.overlay.Marker;
import com.google.gwt.maps.client.overlay.MarkerOptions;
import com.google.gwt.maps.client.overlay.PolyStyleOptions;
import com.google.gwt.maps.client.overlay.Polygon;
import com.google.gwt.maps.client.overlay.Polyline;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class Principal implements EntryPoint{
	private MapWidget map;

	HTML portalLink;
	HTML viaLink;
	HTML zonaLink;
	
	DialogBox dialogoPortal;
	DialogBox dialogoVia;
	DialogBox dialogoZona;
	
	private InfoWindow info;

	private final GreetingServiceAsync greetingService = GWT
			.create(GreetingService.class);
	
	MarkerOptions portalIcon1;
	MarkerOptions portalIcon2;
	MarkerOptions portalIcon3;
	MarkerOptions viaIcon1;
	MarkerOptions viaIcon2;
	MarkerOptions viaIcon3;
	
	Marker markerInfo = null;
	
	LinkedList<ZonaClient> zonas = new LinkedList<ZonaClient>();
	ZonaClient highlighted = null;
	
	public void onModuleLoad() {
		buildUi();
		crearDialogoPortal();
		crearDialogoVia();
		crearDialogoZona();
		
		Icon icon = Icon.newInstance();
		icon.setIconAnchor(Point.newInstance(16, 16));
		icon.setInfoWindowAnchor(Point.newInstance(32, 0));
		icon.setImageURL("http://www.visual-case.it/vc/pics/casetta_base.png");
		portalIcon1 = MarkerOptions.newInstance();
		portalIcon1.setIcon(icon);
        
		icon = Icon.newInstance();
		icon.setIconAnchor(Point.newInstance(16, 16));
		icon.setInfoWindowAnchor(Point.newInstance(32, 0));
		icon.setImageURL("http://www.visual-case.it/vc/pics/casetta_green.png");
		portalIcon2 = MarkerOptions.newInstance();
		portalIcon2.setIcon(icon);
		
		icon = Icon.newInstance();
		icon.setIconAnchor(Point.newInstance(16, 16));
		icon.setInfoWindowAnchor(Point.newInstance(32, 0));
		icon.setImageURL("http://www.visual-case.it/vc/pics/casetta_red.png");
		portalIcon3 = MarkerOptions.newInstance();
		portalIcon3.setIcon(icon);
		
		icon = Icon.newInstance();
		icon.setIconAnchor(Point.newInstance(16, 16));
		icon.setInfoWindowAnchor(Point.newInstance(32, 0));
		icon.setImageURL("http://maps.google.com/mapfiles/kml/pal4/icon23.png");
		viaIcon1 = MarkerOptions.newInstance();
		viaIcon1.setIcon(icon);
		viaIcon1.setDraggable(true);
		   
		icon = Icon.newInstance();
		icon.setIconAnchor(Point.newInstance(16, 16));
		icon.setInfoWindowAnchor(Point.newInstance(32, 0));
		icon.setImageURL("http://maps.google.com/mapfiles/kml/pal4/icon54.png");
		viaIcon2 = MarkerOptions.newInstance();
		viaIcon2.setIcon(icon);
		viaIcon2.setDraggable(true);
		
		icon = Icon.newInstance();
		icon.setIconAnchor(Point.newInstance(16, 16));
		icon.setInfoWindowAnchor(Point.newInstance(32, 0));
		icon.setImageURL("http://maps.google.com/mapfiles/kml/pal4/icon7.png");
		viaIcon3 = MarkerOptions.newInstance();
		viaIcon3.setIcon(icon);
		viaIcon3.setDraggable(true);
		
		
		portalLink.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				dialogoPortal.show();
				dialogoPortal.center();
			}
		});
		
		viaLink.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				dialogoVia.show();
				dialogoVia.center();
			}
		});
		
		zonaLink.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				dialogoZona.show();
				dialogoZona.center();
			}
		});
	}

	
	private Widget panelBusquedaPortal(){
		VerticalPanel vertical = new VerticalPanel();
		HorizontalPanel horizontal1 = new HorizontalPanel();
		HorizontalPanel horizontal2 = new HorizontalPanel();
		
		horizontal1.setSpacing(10);
		horizontal2.setSpacing(10);
		
		final TextBox via = new TextBox();
		final TextBox numero = new TextBox();
		via.setWidth("20em");
		numero.setWidth("5em");
		
		HTML textVia = new HTML("Vía:  ");
		HTML textNumero = new HTML("Número de portal:  ");

		textVia.setStyleName("texto13");
		textNumero.setStyleName("texto13");

		Button button = new Button("Buscar");
		
		horizontal1.add(textVia);
		horizontal1.add(via);
		
		horizontal2.add(textNumero);
		horizontal2.add(numero);
		horizontal2.add(button);

		vertical.add(horizontal1);
		vertical.add(horizontal2);
		
		button.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				String texto = via.getText();
				String[] resultado = texto.split(",");
				if (resultado.length > 2 || resultado[0].isEmpty()){
					return;
				}
				System.out.println("CALLE: " + resultado[0]);
				System.out.println("NUMERO: " + resultado[1]);
				String nombre = resultado[0];
				int numero = Integer.parseInt(resultado[1].trim());
				
//				try {
//					greetingService.getPortales(nombre, numero,
//							new AsyncCallback<ArrayList<Portal>>() {
//								public void onFailure(Throwable caught) {
//									System.out.println("Error.");
//								}
//
//								public void onSuccess(final ArrayList<Portal> portales) {
//									if (portales == null){
//										System.out.println("LAS DE ABAJO LO OYEN TODO");
//										InfoWindow info = map.getInfoWindow();
//										info.open(map.getCenter(), new InfoWindowContent("no hay ningún portal con esa dirección"));
//									}
//									else{
//										for (Portal p: portales){
//											map.addOverlay(createMarkerPortal(p, true));
//										}
//									}
//								}
//								});
//							}
//					catch (Exception e) {
//					e.printStackTrace();
//				}
				

				
			}
		});
		return vertical;
	}
	
	private Widget panelBusquedaVia(){
		VerticalPanel vertical = new VerticalPanel();
		HorizontalPanel horizontal1 = new HorizontalPanel();
		HorizontalPanel horizontal2 = new HorizontalPanel();
		
		horizontal1.setSpacing(10);
		horizontal2.setSpacing(10);
		
		final TextBox via = new TextBox();
		via.setWidth("20em");
		
		HTML textVia = new HTML("Vía:  ");

		textVia.setStyleName("texto13");

		Button button = new Button("Buscar");
		
		horizontal1.add(textVia);
		horizontal1.add(via);
		
		horizontal2.add(button);

		vertical.add(horizontal1);
		vertical.add(horizontal2);
		
		button.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				dialogoVia.hide();
				String texto = via.getText();
				String[] resultado = texto.split(" ");
				for (int i = 0; i < resultado.length; i++){
					resultado[i] = resultado[i].trim();
				}
				if (resultado[0].isEmpty()){
					return;
				}
				try {
					greetingService.getVias(resultado,
							new AsyncCallback<ArrayList<Via>>() {
								public void onFailure(Throwable caught) {
									System.out.println("Error.");
								}

								public void onSuccess(final ArrayList<Via> vias) {
									if (vias == null){
										System.out.println("LAS DE ABAJO LO OYEN TODO");
										InfoWindow info = map.getInfoWindow();
										info.open(map.getCenter(), new InfoWindowContent("no hay ningún portal con esa dirección"));
									}
									else{
										for (Via v: vias){
											map.addOverlay(createMarkerVia(v, true));
										}
									}
								}
								});
							}
					catch (Exception e) {
					e.printStackTrace();
				}
				

				
			}
		});
		return vertical;
	}


    
	private void buildUi() {
        map = new MapWidget();
        LatLng tenerife = LatLng.newInstance(28.5160,-16.3761);
        map.setSize("100%", "100%");
        map.setCenter(tenerife, 13);
        map.setUIToDefault();
        
        VerticalPanel columna = new VerticalPanel();
        columna.setSize("370px", "100%");
        columna.setStyleName("columna");
        
        VerticalPanel columna2 = new VerticalPanel();
        columna2.setWidth("293px");
        
        HTML titulo1 = new HTML("<h1>Información padronal<br><br>San Cristóbal de La Laguna</h1>");
        HTML bienvenida = new HTML("<b>Bienvenido/a la aplicación de ejemplo del proyecto RISP!</b>");
        HTML texto1 = new HTML("<br>Esta aplicación consiste en " +
        		"la reutilización de la información padronal de las bases de datos de la " +
        		"Gerencia de Urbanismo de San Cristóbal de La Laguna, y que ha sido publicada gracias al trabajo " +
        		"realizado por los mismos alumnos que han desarrollado este portal. Aquí, usted puede obtener diversa información relacionada con el registro " +
        		"del padrón que se realiza en el municipio. A continuación se presenta la lista de cosas que se pueden " +
        		"hacer:");
        portalLink = new HTML("<a href=\"javascript:undefined;\">"
                + "Mostrar información padronal de un portal" + "</a>");
        viaLink = new HTML("<a href=\"javascript:undefined;\">"
                + "Mostrar información padronal de una vía" + "</a>");
        zonaLink = new HTML("<a href=\"javascript:undefined;\">"
                + "Mostrar información padronal de una zona" + "</a>");
		HorizontalPanel gerencia = new HorizontalPanel();
		gerencia.setSpacing(5);
		HTML proyecto = new HTML("<a href=\"http://code.google.com/p/risp/\" target=\"_blank\">Página web del proyecto RISP </a>");
        gerencia.add(new Image("http://www.gerenciaurbanismo.com/gerencia/GERENCIA/published/DEFAULT/img/layout_common/gerencia.gif"));
        gerencia.add(new Image("http://www.gerenciaurbanismo.com/gerencia/GERENCIA/published/DEFAULT/img/layout_common/la_laguna.gif"));
        
        titulo1.setStylePrimaryName("texto13");
        bienvenida.setStyleName("texto13");
        texto1.setStyleName("texto13");
		proyecto.setStyleName("texto13");
        viaLink.setStyleName("texto13");
        portalLink.setStyleName("texto13");
        zonaLink.setStyleName("texto13");
        
        Image ull = new Image("http://www.ull.es/Public/images/wull/logo.gif");
        
        columna2.add(titulo1);
        columna2.add(bienvenida);
        columna2.add(texto1);
        columna2.add(new HTML("<br>"));
        columna2.add(portalLink);
        columna2.add(new HTML("<br>"));
        columna2.add(viaLink);
        columna2.add(new HTML("<br>"));
        columna2.add(zonaLink);
        columna2.add(new HTML("<br>"));
        columna2.add(new HTML("<br>"));
        columna2.add(new HTML("<br>"));
        columna2.add(new HTML("<br>"));
        columna2.add(new HTML("<br>"));
        columna2.add(new HTML("<br>"));
        columna2.add(proyecto);
        columna2.add(ull);
        columna2.add(gerencia);

        columna2.setCellHorizontalAlignment(ull, HasHorizontalAlignment.ALIGN_CENTER);
        columna2.setCellHorizontalAlignment(gerencia, HasHorizontalAlignment.ALIGN_CENTER);
        columna2.setCellHorizontalAlignment(proyecto, HasHorizontalAlignment.ALIGN_CENTER);
        
        HorizontalPanel horizontal = new HorizontalPanel();
        VerticalPanel vertical = new VerticalPanel();
        vertical.setWidth("40px");
        vertical.add(new HTML("<br>"));
        horizontal.add(vertical);
        horizontal.add(columna2);
        columna.add(horizontal);
        
        RootPanel.get().add(map, 0, 0);
        RootPanel.get().add(columna, 50, 0);
	}
	
	
	private void crearDialogoPortal() {
		Button portal = new Button("Buscar por localización");
		Button zona = new Button("Buscar por zona");
		Button volver = new Button("Volver");
		
		volver.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				dialogoPortal.hide();
			}
		});
		
		zona.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event) {
				//disableButtons();
				crearPolilinea(1);
				//enableButtons();
			}
		});
		
		portal.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event) {
				//disableButtons();
				map.addMapClickHandler(new MapClickHandler(){
					public void onClick(MapClickEvent event) {
						map.removeMapClickHandler(this);
						if (event.getLatLng() != null){
							nuevoPortal(event.getLatLng());
						}
						else{
							nuevoPortal(event.getOverlayLatLng());
						}
						//enableButtons();
					}
				});
			}
		});
		

		
		dialogoPortal = new DialogBox();
	    //dialogoPortal.setText("Información de empadronamiento asociada a los portales del municipio.");
	    dialogoPortal.setGlassEnabled(true);
		dialogoPortal.setAnimationEnabled(true);
		
	    HTML text1 = new HTML("Se puede obtener información padronal de un portal por su localización geográfica. " +
	    	"Para ello, pulse el siguiente botón y haga click en el mapa.");
	    
	    HTML text2 = new HTML("También se puede obtener información padronal de todos los portales que se encuentren dentro de una zona específica. " +
	    	"Para ello, pulse el siguiente botón y construya en el mapa la zona de interés, mediante un polígono.");
	    
	    HTML text3 = new HTML("Por último, se puede buscar un portal introduciendo la dirección de la vía y el número de portal.");
	    
	    text1.setStyleName("texto13");
	    text2.setStyleName("texto13");
	    text3.setStyleName("texto13");
	  
	    VerticalPanel vertical = new VerticalPanel();
	    vertical.setSpacing(10);
	    dialogoPortal.setWidget(vertical);

	    //vertical.add(new HTML("<br>"));
	    vertical.add(text1);
	    vertical.add(portal);
	    vertical.add(new HTML("<br>"));
	    vertical.add(text2);
	    vertical.add(zona);
	    vertical.add(new HTML("<br>"));
	    vertical.add(text3);
	    vertical.add(panelBusquedaPortal());
	    vertical.add(volver);
	    
	    vertical.setCellHorizontalAlignment(volver, HasHorizontalAlignment.ALIGN_RIGHT);
	}

	private void crearDialogoVia() {
		Button via = new Button("Buscar por localización");
		Button zona = new Button("Buscar por zona");
		Button volver = new Button("Volver");
		
		volver.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				dialogoVia.hide();
			}
		});
		
		zona.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event) {
				crearPolilinea(2);
			}
		});
		
		via.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event) {
				map.addMapClickHandler(new MapClickHandler(){
					public void onClick(MapClickEvent event) {
						map.removeMapClickHandler(this);
						if (event.getLatLng() != null){
							nuevaVia(event.getLatLng());
						}
						else{
							nuevaVia(event.getOverlayLatLng());
						}
					}
				});
			}
		});
		

		
		dialogoVia = new DialogBox();
	    //dialogoVia.setText("Información de empadronamiento asociada a las vías del municipio.");
	    dialogoVia.setGlassEnabled(true);
		dialogoVia.setAnimationEnabled(true);
		
	    HTML text1 = new HTML("Se puede obtener información padronal de una vía por su localización geográfica. " +
	    	"Para ello, pulse el siguiente botón y haga click en el mapa.");
	    
	    HTML text2 = new HTML("También se puede obtener información padronal de todas las vías que se encuentren dentro de una zona específica. " +
	    	"Para ello, pulse el siguiente botón y construya en el mapa la zona de interés, mediante un polígono.");
	    
	    HTML text3 = new HTML("Por último, se puede buscar una vía introduciendo el nombre en el siguiente campo de texto.");
	    
	    text1.setStyleName("texto13");
	    text2.setStyleName("texto13");
	    text3.setStyleName("texto13");
	  
	    VerticalPanel vertical = new VerticalPanel();
	    vertical.setSpacing(10);
	    dialogoVia.setWidget(vertical);

	    //vertical.add(new HTML("<br>"));
	    vertical.add(text1);
	    vertical.add(via);
	    vertical.add(new HTML("<br>"));
	    vertical.add(text2);
	    vertical.add(zona);
	    vertical.add(new HTML("<br>"));
	    vertical.add(text3);
	    vertical.add(panelBusquedaVia());
	    vertical.add(volver);
	    
	    vertical.setCellHorizontalAlignment(volver, HasHorizontalAlignment.ALIGN_RIGHT);
	}
	
	private void crearDialogoZona() {
		Button zona = new Button("Buscar por zona");
		Button volver = new Button("Volver");
		
		volver.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				dialogoZona.hide();
			}
		});
		
		zona.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event) {
				crearPolilinea(0);
			}
		});
		

		dialogoZona = new DialogBox();
		
		//dialogoZona.setText("Información de empadronamiento asociada a una zona del municipio.");
		dialogoZona.setGlassEnabled(true);
		dialogoZona.setAnimationEnabled(true);

	    HTML text1 = new HTML("Se puede obtener información padronal de una zona específica del municipio. " +
	    	"Para ello, pulse el siguiente botón y construya en el mapa la zona de interés, mediante un polígono.");
	    
	    
	    text1.setStyleName("texto13");

	    VerticalPanel vertical = new VerticalPanel();
	    vertical.setSpacing(10);
	    dialogoZona.setWidget(vertical);

	    //vertical.add(new HTML("<br>"));
	    vertical.add(text1);
	    vertical.add(zona);
	    vertical.add(volver);
	    
	    vertical.setCellHorizontalAlignment(volver, HasHorizontalAlignment.ALIGN_RIGHT);
	}
	
	private void infoWindowPortal(final Marker marker, final Portal portal, boolean verTodasVias){
		InfoWindow info = map.getInfoWindow();
		String nombreCalle = "Desconocido";
		if (portal.getVia() != null){
			nombreCalle = "<b>" +
			portal.getVia() + 
			", " + 
			portal.getNumero() + 
			"</b>";
		}

		VerticalPanel vertical = new VerticalPanel();

		
		HTML text = new HTML(nombreCalle
				+ "<br>Personas empadronadas: "
				+ portal.getHabitantes() + "<br>Hojas padronales: "
				+ portal.getHojas());
		vertical.add(text);

		
		if(verTodasVias){
			HTML link1 = new HTML();
			link1.addStyleName("ver");
			link1.setHTML("<br>Ver toda la vía");
			link1.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					try {
						greetingService.getVia(portal,
								new AsyncCallback<Via>() {
									public void onFailure(Throwable caught) {}
									public void onSuccess(final Via via) {
										greetingService.getPortales(via, new AsyncCallback<ArrayList<Portal>>(){
											public void onFailure(Throwable caught) {}
											public void onSuccess(ArrayList<Portal> result) {
												map.removeOverlay(marker);
												for (Portal portal: result){
													map.addOverlay(createMarkerPortal(portal, false));
												}
												map.addOverlay(createMarkerVia(via, false));
											}
										});
									}
								});
							}
						catch (Exception e) {
							e.printStackTrace();
						}
					}
			});
			vertical.add(link1);
		}
		info.open(marker, new InfoWindowContent(vertical));
		markerInfo = marker;
	}
	
	
	private void infoWindowVia(final Marker marker, final Via via, final boolean verTodosPortales){
		InfoWindow info = map.getInfoWindow();
		String nombreCalle = "<b>" +
		via.getNombre() + 
			"</b>";
		HTML text = new HTML(nombreCalle
				+ "<br>Personas empadronadas: "
				+ via.getHabitantes()
				+ "<br>Longitud de la vía: "
				+ via.getLongitud() + " m"
				+ "<br>Código de vía: "
				+ via.getCodigo());
		markerInfo = marker;
		
		
		
		

		final VerticalPanel vertical = new VerticalPanel();
		vertical.add(text);
		
		if(verTodosPortales){
			HTML link1 = new HTML();
			link1.addStyleName("ver");
			link1.setHTML("<br>Ver portales");
			link1.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					try {
						greetingService.getPortales(via, new AsyncCallback<ArrayList<Portal>>(){
							public void onFailure(Throwable caught) {}
							public void onSuccess(ArrayList<Portal> result) {
								//vertical.remove(link1);
								map.removeOverlay(marker);
								for (Portal portal: result){
									map.addOverlay(createMarkerPortal(portal, false));
								}
								map.addOverlay(createMarkerVia(via, false));
							}
						});
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			vertical.add(link1);
		}
		info.open(marker, new InfoWindowContent(vertical));
	}
	
	
	private Marker createMarkerPortal(final Portal portal, final boolean verTodasVias) {
		MarkerOptions markerOpt = MarkerOptions.newInstance();
		markerOpt.setClickable(true);
		MarkerOptions opt = portalIcon1;
		if (portal.getHabitantes() > 10){
			opt = portalIcon2;
		}
		if (portal.getHabitantes() > 50){
			opt = portalIcon3;
		}
		
		
		
		final Marker marker = new Marker(LatLng.newInstance(portal.getCoordenadas()
				.getLatitude(), portal.getCoordenadas().getLongitude()), opt);

		
		
		
		
		marker.addMarkerMouseOverHandler(new MarkerMouseOverHandler() {
			public void onMouseOver(MarkerMouseOverEvent event) {
				if (markerInfo != null && markerInfo == marker){
					return;
				}
				infoWindowPortal(marker, portal, verTodasVias);
			}
		});
		
		final MarkerMouseOutHandler out = new MarkerMouseOutHandler(){
			public void onMouseOut(MarkerMouseOutEvent event) {
				InfoWindow info = map.getInfoWindow();
				info.close();
			}
		};
		marker.addMarkerMouseOutHandler(out);
		
		
		marker.addMarkerClickHandler(new MarkerClickHandler() {
			public void onClick(MarkerClickEvent event) {
				marker.removeMarkerMouseOutHandler(out);
			}
		});
		
		marker.addMarkerInfoWindowCloseHandler(new MarkerInfoWindowCloseHandler() {
			public void onInfoWindowClose(MarkerInfoWindowCloseEvent event) {
				marker.removeMarkerMouseOutHandler(out);
				marker.addMarkerMouseOutHandler(out);
				if (markerInfo == marker){
					markerInfo = null;
				}

			}
		});
		return marker;
	}
	
	private Marker createMarkerVia(final Via via, final boolean verTodosPortales) {
		
		MarkerOptions opt = viaIcon1;
		if (via.getHabitantes() > 100){
			opt = viaIcon2;
		}
		if (via.getHabitantes() > 500){
			opt = viaIcon3;
		}
		
		final Marker marker = new Marker(LatLng.newInstance(via.getCoordenadas()
				.getLatitude(), via.getCoordenadas().getLongitude()), opt);
		marker.setDraggingEnabled(true);

		marker.addMarkerMouseOverHandler(new MarkerMouseOverHandler() {
			public void onMouseOver(MarkerMouseOverEvent event) {
				if (markerInfo != null && markerInfo == marker){
					return;
				}
				infoWindowVia(marker, via, verTodosPortales);
			}
		});
		
		final MarkerMouseOutHandler out = new MarkerMouseOutHandler(){
			public void onMouseOut(MarkerMouseOutEvent event) {
				InfoWindow info = map.getInfoWindow();
				info.close();
			}
		};
		marker.addMarkerMouseOutHandler(out);
		
		
		marker.addMarkerClickHandler(new MarkerClickHandler() {
			public void onClick(MarkerClickEvent event) {
				marker.removeMarkerMouseOutHandler(out);
			}
		});
		
		marker.addMarkerInfoWindowCloseHandler(new MarkerInfoWindowCloseHandler() {
			public void onInfoWindowClose(MarkerInfoWindowCloseEvent event) {
				marker.removeMarkerMouseOutHandler(out);
				marker.addMarkerMouseOutHandler(out);
				if (markerInfo == marker){
					markerInfo = null;
				}

			}
		});
		return marker;
	}
	
	
	private void crearPolilinea(final int opcion) {
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
				if (opcion == 0){
					nuevaZonaVacia(event.getSender());
				}
				else if(opcion == 1){
					nuevaZonaPortales(event.getSender());
				}
				else if(opcion == 2){
					nuevaZonaVias(event.getSender());
				}
				//enableButtons();
			}
		});
	}
	
	
	private void nuevaZonaVacia(final Polyline pline) {
		try {
			final MyPolygon myPoly = new MyPolygon(pline, map);
			greetingService.getZonass(myPoly,
					new AsyncCallback<Zona>() {
						public void onFailure(Throwable caught) {
							System.out.println("Error.");
						}
						public void onSuccess(final Zona result) {
							final Polygon poly = MyPolygon.getPolygon(pline);
							ZonaClient zona = new ZonaClient(result);
							zona.setPoly(poly);
							zona.setMyPoly(myPoly);
							zona.setVer(true);
							nuevaZona(zona);
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void nuevaZona(ZonaClient zona){
		zona.getPoly().setStrokeStyle(PolyStyleOptions.newInstance(zona.calculateColor()));
		zona.getPoly().setFillStyle(PolyStyleOptions.newInstance(zona.getColor(), 10, 0.3));
		
		map.addOverlay(zona.getPoly());
		zonas.addFirst(zona);
		if (highlighted != null){
			highlighted.getPoly().setFillStyle(PolyStyleOptions.newInstance(highlighted.getColor(), 10, 0.1));
		}
		highlighted = zona;
		setInfo(zona);
		zona.getPoly().addPolygonClickHandler(new PolygonClickHandler(){
		public void onClick(PolygonClickEvent event) {
			for (ZonaClient z: zonas){
				if (MyPolygon.contains(z.getPoly(), event.getLatLng())){
					zonas.remove(z);
					zonas.addLast(z);
					if (highlighted != null && highlighted.getPoly() != z.getPoly()){
						highlighted.getPoly().setFillStyle(PolyStyleOptions.newInstance(highlighted.getColor(), 10, 0.1));
						z.getPoly().setFillStyle(PolyStyleOptions.newInstance(z.getColor(), 10, 0.3));
					}
					highlighted = z;
					setInfo(z);
					break;
					}
				}
			}
		});
	}
	
	
	
	private void setInfo(final ZonaClient zona){
		info = map.getInfoWindow();
		double sizeArea = zona.getPoly().getArea();
		int densidad = (int) (zona.getHabitantes() / (sizeArea / 1000000));
	
		HTML text = new HTML("<b>Área de la zona:</b> "
						+ (int)sizeArea + " m<sup>2</sup>"
						+ "<br><b>Personas empadronadas:</b> " + zona.getHabitantes()
						+ "<br><b>Hojas padronales:</b> " + zona.getHojas()
						+ "<br><b>Densidad poblacional:</b> " + densidad + " hab/km<sup>2</sup>");
	
		final VerticalPanel vertical = new VerticalPanel();
		vertical.add(text);
		
		if(zona.isVer()){
			HTML link1 = new HTML();
			link1.setHTML("<br>Ver vías");
			link1.addStyleName("ver");
			link1.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					try {
						zona.setVer(false);
						map.getInfoWindow().close();
						zonaVias(zona);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			vertical.add(link1);
			
			HTML link2 = new HTML();
			link2.addStyleName("ver");
			link2.setHTML("Ver portales");
			link2.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					try {
						zona.setVer(false);
						map.getInfoWindow().close();
						zonaPortales(zona);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			vertical.add(link2);
		}
		info.open(zona.getPoly().getBounds().getCenter(), new InfoWindowContent(vertical));	
	
	
	}
	
	
	private void nuevaZonaVias(final Polyline pline) {
		try {
			final MyPolygon myPoly = new MyPolygon(pline, map);
			greetingService.getVias(myPoly,
					new AsyncCallback<Zona>() {
						public void onFailure(Throwable caught) {
							System.out.println("Error.");
						}

						public void onSuccess(Zona result) {
							ArrayList<Via> vias = result.getVias();
							for (int i = 0; i < vias.size(); i++) {
								map.addOverlay(createMarkerVia(vias.get(i), true));
							}

							ZonaClient zona = new ZonaClient();
							zona.setPoly(MyPolygon.getPolygon(pline));
							zona.setMyPoly(myPoly);
							zona.setVer(false);
							zona.setHabitantes(result.getHabitantes());
							zona.setHojas(result.getHojas());
							nuevaZona(zona);
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private void zonaVias(ZonaClient zona) {
		try {
			greetingService.getVias(zona.getMyPoly(),
					new AsyncCallback<Zona>() {
						public void onFailure(Throwable caught) {
							System.out.println("Error.");
						}
						public void onSuccess(Zona result) {
							ArrayList<Via> vias = result.getVias();
							for (int i = 0; i < vias.size(); i++) {
								map.addOverlay(createMarkerVia(vias.get(i), true));
							}
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	private void nuevaZonaPortales(final Polyline pline) {
		try {
			final MyPolygon myPoly = new MyPolygon(pline, map);
			greetingService.getPortales(myPoly,
					new AsyncCallback<ArrayList<Portal>>() {
						public void onFailure(Throwable caught) {
							System.out.println("Error.");
						}

						public void onSuccess(ArrayList<Portal> result) {
							int habitantes = 0;
							int hojas = 0;
							
							for (int i = 0; i < result.size(); i++) {
								map.addOverlay(createMarkerPortal(result.get(i), false));
								habitantes += result.get(i).getHabitantes();
								hojas += result.get(i).getHojas();
							}	
							final Polygon poly = MyPolygon.getPolygon(pline);
							ZonaClient zona = new ZonaClient();
							zona.setPoly(poly);
							zona.setMyPoly(myPoly);
							zona.setVer(false);
							zona.setHabitantes(habitantes);
							zona.setHojas(hojas);
							nuevaZona(zona);

						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void zonaPortales(ZonaClient zona) {
		try {
			greetingService.getPortales(zona.getMyPoly(),
					new AsyncCallback<ArrayList<Portal>>() {
						public void onFailure(Throwable caught) {
							System.out.println("Error.");
						}

						public void onSuccess(ArrayList<Portal> result) {
							for (int i = 0; i < result.size(); i++) {
								map.addOverlay(createMarkerPortal(result.get(i), false));
							}	
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void nuevoPortal(final LatLng point) {
		try {
			greetingService.getPortal(new MyLatLng(point),
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
								map.addOverlay(createMarkerPortal(portal, true));
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
			greetingService.getVia(new MyLatLng(point),
					new AsyncCallback<Via>() {
						public void onFailure(Throwable caught) {}
						public void onSuccess(final Via via) {
							if (via == null){
								InfoWindow info = map.getInfoWindow();
								info.open(point, new InfoWindowContent("no hay ninguna vía en las cercanías"));
							}
							else{
								greetingService.getPortales(via, new AsyncCallback<ArrayList<Portal>>(){
									public void onFailure(Throwable caught) {}
									public void onSuccess(ArrayList<Portal> result) {
										for (Portal portal: result){
											map.addOverlay(createMarkerPortal(portal, false));
										}
										map.addOverlay(createMarkerVia(via, false));
									}
								});
							}
						}
					});
				}
			catch (Exception e) {
			e.printStackTrace();
		}
	}
}


//private void buildUi(String texto) {
//map = new MapWidget();
//LatLng tenerife = LatLng.newInstance(28.5160,-16.3761);
//map.setSize("100%", "100%");
//map.setCenter(tenerife, 13);
//map.setUIToDefault();
//
////AbsolutePanel panel = new AbsolutePanel();
//
//VerticalPanel columna = new VerticalPanel();
////columna.setSize("370px", "100%");
//columna.setWidth("370px");
//columna.setStyleName("columna");
//
////panel.setSize("100%", "100%");
//
//
////panel.add(map, 0, 0);
//
////panel.add(columna, 50, 0);
//
//
////panel.add(panelBusqueda(), 425, (int) (Window.getClientHeight() * 0.92));
//
////RootPanel.get().add(panel);
//RootPanel.get().add(map, 0, 0);
//RootPanel.get().add(columna, 50, 0);
//
//portalButton = new Button("simple");
//viaButton = new Button("simple");
//portalButtonZona = new Button("múltiple");
//viaButtonZona = new Button("múltiple");
//zonaButton = new Button("continuar");
//
//HTML titulo1 = new HTML("<h1>Información padronal<br>San Cristóbal de La Laguna</h1>");
//
//HTML bienvenida = new HTML("<br><b>Bienvenido/a la aplicacion de ejemplo del proyecto RISP!</b>");
//
//HTML texto1 = new HTML("<br>Esta aplicación consiste en " +
//		"la reutilización de la información padronal de las bases de datos de la " +
//		"Gerencia de Urbanismo de San Cristóbal de La Laguna, y que ha sido publicada gracias al trabajo " +
//		"realizado por los mismos alumnos que han desarrollado este portal. Aquí, usted puede obtener diversa información relacionada con el registro " +
//		"del padrón que se realiza en el municipio. A continuación se presenta la lista de cosas que se pueden " +
//		"hacer:");
//
//
//bienvenida.setStyleName(texto);
//texto1.setStyleName(texto);
//
//
//AbsolutePanel titulo = new AbsolutePanel();
//titulo.setSize("293px", "20%");
////titulo.setStyleName("uno");
//
//AbsolutePanel intro = new AbsolutePanel();
//intro.setSize("293px", "30%");
////intro.setStyleName("dos");
//
//AbsolutePanel funciones = new AbsolutePanel();
//funciones.setSize("293px", "30%");
////funciones.setStyleName("tres");
//
//AbsolutePanel logos = new AbsolutePanel();
//logos.setSize("293px", "20%");
////logos.setStyleName("cuatro");
//
//titulo.add(titulo1);
////titulo.add(titulo2);
//
//
////vpanel.add(titulo);
//
//intro.add(bienvenida);
//intro.add(texto1);
////vpanel.add(intro);
//
//
//
//
//
//
//AbsolutePanel portalPanel = new AbsolutePanel();
//HTML portalText = new HTML("Información de empadronamiento asociada a un edificio del municipio. " +
//		"También se puede obtener la información de todos los edificios que se encuentren dentro de una zona que usted elija. " +
//		"<br><br>Seleccione el tipo de búsqueda que desea e interactúe con el mapa:");
//portalText.setStyleName(texto);
//portalPanel.add(portalText);
//HorizontalPanel portalBotones = new HorizontalPanel();
//portalBotones.setSpacing(10);
//portalBotones.add(portalButton);
//portalBotones.add(portalButtonZona);
//portalPanel.add(portalBotones);
//
//
//portalDisclosure.addOpenHandler(new OpenHandler<DisclosurePanel>() {
//	public void onOpen(OpenEvent<DisclosurePanel> event) {
//		if (viaDisclosure.isOpen()){
//			viaDisclosure.setOpen(false);
//		}
//		if (zonaDisclosure.isOpen()){
//			zonaDisclosure.setOpen(false);
//		}
//	}
//});
//portalDisclosure.add(portalPanel);
//portalDisclosure.setStyleName(texto);
//funciones.add(portalDisclosure);
//
//
//AbsolutePanel viaPanel = new AbsolutePanel();
//HTML viaText = new HTML("Información de empadronamiento asociada a una vía del municipio." +
//		" También puede obtener la información de todas las vías que se encuentren dentro de una zona que usted elija. " +
//		"<br><br>Seleccione el tipo de búsqueda que desea e interactúe con el mapa:");
//viaText.setStyleName(texto);
//viaPanel.add(viaText);
//HorizontalPanel viaBotones = new HorizontalPanel();
//viaBotones.setSpacing(10);
//viaBotones.add(viaButton);
//viaBotones.add(viaButtonZona);
//viaPanel.add(viaBotones);
//
//viaDisclosure.addOpenHandler(new OpenHandler<DisclosurePanel>() {
//	public void onOpen(OpenEvent<DisclosurePanel> event) {
//		if (portalDisclosure.isOpen()){
//			portalDisclosure.setOpen(false);
//		}
//		if (zonaDisclosure.isOpen()){
//			zonaDisclosure.setOpen(false);
//		}
//	}
//});
//viaDisclosure.add(viaPanel);
//viaDisclosure.setStyleName(texto);
//funciones.add(viaDisclosure);
//
//AbsolutePanel zonaPanel = new AbsolutePanel();
//HTML zonaText = new HTML("Se muestra la información de empadronamiento asociada a una zona del municipio que usted elija. " +
//		"<br><br>Pulse el botón de continuar e interactúe con el mapa para personalizar la zona de interés mediante una polilínea:");
//zonaText.setStyleName(texto);
//zonaPanel.add(zonaText);
//HorizontalPanel zonaBotones = new HorizontalPanel();
//zonaBotones.setSpacing(10);
//zonaBotones.add(zonaButton);
//zonaPanel.add(zonaBotones);
//
//
//zonaDisclosure.addOpenHandler(new OpenHandler<DisclosurePanel>() {
//	public void onOpen(OpenEvent<DisclosurePanel> event) {
//		if (portalDisclosure.isOpen()){
//			portalDisclosure.setOpen(false);
//		}
//		if (viaDisclosure.isOpen()){
//			viaDisclosure.setOpen(false);
//		}
//	}
//});
//zonaDisclosure.add(zonaPanel);
//zonaDisclosure.setStyleName(texto);
//funciones.add(zonaDisclosure);
//
//HorizontalPanel gerencia = new HorizontalPanel();
//
//gerencia.setSpacing(5);
//
//HTML proyecto = new HTML("<a href=\"http://code.google.com/p/risp/\" target=\"_blank\">Página web del proyecto RISP </a>");
//proyecto.setStyleName(texto);
//
//logos.add(proyecto);
//logos.add(new Image("http://www.ull.es/Public/images/wull/logo.gif"));
//gerencia.add(new Image("http://www.gerenciaurbanismo.com/gerencia/GERENCIA/published/DEFAULT/img/layout_common/gerencia.gif"));
//gerencia.add(new Image("http://www.gerenciaurbanismo.com/gerencia/GERENCIA/published/DEFAULT/img/layout_common/la_laguna.gif"));
//logos.add(gerencia);
//
////columna.add(titulo, 40, 0);
////columna.add(intro, 40, titulo.getOffsetHeight());
////columna.add(funciones, 40, titulo.getOffsetHeight() + intro.getOffsetHeight());
////columna.add(logos, 40, titulo.getOffsetHeight() + intro.getOffsetHeight() + funciones.getOffsetHeight());
//
//columna.add(titulo);
//columna.add(intro);
//columna.add(funciones);
//columna.add(logos);
//
//Icon icon = Icon.newInstance();
//icon.setIconAnchor(Point.newInstance(16, 16));
//icon.setInfoWindowAnchor(Point.newInstance(32, 0));
//icon.setImageURL("http://www.visual-case.it/vc/pics/casetta_base.png");
//portalIcon1 = MarkerOptions.newInstance();
//portalIcon1.setIcon(icon);
//
//icon = Icon.newInstance();
//icon.setIconAnchor(Point.newInstance(16, 16));
//icon.setInfoWindowAnchor(Point.newInstance(32, 0));
//icon.setImageURL("http://www.visual-case.it/vc/pics/casetta_green.png");
//portalIcon2 = MarkerOptions.newInstance();
//portalIcon2.setIcon(icon);
//
//icon = Icon.newInstance();
//icon.setIconAnchor(Point.newInstance(16, 16));
//icon.setInfoWindowAnchor(Point.newInstance(32, 0));
//icon.setImageURL("http://www.visual-case.it/vc/pics/casetta_red.png");
//portalIcon3 = MarkerOptions.newInstance();
//portalIcon3.setIcon(icon);
//
//icon = Icon.newInstance();
//icon.setIconAnchor(Point.newInstance(16, 16));
//icon.setInfoWindowAnchor(Point.newInstance(32, 0));
//icon.setImageURL("http://maps.google.com/mapfiles/kml/pal4/icon23.png");
//viaIcon1 = MarkerOptions.newInstance();
//viaIcon1.setIcon(icon);
//viaIcon1.setDraggable(true);
// 
//icon = Icon.newInstance();
//icon.setIconAnchor(Point.newInstance(16, 16));
//icon.setInfoWindowAnchor(Point.newInstance(32, 0));
//icon.setImageURL("http://maps.google.com/mapfiles/kml/pal4/icon54.png");
//viaIcon2 = MarkerOptions.newInstance();
//viaIcon2.setIcon(icon);
//viaIcon2.setDraggable(true);
//
//icon = Icon.newInstance();
//icon.setIconAnchor(Point.newInstance(16, 16));
//icon.setInfoWindowAnchor(Point.newInstance(32, 0));
//icon.setImageURL("http://maps.google.com/mapfiles/kml/pal4/icon7.png");
//viaIcon3 = MarkerOptions.newInstance();
//viaIcon3.setIcon(icon);
//viaIcon3.setDraggable(true);
//
//}
