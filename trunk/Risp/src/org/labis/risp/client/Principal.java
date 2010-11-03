package org.labis.risp.client;

import java.util.ArrayList;
import java.util.LinkedList;


import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;

import com.google.gwt.maps.client.DraggableObject;
import com.google.gwt.maps.client.InfoWindow;
import com.google.gwt.maps.client.InfoWindowContent;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.event.MapClickHandler;
import com.google.gwt.maps.client.event.MarkerClickHandler;
import com.google.gwt.maps.client.event.MarkerInfoWindowCloseHandler;
import com.google.gwt.maps.client.event.MarkerMouseOutHandler;
import com.google.gwt.maps.client.event.MarkerMouseOverHandler;
import com.google.gwt.maps.client.event.PolygonClickHandler;
import com.google.gwt.maps.client.event.PolygonEndLineHandler;
import com.google.gwt.maps.client.event.PolygonRemoveHandler;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.geom.Point;
import com.google.gwt.maps.client.overlay.Icon;
import com.google.gwt.maps.client.overlay.Marker;
import com.google.gwt.maps.client.overlay.MarkerOptions;
import com.google.gwt.maps.client.overlay.PolyStyleOptions;
import com.google.gwt.maps.client.overlay.Polygon;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class Principal implements EntryPoint{
	private MapWidget map;

	HTML portalLink;
	HTML viaLink;
	HTML zonaLink;
	HTML limpiarLink;
	HTML leyendaLink;
	
	DialogBox dialogoPortal;
	DialogBox dialogoVia;
	DialogBox dialogoZona;
	DialogBox dialogoLeyenda;
	
	DialogBox noResultados;
	DialogBox noVia;
	DialogBox noPortal;
	DialogBox error;
	DialogBox errorPoligono;
	
	TextBox textBoxVia;
	TextBox textBoxPortalVia;
	TextBox textBoxPortalNumero;
	
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
		crearDialogoLeyenda();
		noResultados = crearDialogoGenerico("AVISO", "La búsqueda no ha producido ningún resultado.");
		noVia = crearDialogoGenerico("AVISO", "No hay ninguna vía cerca.");
		noPortal = crearDialogoGenerico("AVISO", "No hay ningún portal cerca.");
		error = crearDialogoGenerico("ERROR", "Se ha producido un error. Vuelva a intentarlo.");
		errorPoligono = crearDialogoGenerico("ERROR", "El polígono que ha introducido tiene segmentos que se cruzan. No puede utilizar este tipo de polígonos.");
		
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
				textBoxPortalNumero.setText("");
				textBoxPortalVia.setText("");
				dialogoPortal.show();
				dialogoPortal.center();
			}
		});
		
		viaLink.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				textBoxVia.setText("");
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
		
		limpiarLink.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				map.clearOverlays();
			}
		});
		
		leyendaLink.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				dialogoLeyenda.show();
				dialogoLeyenda.center();
			}
		});
	}

	
	private Widget panelBusquedaPortal(){
		VerticalPanel vertical = new VerticalPanel();
		HorizontalPanel horizontal1 = new HorizontalPanel();
		HorizontalPanel horizontal2 = new HorizontalPanel();
		
		horizontal1.setSpacing(10);
		horizontal2.setSpacing(10);
		
		textBoxPortalVia = new TextBox();
		textBoxPortalNumero = new TextBox();
		textBoxPortalVia.setStyleName("texto13");
		textBoxPortalNumero.setStyleName("texto13");
		textBoxPortalVia.setWidth("20em");
		textBoxPortalNumero.setWidth("5em");
		
		HTML textVia = new HTML("Vía:  ");
		HTML textNumero = new HTML("Número de portal:  ");

		textVia.setStyleName("texto13");
		textNumero.setStyleName("texto13");

		Button button = new Button("<b>Buscar</b>");
		button.setStyleName("texto13");
		
		horizontal1.add(textVia);
		horizontal1.add(textBoxPortalVia);
		
		horizontal2.add(textNumero);
		horizontal2.add(textBoxPortalNumero);
		horizontal2.add(button);

		vertical.add(horizontal1);
		vertical.add(horizontal2);
		
		button.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				String texto = textBoxPortalVia.getText();
				String[] resultado = texto.split(" ");
				for (int i = 0; i < resultado.length; i++){
					resultado[i] = resultado[i].trim();
				}
				if (resultado.length < 2 || resultado[0].isEmpty()){
					return;
				}
				int n = 0;
				try{
					n = Integer.parseInt(textBoxPortalNumero.getText().trim());
				}
				catch (NumberFormatException e){
					return;
				}
				dialogoPortal.hide();
				try {
					map.getDragObject().setDraggableCursor("progress");
					greetingService.getPortales(resultado, n,
							new AsyncCallback<ArrayList<Portal>>() {
								public void onFailure(Throwable caught) {
									error.show();
									error.center();
									map.getDragObject().setDraggableCursor(DraggableObject.getDraggableCursorDefault());
								}
								
								public void onSuccess(final ArrayList<Portal> portales) {
									if (portales == null){
										noResultados.show();
										noResultados.center();
									}
									else{
										Marker m = null;
										Portal por = null;
										for (Portal p: portales){
											m = createMarkerPortal(p, true);
											por = p;
											map.addOverlay(m);
										}
										infoWindowPortal(m, por, true);
										map.setCenter(m.getLatLng());
										for (Portal p: portales){
											LatLng c = LatLng.newInstance(
												p.getCoordenadas().getLatitude(),
												p.getCoordenadas().getLongitude()
											);
											while (!map.getBounds().containsLatLng(c)){
												map.setZoomLevel(map.getZoomLevel() - 1);
											}
										}
									}
									map.getDragObject().setDraggableCursor(DraggableObject.getDraggableCursorDefault());
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
	
	private Widget panelBusquedaVia(){
		VerticalPanel vertical = new VerticalPanel();
		HorizontalPanel horizontal1 = new HorizontalPanel();
		HorizontalPanel horizontal2 = new HorizontalPanel();
		
		horizontal1.setSpacing(10);
		horizontal2.setSpacing(10);
		
		textBoxVia = new TextBox();
		textBoxVia.setStyleName("texto13");
		textBoxVia.setWidth("20em");
		
		HTML textVia = new HTML("Vía:  ");

		textVia.setStyleName("texto13");

		Button button = new Button("<b>Buscar</b>");
		button.setStyleName("texto13");
		
		horizontal1.add(textVia);
		horizontal1.add(textBoxVia);
		
		horizontal2.add(button);

		vertical.add(horizontal1);
		vertical.add(horizontal2);
		
		button.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				String texto = textBoxVia.getText();
				String[] resultado = texto.split(" ");
				for (int i = 0; i < resultado.length; i++){
					resultado[i] = resultado[i].trim();
				}
				if (resultado.length < 2 || resultado[0].isEmpty()){
					return;
				}
				dialogoVia.hide();
				try {
					map.getDragObject().setDraggableCursor("progress");
					greetingService.getVias(resultado,
							new AsyncCallback<ArrayList<Via>>() {
								public void onFailure(Throwable caught) {
									error.show();
									error.center();
									map.getDragObject().setDraggableCursor(DraggableObject.getDraggableCursorDefault());
								}

								public void onSuccess(final ArrayList<Via> vias) {
									if (vias == null){
										noResultados.show();
										noResultados.center();
									}
									else{
										Marker m = null;
										Via via = null;
										for (Via v: vias){
											m = createMarkerVia(v, true);
											via = v;
											map.addOverlay(m);
										}
										infoWindowVia(m, via, true);
										map.setCenter(m.getLatLng());
										for (Via v: vias){
											LatLng c = LatLng.newInstance(
												v.getCoordenadas().getLatitude(),
												v.getCoordenadas().getLongitude()
											);
											while (!map.getBounds().containsLatLng(c)){
												map.setZoomLevel(map.getZoomLevel() - 1);
											}
										}
									}
									map.getDragObject().setDraggableCursor(DraggableObject.getDraggableCursorDefault());
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
        limpiarLink = new HTML("<a href=\"javascript:undefined;\">"
                + "Limpiar mapa" + "</a>");
        leyendaLink = new HTML("<a href=\"javascript:undefined;\">"
                + "Mostrar leyenda" + "</a>");
		HorizontalPanel gerencia = new HorizontalPanel();
		gerencia.setSpacing(5);
		HTML proyecto = new HTML("<a href=\"http://code.google.com/p/risp/\" target=\"_blank\">Página web del proyecto RISP </a>");
		HTML creadores = new HTML("<a href=\"mailto:danieltf@gmail.com, franfumero@gmail.com?subject=ABOUT%20RISP%20PROJECT\">Contacta con los creadores</a>");
        gerencia.add(new Image("http://www.gerenciaurbanismo.com/gerencia/GERENCIA/published/DEFAULT/img/layout_common/gerencia.gif"));
        gerencia.add(new Image("http://www.gerenciaurbanismo.com/gerencia/GERENCIA/published/DEFAULT/img/layout_common/la_laguna.gif"));
        
        titulo1.setStylePrimaryName("texto13");
        bienvenida.setStyleName("texto15");
        texto1.setStyleName("texto13");
		creadores.setStyleName("texto13");
		proyecto.setStyleName("texto13");
        viaLink.setStyleName("texto13");
        portalLink.setStyleName("texto13");
        zonaLink.setStyleName("texto13");
        limpiarLink.setStyleName("texto13");
        leyendaLink.setStyleName("texto13");
        
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
        columna2.add(limpiarLink);
        columna2.add(new HTML("<br>"));
        columna2.add(leyendaLink);
        columna2.add(new HTML("<br>"));
        columna2.add(creadores);
        columna2.add(new HTML("<br>"));
        columna2.add(proyecto);
        columna2.add(ull);
        columna2.add(gerencia);

        columna2.setCellHorizontalAlignment(ull, HasHorizontalAlignment.ALIGN_CENTER);
        columna2.setCellHorizontalAlignment(gerencia, HasHorizontalAlignment.ALIGN_CENTER);
        columna2.setCellHorizontalAlignment(creadores, HasHorizontalAlignment.ALIGN_CENTER);
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
	
	
	
	
	private DialogBox crearDialogoGenerico(String title, String mensaje) {
		Button ok = new Button("ok");
		final DialogBox dialogo = new DialogBox();
		dialogo.setText(title);
		ok.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				dialogo.hide();
			}
		});
		dialogo.setGlassEnabled(true);
		dialogo.setAnimationEnabled(true);
	    HTML texto = new HTML(mensaje);
	    texto.setStyleName("texto15");
	    ok.setStyleName("texto13");
		VerticalPanel vertical = new VerticalPanel();
		vertical.setSpacing(10);
		vertical.add(texto);
		vertical.add(ok);
		vertical.setCellHorizontalAlignment(ok, HasHorizontalAlignment.ALIGN_CENTER);
		dialogo.setWidget(vertical);
		return dialogo;
	}
	
	private void crearDialogoPortal() {
		Button portal = new Button("<b>Buscar portal por localización</b>");
		Button zona = new Button("<b>Buscar portal por zona</b>");
		Button volver = new Button("Volver");
		portal.setStyleName("texto13");
		zona.setStyleName("texto13");
		volver.setStyleName("texto13");
		volver.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				dialogoPortal.hide();
			}
		});
		zona.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event) {
				dialogoPortal.hide();
				crearPoligono(1);
			}
		});
		portal.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event) {
				dialogoPortal.hide();
				map.getDragObject().setDraggableCursor("crosshair");
				map.addMapClickHandler(new MapClickHandler(){
					public void onClick(MapClickEvent event) {
						map.removeMapClickHandler(this);
						map.getDragObject().setDraggableCursor(DraggableObject.getDraggableCursorDefault());
						if (event.getLatLng() != null){
							nuevoPortal(event.getLatLng());
						}
						else{
							nuevoPortal(event.getOverlayLatLng());
						}
					}
				});
			}
		});
		dialogoPortal = new DialogBox();
		dialogoPortal.setTitle("Información padronal de un portal");
		dialogoPortal.setText("INFORMACIÓN PADRONAL DE UN PORTAL");
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
		Button via = new Button("<b>Buscar vía por localización</b>");
		Button zona = new Button("<b>Buscar vía por zona</b>");
		Button volver = new Button("Volver");
		via.setStyleName("texto13");
		zona.setStyleName("texto13");
		volver.setStyleName("texto13");
		volver.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				dialogoVia.hide();
			}
		});
		zona.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event) {
				dialogoVia.hide();
				crearPoligono(2);
			}
		});
		via.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event) {
				dialogoVia.hide();
				map.getDragObject().setDraggableCursor("crosshair");
				map.addMapClickHandler(new MapClickHandler(){
					public void onClick(MapClickEvent event) {
						map.removeMapClickHandler(this);
						map.getDragObject().setDraggableCursor(DraggableObject.getDraggableCursorDefault());
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
		dialogoVia.setTitle("Información padronal de una vía");
		dialogoVia.setText("INFORMACIÓN PADRONAL DE UNA VÍA");
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
		Button zona = new Button("<b>Crear zona</b>");
		Button volver = new Button("Volver");
		zona.setStyleName("texto13");
		volver.setStyleName("texto13");
		volver.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				dialogoZona.hide();
			}
		});
		zona.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event) {
				dialogoZona.hide();
				crearPoligono(0);
			}
		});
		dialogoZona = new DialogBox();
		dialogoZona.setTitle("Información padronal de una zona");
		dialogoZona.setText("INFORMACIÓN PADRONAL DE UNA ZONA");
		dialogoZona.setGlassEnabled(true);
		dialogoZona.setAnimationEnabled(true);
	    HTML text1 = new HTML("Se puede obtener información padronal de una zona específica del municipio. " +
	    	"Para ello, pulse el siguiente botón y construya en el mapa la zona de interés, mediante un polígono.");
	    text1.setStyleName("texto13");
	    VerticalPanel vertical = new VerticalPanel();
	    vertical.setSpacing(10);
	    dialogoZona.setWidget(vertical);
	    vertical.add(text1);
	    vertical.add(zona);
	    vertical.add(volver);
	    vertical.setCellHorizontalAlignment(volver, HasHorizontalAlignment.ALIGN_RIGHT);
	}
	
	private void crearDialogoLeyenda() {
		Button ok = new Button("ocultar");
		dialogoLeyenda = new DialogBox();
		dialogoLeyenda.setTitle("Leyenda");
		dialogoLeyenda.setText("LEYENDA");
		ok.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				dialogoLeyenda.hide();
			}
		});
		dialogoLeyenda.setGlassEnabled(true);
		dialogoLeyenda.setAnimationEnabled(true);
	    ok.setStyleName("texto13");
		VerticalPanel vertical = new VerticalPanel();
		vertical.setSpacing(10);
	
		HorizontalPanel horizontal1 = new HorizontalPanel();
		horizontal1.setSpacing(10);
		horizontal1.add(new Image("http://www.visual-case.it/vc/pics/casetta_base.png"));
		HTML text1 = new HTML("Portal con menos de 10 habitantes");
		text1.setStyleName("texto13");
		horizontal1.add(text1);
		
		HorizontalPanel horizontal2 = new HorizontalPanel();
		horizontal2.setSpacing(10);
		horizontal2.add(new Image("http://www.visual-case.it/vc/pics/casetta_green.png"));
		HTML text2 = new HTML("Portal con entre 10 y 50 habitantes");
		text2.setStyleName("texto13");
		horizontal2.add(text2);
		
		HorizontalPanel horizontal3 = new HorizontalPanel();
		horizontal3.setSpacing(10);
		horizontal3.add(new Image("http://www.visual-case.it/vc/pics/casetta_red.png"));
		HTML text3 = new HTML("Portal con más de 50 habitantes");
		text3.setStyleName("texto13");
		horizontal3.add(text3);
		
		HorizontalPanel horizontal4 = new HorizontalPanel();
		horizontal4.setSpacing(10);
		horizontal4.add(new Image("http://maps.google.com/mapfiles/kml/pal4/icon23.png"));
		HTML text4 = new HTML("Vía con menos de 100 habitantes");
		text4.setStyleName("texto13");
		horizontal4.add(text4);
		
		HorizontalPanel horizontal5 = new HorizontalPanel();
		horizontal5.setSpacing(10);
		horizontal5.add(new Image("http://maps.google.com/mapfiles/kml/pal4/icon54.png"));
		HTML text5 = new HTML("Vía con entre 100 y 500 habitantes");
		text5.setStyleName("texto13");
		horizontal5.add(text5);
		
		HorizontalPanel horizontal6 = new HorizontalPanel();
		horizontal6.setSpacing(10);
		horizontal6.add(new Image("http://maps.google.com/mapfiles/kml/pal4/icon7.png"));
		HTML text6 = new HTML("Vía con más de 500 habitantes");
		text6.setStyleName("texto13");
		horizontal6.add(text6);
		
		vertical.add(horizontal1);
		vertical.add(horizontal2);
		vertical.add(horizontal3);
		vertical.add(horizontal4);
		vertical.add(horizontal5);
		vertical.add(horizontal6);
		vertical.add(ok);
		vertical.setCellHorizontalAlignment(ok, HasHorizontalAlignment.ALIGN_CENTER);
		dialogoLeyenda.setWidget(vertical);
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
		text.setStyleName("texto13");
		vertical.add(text);
		if(verTodasVias && portal.getCodigoVia().compareTo("-") != 0){
			HTML link1 = new HTML();
			link1.addStyleName("ver");
			link1.setHTML("<br>Ver toda la vía");
			link1.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					try {
						map.getInfoWindow().close();
						map.getDragObject().setDraggableCursor("progress");
						greetingService.getVia(portal,
								new AsyncCallback<Via>() {
									public void onFailure(Throwable caught) {
										error.show();
										error.center();
										map.getDragObject().setDraggableCursor(DraggableObject.getDraggableCursorDefault());
									}
									public void onSuccess(final Via via) {
										greetingService.getPortales(via, new AsyncCallback<ArrayList<Portal>>(){
											public void onFailure(Throwable caught) {
												error.show();
												error.center();
												map.getDragObject().setDraggableCursor(DraggableObject.getDraggableCursorDefault());
											}
											public void onSuccess(ArrayList<Portal> result) {
												map.removeOverlay(marker);
												for (Portal portal: result){
													map.addOverlay(createMarkerPortal(portal, false));
												}
												Marker m = createMarkerVia(via, false);
												map.addOverlay(m);
												infoWindowVia(m, via, false);
												map.getDragObject().setDraggableCursor(DraggableObject.getDraggableCursorDefault());
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
		text.setStyleName("texto13");
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
						map.getInfoWindow().close();
						map.getDragObject().setDraggableCursor("progress");
						greetingService.getPortales(via, new AsyncCallback<ArrayList<Portal>>(){
							public void onFailure(Throwable caught) {
								error.show();
								error.center();
								map.getDragObject().setDraggableCursor(DraggableObject.getDraggableCursorDefault());
							}
							public void onSuccess(ArrayList<Portal> result) {
								map.removeOverlay(marker);
								for (Portal portal: result){
									map.addOverlay(createMarkerPortal(portal, false));
								}
								Marker m = createMarkerVia(via, false);
								map.addOverlay(m);
								infoWindowVia(m, via, false);
								map.getDragObject().setDraggableCursor(DraggableObject.getDraggableCursorDefault());
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
		if (portal.getHabitantes() >= 10){
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
				markerInfo = null;
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
		if (via.getHabitantes() >= 100){
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
				markerInfo = null;
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
	
	private void crearPoligono(final int opcion) {
		String color = "#FF0000";
		double opacity = 1.0;
		int weight = 1;
		PolyStyleOptions style = PolyStyleOptions.newInstance(color, weight, opacity);
		final Polygon poly = new Polygon(new LatLng[0]);
		map.addOverlay(poly);
		poly.setDrawingEnabled();
		poly.setStrokeStyle(style);
		poly.addPolygonRemoveHandler(new PolygonRemoveHandler() {
			public void onRemove(PolygonRemoveEvent event) {
				for (int i = 0; i < zonas.size(); i++){
					if (zonas.get(i).getPoly() == poly){
						zonas.remove(i);
						break;
					}
				}
			}
		});
		poly.addPolygonEndLineHandler(new PolygonEndLineHandler() {
			public void onEnd(final PolygonEndLineEvent event) {
				Timer t = new Timer() {
					public void run() {
						if (opcion == 0){
							nuevaZonaVacia(event.getSender(), true);
						}
						else if(opcion == 1){
							nuevaZonaPortales(event.getSender());
						}
						else if(opcion == 2){
							nuevaZonaVias(event.getSender());
						}
					}
				};
				t.schedule(10);
			}
		});
	}
	
	private void nuevaZonaVacia(final Polygon poly, boolean verViasPortales) {
		if (poly.getArea() > 3000000){
			verViasPortales = false;
		}
		final boolean ver = verViasPortales;
		final MyPolygon myPoly = new MyPolygon(poly, map);
		if (myPoly.getTriangle(0) == null){
			errorPoligono.show();
			errorPoligono.center();
			map.removeOverlay(poly);
			return;
		}
		map.getDragObject().setDraggableCursor("progress");
		try {
			greetingService.getZona(myPoly,
					new AsyncCallback<Zona>() {
						public void onFailure(Throwable caught) {
							error.show();
							error.center();
							map.getDragObject().setDraggableCursor(DraggableObject.getDraggableCursorDefault());
						}
						public void onSuccess(final Zona result) {
							ZonaClient zona = new ZonaClient(result);
							zona.setPoly(poly);
							zona.setMyPoly(myPoly);
							zona.setVer(ver);
							nuevaZona(zona);
							map.getDragObject().setDraggableCursor(DraggableObject.getDraggableCursorDefault());
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void nuevaZona(ZonaClient zona){
		zona.getPoly().setStrokeStyle(PolyStyleOptions.newInstance(zona.calculateColor()));
		zona.getPoly().setStrokeStyle(PolyStyleOptions.getInstance(3));
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
		text.setStyleName("texto13");
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
	
	
	private void nuevaZonaVias(final Polygon poly) {
		if (poly.getArea() > 3000000){
			DialogBox zonaGrande = crearDialogoGenerico("AVISO", "La zona solicitada es demasiado grande y tardaría en obtenerse. Se mostrará información padronal sin vías.");
			zonaGrande.addCloseHandler(new CloseHandler<PopupPanel>() {
				public void onClose(CloseEvent<PopupPanel> event) {
					nuevaZonaVacia(poly, false);
				}
			});
			zonaGrande.show();
			zonaGrande.center();
			return;
		}
		final MyPolygon myPoly = new MyPolygon(poly, map);
		if (myPoly.getTriangle(0) == null){
			errorPoligono.show();
			errorPoligono.center();
			map.removeOverlay(poly);
			return;
		}
		map.getDragObject().setDraggableCursor("progress");
		try {
			greetingService.getVias(myPoly,
					new AsyncCallback<Zona>() {
						public void onFailure(Throwable caught) {
							error.show();
							error.center();
							map.getDragObject().setDraggableCursor(DraggableObject.getDraggableCursorDefault());
						}
						public void onSuccess(Zona result) {
							ArrayList<Via> vias = result.getVias();
							for (int i = 0; i < vias.size(); i++) {
								map.addOverlay(createMarkerVia(vias.get(i), true));
							}
							ZonaClient zona = new ZonaClient();
							zona.setPoly(poly);
							zona.setMyPoly(myPoly);
							zona.setVer(false);
							zona.setHabitantes(result.getHabitantes());
							zona.setHojas(result.getHojas());
							nuevaZona(zona);
							map.getDragObject().setDraggableCursor(DraggableObject.getDraggableCursorDefault());
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private void zonaVias(ZonaClient zona) {
		try {
			map.getDragObject().setDraggableCursor("progress");
			greetingService.getVias(zona.getMyPoly(),
					new AsyncCallback<Zona>() {
						public void onFailure(Throwable caught) {
							error.show();
							error.center();
							map.getDragObject().setDraggableCursor(DraggableObject.getDraggableCursorDefault());
						}
						public void onSuccess(Zona result) {
							ArrayList<Via> vias = result.getVias();
							for (int i = 0; i < vias.size(); i++) {
								map.addOverlay(createMarkerVia(vias.get(i), true));
							}
							map.getDragObject().setDraggableCursor(DraggableObject.getDraggableCursorDefault());
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	private void nuevaZonaPortales(final Polygon poly) {
		if (poly.getArea() > 1500000){
			DialogBox zonaGrande = crearDialogoGenerico("AVISO", "La zona solicitada es demasiado grande y tardaría en obtenerse. Se mostrará información padronal sin portales.");
			zonaGrande.addCloseHandler(new CloseHandler<PopupPanel>() {
				public void onClose(CloseEvent<PopupPanel> event) {
					nuevaZonaVacia(poly, false);
				}
			});
			zonaGrande.show();
			zonaGrande.center();
			return;
		}
		final MyPolygon myPoly = new MyPolygon(poly, map);
		if (myPoly.getTriangle(0) == null){
			errorPoligono.show();
			errorPoligono.center();
			map.removeOverlay(poly);
			return;
		}
		map.getDragObject().setDraggableCursor("progress");
		try {
			greetingService.getPortales(myPoly,
					new AsyncCallback<ArrayList<Portal>>() {
						public void onFailure(Throwable caught) {
							error.show();
							error.center();
							map.getDragObject().setDraggableCursor(DraggableObject.getDraggableCursorDefault());
						}

						public void onSuccess(ArrayList<Portal> result) {
							int habitantes = 0;
							int hojas = 0;
							for (int i = 0; i < result.size(); i++) {
								map.addOverlay(createMarkerPortal(result.get(i), false));
								habitantes += result.get(i).getHabitantes();
								hojas += result.get(i).getHojas();
							}	
							ZonaClient zona = new ZonaClient();
							zona.setPoly(poly);
							zona.setMyPoly(myPoly);
							zona.setVer(false);
							zona.setHabitantes(habitantes);
							zona.setHojas(hojas);
							nuevaZona(zona);
							map.getDragObject().setDraggableCursor(DraggableObject.getDraggableCursorDefault());
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void zonaPortales(ZonaClient zona) {
		try {
			map.getDragObject().setDraggableCursor("progress");
			greetingService.getPortales(zona.getMyPoly(),
					new AsyncCallback<ArrayList<Portal>>() {
						public void onFailure(Throwable caught) {
							error.show();
							error.center();
							map.getDragObject().setDraggableCursor(DraggableObject.getDraggableCursorDefault());
						}

						public void onSuccess(ArrayList<Portal> result) {
							for (int i = 0; i < result.size(); i++) {
								map.addOverlay(createMarkerPortal(result.get(i), false));
							}
							map.getDragObject().setDraggableCursor(DraggableObject.getDraggableCursorDefault());
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void nuevoPortal(final LatLng point) {
		try {
			map.getDragObject().setDraggableCursor("progress");
			greetingService.getPortal(new MyLatLng(point),
					new AsyncCallback<Portal>() {
						public void onFailure(Throwable caught) {
							error.show();
							error.center();
							map.getDragObject().setDraggableCursor(DraggableObject.getDraggableCursorDefault());
						}

						public void onSuccess(final Portal portal) {
							if (portal == null){
								noPortal.show();
								noPortal.center();
							}
							else{
								Marker m = createMarkerPortal(portal, true);
								map.addOverlay(m);
								infoWindowPortal(m, portal, true);
							}
							map.getDragObject().setDraggableCursor(DraggableObject.getDraggableCursorDefault());
						}
						});
					}
			catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void nuevaVia(final LatLng point) {
		try {
			map.getDragObject().setDraggableCursor("progress");
			greetingService.getVia(new MyLatLng(point),
					new AsyncCallback<Via>() {
						public void onFailure(Throwable caught) {
							error.show();
							error.center();
							map.getDragObject().setDraggableCursor(DraggableObject.getDraggableCursorDefault());
						}
						public void onSuccess(final Via via) {
							if (via == null){
								noVia.show();
								noVia.center();
							}
							else{
								Marker m = createMarkerVia(via, true);
								map.addOverlay(m);
								infoWindowVia(m, via, true);
							}
							map.getDragObject().setDraggableCursor(DraggableObject.getDraggableCursorDefault());
						}
					});
				}
			catch (Exception e) {
			e.printStackTrace();
		}
	}
}
