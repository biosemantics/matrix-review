package edu.arizona.biosemantics.matrixreview.client.matrix.filters;

import java.util.Collections;
import java.util.List;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Event;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.util.DelayedTask;
import com.sencha.gxt.data.shared.loader.FilterConfig;
import com.sencha.gxt.data.shared.loader.ValueFilterHandler;
import com.sencha.gxt.messages.client.DefaultMessages;
import com.sencha.gxt.widget.core.client.event.BeforeHideEvent;
import com.sencha.gxt.widget.core.client.event.BeforeHideEvent.BeforeHideHandler;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.grid.filters.Filter;

import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.Value;

public class ValueFilter extends Filter<Taxon, Value> {

	/**
	 * The default locale-sensitive messages used by this class.
	 */
	public class DefaultStringFilterMessages implements StringFilterMessages {

		@Override
		public String emptyText() {
			return DefaultMessages.getMessages().stringFilter_emptyText();
		}

	}

	/**
	 * The locale-sensitive messages used by this class.
	 */
	public interface StringFilterMessages {
		String emptyText();
	}

	protected TextField field;

	private StringFilterMessages messages;
	private DelayedTask updateTask = new DelayedTask() {

		@Override
		public void onExecute() {
			fireUpdate();
		}
	};
	
	public ValueFilter(ValueProvider<? super Taxon, Value> valueProvider) {
		super(valueProvider);
		setHandler(new ValueFilterHandler());

		field = new TextField() {
			protected void onKeyUp(Event event) {
				super.onKeyUp(event);
				onFieldKeyUp(event);
			}
		};

		menu.add(field);
		menu.addBeforeHideHandler(new BeforeHideHandler() {

			@Override
			public void onBeforeHide(BeforeHideEvent event) {
				// blur the field because of empty text
				// field.el().firstChild().blur();
				// blurField(field);
				field.getElement().selectNode("input").blur();
			}
		});

		setMessages(getMessages());
	}

	@Override
	public List<FilterConfig> getFilterConfig() {
		FilterConfig cfg = createNewFilterConfig();
		cfg.setType("string");
		cfg.setComparison("contains");
		String valueToSend = field.getCurrentValue();
		cfg.setValue(valueToSend);

		return Collections.singletonList(cfg);
	}

	@Override
	public Object getValue() {
		return field.getCurrentValue();
	}
	
	@Override
	public boolean isActivatable() {
		return field.getCurrentValue() != null && field.getCurrentValue().length() > 0;
	}

	@Override
	protected Class<Value> getType() {
		return Value.class;
	}

	protected void onFieldKeyUp(Event event) {
		int key = event.getKeyCode();
		if (key == KeyCodes.KEY_ENTER && field.isValid()) {
			event.stopPropagation();
			event.preventDefault();
			menu.hide(true);
			return;
		}
		updateTask.delay(getUpdateBuffer());
	}

	public void setMessages(StringFilterMessages messages) {
		this.messages = messages;
		field.setEmptyText(messages.emptyText());
	}
	
	public StringFilterMessages getMessages() {
		if (messages == null) {
			messages = new DefaultStringFilterMessages();
		}
		return messages;
	}
	
	protected boolean validateModel(Taxon model) {
		Value value = getValueProvider().getValue(model);
		String val = value.getValue();
		Object fieldValue = getValue();
		String v = fieldValue == null ? "" : fieldValue.toString();
		if (v.length() == 0 && (val == null || val.length() == 0)) {
			return true;
		} else if (val == null) {
			return false;
		} else {
			return val.toLowerCase().indexOf(v.toLowerCase()) > -1;
		}
	}

}
