package edu.arizona.biosemantics.matrixreview.client.desktop.widget;

import com.sencha.gxt.widget.core.client.form.TextArea;

public class ConsoleCreator extends WidgetCreator {

	public class Console extends TextArea {
		
		public void append(String text) {
			String oldText = this.getText();
			this.setText(oldText + "\n" + text);
		}
		
	}

	private Console instance;
	
	@Override
	public Console create() {
		if(instance == null)
			instance = new Console();
		return instance;
	}

}
