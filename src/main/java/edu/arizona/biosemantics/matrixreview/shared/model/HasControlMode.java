package edu.arizona.biosemantics.matrixreview.shared.model;

import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;

public interface HasControlMode {

	public enum ControlMode {
		CATEGORICAL, NUMERICAL, OFF
	}	
	
	public ControlMode getControlMode();
	
	public class ControlModeProperties {
		
		public ModelKeyProvider<ControlMode> key() {
			return new ModelKeyProvider<ControlMode>() {
				@Override
				public String getKey(ControlMode item) {
					return item.name();
				}
			};
		}

		public LabelProvider<ControlMode> name() {
			return new LabelProvider<ControlMode>() {

				@Override
				public String getLabel(ControlMode item) {
					return item.name();
				}
				
			};
		}
		
	}
	
}
