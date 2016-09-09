package edu.arizona.biosemantics.matrixreview.client.common;

import java.util.List;

import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.box.AutoProgressMessageBox;
import com.sencha.gxt.widget.core.client.box.ConfirmMessageBox;
import com.sencha.gxt.widget.core.client.box.MessageBox;

public class Alerter {
	
	public static class InfoMessageBox extends MessageBox {
		public InfoMessageBox(String title, String message) {
			super(title, message);
			setIcon(ICONS.info());
		}
	}
		
	public static MessageBox startLoading() {
		AutoProgressMessageBox box = new AutoProgressMessageBox("Loading", "Loading your data, please wait...");
        box.setProgressText("Loading...");
        box.auto();
        box.show();
        return box;
	}
	
	public static void stopLoading(MessageBox box) {
		box.hide();
	}
	
	public static MessageBox showAlert(String title, String message, Throwable caught) {
		if(caught != null)
			caught.printStackTrace();
		return showAlert(title, message);
	}
	
	public static MessageBox showAlert(String title, String message) {
		AlertMessageBox alert = new AlertMessageBox(title, message);
		alert.show();
		return alert;
	}
	
	public static MessageBox failedNormalization() {
		AlertMessageBox alert = new AlertMessageBox("Unit Normalization", "Please don't normalize \"All listed characters\" and some other characters at the same time!");
		alert.show();
		return alert;
	}
	
	public static MessageBox showConfirm(String title, String message) {
		 ConfirmMessageBox confirm = new ConfirmMessageBox(title, message);
		 confirm.show();
         return confirm;
	}
	
	public static MessageBox finishAllNormalization(){
		
		InfoMessageBox info = new InfoMessageBox("Unit Normalization", "All listed characters are normarlized with select unit!");
		info.show();
		return info;
	}
	
public static MessageBox finishNormalization(List<Normalization> gridNormAll){
	    String characterList ="";
	    for (Normalization item: gridNormAll){
	    	characterList = "\"" + item.getCharacter().toString() + "\", " + characterList;
	    }
		
		InfoMessageBox info = new InfoMessageBox("Unit Normalization", "Character(s) "+ characterList +  "are/is normarlized with select unit(s)!");
		info.show();
		return info;
	}

	public static MessageBox showYesNoCancelConfirm(String title, String message) {
		MessageBox box = new MessageBox(title, message);
        box.setPredefinedButtons(PredefinedButton.YES, PredefinedButton.NO, PredefinedButton.CANCEL);
        box.setIcon(MessageBox.ICONS.question());
        box.show();
        return box;
	}

	public static MessageBox sleclecUnitSets() {
		InfoMessageBox info = new InfoMessageBox("Unit Normalization", "Please select units first!");
		info.show();
		return info;
		
	}
	
}