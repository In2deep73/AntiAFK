package mainPackage;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.awt.AWTException;
import java.util.Optional;

public class Controller {

    @FXML
    private TextField tfPixels;
    @FXML
    private TextField tfSeconds;
    @FXML
    private TextField tfMinutes;
    @FXML
    private TextField tfHours;
    @FXML
    private TextField tfMaxSeconds;
    @FXML
    private TextField tfMaxMinutes;
    @FXML
    private TextField tfMaxHours;
    @FXML
    private TextArea txtArea;
    @FXML
    private Button btnStart;
    @FXML
    private CheckBox cbMaxTime;
    private Thread thread;
    private MoveMouseBackgroundWorker moveMouseBackgroundWorker;


    public void onStartButtonClicked() throws AWTException {
        if (btnStart.getText().equals("Start") && errorCheck()) {
            btnStart.setText("Stop");
            toggleAllSetEditables(false);
            int pixels = Integer.parseInt(tfPixels.getText());
            int seconds = Integer.parseInt(tfSeconds.getText());
            int minutes = Integer.parseInt(tfMinutes.getText());
            int hours = Integer.parseInt(tfHours.getText());
            int totalSeconds = ((hours * 60 + minutes) * 60 + seconds);
            if (cbMaxTime.isSelected()) {
                int maxSeconds = Integer.parseInt(tfMaxSeconds.getText());
                int maxMinutes = Integer.parseInt(tfMaxMinutes.getText());
                int maxHours = Integer.parseInt(tfMaxHours.getText());
                int maxTotalSeconds = ((maxHours * 60 + maxMinutes) * 60 + maxSeconds);
                if (doesItHaveAChanceToRun(totalSeconds, maxTotalSeconds)) {
                    moveMouseBackgroundWorker = new MoveMouseBackgroundWorker(this, pixels, totalSeconds, maxTotalSeconds);
                    Runnable runnable = moveMouseBackgroundWorker;
                    thread = new Thread(runnable);
                    thread.setDaemon(true);
                    thread.start();
                } else {
                    toggleAllSetEditables(true);
                    btnStart.setText("Start");
                }
            } else if (!cbMaxTime.isSelected()) {
                moveMouseBackgroundWorker = new MoveMouseBackgroundWorker(this, pixels, totalSeconds);
                Runnable runnable = moveMouseBackgroundWorker;
                thread = new Thread(runnable);
                thread.setDaemon(true);
                thread.start();
            }
        } else if (btnStart.getText().equals("Stop")) {
            moveMouseBackgroundWorker.setStopThread(true);
            thread.interrupt();
            toggleAllSetEditables(true);
            txtArea.setText("Anti-AFK has stopped running");
            btnStart.setText("Start");
        }
    }

    @FXML
    public void initialize() {
        configureTextFieldsToBeNumbericalOnly();
    }

    public void toggleAllSetEditables(boolean bool){
        tfPixels.setEditable(bool);
        tfSeconds.setEditable(bool);
        tfMinutes.setEditable(bool);
        tfHours.setEditable(bool);
        tfMaxSeconds.setEditable(bool);
        tfMaxMinutes.setEditable(bool);
        tfMaxHours.setEditable(bool);
        cbMaxTime.setDisable(!bool);
    }

    public boolean errorCheck() {
        if (tfPixels.getText().equals("") || tfSeconds.getText().equals("") || tfMinutes.getText().equals("") ||
                tfHours.getText().equals("") || tfMaxSeconds.getText().equals("") || tfMaxMinutes.getText().equals("") ||
                tfMaxHours.getText().equals("")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error:");
            alert.setContentText("All fields must be integers");
            alert.showAndWait();
            return false;
        }
        return true;
    }

    public boolean doesItHaveAChanceToRun(int totSecs, int maxTotSecs) { //todo make both error checks restore the UI
        if (maxTotSecs < totSecs) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Warning");
            alert.setHeaderText("The program never has a chance to move the mouse given the entered values");
            alert.setContentText("Do you wish to continue anyway?");
            ButtonType btnYes = new ButtonType("Sure, YOLO!");
            ButtonType btnNo = new ButtonType("No...");
            alert.getButtonTypes().setAll(btnYes, btnNo);
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == btnYes) {
                return true;
            } else if (result.get() == btnNo) {
                return false;
            } else {
                return false;
            }
        }
        return true;
    }

    public void configureTextFieldsToBeNumbericalOnly() {
        tfPixels.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                tfPixels.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        tfSeconds.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                tfSeconds.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        tfMinutes.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                tfMinutes.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        tfHours.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                tfHours.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        tfMaxSeconds.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                tfMaxSeconds.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        tfMaxMinutes.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                tfMaxMinutes.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        tfMaxHours.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                tfMaxHours.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }

    public void setTextArea(int secs, int maxSecs, String method) {
        if (method.equals("automaticallyEnd")) {
            txtArea.setText(buildString(secs, maxSecs, "automaticallyEnd"));
        }
        if (maxSecs >= 0) {
            txtArea.setText(buildString(secs, maxSecs, "autoDisableActive"));
        } else {
            txtArea.setText(buildString(secs, maxSecs, "autoDisableInactive"));
        }
    }

    public String buildString(int secs, int maxSecs, String method) {
        int secsValue = secs % 60;
        int minsValue = (secs / 60) % 60;
        int hoursValue = secs / 3600;
        String secondsString = "seconds", minsString = "minutes", hoursString = "hours", returnString = null;
        if (secsValue == 1) secondsString = "second";
        if (minsValue == 1) minsString = "minute";
        if (hoursValue == 1) hoursString = "hour";

        if (method.equals("autoDisableActive")) {
            return buildString(maxSecs, secs, "doAutoDisableByRecursiveFlip");
        } else if (method.equals("autoDisableInactive")) {
            if (hoursValue > 0) {
                returnString = "Anti-AFK is running....The computer has been idle for " + hoursValue + " " + hoursString +
                        ", " + minsValue + " " + minsString + " and " + secsValue + " " + secondsString;
            } else if (minsValue > 0) {
                returnString = "Anti-AFK is running....The computer has been idle for " + minsValue + " " + minsString +
                        " and " + secsValue + " " + secondsString;
            } else {
                returnString = "Anti-AFK is running....The computer has been idle for " + secs + " " + secondsString;
            }
        } else if (method.equals("doAutoDisableByRecursiveFlip")) {
            if (hoursValue > 0) {
                returnString = "\nAnti-AFK will stop running if mouse not manually moved in " + hoursValue + " " + hoursString +
                        ", " + minsValue + " " + minsString + " and " + secsValue + " " + secondsString;
            } else if (minsValue > 0) {
                returnString = "\nAnti-AFK will stop running if mouse not manually moved in " + minsValue + " " + minsString +
                        " and " + secsValue + " " + secondsString;
            } else {
                returnString = "\nAnti-AFK will stop running if mouse not manually moved in " + secsValue + " " + secondsString;
            }
            return buildString(maxSecs, secs, "autoDisableInactive") + returnString;
        } else if (method.equals("automaticallyEnd")) {
            if (hoursValue > 0) {
                returnString = "Anti-AFK automatically stopped after running for " + hoursValue + " " + hoursString +
                        ", " + minsValue + " " + minsString + " and " + secsValue + " " + secondsString;
            } else if (minsValue > 0) {
                returnString = "Anti-AFK automatically stopped after running for " + minsValue + " " + minsString +
                        " and " + secsValue + " " + secondsString;
            } else {
                returnString = "Anti-AFK automatically stopped after running for " + secs + " " + secondsString;
            }
        }
        return returnString;
    }

    public void updateUIOnAutomaticDisable(int maxSecs) {
        btnStart.fire();
        txtArea.setText(buildString(maxSecs, 0, "automaticallyEnd"));
    }
}