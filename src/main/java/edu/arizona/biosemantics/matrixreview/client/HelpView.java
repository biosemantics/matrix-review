package edu.arizona.biosemantics.matrixreview.client;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.TabItemConfig;
import com.sencha.gxt.widget.core.client.TabPanel;


public class HelpView implements IsWidget {

	@Override
	public Widget asWidget() {
		TabPanel panel = new TabPanel();
		panel.setBorders(false);
		InstructionsText text = new InstructionsText();
		InstructionsVideo video = new InstructionsVideo();


		panel.add(text, new TabItemConfig("Instructions:Text"));
		panel.add(video, new TabItemConfig("Instructions:Videos"));
		return panel;
	}

}
