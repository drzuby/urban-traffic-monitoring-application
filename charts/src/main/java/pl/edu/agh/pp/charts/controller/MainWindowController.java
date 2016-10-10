package pl.edu.agh.pp.charts.controller;

import ch.qos.logback.classic.Logger;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;
import org.joda.time.DateTime;
import org.slf4j.LoggerFactory;
import pl.edu.agh.pp.charts.Main;
import pl.edu.agh.pp.charts.adapters.ChannelReceiver;
import pl.edu.agh.pp.charts.adapters.Connector;
import pl.edu.agh.pp.charts.input.Input;

import java.io.IOException;
import java.net.InetAddress;
import java.text.DecimalFormat;
import java.util.Properties;
import java.util.regex.Pattern;

/**
 * Created by Dawid on 2016-09-05.
 */
public class MainWindowController {
    private Stage primaryStage = null;
    private Input input;
    private ChartsController chartsController = null;
    private boolean connectedFlag = false;
    private final Logger logger = (Logger) LoggerFactory.getLogger(MainWindowController.class);

    @FXML
    private LineChart<Number, Number> lineChart;
    @FXML
    private Button chartsButton;
    @FXML
    private TextFlow anomaliesTextFlow;
    @FXML
    private Slider leverSld;
    @FXML
    private Label changeLeverTo;
    @FXML
    private Label currentLeverOnServer;
    @FXML
    private Button sendSettingsButton;
    @FXML
    private Label connectedLabel;
    @FXML
    private TextField serverAddrTxtField;
    @FXML
    private Button connectButton;
    @FXML
    private Button disconnectButton;



    public MainWindowController(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void show() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("/MainWindow.fxml"));
            loader.setController(this);
            BorderPane rootLayout = loader.load();

            primaryStage.setTitle("Urban traffic monitoring - charts");
            Scene scene = new Scene(rootLayout);
            scene.getStylesheets().add(Main.class.getResource("/chart.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    public void putAnomalyMessageonScreen(int id, String message, DateTime dateTime, int duration, Color color) {
        Text text1 = new Text(id + ": " + dateTime.toString() + "; duration = " + duration + "; " + message + "\n");
        text1.setFill(color);
        text1.setFont(Font.font("Helvetica", FontPosture.REGULAR, 16));
        Platform.runLater(() -> anomaliesTextFlow.getChildren().add(0, text1));
    }

    public void putSystemMessageonScreen(String message) {
        putSystemMessageonScreen(message,DateTime.now(),Color.BLACK);
    }

    public void putSystemMessageonScreen(String message,Color color) {
        putSystemMessageonScreen(message,DateTime.now(),color);
    }

    public void putSystemMessageonScreen(String message, DateTime dateTime, Color color) {
        Text text1 = new Text(dateTime.toString() + "  " +message + "\n");
        text1.setFill(color);
        text1.setFont(Font.font("Helvetica", FontPosture.REGULAR, 16));
        initSlider();
        Platform.runLater(() -> anomaliesTextFlow.getChildren().add(0, text1));
    }

    private String getAddressServerInfo(){
//TODO keeping server info in Connector?
        return "1.1.1.1";
    }

    private String getLeverServerInfo(){
//TODO keeping server info in Connector?
        return "10";
    }

    public void LeverChangedOnServer(String value){
        //TODO use after lever value changed on server
        currentLeverOnServer.setText(value);
    }

    private void initSlider() {
        int maxValue = 40;
        int minValue = 1;
        leverSld.setMajorTickUnit(2);
        leverSld.setMinorTickCount(1);
        leverSld.setSnapToTicks(true);
        leverSld.setMin(minValue);
        leverSld.setMax(maxValue);
        DecimalFormat df = new java.text.DecimalFormat();
        df.setMaximumFractionDigits(1);
        leverSld.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
                Double value = leverSld.getValue();
                changeLeverTo.setText(String.valueOf(value.intValue()));
            }
        });
    }

    private void setConnectedState(){
        if(connectedFlag){
            connectedLabel.setText(getAddressServerInfo());
            connectedLabel.setTextFill(Color.BLACK);
            connectButton.setDisable(true);
            disconnectButton.setDisable(false);
        }
        else {
            connectedLabel.setText("NOT CONNECTED");
            connectedLabel.setTextFill(Color.RED);
            connectButton.setDisable(false);
            disconnectButton.setDisable(true);
        }
    }

    @FXML
    private void initialize() throws IOException {
        anomaliesTextFlow.setTextAlignment(TextAlignment.CENTER);
        anomaliesTextFlow.setMaxHeight(150);
        changeLeverTo.setText("1");
        putSystemMessageonScreen("NOT CONNECTED",Color.RED);
        setConnectedState();
        initSlider();
        currentLeverOnServer.setText(getLeverServerInfo());
    }

    @FXML
    private void handleChartsButtonAction(ActionEvent e) {
        if (chartsController == null) {
            chartsController = new ChartsController(primaryStage, this);
        }
        chartsController.show();
    }

    @FXML
    private void handleTestButtonAction(ActionEvent e) {
        putAnomalyMessageonScreen(666, "Test anomaly", DateTime.now(), 0, Color.PINK);
    }

    @FXML
    private void handleConnectAction(ActionEvent e) {
        Connector connector = new Connector();
        connector.setController(this);

        InetAddress server_addr = null;
        try {
            String address = serverAddrTxtField.getText();
            if(!Pattern.matches("\\d{1,3}.\\d{1,3}.\\d{1,3}.\\d{1,3}",address)){
                logger.error("Wrong server address pattern");
                serverAddrTxtField.setStyle("-fx-text-box-border: red;");
                return;
            }
            else{
                serverAddrTxtField.setStyle("-fx-text-box-border: black;");
            }
            server_addr = InetAddress.getByName(address);
            int server_port = 7500;
            boolean nio = true;

            Properties properties = System.getProperties();
            properties.setProperty("jgroups.bind_addr", server_addr.toString());

            ChannelReceiver client = new ChannelReceiver();
            client.start(server_addr, server_port, nio);
            connectedFlag = true;
            setConnectedState();
            putSystemMessageonScreen("Connected to: " + getAddressServerInfo());
        } catch (Exception e1) {
            logger.error("Connecting error");
            e1.printStackTrace();
        }

    }

    @FXML
    private void handleDisconnectAction(ActionEvent e) {
        connectedFlag = false;
        setConnectedState();
    }

    @FXML
    private void handleOnLeverChanged(ActionEvent e) {
        Connector.onLeverChange(changeLeverTo.getText());
    }

}


