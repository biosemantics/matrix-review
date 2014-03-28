package com.sencha.gxt.widget.core.client.grid;

public class RowConfig<M> {

	private M model;
	
	public RowConfig(M model) {
		this.model = model;
	}

	public M getModel() {
		return model;
	}
	
}
