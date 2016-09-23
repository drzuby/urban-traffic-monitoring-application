package pl.edu.agh.pp.charts.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import pl.edu.agh.pp.charts.Main;
import pl.edu.agh.pp.charts.input.Input;
import pl.edu.agh.pp.charts.input.ResourcesHolder;
import pl.edu.agh.pp.charts.parser.Parser;

import java.io.File;
import java.util.*;

/**
 * Created by Dawid on 2016-05-20.
 */
public class ChartsController {
    private Stage primaryStage = null;
    private FileChooser fileChooser = null;
    private Parser parser;
    private Input input;
    private Set<Integer> idsSet = new HashSet<>();
    private Set<String> datesSet = new HashSet<>();
    private ObservableList<String> daysList = FXCollections.observableArrayList();
    private ObservableList<String> idsList = FXCollections.observableArrayList();
    private ObservableList<String> typesList = FXCollections.observableArrayList();
    private MainWindowController parent;
    @FXML
    private LineChart<Number, Number> lineChart;
    @FXML
    private Button fileButton;
    @FXML
    private Button startButton;
    @FXML
    private Label warn;
    @FXML
    private CheckBox durationCheckBox;
    @FXML
    private ComboBox<String> idComboBox;
    @FXML
    private ComboBox<String> dayComboBox;
    @FXML
    private CheckBox clearCheckBox;
    @FXML
    private ComboBox<String> typeComboBox;
    @FXML
    private Button button134;
    @FXML
    private Button button578;
    @FXML
    private Button reverseRouteButton;
    @FXML
    private Label logsListLabel;
    public ChartsController(Stage primaryStage, MainWindowController parent) {
        this.primaryStage = primaryStage;
        this.parent = parent;
    }

    public void show() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("/Charts.fxml"));
            loader.setController(this);
            BorderPane rootLayout = loader.load();

            primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                public void handle(WindowEvent we) {
                    if (input != null) {
                        input.cleanUp();
                    }
                }
            });
            primaryStage.setTitle("Urban traffic monitoring - charts");
            Scene scene = new Scene(rootLayout);
            scene.getStylesheets().add(Main.class.getResource("/chart.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void initialize() {
        fileChooser = new FileChooser();
        parser = new Parser();
        input = new Input();
        File file = new File("./");
        fileChooser.setInitialDirectory(file);
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Log Files", "*.log"));
        warn.setStyle("-fx-text-fill: red");
        lineChart.setTitle("");
        startButton.setDefaultButton(true);
        clearCheckBox.setSelected(true);
        typeComboBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                String value = typeComboBox.getSelectionModel().getSelectedItem();
                if (value.equals("Exact date")) {
                    fillInDates();
                } else if (value.equals("Aggregated day of week")) {
                    fillInDaysOfWeek();
                }
            }
        });
        Image reverseButtonImage = new Image(Main.class.getResourceAsStream("/reverse.png"));
        reverseRouteButton.setGraphic(new ImageView(reverseButtonImage));
        setLogsLabel();
    }

    @FXML
    private void handleFileButtonAction(ActionEvent e) {
        List<File> files = fileChooser.showOpenMultipleDialog(primaryStage);
        if (files == null || files.isEmpty()) {
            return;
        }
        input.getRoutes();
        for (File file : files) {
            ResourcesHolder.getInstance().addLog(file.getName());
            parser.setFile(file);
            parser.parse(input);
        }

        idsList = FXCollections.observableArrayList();
        idComboBox.setItems(idsList);
        for (String id : input.getIds()) {
            idsSet.add(Integer.parseInt(id));
        }
        List<Integer> ids = new ArrayList<>(idsSet);
        Collections.sort(ids);
        for (Integer id : ids) {
            idsList.add(input.getRoute(String.valueOf(id)));
        }
        idComboBox.setItems(idsList);

        if (typesList.size() != 2) {
            typesList.add("Exact date");
            typesList.add("Aggregated day of week");
        }
        typeComboBox.setItems(typesList);

        setLogsLabel();
    }

    @FXML
    private void handleBackButtonAction(ActionEvent e) {
        parent.show();
    }

    private void fillInDates() {
        clearDayComboBox();
        for (String day : ResourcesHolder.getInstance().getDays()) {
            datesSet.add(day);
        }
        List<String> dates = new ArrayList<>(datesSet);
        Collections.sort(dates);
        for (String date : dates) {
            daysList.add(date);
        }
        dayComboBox.setItems(daysList);
        int size = daysList.size();
        dayComboBox.setVisibleRowCount(size < 8 ? size : 7);
    }

    private void fillInDaysOfWeek() {
        clearDayComboBox();
        daysList.add("Monday");
        daysList.add("Tuesday");
        daysList.add("Wednesday");
        daysList.add("Thursday");
        daysList.add("Friday");
        daysList.add("Saturday");
        daysList.add("Sunday");
        dayComboBox.setItems(daysList);
        dayComboBox.setVisibleRowCount(7);
    }

    private void setLogsLabel() {
        String tmp = "Chosen log files:\n";
        if (ResourcesHolder.getInstance().getLogs().isEmpty()) {
            tmp += "NONE";
        } else {
            for (String log : ResourcesHolder.getInstance().getLogs()) {
                tmp += log + "\n";
            }
        }
        logsListLabel.setText(tmp);
    }

    private void clearDayComboBox() {
        daysList = FXCollections.observableArrayList();
        dayComboBox.setItems(daysList);
    }

    private void createTooltips() {
        for (XYChart.Series<Number, Number> s : lineChart.getData()) {
            for (XYChart.Data<Number, Number> d : s.getData()) {
                double num = (double) d.getXValue();
                long iPart;
                double fPart;
                iPart = (long) num;
                fPart = num - iPart;
                Tooltip.install(d.getNode(), new Tooltip("Time of the day: " + iPart + "h " + (long) (fPart * 60) + "min" + "\nDuration: " + d.getYValue().toString() + " seconds"));

                //Adding class on hover
                d.getNode().setOnMouseEntered(event -> d.getNode().getStyleClass().add("onHover"));

                //Removing class on exit
                d.getNode().setOnMouseExited(event -> d.getNode().getStyleClass().remove("onHover"));
            }
        }
    }

    @FXML
    private void handleStartAction(ActionEvent e) {
        if (dayComboBox.getSelectionModel().getSelectedItem() == null
                || idComboBox.getSelectionModel().getSelectedItem() == null
                || typeComboBox.getSelectionModel().getSelectedItem() == null) {
            warn.setText("Select all parameters");
            return;
        }
        warn.setText("");
        if (clearCheckBox.isSelected()) {
            lineChart.getData().clear();
        }

        XYChart.Series<Number, Number> seriesDurationInTraffic = new XYChart.Series<>();
        XYChart.Series<Number, Number> seriesDuration = new XYChart.Series<>();

        String type = typeComboBox.getSelectionModel().getSelectedItem();
        String day = dayComboBox.getSelectionModel().getSelectedItem();
        String id = input.getId(idComboBox.getSelectionModel().getSelectedItem());
        Map<Double, Double> trafficValues = null;
        Map<Double, Double> normalValues = null;
        if (type.equals("Exact date")) {
            trafficValues = input.getData(day, id, true, false);
            if (durationCheckBox.isSelected()) normalValues = input.getData(day, id, false, false);
        } else if (type.equals("Aggregated day of week")) {
            day = day.substring(0, 3).toUpperCase();
            trafficValues = input.getData(day, id, true, true);
            if (durationCheckBox.isSelected()) normalValues = input.getData(day, id, false, true);
        }

        for (Double key : trafficValues.keySet()) {
            seriesDurationInTraffic.setName("Duration in traffic - Day: " + day + ", ID: " + idComboBox.getSelectionModel().getSelectedItem());
            seriesDurationInTraffic.getData().add(new XYChart.Data<Number, Number>(key, trafficValues.get(key)));
        }
        if (durationCheckBox.isSelected()) {
            for (Double key : normalValues.keySet()) {
                seriesDuration.setName("Duration - Day: " + day + ", ID: " + idComboBox.getSelectionModel().getSelectedItem());
                seriesDuration.getData().add(new XYChart.Data<Number, Number>(key, normalValues.get(key)));
            }
        }

        lineChart.getData().add(seriesDurationInTraffic);
        lineChart.getData().add(seriesDuration);

        createTooltips();
    }

    @FXML
    private void handleSummaryAction1(ActionEvent e) {
        drawSummaryChart(4);
    }

    @FXML
    private void handleSummaryAction2(ActionEvent e) {
        drawSummaryChart(8);
    }

    private void drawSummaryChart(int route) {
        if (dayComboBox.getSelectionModel().getSelectedItem() == null
                || typeComboBox.getSelectionModel().getSelectedItem() == null) {
            warn.setText("You have to select type and date!");
            return;
        }
        warn.setText("");
        if (clearCheckBox.isSelected()) {
            lineChart.getData().clear();
        }

        XYChart.Series<Number, Number> seriesDurationInTraffic = new XYChart.Series<>();
        XYChart.Series<Number, Number> seriesDuration = new XYChart.Series<>();

        XYChart.Series<Number, Number> seriesDurationSummaryInTraffic = new XYChart.Series<>();
        XYChart.Series<Number, Number> seriesDurationSummary = new XYChart.Series<>();

        String type = typeComboBox.getSelectionModel().getSelectedItem();
        String day = dayComboBox.getSelectionModel().getSelectedItem();
        Map<Double, Double> summaryTraffic = null;
        Map<Double, Double> traffic = null;

        Map<Double, Double> durationSummaryTraffic = null;
        Map<Double, Double> durationTraffic = null;

        if (type.equals("Exact date")) {
            if (route == 4) {
                summaryTraffic = input.getSummary(day, 1, 3, true, false);
                traffic = input.getData(day, String.valueOf(route), true, false);
                if (durationCheckBox.isSelected()) {
                    durationSummaryTraffic = input.getSummary(day, 1, 3, false, false);
                    durationTraffic = input.getData(day, String.valueOf(route), false, false);
                }
            } else if (route == 8) {
                summaryTraffic = input.getSummary(day, 5, 7, true, false);
                traffic = input.getData(day, String.valueOf(route), true, false);
                if (durationCheckBox.isSelected()) {
                    durationSummaryTraffic = input.getSummary(day, 5, 7, false, false);
                    durationTraffic = input.getData(day, String.valueOf(route), false, false);
                }
            }
        } else if (type.equals("Aggregated day of week")) {
            day = day.substring(0, 3).toUpperCase();
            if (route == 4) {
                summaryTraffic = input.getSummary(day, 1, 3, true, true);
                traffic = input.getData(day, String.valueOf(route), true, true);
                if (durationCheckBox.isSelected()) {
                    durationSummaryTraffic = input.getSummary(day, 1, 3, false, true);
                    durationTraffic = input.getData(day, String.valueOf(route), false, true);
                }
            } else if (route == 8) {
                summaryTraffic = input.getSummary(day, 5, 7, true, true);
                traffic = input.getData(day, String.valueOf(route), true, true);
                if (durationCheckBox.isSelected()) {
                    durationSummaryTraffic = input.getSummary(day, 5, 7, false, true);
                    durationTraffic = input.getData(day, String.valueOf(route), false, true);
                }
            }
        }

        String ids = route == 4 ? "1-3" : "5-7";
        for (Double key : summaryTraffic.keySet()) {
            seriesDurationSummaryInTraffic.setName("Duration in traffic - Day: " + day + ", ID: " + ids);
            seriesDurationSummaryInTraffic.getData().add(new XYChart.Data<Number, Number>(key, summaryTraffic.get(key)));
        }
        if (durationCheckBox.isSelected()) {
            for (Double key : durationSummaryTraffic.keySet()) {
                seriesDurationSummary.setName("Duration - Day: " + day + ", ID: " + ids);
                seriesDurationSummary.getData().add(new XYChart.Data<Number, Number>(key, durationSummaryTraffic.get(key)));
            }
        }

        for (Double key : traffic.keySet()) {
            seriesDurationInTraffic.setName("Duration in traffic - Day: " + day + ", ID: " + route);
            seriesDurationInTraffic.getData().add(new XYChart.Data<Number, Number>(key, traffic.get(key)));
        }
        if (durationCheckBox.isSelected()) {
            for (Double key : durationTraffic.keySet()) {
                seriesDuration.setName("Duration - Day: " + day + ", ID: " + route);
                seriesDuration.getData().add(new XYChart.Data<Number, Number>(key, durationTraffic.get(key)));
            }
        }

        lineChart.getData().add(seriesDurationSummaryInTraffic);
        lineChart.getData().add(seriesDurationSummary);
        lineChart.getData().add(seriesDurationInTraffic);
        lineChart.getData().add(seriesDuration);

        createTooltips();
    }

    @FXML
    private void handleDurationAction(ActionEvent e) {
        handleStartAction(e);
    }

    @FXML
    private void handleClearOnDrawAction(ActionEvent e) {
        if (clearCheckBox.isSelected()) {
            lineChart.getData().clear();
            handleStartAction(e);
        }
    }

    @FXML
    private void handleClearAction(ActionEvent e) {
        lineChart.getData().clear();
    }

    @FXML
    private void handleReverseRotuteAction(ActionEvent e) {
        try {
            String id = input.getId(idComboBox.getSelectionModel().getSelectedItem());
            if (id != null) {
                idComboBox.getSelectionModel().select(input.getReverse(id));
            }
        } catch (NullPointerException exception) {
            warn.setText("Nothing to reverse");
        }
    }
}
