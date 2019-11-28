/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package ChessBot;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.util.Random;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import xyz.niflheim.stockfish.exceptions.StockfishInitException;

public class App implements NativeKeyListener {

    private static final Color light = new Color(238, 238, 210);
    private static final Color dark = new Color(118, 150, 86);
    private static final Color light_selected = new Color(246, 246, 130);
    private static final Color dark_selected = new Color(186, 202, 68);

    private static ChessBot chessBot;
    private boolean running;

    public App() throws StockfishInitException {
        App.chessBot = new ChessBot(light, dark, light_selected, dark_selected, 20, 800);
        this.running = false;
    }

    public static void main(String[] args) throws NativeHookException, StockfishInitException {
        GlobalScreen.registerNativeHook();
        GlobalScreen.addNativeKeyListener(new App());
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent nativeEvent) {
        // Do Nothing
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent nativeEvent) {
        // Do Nothing
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent nativeEvent) {
        if (nativeEvent.getKeyCode() == NativeKeyEvent.VC_F6) {
            this.running = !this.running;
            this.jitterBot(50);
        }

        if (this.running) {
            switch (nativeEvent.getKeyCode()) {
            case NativeKeyEvent.VC_I: // Pressed 'i'
                App.chessBot.getBoard();
            case NativeKeyEvent.VC_M: // Pressed 'm'
                App.chessBot.getIsWhite();
                App.chessBot.generatePieceMap();
                this.jitterBot(100);
                break;
            case NativeKeyEvent.VC_C: // Pressed 'c'
                App.chessBot.getIsWhite();
                this.jitterBot(50);
                break;
            case NativeKeyEvent.VC_P: // Pressed 'p'
                App.chessBot.makeMove();
                this.jitterBot(5);
                break;
            case NativeKeyEvent.VC_ESCAPE: // Pressed esc
                System.exit(0);
                break;
            default:
                break;
            }
        }
    }

    private void jitterBot(int c) {
        int t = 10;
        int s = 5;
        Robot r;
        try {
            r = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
            r = null;
        }
        Random ran = new Random();
        Point origin = MouseInfo.getPointerInfo().getLocation();
        r.setAutoDelay(t);
        for (int i = 0; i < c; i++) {
            r.mouseMove((int) origin.getX() + ran.nextInt(s), (int) origin.getY() + ran.nextInt(s));
        }
    }
}
