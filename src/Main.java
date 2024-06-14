
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.util.Duration;

public class Main extends Application {

	File file;

	double[][] dislikeMatrix;

	String[] names;
	List<String> arrangementList = new ArrayList<String>();
	List<Double> costList = new ArrayList<Double>();
	List<VBox> chairs;
	Label costLabel = new Label();

	public void start(Stage stage) throws Exception {
		BorderPane bp = new BorderPane();

		Button DevelopersBtn = new Button("Developers");
		bp.setTop(DevelopersBtn);

		ImageView logoTopView = new ImageView(new Image("bzuLogo.png"));
		logoTopView.setPreserveRatio(true);
		logoTopView.setFitHeight(289.5 / 4);
		logoTopView.setFitWidth(422.5 / 4);
		logoTopView.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> {
			Glow glow = new Glow();
			glow.setLevel(1.0);

			ColorAdjust colorAdjust = new ColorAdjust();
			colorAdjust.setBrightness(0.2);
			colorAdjust.setContrast(0.3);
			colorAdjust.setSaturation(0.6);
			colorAdjust.setHue(0.08);

			glow.setInput(colorAdjust);
			logoTopView.setEffect(glow);
		});
		logoTopView.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
			logoTopView.setEffect(null);
		});

		ColorAdjust colorAdjustTop = new ColorAdjust();
		colorAdjustTop.setHue(-0.05);
		colorAdjustTop.setBrightness(0.2);
		colorAdjustTop.setSaturation(-0.3);
		logoTopView.setEffect(colorAdjustTop);

		HBox topBox = new HBox(logoTopView);
		topBox.setAlignment(Pos.TOP_LEFT);
		bp.setTop(topBox);

		ImageView logoView = new ImageView(new Image("table3.png"));
		logoView.setStyle(
				"-fx-border-color: black; -fx-border-width: 5; -fx-border-radius: 100; -fx-background-radius: 100; -fx-padding: 10;");

		logoView.setFitHeight(370.5);
		logoView.setFitWidth(370.5);

		logoView.setRotate(360);

		logoView.setEffect(getDropShadow());

		addZoomEffect(logoView, 0.1, 1.0, 1500);

		RotateTransition rotateTransition = new RotateTransition(Duration.seconds(1.5), logoView);
		rotateTransition.setByAngle(360);
		rotateTransition.setCycleCount(1);
		rotateTransition.setAutoReverse(false);

		rotateTransition.play();

		Image seatingImage = new Image("seating4.png");
		Image arrangementImage = new Image("arrangement4.png");
		ImageView seatingImageView = new ImageView(seatingImage);
		ImageView arrangementImageView = new ImageView(arrangementImage);

		seatingImageView.setFitHeight(seatingImage.getHeight() / 5.4);
		seatingImageView.setFitWidth(seatingImage.getWidth() / 5.4);
		arrangementImageView.setFitHeight(arrangementImage.getHeight() / 4.8);
		arrangementImageView.setFitWidth(arrangementImage.getWidth() / 4.8);

		VBox mainText = new VBox(28, seatingImageView, arrangementImageView);
		mainText.setAlignment(Pos.CENTER);
		StackPane stackPane = new StackPane(logoView, mainText);

		stackPane.setAlignment(Pos.CENTER);
		bp.setCenter(stackPane);

		SequentialTransition sequentialTransition = new SequentialTransition();
		addZoomEffect(seatingImageView, 0.1, 1.0, 700, sequentialTransition);
		addZoomEffect(arrangementImageView, 0.1, 1.0, 700, sequentialTransition);

		sequentialTransition.play();

		Button loadDislike = new Button("Load The Dislike Matrix File");
		HBox options = new HBox(7, loadDislike);

		options.setAlignment(Pos.CENTER);
		bp.setBottom(options);
		Scene scene = new Scene(bp, 1200, 700);

		bp.setPadding(new Insets(15, 15, 15, 15));
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("File Chooser");

		ExtensionFilter filterTXT = new ExtensionFilter("Text files", "*txt");

		fileChooser.getExtensionFilters().addAll(filterTXT);

		RadioButton hillClimbingButton = new RadioButton("Hill Climbing");
		RadioButton genaticButton = new RadioButton("Genatic Algorithim");
		RadioButton simulatedButton = new RadioButton("Simulated Annealing");
		String style = "-fx-background-color: #322C2B; " + "-fx-text-fill: #E4C59E; " + "-fx-background-radius: 20; "
				+ "-fx-padding: 0.5em 1em; " + "-fx-font-size: 20px;";
		hillClimbingButton.setStyle(style);
		genaticButton.setStyle(style);
		simulatedButton.setStyle(style);

		ToggleGroup tg = new ToggleGroup();
		hillClimbingButton.setToggleGroup(tg);
		genaticButton.setToggleGroup(tg);
		simulatedButton.setToggleGroup(tg);

		hillClimbingButton.setOnAction(e -> {


			arrangementList.clear();
			
			Solution solution = SearchAlgorithms.hillClimbing(dislikeMatrix, 100);
			int[] bestArragnement = solution.getArrangement();
			
			for (int i = 0; i < bestArragnement.length; i++)
				arrangementList.add(names[bestArragnement[i]]);
			
			double totalCost = solution.getCost();

			totalCost = Double.parseDouble(String.format("%.2f", totalCost));

			if (stackPane.getChildren().contains(costLabel))
				stackPane.getChildren().remove(costLabel);

			costLabel.setText("Hill Climbing : " + totalCost);
			costLabel.setStyle(
					"-fx-background-color: #AF8260; -fx-padding: 5px; -fx-border-color: #E4C59E; -fx-border-width: 2px; -fx-border-radius: 5px; -fx-background-radius: 5px; -fx-text-fill: white;");

			costLabel.setAlignment(Pos.CENTER);

			RotateTransition rt = new RotateTransition(Duration.seconds(1), logoView);
			rt.setByAngle(360);
			rt.setInterpolator(Interpolator.LINEAR);

			if (chairs != null)
				stackPane.getChildren().removeAll(chairs);

			chairs = addChairsAroundTable(stackPane, arrangementList);
			stackPane.getChildren().add(costLabel);

			ParallelTransition parallelTransition = new ParallelTransition();
			parallelTransition.getChildren().add(rt);

			ScaleTransition st = new ScaleTransition(Duration.seconds(1), costLabel);
			st.setFromX(0);
			st.setFromY(0);
			st.setToX(1);
			st.setToY(1);
			st.setInterpolator(Interpolator.EASE_OUT);
			parallelTransition.getChildren().add(st);

			for (VBox chair : chairs) {
				st = new ScaleTransition(Duration.seconds(1), chair);
				st.setFromX(0);
				st.setFromY(0);
				st.setToX(1);
				st.setToY(1);
				st.setInterpolator(Interpolator.EASE_OUT);
				parallelTransition.getChildren().add(st);
			}

			parallelTransition.play();
		});
		
		genaticButton.setOnAction(e -> {


			arrangementList.clear();
			
			Solution solution = SearchAlgorithms.geneticAlgorithm(dislikeMatrix, 100, 1000, 0.1);
			int[] bestArragnement = solution.getArrangement();
			
			for (int i = 0; i < bestArragnement.length; i++)
				arrangementList.add(names[bestArragnement[i]]);
			
			double totalCost = solution.getCost();

			totalCost = Double.parseDouble(String.format("%.2f", totalCost));

			if (stackPane.getChildren().contains(costLabel))
				stackPane.getChildren().remove(costLabel);

			costLabel.setText("Genatic Algorithm : " + totalCost);
			costLabel.setStyle(
					"-fx-background-color: #AF8260; -fx-padding: 5px; -fx-border-color: #E4C59E; -fx-border-width: 2px; -fx-border-radius: 5px; -fx-background-radius: 5px; -fx-text-fill: white;");

			costLabel.setAlignment(Pos.CENTER);

			RotateTransition rt = new RotateTransition(Duration.seconds(1), logoView);
			rt.setByAngle(360);
			rt.setInterpolator(Interpolator.LINEAR);

			if (chairs != null)
				stackPane.getChildren().removeAll(chairs);

			chairs = addChairsAroundTable(stackPane, arrangementList);
			stackPane.getChildren().add(costLabel);

			ParallelTransition parallelTransition = new ParallelTransition();
			parallelTransition.getChildren().add(rt);

			ScaleTransition st = new ScaleTransition(Duration.seconds(1), costLabel);
			st.setFromX(0);
			st.setFromY(0);
			st.setToX(1);
			st.setToY(1);
			st.setInterpolator(Interpolator.EASE_OUT);
			parallelTransition.getChildren().add(st);

			for (VBox chair : chairs) {
				st = new ScaleTransition(Duration.seconds(1), chair);
				st.setFromX(0);
				st.setFromY(0);
				st.setToX(1);
				st.setToY(1);
				st.setInterpolator(Interpolator.EASE_OUT);
				parallelTransition.getChildren().add(st);
			}

			parallelTransition.play();
		});
		
		simulatedButton.setOnAction(e -> {


			arrangementList.clear();
			
			Solution solution = SearchAlgorithms.simulatedAnnealing(dislikeMatrix, 1000, 0.99, 10000);
			int[] bestArragnement = solution.getArrangement();
			
			for (int i = 0; i < bestArragnement.length; i++)
				arrangementList.add(names[bestArragnement[i]]);
			
			double totalCost = solution.getCost();

			totalCost = Double.parseDouble(String.format("%.2f", totalCost));

			if (stackPane.getChildren().contains(costLabel))
				stackPane.getChildren().remove(costLabel);

			costLabel.setText("Simulated Annealing : " + totalCost);
			costLabel.setStyle(
					"-fx-background-color: #AF8260; -fx-padding: 5px; -fx-border-color: #E4C59E; -fx-border-width: 2px; -fx-border-radius: 5px; -fx-background-radius: 5px; -fx-text-fill: white;");

			costLabel.setAlignment(Pos.CENTER);

			RotateTransition rt = new RotateTransition(Duration.seconds(1), logoView);
			rt.setByAngle(360);
			rt.setInterpolator(Interpolator.LINEAR);

			if (chairs != null)
				stackPane.getChildren().removeAll(chairs);

			chairs = addChairsAroundTable(stackPane, arrangementList);
			stackPane.getChildren().add(costLabel);

			ParallelTransition parallelTransition = new ParallelTransition();
			parallelTransition.getChildren().add(rt);

			ScaleTransition st = new ScaleTransition(Duration.seconds(1), costLabel);
			st.setFromX(0);
			st.setFromY(0);
			st.setToX(1);
			st.setToY(1);
			st.setInterpolator(Interpolator.EASE_OUT);
			parallelTransition.getChildren().add(st);

			for (VBox chair : chairs) {
				st = new ScaleTransition(Duration.seconds(1), chair);
				st.setFromX(0);
				st.setFromY(0);
				st.setToX(1);
				st.setToY(1);
				st.setInterpolator(Interpolator.EASE_OUT);
				parallelTransition.getChildren().add(st);
			}

			parallelTransition.play();
		});




		loadDislike.setOnAction(e -> {
			file = fileChooser.showOpenDialog(stage);
			try {
				readMatrix();
				
				if (file == null) {
					throw new FileNotFoundException();
				}

				zoomOutAndRemove(seatingImageView, stackPane, 300);
				zoomOutAndRemove(arrangementImageView, stackPane, 300);

				applyScaleTransition(loadDislike, 1.0, 1.0, 0.0, 0.0, 300, event -> {
					options.getChildren().remove(loadDislike);

					options.getChildren().addAll(hillClimbingButton, genaticButton, simulatedButton);

					Node[] buttons = {hillClimbingButton, genaticButton, simulatedButton };
					for (Node button : buttons) {
						applyScaleTransition(button, 0.0, 0.0, 1.0, 1.0, 300, null);
					}
				});
			} catch (FileNotFoundException e2) {
			} catch (Exception e2) {

				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Error");
				alert.setHeaderText("Invalid File Format");
				alert.showAndWait();
			}

		});

		scene.getStylesheets().add("LightMode.css"); 
		stage.setScene(scene); 
		stage.setTitle("Seating Arrangement"); 
		stage.setMaximized(true);
		stage.show(); 
	}

	public static void main(String[] args) {
		launch(args);
	}

	// Read The matrix from a specific file
	private void readMatrix() throws IOException {
		if (file == null)
			throw new FileNotFoundException();

		Scanner sc = new Scanner(file);

		String[] firstLineString = sc.nextLine().trim().split("\t");
		names = firstLineString;

		dislikeMatrix = new double[names.length][names.length];
		int i = 0;
		while (sc.hasNext()) {
			String[] tkz = sc.nextLine().trim().split("\t");
			for (int j = 1; j <= names.length; j++)
				dislikeMatrix[i][j - 1] = Double.parseDouble(tkz[j]);
			i++;
		}
	}

	private void addZoomEffect(ImageView imageView, double fromScale, double toScale, int durationMillis,
			SequentialTransition sequentialTransition) {
		ScaleTransition st = new ScaleTransition(Duration.millis(durationMillis), imageView);
		st.setFromX(fromScale);
		st.setFromY(fromScale);
		st.setToX(toScale);
		st.setToY(toScale);

		sequentialTransition.getChildren().add(st);
	}

	private Effect getDropShadow() {
		DropShadow dropShadow = new DropShadow();
		dropShadow.setOffsetX(5);
		dropShadow.setOffsetY(5);
		dropShadow.setColor(Color.rgb(50, 50, 50, 0.5));
		return dropShadow;
	}

	private void addZoomEffect(ImageView imageView, double fromScale, double toScale, int durationMillis) {
		ScaleTransition st = new ScaleTransition(Duration.millis(durationMillis), imageView);
		st.setFromX(fromScale);
		st.setFromY(fromScale);
		st.setToX(toScale);
		st.setToY(toScale);
		st.play();
	}

	private void applyScaleTransition(Node node, double fromX, double fromY, double toX, double toY, long duration,
			EventHandler<ActionEvent> onFinish) {
		ScaleTransition st = new ScaleTransition(Duration.millis(duration), node);
		st.setFromX(fromX);
		st.setFromY(fromY);
		st.setToX(toX);
		st.setToY(toY);
		st.setOnFinished(onFinish);
		st.play();
	}

	private void zoomOutAndRemove(Node node, StackPane container, int durationMillis) {
		ScaleTransition st = new ScaleTransition(Duration.millis(durationMillis), node);
		st.setFromX(1.0);
		st.setFromY(1.0);
		st.setToX(0.0);
		st.setToY(0.0);
		st.setOnFinished(e -> container.getChildren().remove(node));
		st.play();
	}

	private List<VBox> addChairsAroundTable(StackPane container, List<String> names) {
		List<VBox> chairContainers = new ArrayList<>();
		int numChairs = names.size();
		double radius = 220; 

		double angleStep = 360.0 / numChairs;

		for (int i = 0; i < numChairs; i++) {
			Image chairImage = new Image("chair.png");
			ImageView chair = new ImageView(chairImage);
			Label nameLabel = new Label(names.get(i));
			nameLabel.setStyle(
					"-fx-background-color: #AF8260; -fx-padding: 5px; -fx-border-color: #E4C59E; -fx-border-width: 2px; -fx-border-radius: 5px; -fx-background-radius: 5px; -fx-text-fill: white;");

			VBox vBox = new VBox(nameLabel, chair);
			vBox.setAlignment(Pos.CENTER);
			chair.setFitWidth(chairImage.getWidth() / 4.2);
			chair.setFitHeight(chairImage.getHeight() / 4.2);

			double angle = angleStep * i;
			double radian = Math.toRadians(angle);

			vBox.setTranslateX(-radius * Math.sin(radian));
			vBox.setTranslateY(radius * Math.cos(radian));
			vBox.setRotate(angle - 360);
			vBox.setOpacity(0); // Start invisible for fade in

			FadeTransition ft = new FadeTransition(Duration.seconds(1), vBox);
			ft.setToValue(1);
			ft.play();

			container.getChildren().add(vBox);
			chairContainers.add(vBox);
		}
		return chairContainers;
	}

}
