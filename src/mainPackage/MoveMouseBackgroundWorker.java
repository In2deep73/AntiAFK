/**
 * @author https://github.com/jeffament
 */

package mainPackage;

import javafx.application.Platform;
import java.awt.*;

public class MoveMouseBackgroundWorker implements Runnable {

    private Robot robot;
    private int pixels;
    private int seconds;
    private int secondsWaiting = 0;
    private int secondsLeft = -1;
    private int secondsLeftHardCopy;
    private volatile boolean wasLastMoveLeft = true;
    private volatile boolean stopThread;
    private int[] mousePositionBeforeWaiting = new int[2];
    private int[] mousePositionAfterWaiting = new int[2];
    private Controller controller;
    private String method;

    void setStopThread(boolean stopThread) {
        this.stopThread = stopThread;
    }

    MoveMouseBackgroundWorker(Controller controller, int pixels, int totalSeconds) throws AWTException {
        this.robot = new Robot();
        this.controller = controller;
        this.pixels = pixels;
        this.seconds = totalSeconds;
        this.method = "autoDisableInactive";
        this.secondsLeftHardCopy = this.secondsLeft;
        this.stopThread = false;
    }

    MoveMouseBackgroundWorker(Controller controller, int pixels, int totalSeconds, int maxSeconds) throws AWTException {
        this.robot = new Robot();
        this.controller = controller;
        this.pixels = pixels;
        this.seconds = totalSeconds;
        this.secondsLeft = maxSeconds;
        this.method = "autoDisableActive";
        this.secondsLeftHardCopy = this.secondsLeft;
        this.stopThread = false;
    }

    @Override
    public void run() {
        secondsWaiting = 0;
        controller.setTextArea(secondsWaiting, secondsLeft, this.method);

        while (!stopThread) {
            try {
                mousePositionBeforeWaiting = getCurrentMousePosition();
                sleepOneSecondAndIncrementCounters();
                mousePositionAfterWaiting = getCurrentMousePosition();
                if (stopThread) { throw new InterruptedException(); }
                if (mouseMovedAfterWaiting()) { throw new Exception(); }
                if (secondsWaiting == seconds) {
                    moveTheMouse();
                }
                if (secondsLeft == 0) {
                    Platform.runLater(() -> controller.updateUIOnAutomaticDisable(secondsLeftHardCopy));
                    throw new InterruptedException();
                } else {
                    controller.setTextArea(secondsWaiting, secondsLeft, this.method);
                }
            } catch (InterruptedException ignored) {} // stop button pressed
            catch (Exception e) { handleMouseManuallyMoved(); }
        }
    }

    private void sleepOneSecondAndIncrementCounters() throws InterruptedException {
        Thread.sleep(1000);
        secondsWaiting++;
        secondsLeft--;
    }

    private boolean mouseMovedAfterWaiting() {
        return !(mousePositionBeforeWaiting[0] == mousePositionAfterWaiting[0]
                && mousePositionBeforeWaiting[1] == mousePositionAfterWaiting[1]);
    }

    private int[] getCurrentMousePosition() {
        Point point = MouseInfo.getPointerInfo().getLocation();
        int xPosition = (int) point.getX();
        int yPosition = (int) point.getY();
        return new int[]{xPosition, yPosition};
    }

    private void handleMouseManuallyMoved(){
        secondsWaiting = 0;
        secondsLeft = secondsLeftHardCopy;
        controller.setTextArea(secondsWaiting, secondsLeft, this.method);
    }

    private void moveTheMouse() {
        Point point = MouseInfo.getPointerInfo().getLocation();
        int x = (int) point.getX();
        int y = (int) point.getY();
        if (this.wasLastMoveLeft) {
            robot.mouseMove(x + pixels, y);
            this.wasLastMoveLeft = false;
        } else {
            robot.mouseMove(x - pixels, y);
            this.wasLastMoveLeft = true;
        }
        secondsWaiting = 0;
        controller.setTextArea(secondsWaiting, secondsLeft, this.method);
    }
}