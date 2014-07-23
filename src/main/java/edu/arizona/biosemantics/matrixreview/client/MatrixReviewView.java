package edu.arizona.biosemantics.matrixreview.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.layout.client.Layout.AnimationCallback;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AnimatedLayout;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.box.MessageBox;
import com.sencha.gxt.widget.core.client.box.MultiLinePromptMessageBox;
import com.sencha.gxt.widget.core.client.box.PromptMessageBox;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent.DialogHideHandler;
import com.sencha.gxt.widget.core.client.grid.CheckBoxSelectionModel;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;

import edu.arizona.biosemantics.matrixreview.client.desktop.DesktopView;
import edu.arizona.biosemantics.matrixreview.client.event.AutosaveEvent;
import edu.arizona.biosemantics.matrixreview.client.event.LoadTaxonMatrixEvent;
import edu.arizona.biosemantics.matrixreview.client.event.ShowDesktopEvent;
import edu.arizona.biosemantics.matrixreview.client.event.ToggleDesktopEvent;
import edu.arizona.biosemantics.matrixreview.client.matrix.MatrixView;
import edu.arizona.biosemantics.matrixreview.client.matrix.TaxonStore;
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
	
	private Button loadVersionButton;
	private Button compareVersionsButton;
	private Button saveNewVersionButton;
	private Label autosaveLabel;
	
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
		};
		autosaveTimer.scheduleRepeating(autosaveIntervalMillis);
	}
	
	public Widget init() {
		eventBus = new SimpleEventBus();
		matrixView = new MatrixView(eventBus, currentVersion.getTaxonMatrix());
		desktopView = new DesktopView(eventBus, currentVersion.getTaxonMatrix());
		//modelManager = new StoreModelManager(eventBus, matrixView, desktopView);
		eventBus.fireEvent(new LoadTaxonMatrixEvent(currentVersion.getTaxonMatrix()));
		
		Label mostRecentVersionLabel = new Label("Last saved version: " );
		compareVersionsButton = new Button("View/Compare Versions");
		loadVersionButton = new Button("Load Version...");
		saveNewVersionButton = new Button("Save as New Version");
		
		HorizontalPanel upperPanel = new HorizontalPanel();
		
		upperPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		upperPanel.getElement().getStyle().setWidth(80, Unit.PCT);
		upperPanel.add(mostRecentVersionLabel);
		upperPanel.add(loadVersionButton);
		upperPanel.add(compareVersionsButton);
		
		autosaveLabel = new Label();
		
		HorizontalPanel lowerPanel = new HorizontalPanel();
		lowerPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		lowerPanel.getElement().getStyle().setWidth(80, Unit.PCT);
		lowerPanel.add(autosaveLabel);
		lowerPanel.add(saveNewVersionButton);
		
		DockLayoutPanel matrixPanel = new DockLayoutPanel(Unit.EM);
		matrixPanel.addNorth(upperPanel, 2.5);
		matrixPanel.addSouth(lowerPanel, 2.5);
		matrixPanel.add(matrixView);
		
		splitLayoutPanel = new SplitLayoutPanel();
		splitLayoutPanel.addSouth(desktopView, 0);
		splitLayoutPanel.add(matrixPanel);
		
		
		addEventHandlers();
		/*desktopView.addClickHandler(new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			toggleFooter();
		}
		});*/
		
		// Set up 'Select Version' dialog grid
		//TODO: add tooltips in case value is cut off.
		//TODO: Make it so that the most recent version appears at the top. 
		VersionInfoProperties gridProperties = GWT.create(VersionInfoProperties.class);
		ColumnConfig<VersionInfo, Date> createdCol = new ColumnConfig<VersionInfo, Date>(gridProperties.created(), 175, "Created");
		ColumnConfig<VersionInfo, String> authorCol = new ColumnConfig<VersionInfo, String>(gridProperties.author(), 125, "Author");
		ColumnConfig<VersionInfo, String> commentCol = new ColumnConfig<VersionInfo, String>(gridProperties.comment(), 450, "Comment");
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
				autosaveLabel.setText("Autosaved at " + (new Date()) + ".");
			}
		});
		
		compareVersionsButton.addClickHandler(new CompareVersionsHandler());
		
		loadVersionButton.addClickHandler(new LoadVersionHandler());
		
		saveNewVersionButton.addClickHandler(new SaveNewVersionHandler());
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
		grid.getSelectionModel().setSelectionMode(selectionMode);
		
		MessageBox box = new MessageBox("Select Version(s)");
		box.add(new Label("Available versions: "));
		box.add(grid);
		box.setPredefinedButtons(PredefinedButton.CANCEL, PredefinedButton.OK);
		box.setHideOnButtonClick(true);
		box.addDialogHideHandler(new DialogHideHandler() {
			@Override
			public void onDialogHide(DialogHideEvent event){
				if (event.getHideButton().equals(PredefinedButton.OK)){
					List<VersionInfo> selectedVersions = grid.getSelectionModel().getSelectedItems();
					listener.onSelect(selectedVersions);
				} else { //Cancel button was clicked.
					listener.onSelect(null);
				}
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
		
		matrixService.commitCurrentVersion(newVersion.getTaxonMatrix(), new AsyncCallback<Boolean>(){
			@Override
			public void onFailure(Throwable caught){
				caught.printStackTrace();
			}
			
			@Override
			public void onSuccess(Boolean result){
				if (result == true)
					Window.Location.reload();
			}
		});
	}
	
	private void showCompareVersionsDialog(List <SimpleMatrixVersion> oldVersions){
		final MatrixCompareView view = new MatrixCompareView(oldVersions, currentVersion);
		
		final Dialog dialog = new Dialog();
		dialog.setPredefinedButtons(PredefinedButton.CANCEL, PredefinedButton.OK);
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
	private class CompareVersionsHandler implements ClickHandler {
		@Override
		public void onClick(ClickEvent event) {
			System.out.println("Compare Versions button pressed!");
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
	private class SaveNewVersionHandler implements ClickHandler {
		@Override
		public void onClick(ClickEvent event) {
			
			final PromptMessageBox authorBox = new PromptMessageBox("Version Info", "Please specify an author for this version: ");
			authorBox.addDialogHideHandler(new DialogHideHandler() {
				@Override
				public void onDialogHide(DialogHideEvent event) {
					final String author = authorBox.getValue();
					if (event.getHideButton().equals(PredefinedButton.OK) && author != null){
						
						final MultiLinePromptMessageBox commentBox = new MultiLinePromptMessageBox("Version Info", "Please include a comment about this version: ");
						commentBox.addDialogHideHandler(new DialogHideHandler() {
							@Override
							public void onDialogHide(DialogHideEvent event) {
								final String comment = commentBox.getValue();
								if (event.getHideButton().equals(PredefinedButton.OK) && comment != null){
									//make server request to commit new version.
									matrixService.commitNewVersion(currentVersion.getTaxonMatrix(), author, comment, new AsyncCallback<Boolean>(){
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
							}
						});
						commentBox.show();
					}
				}
			});
			authorBox.show();
		}
	}
	
	/**
	 * On 'Load version' click: 
	 *	1) Get list of available versions from server
	 *	2) Show dialog to let user select a version.
	 *	3) TODO: Ask user to comfirm the loss of any current changes.
	 *	4) Call loadVersion with the selected version. 
	 */
	private class LoadVersionHandler implements ClickHandler {
		@Override
		public void onClick(ClickEvent event) {
			System.out.println("Load Version button pressed!");
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
								VersionInfo selected = selectedVersions.get(0);
								
								//TODO: add confirm message. ("Any changes that have not been committed will be lost.")
								
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
					}); //end showSelectVersionsDialog
					
				}
			}); //end matrixService.getAvailableVersions
		}
	}
}
