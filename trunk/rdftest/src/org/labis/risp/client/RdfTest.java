package org.labis.risp.client;

import org.labis.risp.shared.FieldVerifier;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.maps.client.InfoWindow;
import com.google.gwt.maps.client.InfoWindowContent;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.control.LargeMapControl;
import com.google.gwt.maps.client.event.MarkerClickHandler;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.overlay.Marker;
import com.google.gwt.maps.client.overlay.MarkerOptions;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.xml.client.DOMException;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class RdfTest implements EntryPoint {
	private MapWidget map;

	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	private static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network "
			+ "connection and try again.";

	/**
	 * Create a remote service proxy to talk to the server-side Greeting service.
	 */
	private final GreetingServiceAsync greetingService = GWT
			.create(GreetingService.class);

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
//		final Button sendButton = new Button("Send");
//		final TextBox nameField = new TextBox();
//		nameField.setText("GWT User");
//		final Label errorLabel = new Label();
//
//		// We can add style names to widgets
//		sendButton.addStyleName("sendButton");
//
//		// Add the nameField and sendButton to the RootPanel
//		// Use RootPanel.get() to get the entire body element
//		RootPanel.get("nameFieldContainer").add(nameField);
//		RootPanel.get("sendButtonContainer").add(sendButton);
//		RootPanel.get("errorLabelContainer").add(errorLabel);
//
//		// Focus the cursor on the name field when the app loads
//		nameField.setFocus(true);
//		nameField.selectAll();
//
//		// Create the popup dialog box
//		final DialogBox dialogBox = new DialogBox();
//		dialogBox.setText("Remote Procedure Call");
//		dialogBox.setAnimationEnabled(true);
//		final Button closeButton = new Button("Close");
//		// We can set the id of a widget by accessing its Element
//		closeButton.getElement().setId("closeButton");
//		final Label textToServerLabel = new Label();
//		final HTML serverResponseLabel = new HTML();
//		VerticalPanel dialogVPanel = new VerticalPanel();
//		dialogVPanel.addStyleName("dialogVPanel");
//		dialogVPanel.add(new HTML("<b>Sending name to the server:</b>"));
//		dialogVPanel.add(textToServerLabel);
//		dialogVPanel.add(new HTML("<br><b>Server replies:</b>"));
//		dialogVPanel.add(serverResponseLabel);
//		dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
//		dialogVPanel.add(closeButton);
//		dialogBox.setWidget(dialogVPanel);
//
//		// Add a handler to close the DialogBox
//		closeButton.addClickHandler(new ClickHandler() {
//			public void onClick(ClickEvent event) {
//				dialogBox.hide();
//				sendButton.setEnabled(true);
//				sendButton.setFocus(true);
//			}
//		});
//
//		// Create a handler for the sendButton and nameField
//		class MyHandler implements ClickHandler, KeyUpHandler {
//			/**
//			 * Fired when the user clicks on the sendButton.
//			 */
//			public void onClick(ClickEvent event) {
//				sendNameToServer();
//			}
//
//			/**
//			 * Fired when the user types in the nameField.
//			 */
//			public void onKeyUp(KeyUpEvent event) {
//				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
//					sendNameToServer();
//				}
//			}
//
//			/**
//			 * Send the name from the nameField to the server and wait for a response.
//			 */
//			private void sendNameToServer() {
//				// First, we validate the input.
//				errorLabel.setText("");
//				String textToServer = nameField.getText();
//				if (!FieldVerifier.isValidName(textToServer)) {
//					errorLabel.setText("Please enter at least four characters");
//					return;
//				}
//
//				// Then, we send the input to the server.
//				sendButton.setEnabled(false);
//				textToServerLabel.setText(textToServer);
//				serverResponseLabel.setText("");
//				greetingService.greetServer(textToServer,
//						new AsyncCallback<String>() {
//							public void onFailure(Throwable caught) {
//								// Show the RPC error message to the user
//								dialogBox
//										.setText("Remote Procedure Call - Failure");
//								serverResponseLabel
//										.addStyleName("serverResponseLabelError");
//								serverResponseLabel.setHTML(SERVER_ERROR);
//								dialogBox.center();
//								closeButton.setFocus(true);
//							}
//
//							public void onSuccess(String result) {
//								dialogBox.setText("Remote Procedure Call");
//								serverResponseLabel
//										.removeStyleName("serverResponseLabelError");
//								serverResponseLabel.setHTML(result);
//								dialogBox.center();
//								closeButton.setFocus(true);
//							}
//						});
//			}
//		}
//
//		// Add a handler to send the name to the server
//		MyHandler handler = new MyHandler();
//		sendButton.addClickHandler(handler);
//		nameField.addKeyUpHandler(handler);
		
		try {
			greetingService.greetServer("texto",
			new AsyncCallback<String>() {
				public void onFailure(Throwable caught) {
					// Error en la RPC
					System.out.println("Error.");
				}

				public void onSuccess(String result) {
					parseXml(result);
				}
			});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Add a map
		LatLng tenerife = LatLng.newInstance(28.4682385853027,-16.2546157836914);
	    // Open a map centered on Santa Cruz de Tenerife

	    map = new MapWidget();
	    map.setSize("500px", "300px");
	    
	    // Add some controls for the zoom level
	    map.addControl(new LargeMapControl());
	    
	    // Add a marker
	    //map.addOverlay(new Marker(tenerife));

	    // Add an info window to highlight a point of interest
//	    map.getInfoWindow().open(map.getCenter(), 
//	        new InfoWindowContent("World's Largest Ball of Sisal Twine"));
	    
	    // Add the map to the HTML host page
	    RootPanel.get("mapContainer").add(map);
		
	}
	
    private void parseXml(String serverXml) {
        if (serverXml.equals("Error")) {
            map.setVisible(false);
        } else {
            try {
                // parse the XML document into a DOM
                Document messageDom = XMLParser.parse(serverXml);

                NodeList companiesList = messageDom.getElementsByTagName("company");
                boolean centered = false;

                for (int i = 0; i < companiesList.getLength(); i++) {
                    NodeList compAttr = companiesList.item(i).getChildNodes();
                    double lat = Double.parseDouble(compAttr.item(1).getFirstChild().getNodeValue());
                    double lng = Double.parseDouble(compAttr.item(2).getFirstChild().getNodeValue());
                    if ((lat != -1.0) && (lng != -1.0)) {
                        String compName = compAttr.item(0).getFirstChild().getNodeValue();
                        String info = compAttr.item(3).getFirstChild().getNodeValue();

                        map.addOverlay(createMarker(lat, lng, compName, info));
                        if (!centered) {
                            map.setCenter(LatLng.newInstance(lat, lng), 7);
                            centered = true;
                        }
                    }
                }
            } catch (DOMException e) {
                Window.alert("Could not parse XML document.");
            }
        }
    }

    private Marker createMarker(Double lat, Double lng, final String name, final String information) {
        MarkerOptions markerOpt = MarkerOptions.newInstance();
//        markerOpt.setIcon(icon);
        markerOpt.setClickable(true);
        final Marker marker = new Marker(LatLng.newInstance(lat, lng));

        marker.addMarkerClickHandler(new MarkerClickHandler() {

            public void onClick(MarkerClickEvent event) {
                InfoWindow info = map.getInfoWindow();
                info.open(marker,
                        new InfoWindowContent("<b>" + name + "</b><br>Información: " + information));
            }
        });

        return marker;
    }

}
