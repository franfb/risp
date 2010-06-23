package org.labis.risp.client;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

import org.apache.commons.digester.SetRootRule;

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
import com.google.gwt.maps.client.event.MapMouseMoveHandler;
import com.google.gwt.maps.client.event.MarkerClickHandler;
import com.google.gwt.maps.client.event.MarkerInfoWindowCloseHandler;
import com.google.gwt.maps.client.event.MarkerMouseOutHandler;
import com.google.gwt.maps.client.event.MarkerMouseOverHandler;
import com.google.gwt.maps.client.event.PolygonClickHandler;
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
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.HorizontalSplitPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;

public class Principal implements EntryPoint{
	private MapWidget map;
	private Button portalButton;
	private Button portalButtonZona;
	private Button viaButtonZona;
	private Button viaButton;
	private Button zonaButton;
	
	final DisclosurePanel viaDisclosure = new DisclosurePanel(
    "Mostrar información padronal de una vía");
	final DisclosurePanel portalDisclosure = new DisclosurePanel(
	"Mostrar información padronal de un portal");
	final DisclosurePanel zonaDisclosure = new DisclosurePanel(
	"Mostrar información padronal de una zona");
	
	private InfoWindow info;

	private final GreetingServiceAsync greetingService = GWT
			.create(GreetingService.class);
	
	
//	MarkerOptions portalIcon;
//	MarkerOptions viaIcon;
//	MarkerOptions viaIconGrande;

	MarkerOptions portalIcon1;
	MarkerOptions portalIcon2;
	MarkerOptions portalIcon3;
	MarkerOptions viaIcon1;
	MarkerOptions viaIcon2;
	MarkerOptions viaIcon3;
	
	
	//LinkedList<Polygon> polys = new LinkedList<Polygon>();
	LinkedList<ZonaClient> zonas = new LinkedList<ZonaClient>();
	ZonaClient highlighted = null;
	
	
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
		
		
		
		
		portalButtonZona.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event) {
				disableButtons();
				createPolylinePortal();
			}
		});

		viaButtonZona.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event) {
				disableButtons();
				createPolylineVia();
			}
		});
		
		zonaButton.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event) {
				disableButtons();
				createPolylineZona();
			}
		});
		
		viaButton.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event) {
				disableButtons();
				map.addMapClickHandler(new MapClickHandler(){
					public void onClick(MapClickEvent event) {
						map.removeMapClickHandler(this);
						if (event.getLatLng() != null){
							nuevaVia(event.getLatLng());
						}
						else{
							nuevaVia(event.getOverlayLatLng());
						}
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
						if (event.getLatLng() != null){
							nuevoPortal(event.getLatLng());
						}
						else{
							nuevoPortal(event.getOverlayLatLng());
						}
						enableButtons();
					}
				});
			}
		});
	}

	
	
	private void buildUi() {
        map = new MapWidget();
        LatLng tenerife = LatLng.newInstance(28.5160,-16.3761);
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
        HTML portalText = new HTML("Información de empadronamiento asociada a un edificio del municipio. " +
        		"También se puede obtener la información de todos los edificios que se encuentren dentro de una zona que usted elija. " +
        		"<br><br>Seleccione el tipo de búsqueda que desea e interactúe con el mapa:");
        portalText.setStyleName("texto");
        portalPanel.add(portalText);
        HorizontalPanel portalBotones = new HorizontalPanel();
        portalBotones.setSpacing(10);
        portalBotones.add(portalButton);
        portalBotones.add(portalButtonZona);
        portalPanel.add(portalBotones);
        
        
        portalDisclosure.addOpenHandler(new OpenHandler<DisclosurePanel>() {
        	public void onOpen(OpenEvent<DisclosurePanel> event) {
        		if (viaDisclosure.isOpen()){
        			viaDisclosure.setOpen(false);
        		}
        		if (zonaDisclosure.isOpen()){
        			zonaDisclosure.setOpen(false);
        		}
        	}
        });
        portalDisclosure.add(portalPanel);
        portalDisclosure.setStyleName("texto");
		funciones.add(portalDisclosure);
		

		
		
		
		AbsolutePanel viaPanel = new AbsolutePanel();
        HTML viaText = new HTML("Información de empadronamiento asociada a una vía del municipio." +
        		" También puede obtener la información de todas las vías que se encuentren dentro de una zona que usted elija. " +
        		"<br><br>Seleccione el tipo de búsqueda que desea e interactúe con el mapa:");
        viaText.setStyleName("texto");
        viaPanel.add(viaText);
        HorizontalPanel viaBotones = new HorizontalPanel();
        viaBotones.setSpacing(10);
        viaBotones.add(viaButton);
        viaBotones.add(viaButtonZona);
        viaPanel.add(viaBotones);
        


        
        
        viaDisclosure.addOpenHandler(new OpenHandler<DisclosurePanel>() {
        	public void onOpen(OpenEvent<DisclosurePanel> event) {
        		if (portalDisclosure.isOpen()){
        			portalDisclosure.setOpen(false);
        		}
        		if (zonaDisclosure.isOpen()){
        			zonaDisclosure.setOpen(false);
        		}
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
        
        
        zonaDisclosure.addOpenHandler(new OpenHandler<DisclosurePanel>() {
        	public void onOpen(OpenEvent<DisclosurePanel> event) {
        		if (portalDisclosure.isOpen()){
        			portalDisclosure.setOpen(false);
        		}
        		if (viaDisclosure.isOpen()){
        			viaDisclosure.setOpen(false);
        		}
        		//dis.setHeader(new HTML("PULSA PARA ABRIR MI NIÑO"));
        		//dis.setTitle("Abrir aqui para mostrar la info mas preciosa");
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
        
        
        
        
//        Icon icon = Icon.newInstance();
//		icon.setIconAnchor(Point.newInstance(16, 16));
//		icon.setInfoWindowAnchor(Point.newInstance(32, 0));
//		icon.setImageURL("http://maps.google.com/mapfiles/kml/pal3/icon56.png");
//		portalIcon = MarkerOptions.newInstance();
//		portalIcon.setIcon(icon);
//		
//		icon = Icon.newInstance();
//		icon.setIconAnchor(Point.newInstance(16, 16));
//		icon.setInfoWindowAnchor(Point.newInstance(32, 0));
//		icon.setImageURL("http://maps.google.com/mapfiles/kml/pal2/icon16.png");
//		viaIcon = MarkerOptions.newInstance();
//		viaIcon.setIcon(icon);
//		
//		icon = Icon.newInstance();
//		icon.setIconAnchor(Point.newInstance(16, 16));
//		icon.setInfoWindowAnchor(Point.newInstance(32, 0));
//		icon.setImageURL("http://maps.google.com/mapfiles/kml/pal2/icon16.png");
//		icon.setIconSize(Size.newInstance(40, 40));
//		viaIconGrande = MarkerOptions.newInstance();
//		viaIconGrande.setIcon(icon);
		
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
		   
		icon = Icon.newInstance();
		icon.setIconAnchor(Point.newInstance(16, 16));
		icon.setInfoWindowAnchor(Point.newInstance(32, 0));
		icon.setImageURL("http://maps.google.com/mapfiles/kml/pal4/icon54.png");
		viaIcon2 = MarkerOptions.newInstance();
		viaIcon2.setIcon(icon);
		
		icon = Icon.newInstance();
		icon.setIconAnchor(Point.newInstance(16, 16));
		icon.setInfoWindowAnchor(Point.newInstance(32, 0));
		icon.setImageURL("http://maps.google.com/mapfiles/kml/pal4/icon7.png");
		viaIcon3 = MarkerOptions.newInstance();
		viaIcon3.setIcon(icon);
		
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
				+ "<br>Personas empadronadas: "
				+ portal.getHabitantes() + "<br>Hojas padronales: "
				+ portal.getHojas()));
	}
	
	
	private void infoWindowVia(Marker marker, Via via){
		InfoWindow info = map.getInfoWindow();
		String nombreCalle = "<b>" +
		via.getNombre() + 
			"</b>";
		info.open(marker, new InfoWindowContent(nombreCalle
				+ "<br>Personas empadronadas: "
				+ via.getHabitantes()
				+ "<br>Longitud de la vía: "
				+ via.getLongitud() + " m"
				+ "<br>Código de vía: "
				+ via.getCodigo()));
	}
	
	
	private Marker createMarkerPortal(final Portal portal) {
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
			}
		});
		marker.addMarkerInfoWindowCloseHandler(new MarkerInfoWindowCloseHandler() {
			public void onInfoWindowClose(MarkerInfoWindowCloseEvent event) {
				marker.addMarkerMouseOutHandler(out);
			}
		});
		return marker;
	}
	
	private Marker createMarkerVia(final Via via) {
		
		MarkerOptions opt = viaIcon1;
		if (via.getHabitantes() > 100){
			opt = viaIcon2;
		}
		if (via.getHabitantes() > 500){
			opt = viaIcon3;
		}
		
		final Marker marker = new Marker(LatLng.newInstance(via.getCoordenadas()
				.getLatitude(), via.getCoordenadas().getLongitude()), opt);
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
			}
		});
		marker.addMarkerInfoWindowCloseHandler(new MarkerInfoWindowCloseHandler() {
			public void onInfoWindowClose(MarkerInfoWindowCloseEvent event) {
				marker.addMarkerMouseOutHandler(out);
			}
		});
		return marker;
	}
	
	
	private void createPolylinePortal() {
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
				nuevaZonaPortal(event.getSender());
				enableButtons();
			}
		});
	}

	
	private void createPolylineZona() {
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
	
	
	private void createPolylineVia() {
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
	
	
	private void nuevaZona(final Polyline pline) {
		try {
			greetingService.getZona(new MyPolygon(pline, map),
					new AsyncCallback<Zona>() {
						public void onFailure(Throwable caught) {
							System.out.println("Error.");
						}
						public void onSuccess(final Zona result) {
							final Polygon poly = MyPolygon.getPolygon(pline);
							ZonaClient zona = new ZonaClient(result);
							zona.setPoly(poly);
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
	
	
	
	private void setInfo(ZonaClient zona){
		info = map.getInfoWindow();
		double sizeArea = zona.getPoly().getArea();
		int densidad = (int) (zona.getHabitantes() / (sizeArea / 1000000));
		info.open(zona.getPoly().getBounds().getCenter(),
				new InfoWindowContent("<b>Área de la zona:</b> "
						+ (int)sizeArea + " m<sup>2</sup>"
						+ "<br><b>Personas empadronadas:</b> " + zona.getHabitantes()
						+ "<br><b>Hojas padronales:</b> " + zona.getHojas()
						+ "<br><b>Densidad poblacional:</b> " + densidad + " hab/km<sup>2</sup>")); 
	}
	
	
	private void nuevaZonaVias(final Polyline pline) {
		try {
			greetingService.getVias(new MyPolygon(pline, map),
					new AsyncCallback<Zona>() {
						public void onFailure(Throwable caught) {
							System.out.println("Error.");
						}

						public void onSuccess(Zona result) {
							ArrayList<Via> vias = result.getVias();
							for (int i = 0; i < vias.size(); i++) {
								map.addOverlay(createMarkerVia(vias.get(i)));
							}

							final Polygon poly = MyPolygon.getPolygon(pline);
							ZonaClient zona = new ZonaClient();
							zona.setPoly(poly);
							zona.setHabitantes(result.getHabitantes());
							zona.setHojas(result.getHojas());
							nuevaZona(zona);
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	private void nuevaZonaPortal(final Polyline pline) {
		try {
			greetingService.getPortales(new MyPolygon(pline, map),
					new AsyncCallback<ArrayList<Portal>>() {
						public void onFailure(Throwable caught) {
							System.out.println("Error.");
						}

						public void onSuccess(ArrayList<Portal> result) {
							int habitantes = 0;
							int hojas = 0;
							
							for (int i = 0; i < result.size(); i++) {
								map.addOverlay(createMarkerPortal(result.get(i)));
								habitantes += result.get(i).getHabitantes();
								hojas += result.get(i).getHojas();
							}	
							final Polygon poly = MyPolygon.getPolygon(pline);
							ZonaClient zona = new ZonaClient();
							zona.setPoly(poly);
							zona.setHabitantes(habitantes);
							zona.setHojas(hojas);
							nuevaZona(zona);

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
										map.addOverlay(createMarkerVia(via));
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
