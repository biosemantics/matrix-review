package edu.arizona.biosemantics.matrixreview.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.layout.client.Layout.AnimationCallback;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AnimatedLayout;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.Store.StoreSortInfo;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.box.ConfirmMessageBox;
import com.sencha.gxt.widget.core.client.box.MessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutData;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutPack;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer.HBoxLayoutAlign;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent.DialogHideHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.event.ViewReadyEvent;
import com.sencha.gxt.widget.core.client.event.ViewReadyEvent.ViewReadyHandler;
import com.sencha.gxt.widget.core.client.grid.CheckBoxSelectionModel;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.tips.QuickTip;

import edu.arizona.biosemantics.matrixreview.client.compare.MatrixCompareView;
import edu.arizona.biosemantics.matrixreview.client.desktop.DesktopView;
import edu.arizona.biosemantics.matrixreview.client.event.AutosaveEvent;
import edu.arizona.biosemantics.matrixreview.client.event.CommitNewVersionEvent;
import edu.arizona.biosemantics.matrixreview.client.event.LoadTaxonMatrixEvent;
import edu.arizona.biosemantics.matrixreview.client.event.ShowDesktopEvent;
import edu.arizona.biosemantics.matrixreview.client.event.ToggleDesktopEvent;
import edu.arizona.biosemantics.matrixreview.client.matrix.MatrixView;
import edu.arizona.biosemantics.matrixreview.client.matrix.TaxonStore;
import edu.arizona.biosemantics.matrixreview.client.matrix.menu.VersionInfoDialog;
import edu.arizona.biosemantics.matrixreview.client.matrix.shared.MatrixVersion;
import edu.arizona.biosemantics.matrixreview.client.matrix.shared.SimpleMatrixVersion;
import edu.arizona.biosemantics.matrixreview.client.matrix.shared.VersionInfo;
import edu.arizona.biosemantics.matrixreview.client.matrix.shared.VersionInfoProperties;
import edu.arizona.biosemantics.matrixreview.shared.IMatrixService;
import edu.arizona.biosemantics.matrixreview.shared.IMatrixServiceAsync;

public class MatrixReviewView extends Composite implements /*implements IsWidget, */RequiresResize, ProvidesResize, AnimatedLayout {

	private MatrixVersion currentVersion;
	private TaxonStore taxonStore;
	
	private SimpleEventBus eventBus;
	
	private static final int autosaveIntervalMillis = 60000;
	
	private SplitLayoutPanel splitLayoutPanel;
	private MatrixView matrixView;
	private DesktopView desktopView;
	private int desktopHeight = 300;
	
	private HBoxLayoutContainer northContent;
	private TextButton loadVersionButton;
	private TextButton compareVersionsButton;
	private TextButton saveNewVersionButton;
	private Label autosaveLabel;
	
	private final DateTimeFormat selectVersionDateFormat = DateTimeFormat.getFormat("EEE, MMM d, yyyy  hh:mm aaa");
	private final DateTimeFormat autosaveDateFormat = DateTimeFormat.getFormat("h:mm aaa");
	
	private ListStore<VersionInfo> listStore;
	private Grid<VersionInfo> grid;
	
	IMatrixServiceAsync matrixService;
	
	public MatrixReviewView(MatrixVersion version) {
		this.currentVersion = version;	
		this.initWidget(init());
		matrixService = GWT.create(IMatrixService.class);
		
		
		//Schedule repeating autosave
		Timer autosaveTimer = new Timer(){
			@Override
			public void run(){
				saveCurrentVersion();
			}
		};
		autosaveTimer.scheduleRepeating(autosaveIntervalMillis);
	}
	
	public Widget init() {
		eventBus = new SimpleEventBus();
		matrixView = new MatrixView(eventBus, currentVersion.getTaxonMatrix());
		desktopView = new DesktopView(eventBus, currentVersion.getTaxonMatrix());
		//modelManager = new StoreModelManager(eventBus, matrixView, desktopView);
		eventBus.fireEvent(new LoadTaxonMatrixEvent(currentVersion.getTaxonMatrix()));
		
		compareVersionsButton = new TextButton("View/Compare Versions");
		loadVersionButton = new TextButton("Load Version...");
		saveNewVersionButton = new TextButton("Save New Version");
		autosaveLabel = new Label();
		autosaveLabel.getElement().getStyle().setColor("#A8A8A8");
		autosaveLabel.getElement().getStyle().setFontSize(9, Unit.PT);
		
		northContent = new HBoxLayoutContainer();
		northContent.setPack(BoxLayoutPack.END);
		northContent.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);
		northContent.add(autosaveLabel, new BoxLayoutData(new Margins(0, 20, 0, 0)));
		northContent.add(saveNewVersionButton, new BoxLayoutData(new Margins(0, 10, 0, 0)));
		northContent.add(loadVersionButton, new BoxLayoutData(new Margins(0, 30, 0, 0)));
		northContent.add(compareVersionsButton, new BoxLayoutData(new Margins(0, 40, 0, 0)));

		final BorderLayoutContainer container = new BorderLayoutContainer();
		container.getElement().getStyle().setBackgroundColor("white");
		
		BorderLayoutData northData = new BorderLayoutData(40);
		
		container.setNorthWidget(northContent, northData);
		container.setCenterWidget(matrixView, new MarginData());
		
		splitLayoutPanel = new SplitLayoutPanel();
		splitLayoutPanel.addSouth(desktopView, 0);
		splitLayoutPanel.add(container);
		
		
		
		addEventHandlers();
		/*desktopView.addClickHandler(new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			toggleFooter();
		}
		});*/
		
		// Set up 'Select Version' dialog grid
		VersionInfoProperties gridProperties = GWT.create(VersionInfoProperties.class);
		ColumnConfig<VersionInfo, String> createdCol = new ColumnConfig<VersionInfo, String>(new ValueProvider<VersionInfo, String>(){
			@Override
			public String getValue(VersionInfo info) {
				return selectVersionDateFormat.format(info.getCreated());
			}
			public void setValue(VersionInfo object, String value) {}
			public String getPath() {return "";}
			
		}, 175, "Created");
		ColumnConfig<VersionInfo, String> authorCol = new ColumnConfig<VersionInfo, String>(gridProperties.author(), 125, "Author");
		ColumnConfig<VersionInfo, String> commentCol = new ColumnConfig<VersionInfo, String>(gridProperties.comment(), 450, "Comment");
		commentCol.setCell(new AbstractCell<String>(){
			@Override
			public void render(com.google.gwt.cell.client.Cell.Context context,
					String value, SafeHtmlBuilder sb) {
				sb.appendHtmlConstant("<div qtip=\"" + value + "\">" + value);
				sb.appendHtmlConstant("</div>");
			}
		});
		
		IdentityValueProvider<VersionInfo> identity = new IdentityValueProvider<VersionInfo>();
		CheckBoxSelectionModel<VersionInfo> selectionModel = new CheckBoxSelectionModel<VersionInfo>(identity);
		
		List<ColumnConfig<VersionInfo, ?>> columns = new ArrayList<ColumnConfig<VersionInfo, ?>>();
		columns.add(selectionModel.getColumn()); //Check box colummn
		columns.add(createdCol); //'Created' column
		columns.add(authorCol); //'Author' column
		columns.add(commentCol); //'Comment' column
		ColumnModel<VersionInfo> model = new ColumnModel<VersionInfo>(columns);
		
		listStore = new ListStore<VersionInfo>(gridProperties.key());
		
		grid = new Grid<VersionInfo>(listStore, model);
		grid.setSelectionModel(selectionModel);
		new QuickTip(grid); //register a quick tip manager with this grid.
		

		return splitLayoutPanel;
	}
	
	private void addEventHandlers() {
		eventBus.addHandler(ShowDesktopEvent.TYPE, new ShowDesktopEvent.ShowDesktopEventHandler() {
			@Override
			public void onShow(ShowDesktopEvent showDesktopEvent) {
				showDesktop();
			}
		});
		eventBus.addHandler(ToggleDesktopEvent.TYPE, new ToggleDesktopEvent.ToggleDesktopEventHandler() {
			@Override
			public void onToggle(ToggleDesktopEvent toggleDesktopEvent) {
				toggleDesktop();
			}
		});
		eventBus.addHandler(AutosaveEvent.TYPE, new AutosaveEvent.AutosaveEventHandler() {
			@Override
			public void onAutosave(AutosaveEvent event) {
				autosaveLabel.setText("Autosaved at " + autosaveDateFormat.format(new Date()) + ".");
				northContent.forceLayout();
			}
		});
		
		eventBus.addHandler(CommitNewVersionEvent.TYPE, new CommitNewVersionEvent.CommitNewVersionEventHandler() {
			@Override
			public void onCommitRequest(CommitNewVersionEvent event) {
				System.out.println("Committing new version.");
				//make server request to commit new version.
				matrixService.commitNewVersion(currentVersion.getTaxonMatrix(), event.getAuthor(), event.getComment(), new AsyncCallback<Boolean>(){
					@Override
					public void onFailure(Throwable caught) {
						caught.printStackTrace();
					}
						@Override
					public void onSuccess(Boolean result) {
						if (result == true){
							MessageBox alert = new MessageBox("Save Successful", "Your new version was created successfully.");
							alert.show();
						} else {
							AlertMessageBox alert = new AlertMessageBox("Error", "Your new version was not saved. Please try again later.");
							alert.show();
						}
					}
				});
				
			}
		});
		
		compareVersionsButton.addSelectHandler(new CompareVersionsHandler());
		
		loadVersionButton.addSelectHandler(new LoadVersionHandler());
		
		saveNewVersionButton.addSelectHandler(new SaveNewVersionHandler());
	}
	
	/**
	 * Displays version info and allows the user to select one (or more, depending on the 
	 * SelectionMode).
	 * 
	 * @param availableVersions A list of versions to display to the user as options. 
	 * @param selectionMode SelectionMode.SINGLE or SelectionMode.MULTI.
	 * @param listener Will call listener's onSelect method when the window is hidden. 
	 */
	private void showSelectVersionsDialog(List<VersionInfo> availableVersions, SelectionMode selectionMode, final SelectVersionsListener listener){
		listStore.clear();
		listStore.addAll(availableVersions);
		VersionInfoProperties props = GWT.create(VersionInfoProperties.class);
		listStore.addSortInfo(new StoreSortInfo<VersionInfo>(props.created(), SortDir.DESC));
		grid.getSelectionModel().setSelectionMode(selectionMode);
		grid.getSelectionModel().selectAll();
		
		final MessageBox box = new MessageBox("Select Version(s)");
		box.add(new Label("Available versions: "));
		box.add(grid);
		box.setPredefinedButtons(PredefinedButton.OK, PredefinedButton.CANCEL);
		box.setHideOnButtonClick(true);
		box.addDialogHideHandler(new DialogHideHandler() {
			@Override
			public void onDialogHide(DialogHideEvent event){
				if (event.getHideButton().equals(PredefinedButton.OK)){
					List<VersionInfo> selectedVersions = grid.getSelectionModel().getSelectedItems();
					//sort list by date, otherwise columns will be in the order that the user selected them.
					Collections.sort(selectedVersions, new Comparator<VersionInfo>(){
						@Override
						public int compare(VersionInfo a, VersionInfo b) {
							return a.getCreated().compareTo(b.getCreated());
						}
					});
					listener.onSelect(selectedVersions);
				} else { //Cancel button was clicked.
					listener.onSelect(null);
				}
			}
		});
		grid.addViewReadyHandler(new ViewReadyHandler(){
			@Override
			public void onViewReady(ViewReadyEvent event) {
				box.center();
			}
		});
		box.show();
	}
	
	/**
	 * See showSelectVersionsDialog. Used to notify the caller when the user has selected 0 or more
	 * versions and the Select Versions dialog box has closed. 
	 */
	private interface SelectVersionsListener{
		public void onSelect(List<VersionInfo> list);
	}
	
	/**
	 * Requests that the server overwrite the 'current' version with 'newVersion' and then
	 * reloads the page. 
	 */
	private void loadVersion(MatrixVersion newVersion){
		/*currentVersion = newVersion;
		/eventBus.fireEvent(new LoadTaxonMatrixEvent(currentVersion.getTaxonMatrix()));*/
		
		currentVersion = newVersion;
		matrixView.loadMatrix(newVersion.getTaxonMatrix());
		
		/*matrixService.commitCurrentVersion(newVersion.getTaxonMatrix(), new AsyncCallback<Boolean>(){
			@Override
			public void onFailure(Throwable caught){
				caught.printStackTrace();
			}
			
			@Override
			public void onSuccess(Boolean result){
				if (result == true)
					Window.Location.reload();
			}
		});*/
	}
	
	private void showCompareVersionsDialog(List <SimpleMatrixVersion> oldVersions){
		final MatrixCompareView view = new MatrixCompareView(oldVersions, currentVersion);
		
		final Dialog dialog = new Dialog();
		dialog.setModal(true);
		dialog.setPredefinedButtons(PredefinedButton.OK, PredefinedButton.CANCEL);
		dialog.setHeadingText("Compare Versions");
		dialog.setWidth(900);
		dialog.setHeight(500);
		dialog.add(view.asWidget());
		dialog.setHideOnButtonClick(true);
		dialog.addDialogHideHandler(new DialogHideHandler(){
			@Override
			public void onDialogHide(DialogHideEvent event) {
				if (event.getHideButton() == PredefinedButton.OK){
					currentVersion = view.getModifiedVersion();
					matrixView.loadMatrix(currentVersion.getTaxonMatrix());
					forceLayout();
				}
			}	
		});
		dialog.show();
	}

	protected void showDesktop() {
		splitLayoutPanel.forceLayout();
		if(splitLayoutPanel.getWidgetSize(desktopView) < desktopHeight) 
			splitLayoutPanel.setWidgetSize(desktopView, desktopHeight);
		splitLayoutPanel.animate(500);
	}

	protected void toggleDesktop() {
		splitLayoutPanel.forceLayout();
		if(splitLayoutPanel.getWidgetSize(desktopView) == desktopHeight) 
			splitLayoutPanel.setWidgetSize(desktopView, 0);
		else if(splitLayoutPanel.getWidgetSize(desktopView) == 0) 
			splitLayoutPanel.setWidgetSize(desktopView, desktopHeight);
		splitLayoutPanel.animate(500);
	}

	@Override
	public void onResize() {
		splitLayoutPanel.onResize();
	}

	@Override
	public void animate(int duration) {
		splitLayoutPanel.animate(duration);
	}

	@Override
	public void animate(int duration, AnimationCallback callback) {
		splitLayoutPanel.animate(duration, callback);
	}

	@Override
	public void forceLayout() {
		splitLayoutPanel.forceLayout();
	}
	
	/**
	 * On 'View/compare versions' click: 
	 *	1) Get list of available versions from server
	 *	2) Show dialog to let user select multiple versions
	 *	3) Get a list of the selected associated SimpleMatrixVersions from the server
	 *	4) Create a MatrixCompareView with the received SimpleMatrixVersions and display it. 
	 */
	private class CompareVersionsHandler implements SelectHandler {
		@Override
		public void onSelect(SelectEvent event) {
			saveCurrentVersion();
			matrixService.getAvailableVersions(new AsyncCallback<List<VersionInfo>>(){
				@Override
				public void onFailure(Throwable caught) {
					caught.printStackTrace();
				}
					@Override
				public void onSuccess(List<VersionInfo> availableVersions) { //got a list of available versions from the server.

					showSelectVersionsDialog(availableVersions, SelectionMode.MULTI, new SelectVersionsListener(){
						@Override
						public void onSelect(List<VersionInfo> selectedVersions){ //user selected some versions from the list.
							if (selectedVersions != null && selectedVersions.size() > 0){
								
								//get list of versionIDs from list of VersionInfo
								List<String> versionIDs= new ArrayList<String>();
								for (VersionInfo version: selectedVersions){
									versionIDs.add(version.getVersionID());
								}
								
								matrixService.getVersions(versionIDs, new AsyncCallback<List<SimpleMatrixVersion>>(){
									@Override
									public void onFailure(Throwable caught){
										caught.printStackTrace();
									}
									
									@Override
									public void onSuccess(List<SimpleMatrixVersion> versions){ //got the list of SimpleVersions from the server.
										showCompareVersionsDialog(versions);
									}
								});
							}
						}
					}); //end showSelectVersionsDialog
				}
			});//end matrixService.getAvailableVersions
		}
	}
	
	/**
	 * On 'Save new version' click
	 * 	1) Prompt user for author
	 * 	2) Prompt user for comment
	 * 	3) Send Commit request to server
	 * 	4) Show success or failure. 
	 */
	private class SaveNewVersionHandler implements SelectHandler {
		@Override
		public void onSelect(SelectEvent event) {
			saveCurrentVersion();
			VersionInfoDialog dialog = new VersionInfoDialog(eventBus);
			dialog.show();
		}
	}
	
	/**
	 * On 'Load version' click: 
	 *	1) Get list of available versions from server
	 *	2) Show dialog to let user select a version.
	 *	3) TODO: Ask user to confirm the loss of any current changes.
	 *	4) Call loadVersion with the selected version. 
	 */
	private class LoadVersionHandler implements SelectHandler {
		@Override
		public void onSelect(SelectEvent event) {
			matrixService.getAvailableVersions(new AsyncCallback<List<VersionInfo>>(){
				@Override
				public void onFailure(Throwable caught) {
					caught.printStackTrace();
				}
					@Override
				public void onSuccess(List<VersionInfo> availableVersions) {
						
					showSelectVersionsDialog(availableVersions, SelectionMode.SINGLE, new SelectVersionsListener(){
						@Override
						public void onSelect(List<VersionInfo> selectedVersions){
							if (selectedVersions != null && selectedVersions.size() > 0){
								final VersionInfo selected = selectedVersions.get(0);
								
								ConfirmMessageBox confirmBox = new ConfirmMessageBox("Confirm Load", "Any changes that have not been saved will be lost. Do you want to continue?");
								confirmBox.addDialogHideHandler(new DialogHideHandler(){
									@Override
									public void onDialogHide(DialogHideEvent event) {
										if (event.getHideButton() == PredefinedButton.YES){
											matrixService.getVersion(selected.getVersionID(), new AsyncCallback<MatrixVersion>(){
												@Override
												public void onFailure(Throwable caught){
													caught.printStackTrace();
												}
												
												@Override
												public void onSuccess(MatrixVersion newVersion){
													loadVersion(newVersion);
												}
											});
										}
									}
								});
								confirmBox.show();
							}
						}
					}); //end showSelectVersionsDialog
					
				}
			}); //end matrixService.getAvailableVersions
		}
	}
	
	private void saveCurrentVersion(){
		matrixService.commitCurrentVersion(currentVersion.getTaxonMatrix(), new AsyncCallback<Boolean>(){
			@Override
			public void onFailure(Throwable caught){
				caught.printStackTrace();
			}
			
			@Override
			public void onSuccess(Boolean result){
				if (result == true) //autosave was successful. 
					eventBus.fireEvent(new AutosaveEvent());
			}
		});
	}
}
