package gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import binpacking_models.BinPackingSolution;
import binpacking_models.GeometricBinPackingSolution;
import binpacking_models.PermutationBinPackingSolution;
import generators.RectangleGenerator;
import geometric_models.BinPackingRectangle;
import geometric_models.Box;
import interactive_algorithms.InteractiveBinPackingLocalSearch;
import interactive_algorithms.SimulatedAnnealing;
import interfaces.InteractiveBinPackingSolver;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import neighborhoods.GeometricNeighborhood;
import neighborhoods.PermutationNeighborhood;

public class MainWindowController {

	private RectangleGenerator rg;
	private InteractiveBinPackingSolver solver;

	@FXML
	private MenuItem newInstanceButton = new MenuItem();
	@FXML
	private Label currentInstanceInfo;
	@FXML
	private Canvas canvas;
	@FXML
	private Button nextIterationButton;
	private int iteration;

	private BinPackingSolution initialSolution;
	private BinPackingRectangle[] rectangles;
	private NewInstanceParameters instanceParameters;

	public void openNewInstancePane() {

		Dialog<NewInstanceParameters> dialog = new Dialog<>();
		dialog.setTitle("Enter values for new instance");
		ButtonType okButtonType = new ButtonType("Done", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);
		TextField boxLengthField = new TextField();
		TextField rectCountField = new TextField();
		TextField minLengthField = new TextField();
		TextField maxLengthField = new TextField();

		Label boxLengthLabel = new Label("Box length: ");
		Label rectCountLabel = new Label("BinPackingRectangle count: ");
		Label minLengthLabel = new Label("Min rectangle sinde-length: ");
		Label maxLengthLabel = new Label("Max rectangle sinde-length: ");
		
		GridPane gridPane = new GridPane();
		gridPane.add(boxLengthField, 1, 0);
		gridPane.add(boxLengthLabel, 0, 0);
		gridPane.add(rectCountField, 1, 1);
		gridPane.add(rectCountLabel, 0, 1);
		gridPane.add(minLengthField, 1, 2);
		gridPane.add(minLengthLabel, 0, 2);
		gridPane.add(maxLengthField, 1, 3);
		gridPane.add(maxLengthLabel, 0, 3);
		dialog.getDialogPane().setContent(gridPane);
		Platform.runLater(() -> boxLengthField.requestFocus());
		dialog.setResultConverter(dialogButton -> {
			if (dialogButton == okButtonType) {
				return new NewInstanceParameters(Integer.parseInt(boxLengthField.getText()),
						Integer.parseInt(rectCountField.getText()), Integer.parseInt(minLengthField.getText()),
						Integer.parseInt(maxLengthField.getText())

				);
			}
			return null;
		});

		Optional<NewInstanceParameters> result = dialog.showAndWait();
		result.ifPresent(parameters -> {
			instanceParameters = parameters;
			currentInstanceInfo.setText("Current Instance: BoxLength=" + parameters.boxLength + ", RectangleCount="
					+ parameters.rectangleCount + ", minRectSideLenth=" + parameters.minSideLength
					+ ", maxRectSideLenth=" + parameters.maxSideLength);

			// Generate new instance

			RectangleGenerator generator = new RectangleGenerator();
			rectangles = generator.generateRandomly(parameters.rectangleCount, parameters.minSideLength,
					parameters.maxSideLength);

		});
	}

	public void initializeStandardGeometricLocalSearch() {
		initialSolution = new GeometricBinPackingSolution(new ArrayList<BinPackingRectangle>(Arrays.asList(rectangles)),
				instanceParameters.boxLength, 0);
		solver = new InteractiveBinPackingLocalSearch(initialSolution, new GeometricNeighborhood(initialSolution));

		iteration = 0;
		drawSolution(initialSolution);
	}

	
	public void initializeStandardGeometricSimulatedAnnealing() {
		initialSolution = new GeometricBinPackingSolution(new ArrayList<BinPackingRectangle>(Arrays.asList(rectangles)),
				instanceParameters.boxLength, 0);
		solver =  new SimulatedAnnealing(initialSolution, new GeometricNeighborhood(initialSolution));

		iteration = 0;
		drawSolution(initialSolution);
	}

	
	public void initializeStandardGeometricTabooSearch() {
		initialSolution = new GeometricBinPackingSolution(new ArrayList<BinPackingRectangle>(Arrays.asList(rectangles)),
				instanceParameters.boxLength, 0);
		solver = new InteractiveBinPackingLocalSearch(initialSolution, new GeometricNeighborhood(initialSolution));

		iteration = 0;
		drawSolution(initialSolution);
	}

	public void initializeStandardPermutationLocalSearch() {
		initialSolution = new PermutationBinPackingSolution(new ArrayList<BinPackingRectangle>(Arrays.asList(rectangles)),
				instanceParameters.boxLength, 0);
		solver = new InteractiveBinPackingLocalSearch(initialSolution,
				new PermutationNeighborhood(instanceParameters.boxLength));

		iteration = 0;
		drawSolution(initialSolution);
	}

	private void drawSolution(BinPackingSolution s) {
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		
		int multiplier = 30;
		int boxIndex = 0;

		for (Box b : s.boxes) {
			System.out.println("Width: "+ canvas.getWidth() + "Height: " + canvas.getHeight() +
					"/n Area: " + canvas.getWidth()* canvas.getHeight() + 
					"/n per box: " + (canvas.getWidth()* canvas.getHeight())/(s.boxes.size()));
			gc.setStroke(Color.BLUE);
			gc.setLineWidth(1);
			gc.strokeRect(boxIndex * multiplier * b.getLength() + boxIndex * 10, 0, b.getLength() * multiplier,
					b.getLength() * multiplier);

			for (BinPackingRectangle r : b.getRectangles()) {
				gc.setFill(Color.WHITE);
				gc.fillRect(boxIndex * multiplier * b.getLength() + 1 + r.getX() * multiplier + +boxIndex * 10,
						1 + r.getY() * multiplier, r.getWidth() * multiplier, r.getHeight() * multiplier);
				gc.setFill(r.highlight ? Color.YELLOW : Color.GREEN);
				gc.fillRect(boxIndex * multiplier * b.getLength() + 2 + r.getX() * multiplier + +boxIndex * 10,
						2 + r.getY() * multiplier, r.getWidth() * multiplier - 1, r.getHeight() * multiplier - 1);
			}
			boxIndex++;
		}
	}

	public void drawNextIteration() {
		if ((BinPackingSolution) solver.getCurrentSolution() != null) {
			iteration++;
			if (iteration % 3 == 1 && (BinPackingSolution) solver.getNextSolution() != null) {
				for (BinPackingRectangle r1 : ((BinPackingSolution) solver.getNextSolution()).rectangles) {
					if (r1.highlight) {
						for (BinPackingRectangle r2 : ((BinPackingSolution) solver.getCurrentSolution()).rectangles) {
							if (r1.getId() == r2.getId()) {
								r2.highlight = true;
							}
						}
					}
				}
				drawSolution((BinPackingSolution) solver.getCurrentSolution());
			} else if (iteration % 3 == 2 && (BinPackingSolution) solver.getNextSolution() != null) {
				drawSolution((BinPackingSolution) solver.getNextSolution());
			} else {
				solver.performNextStep();
				for (BinPackingRectangle r2 : ((BinPackingSolution) solver.getCurrentSolution()).rectangles) {
					r2.highlight = false;
				}
				drawSolution((BinPackingSolution) solver.getCurrentSolution());
			}
			if((BinPackingSolution) solver.getNextSolution() == null){
		
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Information Dialog");
				alert.setHeaderText(null);
				alert.setContentText("finished");
				alert.showAndWait();
				
			}
		}
	}
}
