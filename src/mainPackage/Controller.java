/**
 * @author https://github.com/jeffament
 */

package mainPackage;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.awt.AWTException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

public class Controller {

    @FXML
    private TextField tfPixels, tfSeconds, tfMinutes, tfHours, tfMaxSeconds, tfMaxMinutes, tfMaxHours;
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
        Collection<TextField> textFields = Arrays.asList(tfPixels, tfSeconds, tfMinutes, tfHours, tfMaxSeconds, tfMaxMinutes, tfMaxHours);
        // Configure TextFields to only accept numbers
        for(TextField tf: textFields){
            tf.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.matches("\\d*")) {
                    tf.setText(newValue.replaceAll("[^\\d]", ""));
                }
            });
        }
    }

    private void toggleAllSetEditables(boolean bool) {
        tfPixels.setEditable(bool);
        tfSeconds.setEditable(bool);
        tfMinutes.setEditable(bool);
        tfHours.setEditable(bool);
        tfMaxSeconds.setEditable(bool);
        tfMaxMinutes.setEditable(bool);
        tfMaxHours.setEditable(bool);
        cbMaxTime.setDisable(!bool);
    }

    private boolean errorCheck() {
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

    private boolean doesItHaveAChanceToRun(int totSecs, int maxTotSecs) { //todo make both error checks restore the UI
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

    void setTextArea(int secs, int maxSecs, String method) { //todo review logic here; possible candidate for else if
        if (method.equals("automaticallyEnd")) {
            txtArea.setText(buildString(secs, maxSecs, "automaticallyEnd"));
        }
        if (maxSecs >= 0) {
            txtArea.setText(buildString(secs, maxSecs, "autoDisableActive"));
        } else {
            txtArea.setText(buildString(secs, maxSecs, "autoDisableInactive"));
        }
    }

    private String getTimeString(int secs) { // todo sloppy stringbuilder!
        int secsValue = secs % 60;
        int minsValue = (secs / 60) % 60;
        int hoursValue = secs / 3600;
        String secondsString = ((secsValue == 1) ? "second" : "seconds");
        String minsString = ((minsValue == 1) ? "minute" : "minutes");
        String hoursString = ((hoursValue == 1) ? "hour" : "hours");

        StringBuilder sb = new StringBuilder();

        if(hoursValue>0){
            sb.append(hoursValue).append(" ")
                    .append(hoursString).append(", ");
        }if(hoursValue>0 || minsValue>0){
            sb.append(minsValue)
                    .append(" ")
                    .append(minsString)
                    .append( " and ");
        }

        sb.append(secsValue)
                .append(" ")
                .append(secondsString);

        return sb.toString();
    }

    private String buildString(int secs, int maxSecs, String method) {
        String timeString = getTimeString(secs);
        String returnString = null;
        switch (method){
            case "autoDisableActive":
                returnString =  buildString(maxSecs, secs, "doAutoDisableByRecursiveFlip");
                break;
            case "autoDisableInactive":
                returnString =  "Idle time: " + timeString;
                break;
            case "doAutoDisableByRecursiveFlip":
                returnString = "\nStop running in: " + timeString;
                return buildString(maxSecs, secs, "autoDisableInactive") + returnString;
            case "automaticallyEnd":
                return "Automatically stopped after " + timeString;
        }
        return returnString;
    }

    void updateUIOnAutomaticDisable(int maxSecs) {
        btnStart.fire();
        String autoEndText = buildString(maxSecs, 0, "automaticallyEnd");
        txtArea.setText(autoEndText);
    }
}