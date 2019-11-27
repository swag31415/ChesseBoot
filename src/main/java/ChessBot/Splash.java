package ChessBot;

import java.awt.AWTException;
import java.awt.Color;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import xyz.niflheim.stockfish.exceptions.StockfishInitException;

public class Splash extends Controller {

    private static final Color light = new Color(238, 238, 210);
    private static final Color dark = new Color(118, 150, 86);
    private static final Color light_selected = new Color(246, 246, 130);
    private static final Color dark_selected = new Color(186, 202, 68);

    private static ChessBot chessBot;

    @FXML
    private Button b1, b2, b3, b4;

    private static final String run_init = "Run Init";
    private static final String get_color = "Get Color";
    private static final String generate_mapping = "Generate Mapping";
    private static final String play = "Play";

    @FXML
    void button_pressed(ActionEvent event) {
        String text = ((Button) event.getSource()).getText();

        switch (text) {
        case run_init:
            chessBot.getBoard();
        case generate_mapping:
            chessBot.getIsWhite();
            chessBot.generatePieceMap();
            break;
        case get_color:
            chessBot.getIsWhite();
            break;
        case play:
            chessBot.makeMove();
            break;

        default:
            break;
        }

        if (text.equals(run_init)) {
            b2.setText(get_color);
            b3.setText(generate_mapping);
            b4.setText(play);
        }
    }

    @Override
    protected void init() {
        Button[] buttons = { b1, b2, b3, b4 };

        for (Button button : buttons) {
            button.setText(run_init);
        }

        try {
            Splash.chessBot = new ChessBot(light, dark, light_selected, dark_selected);
        } catch (AWTException | StockfishInitException e) {
            e.printStackTrace();
        }
    }

}
