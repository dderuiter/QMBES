/**********************************************************************************************************************
 *
 * WARNING:
 * Copyright Ⓒ 2016 by D.DeRuiter
 * Do not use, modify, or distribute in any way without express written consent.
 *
 * PROJECT:
 * QMBES (Quine McCluskey Boolean Expression Simplifier)
 *
 * DESCRIPTION:
 * Controller class for performing logic for the Quine McCluskey algorithm and updating the corresponding view.
 *
 * SOFTWARE HISTORY:
 * Date          Developer      Modification
 * ----------    -----------    ------------
 * 04/26/2016    D. DeRuiter    Initial coding.
 *
 **********************************************************************************************************************/

package com.deruiter.controller;

import com.deruiter.model.algorithm.QuineMcCluskey;
import com.deruiter.model.exception.DistributivePropertyException;
import com.deruiter.model.exception.InvalidInputException;
import com.deruiter.model.group.Group;
import com.deruiter.model.group.term.Term;
import com.deruiter.model.gui.MintermTable;
import com.deruiter.model.gui.MintermTableRow;
import com.deruiter.model.input.Parser;
import com.deruiter.model.report.PrimeImplicantChart;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Paint;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.List;

public class QMController
{
	// Text Fields
	@FXML
	private TextField tf_UserInput;

	// Text Areas
	@FXML
	private TextArea ta_FuncToMinimize;

	// Table Columns
	@FXML
	private TableColumn<ObservableList<StringProperty>, String> tc_IDs;
	@FXML
	private TableColumn<ObservableList<StringProperty>, String> tc_Terms;
	@FXML
	private TableColumn<ObservableList<StringProperty>, String> tc_BitStrings;
	@FXML
	private TableColumn<ObservableList<StringProperty>, String> tc_Primes;

	// Table Views
	@FXML
	private TableView<MintermTableRow> tv_MintermTable;
	@FXML
	private TableView<ObservableList<StringProperty>> tv_PrimeImplicantChart;

	// List Views
	@FXML
	private ListView<String> lv_Solutions;

	// Labels
	@FXML
	private Label l_TotalSolutionCount;
	@FXML
	private Label l_PrimeImplicantChartTitle;
	@FXML
	private Label l_ProgressPercent;

	// Labels Programmatically Created
	private Label l_RunFail;
	private Label l_NoData;

	// Buttons
	@FXML
	private Button b_GenerateFunction;
	@FXML
	private Button b_RunAlgorithm;
	@FXML
	private Button b_EnterFunction;
	@FXML
	private Button b_Cancel;

	// Combo Boxes
	@FXML
	private ComboBox<Integer> cb_LiteralCount;
	@FXML
	private ComboBox<Integer> cb_TermCount;
	@FXML
	private ComboBox<Integer> cb_Steps;

	// Check Boxes
	@FXML
	private CheckBox cb_ShowReducedChart;

	// Progress Bar
	@FXML
	private ProgressBar pb_AlgorithmProgress;

	// Non-JavaFX Property Variables
	private ListProperty<String> prop_FinalSolutions = new SimpleListProperty<>();
	private ListProperty<Integer> prop_PossibleLiteralNums = new SimpleListProperty<>();
	private ListProperty<Integer> prop_PossibleTermNums = new SimpleListProperty<>();
	private ListProperty<Integer> prop_Steps = new SimpleListProperty<>();
	private BooleanProperty prop_RunFinished = new SimpleBooleanProperty();
	private DoubleProperty prop_WorkDone = new SimpleDoubleProperty();

	// Non-JavaFX Variables
	private int termMaxCount;
	private String funcToMinimize;
	private List<String> finalSolutions = new ArrayList<String>();
	private List<MintermTable> mintermTables = new ArrayList<MintermTable>();
	private Thread thread;
	private Boolean runFailed = false;

    /**
     * Constructor is called before initialize method.
     * Must remain a default constructor (no parameters) for load() call to work properly.
     */
    public QMController()
    {
    }

    /**
     * Initializes controller class. This method is automatically called after fxml file has been loaded.
     */
    @FXML
    private void initialize()
    {
    	l_RunFail = new Label("Unable to minimize Boolean Expression.\n(Reason: Maximum number of terms for Petrick's method exceeded.)");
    	l_RunFail.setTextFill(Paint.valueOf("#CC0000"));

    	l_NoData = new Label("");

    	// Disable GUI Items
    	b_EnterFunction.setDisable(true);
    	b_GenerateFunction.setDisable(true);
    	b_RunAlgorithm.setDisable(true);
    	b_Cancel.setDisable(true);
    	cb_TermCount.setDisable(true);

    	// Bind to Properties
    	cb_Steps.itemsProperty().bind(prop_Steps);
    	lv_Solutions.itemsProperty().bind(prop_FinalSolutions);
    	pb_AlgorithmProgress.progressProperty().bind(prop_WorkDone);

    	initLiteralChoiceBox();

    	// Set Table Column's data values
    	tc_IDs.setCellValueFactory(new PropertyValueFactory<>("termId"));
    	tc_Terms.setCellValueFactory(new PropertyValueFactory<>("termInRegForm"));
    	tc_BitStrings.setCellValueFactory(new PropertyValueFactory<>("termInBitForm"));
    	tc_Primes.setCellValueFactory(new PropertyValueFactory<>("isPrime"));

    	// Add user input listener
    	tf_UserInput.textProperty().addListener((observable, oldValue, newValue) ->
    	{
    		b_EnterFunction.setDisable(false);
    	});

    	// Add more work completed listener
    	prop_WorkDone.addListener((observable, oldValue, newValue) ->
    	{
    		oldValue = (double)oldValue * 100;
    		newValue = (double)newValue * 100;

    		String oldPerc = String.format("%.2f", oldValue);
    		String newPerc = String.format("%.2f", newValue);

    		// Check if new progress percentage has increased enough to update in GUI
    		if(!oldPerc.equals(newPerc))
    		{
    			Platform.runLater(() -> l_ProgressPercent.setText(newPerc + "%"));
    		}
    	});

    	// Add run finished executing listener
    	prop_RunFinished.addListener((observable, oldValue, newValue) ->
    	{
    		// Check if algorithm run was finished
    		if(newValue == true)
    		{
    			// Disable button since no run to stop
    			b_Cancel.setDisable(true);

    			l_TotalSolutionCount.setText("(" + finalSolutions.size() + " Total)");
    			prop_FinalSolutions.set(FXCollections.observableArrayList(finalSolutions));

    			// Check if run succeeded
    			if(!runFailed)
				{
    				prop_WorkDone.setValue(1.0);
				}
    			else // Either too complex or cancel button pressed
    			{
    				prop_WorkDone.setValue(0.0);
    			}

    	    	// Update contents inside Truth Tables tab
    	    	updateMintermTables();
    	    	updatePrimeImplicantCharts();

    	    	// Enable combo boxes to allow new run to be performed
    	    	cb_LiteralCount.setDisable(false);

    	    	// Check if literal count NOT selected (this means user input Bool expression)
    	    	if(!cb_LiteralCount.getSelectionModel().isEmpty())
    	    	{
    	    		cb_TermCount.setDisable(false);

    	    		// Check if term count has NOT been selected
    	    		if(!cb_TermCount.getSelectionModel().isEmpty())
        	    	{
    	    			b_GenerateFunction.setDisable(false);
        	    	}
    	    	}

    	    	// Enable buttons since run is complete
    	    	b_EnterFunction.setDisable(false);
    		}
    	});

    	// Add minterm table steps Listener
    	cb_Steps.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Integer>()
    	{
    		public void changed(ObservableValue<? extends Integer> ov, Integer oldValue, Integer newValue)
			{
    			// Check that new value selected from Combo Box is not null
    			// (occurs when Combo Box is cleared for a new run)
				if(newValue != null)
				{
					MintermTable table = mintermTables.get(newValue - 1);
					List<MintermTableRow> mintermTableRows = table.getRows();
					ObservableList<MintermTableRow> data = FXCollections.observableArrayList(mintermTableRows);
					tv_MintermTable.setItems(data);
				}
			}
        });

    	// Add Check Box show reduced chart listener
    	cb_ShowReducedChart.selectedProperty().addListener((observable, oldValue, newValue) ->
        {
            if(newValue)
            {
                l_PrimeImplicantChartTitle.setText("Reduced Prime Implicant Chart");
                updatePrimeImplicantCharts();
            }
            else
            {
                l_PrimeImplicantChartTitle.setText("Prime Implicant Chart");
                updatePrimeImplicantCharts();
            }
        });
    }

    /**
     * Initializes the literal count GUI choice box.
     */
    private void initLiteralChoiceBox()
    {
        // Local variables
    	int literalMaxCount = 8;
    	List<Integer> possibleLiteralNums = new ArrayList<>(0);

    	for(int i = 1; i <= literalMaxCount; i++)
    	{
    		possibleLiteralNums.add(i);
		}

    	prop_PossibleLiteralNums.set(FXCollections.observableArrayList(possibleLiteralNums));
    	cb_LiteralCount.itemsProperty().bind(prop_PossibleLiteralNums);

    	// Add literal count change listener
    	cb_LiteralCount.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Integer>()
    	{
    		public void changed(ObservableValue<? extends Integer> ov, Integer oldValue, Integer newValue)
			{
    			termMaxCount = (int)Math.pow(2, newValue.intValue());
    			cb_TermCount.setDisable(false);
    			initTermChoiceBox();
			}
        });
    }

    /**
     * Initializes the term count GUI choice box.
     */
    private void initTermChoiceBox()
    {
        // Local variable
    	List<Integer> possibleTermNums = new ArrayList<>(0);

    	for(int i = 1; i <= termMaxCount; i++)
    	{
    		possibleTermNums.add(i);
		}

    	prop_PossibleTermNums.set(FXCollections.observableArrayList(possibleTermNums));
    	cb_TermCount.itemsProperty().bind(prop_PossibleTermNums);

    	// Add term count change listener
    	cb_TermCount.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
    	{
            b_GenerateFunction.setDisable(false);
        });
    }

    /**
     * Handles what happens when the enter expression button is clicked.
     */
    @FXML
    private void handleEnterFunction()
    {
        // Local variable
    	String inputBoolExpression = "";

    	try
    	{
    		inputBoolExpression = sanitizeInput(tf_UserInput.getText());
    		funcToMinimize = inputBoolExpression;
        	ta_FuncToMinimize.setText(inputBoolExpression);
        	b_RunAlgorithm.setDisable(false);
        	prop_WorkDone.setValue(0.0);

        	// Check if any solutions have been generated previously
        	if(lv_Solutions.getItems() != null)
        	{
        		// Clear previous solutions
        		lv_Solutions.getItems().clear();
        	}
    	}
		catch (InvalidInputException ex)
		{
			// Set function to minimize to empty
			ta_FuncToMinimize.setText(inputBoolExpression);

			// Show user error found in custom Boolean expression
			Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(RootController.getPrimaryStage());
            alert.setTitle("QMBES");
            alert.setHeaderText("Invalid Boolean Expression");
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
		}
    }

    /**
     * Sanitizes an input Boolean expression.
     *
     * @param inputBoolExpression
     * 			- the Boolean expression to check for errors.
     * @return a valid Boolean expression.
     * @throws InvalidInputException
     */
    private String sanitizeInput(String inputBoolExpression) throws InvalidInputException
    {
		String validBoolExpression = Parser.checkInputForErrors(inputBoolExpression);

    	return validBoolExpression;
    }

    /**
     * Handles what happens when the generate function button is clicked.
     */
    @FXML
    private void handleGenerateFunction()
    {
    	int literalCount = (int)cb_LiteralCount.getSelectionModel().getSelectedItem();
    	int termCount = (int)cb_TermCount.getSelectionModel().getSelectedItem();

    	funcToMinimize = QuineMcCluskey.getRandomExpression(literalCount, termCount);
    	ta_FuncToMinimize.setText(funcToMinimize);
    	b_RunAlgorithm.setDisable(false);
    	prop_WorkDone.setValue(0.0);

    	// Check if any solutions have been generated previously
    	if(lv_Solutions.getItems() != null)
    	{
    		// Clear previous solutions
    		lv_Solutions.getItems().clear();
    	}
    }

    /**
     * Handles what happens when the run algorithm button is clicked.
     */
    @FXML
    private void handleRunAlgorithm()
    {
    	// Check if any solutions have been generated previously
    	if(lv_Solutions.getItems() != null)
    	{
    		// Clear previous solutions
    		lv_Solutions.getItems().clear();
    	}

    	lv_Solutions.setPlaceholder(l_NoData);

    	// Disable combo boxes
    	cb_LiteralCount.setDisable(true);
    	cb_TermCount.setDisable(true);

    	// Disable run button (no point in re-minimizing same function)
    	b_RunAlgorithm.setDisable(true);

    	// Disable buttons until run is complete
    	b_EnterFunction.setDisable(true);
    	b_GenerateFunction.setDisable(true);

    	// Enable button since new thread spawned for current run
    	b_Cancel.setDisable(false);

    	// Reset step combo box
    	if(cb_Steps.getItems() != null)
    	{
    		cb_Steps.getItems().clear();
    	}

    	thread = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				try
                {
                	// Set flag that run is in progress
                	prop_RunFinished.setValue(false);

                	try
                	{
                		// Run Quine McCluskey Algorithm and store solutions
                    	finalSolutions = QuineMcCluskey.runAlgorithm(funcToMinimize, prop_WorkDone);
                    	runFailed = false;
                	}
                	catch(DistributivePropertyException ex)
                	{
                		runFailed = true;
                		finalSolutions.clear();
                		Platform.runLater(() -> lv_Solutions.setPlaceholder(l_RunFail));

                		System.out.println(ex.getMessage());
                	}
                	finally
                	{
                		// Set flag that run has finished
                    	Platform.runLater(() -> prop_RunFinished.setValue(true));
                	}
                }
                catch (Exception ex)
                {
                    System.out.println("Error: " + ex.getMessage());
                    ex.printStackTrace();
                }

			}
		});
    	thread.setDaemon(true);
    	thread.start();
    }

    /**
     * Updates the minterm tables with data from latest algorithm run.
     */
    private void updateMintermTables()
    {
    	// Reset lists for new run
    	tv_MintermTable.getItems().clear();
    	mintermTables.clear();

    	// Update steps ComboBox in Truth Tables tab
    	List<Integer> steps = QuineMcCluskey.getSteps();
    	prop_Steps.set(FXCollections.observableArrayList(steps));

    	List<Group> groups = QuineMcCluskey.getGroups();

    	// Loop through all groups
    	for(int i = 0; i < groups.size(); i++)
    	{
    		MintermTable table = new MintermTable();
    		List<Term> terms = groups.get(i).getTerms();

    		// Loop through all terms
    		for(int j = 0; j < terms.size(); j++)
    		{
    			String id = terms.get(j).getId();
    			String regForm = terms.get(j).getRegularForm();
    			String bitForm = terms.get(j).getBitForm();

    			// If term NOT used to form a combined term then it is Prime
    			boolean isPrime = !terms.get(j).wasUsed();

    			// Add row to new table
    			table.addRow(new MintermTableRow(id, regForm, bitForm, isPrime));
    		}

    		 // Add new table to overall truth table list
    		mintermTables.add(table);
    	}
    }

    /**
     * Updates both the prime implicant and (if available) reduced prime implicant chart
     * with data from latest algorithm run.
     */
    private void updatePrimeImplicantCharts()
    {
    	PrimeImplicantChart primeChart = QuineMcCluskey.getPrimeImplicantChart();

    	// Check if any data available to display for chart
    	if(primeChart == null)
    	{
    		return; // Exit because haven't run algorithm yet so no data to show
    	}

    	// Reset Table View
    	tv_PrimeImplicantChart.getItems().clear();
    	tv_PrimeImplicantChart.getColumns().clear();

    	List<Integer> ids;
    	List<Term> primes;
    	char[][] primeGrid;
    	PrimeImplicantChart reducedPrimeChart;

    	// Check if reduced prime implicant chart is to be displayed
    	if(cb_ShowReducedChart.isSelected())
    	{
    		l_PrimeImplicantChartTitle.setText("Reduced Prime Implicant Chart");
    		reducedPrimeChart = QuineMcCluskey.getReducedPrimeImplicantChart();

    		if(reducedPrimeChart == null)
        	{
    			tv_PrimeImplicantChart.setPlaceholder(
    					new Label("Prime Implicant Chart could not be simplified.\n(Reason: no essential primes.)"));
        		return; // Exit because no reduced chart to show
        	}

    		ids = reducedPrimeChart.getIDs();
        	primes = reducedPrimeChart.getPrimes();
        	primeGrid = reducedPrimeChart.getPrimeGrid();
    	}
    	else // Show non-reduced prime implicant chart
    	{
    		l_PrimeImplicantChartTitle.setText("Prime Implicant Chart");

    		ids = primeChart.getIDs();
        	primes = primeChart.getPrimes();
        	primeGrid = primeChart.getPrimeGrid();
    	}

    	// Add prime column
    	tv_PrimeImplicantChart.getColumns().add(createColumn(0, "Prime Term", 100));

    	// Loop through all ID's and add new column in Table View for each
    	for(int idIndex = 0; idIndex < ids.size(); idIndex++)
    	{
    		int colIndex = idIndex + 1; // Offset 1 column due to primes column at index 0

    		// Add new ID column to Table View
    		TableColumn<ObservableList<StringProperty>, String> newIdCol =
    				createColumn(colIndex, Integer.toString(ids.get(idIndex)), 30);
    		newIdCol.setStyle("-fx-alignment: CENTER");
    		tv_PrimeImplicantChart.getColumns().add(newIdCol);
    	}

        // Loop through all rows of prime implicant chart
        for (int row = 0; row < primeGrid.length; row++)
        {
        	ObservableList<StringProperty> rowData = FXCollections.observableArrayList();
        	rowData.add(new SimpleStringProperty(primes.get(row).getRegularForm()));

        	// Loop through all columns of current row
			for(int col = 0; col < primeGrid[0].length; col++)
			{
				rowData.add(new SimpleStringProperty("" + primeGrid[row][col]));
			}

			tv_PrimeImplicantChart.getItems().add(rowData);
        }
    }

    /**
     * Creates a table column for a prime implicant chart.
     *
     * @param columnIndex
     * 			the column index in the Table View.
     * @param columnTitle
     * 			the column title.
     * @param prefWidth
     * 			the preferred width.
     * @return a new table column with the proper specifications.
     */
    private TableColumn<ObservableList<StringProperty>, String> createColumn(final int columnIndex, String columnTitle,
                                                                             int prefWidth)
    {
		TableColumn<ObservableList<StringProperty>, String> column = new TableColumn<>();

		// Set column attributes
		column.setText(columnTitle);
		column.setPrefWidth(prefWidth);
		column.setEditable(false);
		column.setSortable(false);
		column.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList<StringProperty>,String>,
                ObservableValue<String>>()
		{
			@Override
			public ObservableValue<String> call(CellDataFeatures<ObservableList<StringProperty>,
				  String> cellDataFeatures)
			{
				ObservableList<StringProperty> values = cellDataFeatures.getValue();
				if (columnIndex >= values.size())
				{
					return new SimpleStringProperty("");
				}
				else
				{
					return cellDataFeatures.getValue().get(columnIndex);
				}
			}
	    });

		return column;
    }

    /**
     * Handles what happens when the cancel button is clicked during a run which is in-progress.
     */
    @SuppressWarnings("deprecation")
	@FXML
    private void handleCancelRun()
    {
    	// Stop thread
        // Only 1 active thread at any given time so this is OK (no need to worry about deadlock)
    	thread.stop();

    	// Disable cancel run button
    	b_Cancel.setDisable(true);

    	// Enable combo boxes to allow new run to be performed
    	cb_LiteralCount.setDisable(false);
    	cb_TermCount.setDisable(false);

    	// Enable buttons to allow new run to be performed
    	b_EnterFunction.setDisable(false);
    	b_GenerateFunction.setDisable(false);
    	b_RunAlgorithm.setDisable(false);

    	runFailed = true;
    }
}