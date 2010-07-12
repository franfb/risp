package org.labis.risp.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class MyDialog {

//	  public Widget onInitialize() {
//	    // Create the dialog box
//	    final DialogBox dialogBox = new DialogBox();
//	    dialogBox.setGlassEnabled(true);
//	    dialogBox.setAnimationEnabled(true);
//
//	    // Create a button to show the dialog Box
//	    Button openButton = new Button("hola",
//	        new ClickHandler() {
//	          public void onClick(ClickEvent sender) {
//	            dialogBox.center();
//	            dialogBox.show();
//	          }
//	        });
//
//	    // Create a ListBox
//	    HTML listDesc = new HTML("<br><br><br>"
//	        + constants.cwDialogBoxListBoxInfo());
//
//	    ListBox list = new ListBox();
//	    list.setVisibleItemCount(1);
//	    for (int i = 10; i > 0; i--) {
//	      list.addItem(constants.cwDialogBoxItem() + " " + i);
//	    }
//
//	    // Add the button and list to a panel
//	    VerticalPanel vPanel = new VerticalPanel();
//	    vPanel.setSpacing(8);
//	    vPanel.add(openButton);
//	    vPanel.add(listDesc);
//	    vPanel.add(list);
//
//	    // Return the panel
//	    return vPanel;
//	  }
//
//	  /**
//	   * Create the dialog box for this example.
//	   * 
//	   * @return the new dialog box
//	   */
//	  private DialogBox createDialogBox() {
//	    // Create a dialog box and set the caption text
//	    final DialogBox dialogBox = new DialogBox();
//	    dialogBox.ensureDebugId("cwDialogBox");
//	    dialogBox.setText(constants.cwDialogBoxCaption());
//
//	    // Create a table to layout the content
//	    VerticalPanel dialogContents = new VerticalPanel();
//	    dialogContents.setSpacing(4);
//	    dialogBox.setWidget(dialogContents);
//
//	    // Add some text to the top of the dialog
//	    HTML details = new HTML(constants.cwDialogBoxDetails());
//	    dialogContents.add(details);
//	    dialogContents.setCellHorizontalAlignment(details,
//	        HasHorizontalAlignment.ALIGN_CENTER);
//
//	    // Add an image to the dialog
//	    Image image = new Image(Showcase.images.jimmy());
//	    dialogContents.add(image);
//	    dialogContents.setCellHorizontalAlignment(image,
//	        HasHorizontalAlignment.ALIGN_CENTER);
//
//	    // Add a close button at the bottom of the dialog
//	    Button closeButton = new Button(constants.cwDialogBoxClose(),
//	        new ClickHandler() {
//	          public void onClick(ClickEvent event) {
//	            dialogBox.hide();
//	          }
//	        });
//	    dialogContents.add(closeButton);
//	    if (LocaleInfo.getCurrentLocale().isRTL()) {
//	      dialogContents.setCellHorizontalAlignment(closeButton,
//	          HasHorizontalAlignment.ALIGN_LEFT);
//
//	    } else {
//	      dialogContents.setCellHorizontalAlignment(closeButton,
//	          HasHorizontalAlignment.ALIGN_RIGHT);
//	    }
//
//	    // Return the dialog box
//	    return dialogBox;
//	  }

	
}
