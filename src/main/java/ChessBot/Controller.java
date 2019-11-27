package ChessBot;

import javafx.application.Platform;

public abstract class Controller {

    protected int delay = 20;

    public Controller() {
        Platform.runLater(() -> {
            boolean done = false;
            while (!done) {
                done = true;
                try {
                    init();
                } catch (Exception e0) {
                    done = false;
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    e0.printStackTrace();
                }
            }
        });
    }

    abstract protected void init();
}