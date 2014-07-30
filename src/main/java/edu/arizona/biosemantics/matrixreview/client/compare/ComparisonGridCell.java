package edu.arizona.biosemantics.matrixreview.client.compare;

import java.util.List;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.sencha.gxt.widget.core.client.grid.ColumnHeader.ColumnHeaderAppearance;
import com.sencha.gxt.widget.core.client.grid.ColumnHeader.ColumnHeaderStyles;

import edu.arizona.biosemantics.matrixreview.client.compare.ComparisonGrid.CustomGridResources;
import edu.arizona.biosemantics.matrixreview.client.compare.ComparisonGrid.CustomGridResources.CustomGridStyle;

public class ComparisonGridCell extends AbstractCell<String> {
	CustomGridStyle styles = GWT.<CustomGridResources>create(CustomGridResources.class).css();
	ColumnHeaderStyles columnHeaderStyles = GWT.<ColumnHeaderAppearance> create(ColumnHeaderAppearance.class).styles();
	
	private ComparisonGrid<?, ?> parent;
	private List<CellIdentifier> changedCells;
	
	public ComparisonGridCell(ComparisonGrid<?, ?> parent, List<CellIdentifier> changedCells){
		this.parent = parent;
		this.changedCells = changedCells;
	}
	
	@Override
	public void render(com.google.gwt.cell.client.Cell.Context context,
			String value, SafeHtmlBuilder sb) {
		
		Object key = context.getKey();
		
		CellIdentifier cell = new CellIdentifier(parent.getSelectedConstant(), key);
		boolean cellChanged = changedCells.contains(cell);
		
		if (value.equals("")){ //TODO: is there a better way to ensure that the row size does not shrink down for rows with only empty cells?
			value = "&nbsp;";
		}
		String headerStyleClass = columnHeaderStyles.header() + " " + columnHeaderStyles.head();
		if (value.equals("<empty>"))
			sb.appendHtmlConstant("<div class=\"" + styles.blocked() + " " + headerStyleClass + "\"></div>");
		else if (value.equals("<moved>"))
			sb.appendHtmlConstant("<div class=\"" + styles.movedCell() + " " + "\"></div>");
		else{ 	
			if (parent.getMarkChangedCells() && cellChanged){
				sb.appendHtmlConstant("<div class=\"" + styles.changedCell()+ "\">");
			}
			
			sb.appendHtmlConstant("<div class=\"" + styles.cellInnerMargins() + "\">" + value + "</div>");
			
			if (parent.getMarkChangedCells() && cellChanged){
				sb.appendHtmlConstant("</div>");
			}
		}
	}
}