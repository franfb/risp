package org.labis.risp.client;

import java.util.ArrayList;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dev.js.SizeBreakdown;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;

import com.google.gwt.maps.client.InfoWindow;
import com.google.gwt.maps.client.InfoWindowContent;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.event.MapClickHandler;
import com.google.gwt.maps.client.event.MarkerClickHandler;
import com.google.gwt.maps.client.event.MarkerInfoWindowCloseHandler;
import com.google.gwt.maps.client.event.MarkerMouseOutHandler;
import com.google.gwt.maps.client.event.MarkerMouseOverHandler;
import com.google.gwt.maps.client.event.PolygonMouseOutHandler;
import com.google.gwt.maps.client.event.PolygonMouseOverHandler;
import com.google.gwt.maps.client.event.PolylineEndLineHandler;
import com.google.gwt.maps.client.event.MarkerClickHandler.MarkerClickEvent;
import com.google.gwt.maps.client.event.MarkerInfoWindowCloseHandler.MarkerInfoWindowCloseEvent;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.geom.Point;
import com.google.gwt.maps.client.geom.Size;
import com.google.gwt.maps.client.overlay.Icon;
import com.google.gwt.maps.client.overlay.Marker;
import com.google.gwt.maps.client.overlay.MarkerOptions;
import com.google.gwt.maps.client.overlay.PolyStyleOptions;
import com.google.gwt.maps.client.overlay.Polygon;
import com.google.gwt.maps.client.overlay.Polyline;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DisclosureHandler;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.HorizontalSplitPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;

public class RdfTest implements EntryPoint{
	private MapWidget map;
	private Button portalButton;
	private Button portalButtonZona;
	private Button viaButtonZona;
	private Button viaButton;
	private Button zonaButton;
	
	private ArrayList<Zona> areas;
	private InfoWindow info;

	private final GreetingServiceAsync greetingService = GWT
			.create(GreetingService.class);
	
	MarkerOptions portalIcon;
	MarkerOptions viaIcon;
	MarkerOptions viaIconGrande;

	
	private void disableButtons(){
		portalButtonZona.setEnabled(false);
		viaButtonZona.setEnabled(false);
		viaButton.setEnabled(false);
		portalButton.setEnabled(false);
		zonaButton.setEnabled(false);
	}
	
	private void enableButtons(){
		portalButtonZona.setEnabled(true);
		viaButtonZona.setEnabled(true);
		viaButton.setEnabled(true);
		portalButton.setEnabled(true);
		zonaButton.setEnabled(true);
	}
	
	public void onModuleLoad() {
		buildUi();
		
		areas = new ArrayList<Zona>();
		
		
		
		portalButtonZona.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event) {
				disableButtons();
				createPolyline();
			}
		});

		viaButtonZona.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event) {
				disableButtons();
				createPolyline2();
			}
		});
		
		zonaButton.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event) {
				disableButtons();
				createPolyline2();
			}
		});
		
		viaButton.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event) {
				disableButtons();
				map.addMapClickHandler(new MapClickHandler(){
					public void onClick(MapClickEvent event) {
						map.removeMapClickHandler(this);
						nuevaVia(event.getLatLng());
						enableButtons();
					}
				});
			}
		});

		portalButton.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event) {
				disableButtons();
				map.addMapClickHandler(new MapClickHandler(){
					public void onClick(MapClickEvent event) {
						map.removeMapClickHandler(this);
						nuevoPortal(event.getLatLng());
						enableButtons();
					}
				});
			}
		});
	}

	
	
	private void buildUi() {
        map = new MapWidget();
        LatLng tenerife = LatLng.newInstance(28.4860,-16.3161);
        //map.setSize("500px", "400px");
        map.setSize("100%", "100%");
        map.setCenter(tenerife, 13);
        map.setUIToDefault();
        
        AbsolutePanel panel = new AbsolutePanel();
        //HTML texto = new HTML("<div id=\"col\" style=\"width: 300px; height: 100%; background-color: rgb(255, 255, 255);\"></div>");
        VerticalPanel vpanel = new VerticalPanel();
        
        AbsolutePanel columna = new AbsolutePanel();
        

//        FlexTable flex = new FlexTable();
//        flex.setSize("293px", "100%");
        
        
        
        vpanel.setSize("293px", "100%");
        vpanel.setStyleName("vpanel");
        columna.setSize("370px", "100%");
        columna.setStyleName("columna");
        
        
        panel.setSize("100%", "100%");
        panel.add(map, 0, 0);
        panel.add(columna, 50, 0);
        
        
   
        
        
        RootPanel.get().add(panel);
        
        
        portalButton = new Button("simple");
        viaButton = new Button("simple");
        portalButtonZona = new Button("múltiple");
        viaButtonZona = new Button("múltiple");
        zonaButton = new Button("continuar");
        
        HTML titulo1 = new HTML("<h1>Información padronal<br>San Cristóbal de La Laguna</h1>");
        
        HTML bienvenida = new HTML("<br><b>Bienvenido/a la aplicacion de ejemplo del proyecto RISP!</b>");
        
        HTML texto1 = new HTML("<br>Esta aplicación consiste en " +
        		"la reutilización de la información padronal de las bases de datos de la " +
        		"Gerencia de Urbanismo de San Cristóbal de La Laguna, y que ha sido publicada gracias al trabajo " +
        		"realizado por los mismos alumnos que han desarrollado este portal. Aquí, usted puede obtener diversa información relacionada con el registro " +
        		"del padrón que se realiza en el municipio. A continuación se presenta la lista de cosas que se pueden " +
        		"hacer:");
        
        
        bienvenida.setStyleName("texto");
        texto1.setStyleName("texto");
        
        
        AbsolutePanel titulo = new AbsolutePanel();
        titulo.setSize("293px", "20%");
        //titulo.setStyleName("uno");
        
        AbsolutePanel intro = new AbsolutePanel();
        intro.setSize("293px", "30%");
        //intro.setStyleName("dos");
        
        AbsolutePanel funciones = new AbsolutePanel();
        funciones.setSize("293px", "30%");
        //funciones.setStyleName("tres");
        
        AbsolutePanel logos = new AbsolutePanel();
        logos.setSize("293px", "20%");
        //logos.setStyleName("cuatro");
        
        titulo.add(titulo1);
        //titulo.add(titulo2);
        
        
        //vpanel.add(titulo);

        intro.add(bienvenida);
        intro.add(texto1);
        //vpanel.add(intro);
        
        
        
        
        
        
        AbsolutePanel portalPanel = new AbsolutePanel();
        HTML portalText = new HTML("Se muestra el registro padronal asociado a un edificio que contenga " +
        		"alguno. También puede obtener la información de todos los edificios que se encuentren dentro de una misma zona. " +
        		"<br><br>Seleccione el tipo de búsqueda que desea e interactúe con el mapa:");
        portalText.setStyleName("texto");
        portalPanel.add(portalText);
        HorizontalPanel portalBotones = new HorizontalPanel();
        portalBotones.setSpacing(10);
        portalBotones.add(portalButton);
        portalBotones.add(portalButtonZona);
        portalPanel.add(portalBotones);
        
        final DisclosurePanel portalDisclosure = new DisclosurePanel(
		"Mostrar información padronal de un portal");
        portalDisclosure.addOpenHandler(new OpenHandler<DisclosurePanel>() {
        	public void onOpen(OpenEvent<DisclosurePanel> event) {
        		//dis.setHeader(new HTML("PULSA PARA ABRIR MI NIÑO"));
        		//dis.setTitle("Abrir aqui para mostrar la info mas preciosa");
        	}
        });
        portalDisclosure.addCloseHandler(new CloseHandler<DisclosurePanel>() {
        	public void onClose(CloseEvent<DisclosurePanel> event) {
        		//dis.setHeader(new HTML("PULSA PARA CERRAR MI NIÑO"));
        		//dis.setTitle("cierra la info mas preciosa");
        	}
        });
        portalDisclosure.add(portalPanel);
        portalDisclosure.setStyleName("texto");
		funciones.add(portalDisclosure);
		

		
		
		
		
		AbsolutePanel viaPanel = new AbsolutePanel();
        HTML viaText = new HTML("Se muestran los registros padronales asociados a una vía del municipio." +
        		" También puede obtener la información de todas las vías que se encuentren dentro de una misma zona. " +
        		"<br><br>Seleccione el tipo de búsqueda que desea e interactúe con el mapa:");
        viaText.setStyleName("texto");
        viaPanel.add(viaText);
        HorizontalPanel viaBotones = new HorizontalPanel();
        viaBotones.setSpacing(10);
        viaBotones.add(viaButton);
        viaBotones.add(viaButtonZona);
        viaPanel.add(viaBotones);
        
        final DisclosurePanel viaDisclosure = new DisclosurePanel(
        "Mostrar información padronal de una vía");
        viaDisclosure.addOpenHandler(new OpenHandler<DisclosurePanel>() {
        	public void onOpen(OpenEvent<DisclosurePanel> event) {
        		//dis.setHeader(new HTML("PULSA PARA ABRIR MI NIÑO"));
        		//dis.setTitle("Abrir aqui para mostrar la info mas preciosa");
        	}
        });
        viaDisclosure.addCloseHandler(new CloseHandler<DisclosurePanel>() {
        	public void onClose(CloseEvent<DisclosurePanel> event) {
        		//dis.setHeader(new HTML("PULSA PARA CERRAR MI NIÑO"));
        		//dis.setTitle("cierra la info mas preciosa");
        	}
        });
        viaDisclosure.add(viaPanel);
        viaDisclosure.setStyleName("texto");
		funciones.add(viaDisclosure);
		
		
		
		
		
		
		AbsolutePanel zonaPanel = new AbsolutePanel();
        HTML zonaText = new HTML("Se muestra la información de empadronamiento asociada a una zona del municipio que usted elija. " +
        		"<br><br>Pulse el botón de continuar e interactúe con el mapa para personalizar la zona de interés mediante una polilínea:");
        zonaText.setStyleName("texto");
        zonaPanel.add(zonaText);
        HorizontalPanel zonaBotones = new HorizontalPanel();
        zonaBotones.setSpacing(10);
        zonaBotones.add(zonaButton);
        zonaPanel.add(zonaBotones);
        
        final DisclosurePanel zonaDisclosure = new DisclosurePanel(
		"Mostrar información padronal de una zona");
        portalDisclosure.addOpenHandler(new OpenHandler<DisclosurePanel>() {
        	public void onOpen(OpenEvent<DisclosurePanel> event) {
        		//dis.setHeader(new HTML("PULSA PARA ABRIR MI NIÑO"));
        		//dis.setTitle("Abrir aqui para mostrar la info mas preciosa");
        	}
        });
        portalDisclosure.addCloseHandler(new CloseHandler<DisclosurePanel>() {
        	public void onClose(CloseEvent<DisclosurePanel> event) {
        		//dis.setHeader(new HTML("PULSA PARA CERRAR MI NIÑO"));
        		//dis.setTitle("cierra la info mas preciosa");
        	}
        });
        zonaDisclosure.add(zonaPanel);
        zonaDisclosure.setStyleName("texto");
		funciones.add(zonaDisclosure);
		
		
		
		
		//vpanel.add(funciones);
        
		HorizontalPanel gerencia = new HorizontalPanel();
		
		gerencia.setSpacing(5);
		
        //logos.setStyleName("texto11");

//        HTML proyecto = new HTML("Alumnos encargados:<br><ul><li>Cristina Delgado </li><li>" +
//        		"Daniel Pérez </li><li>Erika Martín </li><li>Francisco Fumero </li><li>Marta Álvarez </li></ul>" +
//        		"<br><a href=\"http://code.google.com/p/risp/\">Página web del proyecto RISP</a>");
//		proyecto.setStyleName("texto11");
        
		
		HTML proyecto = new HTML("<a href=\"http://code.google.com/p/risp/\" target=\"_blank\">Página web del proyecto RISP </a>");
		proyecto.setStyleName("texto");
		
		
//		logos.add(new HTML("<a href=\"http://code.google.com/p/risp/\">Página web del proyecto RISP</a>"));
//		logos.add(new HTML("Alumnos encargados:<br><ul><li>Cristina Delgado </li><li>Daniel Pérez </li><li>Erika Martín </li><li>Francisco Fumero </li><li>Marta Álvarez </li></ul>"));
        logos.add(proyecto);
		logos.add(new Image("http://www.ull.es/Public/images/wull/logo.gif"));
        gerencia.add(new Image("http://www.gerenciaurbanismo.com/gerencia/GERENCIA/published/DEFAULT/img/layout_common/gerencia.gif"));
        gerencia.add(new Image("http://www.gerenciaurbanismo.com/gerencia/GERENCIA/published/DEFAULT/img/layout_common/la_laguna.gif"));
        logos.add(gerencia);

        
        //vpanel.add(logos);
        
        //columna.add(vpanel, 40, 0);
        
        columna.add(titulo, 40, 0);
        columna.add(intro, 40, titulo.getOffsetHeight());
        columna.add(funciones, 40, titulo.getOffsetHeight() + intro.getOffsetHeight());
        columna.add(logos, 40, titulo.getOffsetHeight() + intro.getOffsetHeight() + funciones.getOffsetHeight());
        
        
        
        
        Icon icon = Icon.newInstance();
		icon.setIconAnchor(Point.newInstance(16, 16));
		icon.setInfoWindowAnchor(Point.newInstance(32, 0));
		icon.setImageURL("http://maps.google.com/mapfiles/kml/pal3/icon56.png");
		portalIcon = MarkerOptions.newInstance();
		portalIcon.setIcon(icon);
		
		icon = Icon.newInstance();
		icon.setIconAnchor(Point.newInstance(16, 16));
		icon.setInfoWindowAnchor(Point.newInstance(32, 0));
		icon.setImageURL("http://maps.google.com/mapfiles/kml/pal2/icon16.png");
		viaIcon = MarkerOptions.newInstance();
		viaIcon.setIcon(icon);
		
		icon = Icon.newInstance();
		icon.setIconAnchor(Point.newInstance(16, 16));
		icon.setInfoWindowAnchor(Point.newInstance(32, 0));
		icon.setImageURL("http://maps.google.com/mapfiles/kml/pal2/icon16.png");
		icon.setIconSize(Size.newInstance(40, 40));
		viaIconGrande = MarkerOptions.newInstance();
		viaIconGrande.setIcon(icon);
		
		
		
		    
	}

//		final DisclosurePanelImages images = (DisclosurePanelImages)
//				GWT.create(DisclosurePanelImages.class);
//		class DisclosurePanelHeader extends HorizontalPanel
//		{
//		    public DisclosurePanelHeader(boolean isOpen, String html)
//		    {
//		        add(isOpen ? images.disclosurePanelOpen().createImage()
//		              : images.disclosurePanelClosed().createImage());
//		        add(new HTML(html));
//		    }
//		}

		
    
	
	
	

	
	private void infoWindowPortal(Marker marker, Portal portal){
		InfoWindow info = map.getInfoWindow();
		String nombreCalle = "Desconocido";
		if (portal.getVia() != null){
			nombreCalle = "<b>" +
			portal.getVia() + 
			", " + 
			portal.getNumero() + 
			"</b>";
		}
		info.open(marker, new InfoWindowContent(nombreCalle
				+ "<br>Habitantes: "
				+ portal.getHabitantes() + "<br>Hojas padronales: "
				+ portal.getHojas()));
	}
	
	
	private void infoWindowVia(Marker marker, Via via){
		InfoWindow info = map.getInfoWindow();
		String nombreCalle = "<b>" +
			via.getTipo() + 
			" " + 
			via.getNombre() + 
			"</b>";
		info.open(marker, new InfoWindowContent(nombreCalle
				+ "<br>Habitantes: "
				+ via.getHabitantes()
				+ "<br>Longitud de la vía: "
				+ via.getLongitud()
				+ "<br>Código de vía: "
				+ via.getCodigo()));
	}
	
	
	private Marker createMarkerPortal(final Portal portal) {
		MarkerOptions markerOpt = MarkerOptions.newInstance();
		markerOpt.setClickable(true);
		final Marker marker = new Marker(LatLng.newInstance(portal.getCoordenadas()
				.getLatitude(), portal.getCoordenadas().getLongitude()), portalIcon);
		final MarkerMouseOutHandler out = new MarkerMouseOutHandler(){
			public void onMouseOut(MarkerMouseOutEvent event) {
				InfoWindow info = map.getInfoWindow();
				info.close();
			}
		};
		
		marker.addMarkerMouseOverHandler(new MarkerMouseOverHandler() {
			public void onMouseOver(MarkerMouseOverEvent event) {
				infoWindowPortal(marker, portal);
			}
		});
		marker.addMarkerMouseOutHandler(out);
		marker.addMarkerClickHandler(new MarkerClickHandler() {
			public void onClick(MarkerClickEvent event) {
				marker.removeMarkerMouseOutHandler(out);
				//infoWindowPortal(marker, portal);
			}
		});
		marker.addMarkerInfoWindowCloseHandler(new MarkerInfoWindowCloseHandler() {
			public void onInfoWindowClose(MarkerInfoWindowCloseEvent event) {
				marker.addMarkerMouseOutHandler(out);
			}
		});
		return marker;
	}
	
	private Marker createMarkerVia(final Via via, MarkerOptions icon) {
		final Marker marker = new Marker(LatLng.newInstance(via.getCoordenadas()
				.getLatitude(), via.getCoordenadas().getLongitude()), icon);
		final MarkerMouseOutHandler out = new MarkerMouseOutHandler(){
			public void onMouseOut(MarkerMouseOutEvent event) {
				InfoWindow info = map.getInfoWindow();
				info.close();
			}
		};
		
		marker.addMarkerMouseOverHandler(new MarkerMouseOverHandler(){
			public void onMouseOver(MarkerMouseOverEvent event) {
				infoWindowVia(marker, via);
			}
			
		});
		marker.addMarkerMouseOutHandler(out);
		marker.addMarkerClickHandler(new MarkerClickHandler() {
			public void onClick(MarkerClickEvent event) {
				marker.removeMarkerMouseOutHandler(out);
				//infoWindowVia(marker, via);
			}
		});
		marker.addMarkerInfoWindowCloseHandler(new MarkerInfoWindowCloseHandler() {
			public void onInfoWindowClose(MarkerInfoWindowCloseEvent event) {
				marker.addMarkerMouseOutHandler(out);
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
				enableButtons();
			}
		});
	}

	private void createPolyline2() {
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
				nuevaZonaVias(event.getSender());
				enableButtons();
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
			greetingService.getPortales(new MyPolygon(p),
					new MyLatLng(p.getBounds().getNorthEast()), new MyLatLng(p
							.getBounds().getSouthWest()),
					new AsyncCallback<ArrayList<Portal>>() {
						public void onFailure(Throwable caught) {
							System.out.println("Error.");
						}

						public void onSuccess(ArrayList<Portal> result) {
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
							areas.add(new Zona(p, result));
							for (int i = 0; i < result.size(); i++) {
								map.addOverlay(createMarkerPortal(result.get(i)));
							}

						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private void nuevaZonaVias(Polyline pline) {
		LatLng[] coord = new LatLng[pline.getVertexCount()];
		for (int i = 0; i < pline.getVertexCount(); i++) {
			coord[i] = pline.getVertex(i);
		}
		final Polygon p = new Polygon(coord, "#f33f00", 5, 1, "#ff0000", 0.2);

		try {
			greetingService.getVias(new MyPolygon(p),
					new MyLatLng(p.getBounds().getNorthEast()), new MyLatLng(p
							.getBounds().getSouthWest()),
					new AsyncCallback<ArrayList<Via>>() {
						public void onFailure(Throwable caught) {
							System.out.println("Error.");
						}

						public void onSuccess(ArrayList<Via> result) {
							System.out.println("444b");
							map.addOverlay(p);
//							p.addPolygonMouseOverHandler(new PolygonMouseOverHandler(){
//								public void onMouseOver(PolygonMouseOverEvent event) {
//									for (Zona area : areas) {
//										if (area.getPoly() == event.getSource()) {
//											info = map.getInfoWindow();
//											double sizeArea = area.getPoly().getArea();
//											String unit = "m";
////											if (sizeArea > 1000000){
////												sizeArea /= 1000000;
////												unit = "km";
////											}
//											info.open(area.getPoly().getBounds().getCenter(),
//													new InfoWindowContent("Area: "
//															+ (int)sizeArea + " " + unit + "<sup>2</sup>"
//															+ "<br>Population: " + area.getHabitantes()
//															+ "<br>Registers: " + area.getHojas()));
//										}
//									}
//								}
//
//							});
//							p.addPolygonMouseOutHandler(new PolygonMouseOutHandler(){
//								public void onMouseOut(PolygonMouseOutEvent event) {
//									if (info != null) {
//										info.close();
//									}
//								}
//							});
//							areas.add(new Zona(p, result));
							for (int i = 0; i < result.size(); i++) {
								map.addOverlay(createMarkerVia(result.get(i), viaIcon));
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
		//map.setSize("800px", "600px");

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
											map.addOverlay(createMarkerPortal(portal));
										}
										map.addOverlay(createMarkerVia(via, viaIconGrande));
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
