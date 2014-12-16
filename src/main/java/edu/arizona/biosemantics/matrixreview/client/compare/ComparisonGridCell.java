package edu.arizona.biosemantics.matrixreview.client.compare;

import java.util.List;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.CssResource.Import;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.sencha.gxt.theme.base.client.grid.GridBaseAppearance.GridResources;
import com.sencha.gxt.theme.base.client.grid.GridBaseAppearance.GridStyle;
//import com.sencha.gxt.widget.core.client.grid.ColumnHeader.ColumnHeaderAppearance;
//import com.sencha.gxt.widget.core.client.grid.ColumnHeader.ColumnHeaderStyles;
import com.sencha.gxt.widget.core.client.grid.GridView.GridStateStyles;

import edu.arizona.biosemantics.matrixreview.client.compare.ComparisonGridCell.CustomGridResources.CustomGridStyle;

public class ComparisonGridCell extends AbstractCell<String> {
	CustomGridStyle styles = GWT.<CustomGridResources>create(CustomGridResources.class).css();
	//ColumnHeaderStyles columnHeaderStyles = GWT.<ColumnHeaderAppearance> create(ColumnHeaderAppearance.class).styles();
	
	public static final String CELL_BLOCKED = "[blocked]";
	public static final String CELL_MOVED = "[moved]";
	public static final String CELL_MOVED_SHOW_VALUE = "[movedshowvalue]";
	
	private ComparisonGrid<?, ?> parent;
	private List<CellIdentifier> changedCells;
	
	public ComparisonGridCell(ComparisonGrid<?, ?> parent, List<CellIdentifier> changedCells){
		this.parent = parent;
		this.changedCells = changedCells;
	}
	
	@Override
	public void render(com.google.gwt.cell.client.Cell.Context context,
			String value, SafeHtmlBuilder sb) {
		
		String key = (String)context.getKey();
		
		/**TODO: 
		 * Find out what is triggering all these NullPointerExceptions in getToolTip. It seems that
		 * sometimes the key in the cell is null? (Verify.)
		 */
		//System.out.println("Parent " + parent.getClass() + " has constant: " + parent.getSelectedConstant());
		CellIdentifier cell = new CellIdentifier((CellIdentifier.CellIdentifierObject)parent.getSelectedConstant(), key);
		boolean cellChanged = changedCells.contains(cell);
		
		if (value.equals("")){ //TODO: is there a better way to ensure that the row size does not shrink down for rows with only empty cells?
			value = "&nbsp;";
		}
		//String headerStyleClass = columnHeaderStyles.header() + " " + columnHeaderStyles.head();
		
		String qTip = parent.getQuickTip(cell, context.getColumn());
		
		if (value.equals(CELL_BLOCKED)){
			sb.appendHtmlConstant("<div class=\"" + styles.blocked() + "\"></div>");
		} else if (value.equals(CELL_MOVED)){
			sb.appendHtmlConstant("<div class=\"" + styles.cellHeight() + " " + styles.movedCell() + "\">");
		
		} else if (value.startsWith(CELL_MOVED_SHOW_VALUE)){
			
			String rest = value.substring(CELL_MOVED_SHOW_VALUE.length());
			String parentT = "";
			if (rest.startsWith("[parent:")){
				parentT = rest.substring(8, rest.indexOf("]"));
				rest = rest.substring(rest.indexOf("]")+1);
			}
			
			if (rest.equals("")){ //TODO: is there a better way to ensure that the row size does not shrink down for rows with only empty cells?
				rest = "&nbsp;";
			}
			
			//TODO: error check parent, show "root" instead of null
			qTip = "<b>Moved</b>" + (parentT.length() > 0 ? " to taxon: " + parentT : "") + "<br><br>" + qTip;
			
			sb.appendHtmlConstant("<div class=\"" + styles.cellHeight() + " "  + styles.movedCellShowValue() + "\" qtip=\"" + qTip + "\">");
			sb.appendHtmlConstant(rest);
			sb.appendHtmlConstant("</div>");
			
		} else{ 	
			String htmlConstant = "<div class=\"" + styles.cellHeight();
			if (parent.getMarkChangedCells() && cellChanged){
				htmlConstant += " " + styles.changedCell();
			}
			htmlConstant += "\" qtip=\"" + qTip + "\">";
			
			sb.appendHtmlConstant(htmlConstant);
			sb.appendHtmlConstant(value);
			
			sb.appendHtmlConstant("</div>");
		}
	}
	public interface CustomGridResources extends GridResources{
		@ImageOptions(repeatStyle = RepeatStyle.Horizontal, preventInlining = true)
	    ImageResource columnHeader();
		
		@Import(GridStateStyles.class)
		@Source("CustomGrid.css")
	    CustomGridStyle css();
		
		interface CustomGridStyle extends GridStyle{
			public String blocked();
			public String cellInnerMargins();
			public String movedCellShowValue();
			public String movedCell();
			public String changedCell();
			public String cellHeight();
		}
	}
}