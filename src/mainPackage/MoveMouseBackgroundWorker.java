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
    public volatile boolean stopThread;
    private Controller controller;
    private String method;

    public void setStopThread(boolean stopThread) {
        this.stopThread = stopThread;
    }

    public MoveMouseBackgroundWorker(Controller controller, int pixels, int totalSeconds) throws AWTException {
        this.robot = new Robot();
        this.controller = controller;
        this.pixels = pixels;
        this.seconds = totalSeconds;
        this.method = "autoDisableInactive";
        this.secondsLeftHardCopy = this.secondsLeft;
        this.stopThread = false;
    }

    public MoveMouseBackgroundWorker(Controller controller,int pixels, int totalSeconds, int maxSeconds) throws AWTException {
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
                Point point = MouseInfo.getPointerInfo().getLocation();
                int oldX = (int) point.getX();
                int oldY = (int) point.getY();
                Thread.sleep(1000);
                point = MouseInfo.getPointerInfo().getLocation();
                int newX = (int) point.getX();
                int newY = (int) point.getY();
                if (stopThread) {
                    throw new InterruptedException();
                }
                if (newX != oldX || newY != oldY) {
                    throw new Exception();
                }
                secondsWaiting++;
                secondsLeft--;
                if (secondsWaiting == seconds) {
                    moveTheMouse();
                }
                if (secondsLeft == 0) {
                    Platform.runLater(() -> controller.updateUIOnAutomaticDisable(secondsLeftHardCopy));
                    stopThread = true;
                    throw new InterruptedException();
                } else {
                    controller.setTextArea(secondsWaiting, secondsLeft, this.method);
                }
            } catch (InterruptedException e) {
                // stop button pressed
            } catch (Exception e) {
                // mouse manually moved
                secondsWaiting = 0;
                secondsLeft = secondsLeftHardCopy;
                controller.setTextArea(secondsWaiting, secondsLeft, this.method);
            }
        }
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