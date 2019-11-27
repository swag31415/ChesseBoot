package ChessBot;

import java.awt.Color;
import java.awt.event.InputEvent;
import java.awt.Robot;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

import xyz.niflheim.stockfish.StockfishClient;
import xyz.niflheim.stockfish.engine.enums.Query;
import xyz.niflheim.stockfish.engine.enums.QueryType;

public class ChessBot {
    private int startX;
    private int endX;
    private int startY;
    private int endY;

    private Color light;
    private Color dark;
    private Color light_selected;
    private Color dark_selected;

    private Robot r;
    private StockfishClient client;
    private boolean isWhite;
    private Map<Double[], String> pieceMap;

    public ChessBot(Color light, Color dark, Color light_selected, Color dark_selected, Robot r, StockfishClient client) {
        this.light = light;
        this.dark = dark;
        this.light_selected = light_selected;
        this.dark_selected = dark_selected;
        this.r = r;
        this.client = client;
        this.getBoard();
        this.getIsWhite();
        this.generatePieceMap();
    }

    public void makeMove() {
        String fen = guessBoardFen();
        Query query = new Query.Builder(QueryType.Best_Move).setFen(fen).build();

        client.submit(query, result -> {
            move(result, 50);
            System.out.println(result);
        });
    }

    public void generatePieceMap() {
        Double[][] pieces = getPieces();

        pieceMap = new HashMap<Double[], String>();
        Double[] emptyTile = {0.0, 0.0, 0.0, 0.0};
        int offset = 48;
        String[] topSet = new String[16];
        String[] bottomSet = new String[16];

        if (isWhite) {
            String[] blackSet = {"r", "n", "b", "q", "k", "b", "n", "r", "p", "p", "p", "p", "p", "p", "p", "p"};
            String[] whiteSet = {"P", "P", "P", "P", "P", "P", "P", "P", "R", "N", "B", "Q", "K", "B", "N", "R"};
            topSet = blackSet;
            bottomSet = whiteSet;
        } else {
            String[] whiteSet = {"R", "N", "B", "K", "Q", "B", "N", "R", "P", "P", "P", "P", "P", "P", "P", "P"};
            String[] blackSet = {"p", "p", "p", "p", "p", "p", "p", "p", "r", "n", "b", "k", "q", "b", "n", "r"};
            topSet = whiteSet;
            bottomSet = blackSet;
        }
        
        for (int i = 0; i < topSet.length; i++) {
            if (!Arrays.equals(pieces[i], emptyTile)) {
                pieceMap.put(pieces[i], topSet[i]);
            }
        }

        for (int i = 0; i < bottomSet.length; i++) {
            if (!Arrays.equals(pieces[i + offset], emptyTile)) {
                pieceMap.put(pieces[i + offset], bottomSet[i]);
            }
        }

        pieceMap.put(emptyTile, " ");
    }

    public void getIsWhite() {
        Double[][] pieces = getPieces();
        isWhite = pieces[0][0] < pieces[63][0];
    }

    public void getBoard() {
        Color[][] img = Utils.getColors(Utils.getScreenshot(r));
        img = Utils.filterColors(img, light, light_selected);
        img = Utils.filterColors(img, dark, dark_selected);

        int h = img.length;
        int w = img[0].length;
        Color[][] img_t = Utils.transpose(img);
        int count = 0;

        Function<Color, Boolean> isLight = c -> {
            return c.getRGB() == light.getRGB();
        };
        Function<Color, Boolean> isDark = c -> {
            return c.getRGB() == dark.getRGB();
        };

        count = 0;
        for (Color[] row : img) {
            Color[] row_f = Utils.flipArray(row);
            int firstLight = Utils.getFirstIndex(row, isLight);
            int lastLight = (w - 1) - Utils.getFirstIndex(row_f, isLight);
            int firstDark = Utils.getFirstIndex(row, isDark);
            int lastDark = (w - 1) - Utils.getFirstIndex(row_f, isDark);

            if ((firstLight != firstDark) || (lastLight != lastDark)) {
                int first = Math.min(firstLight, firstDark);
                int last = Math.max(lastLight, lastDark);
                if ((first == startX) && (last == endX)) {
                    count++;
                } else {
                    count = 0;
                    startX = first;
                    endX = last;
                }
            } else {
                count = 0;
            }

            if (count >= 10) {
                break;
            }
        }

        count = 0;
        for (Color[] column : img_t) {
            Color[] column_f = Utils.flipArray(column);
            int firstLight = Utils.getFirstIndex(column, isLight);
            int lastLight = (h - 1) - Utils.getFirstIndex(column_f, isLight);
            int firstDark = Utils.getFirstIndex(column, isDark);
            int lastDark = (h - 1) - Utils.getFirstIndex(column_f, isDark);

            if ((firstLight != firstDark) || (lastLight != lastDark)) {
                int first = Math.min(firstLight, firstDark);
                int last = Math.max(lastLight, lastDark);
                if ((first == startY) && (last == endY)) {
                    count++;
                } else {
                    count = 0;
                    startY = first;
                    endY = last;
                }
            } else {
                count = 0;
            }

            if (count >= 10) {
                break;
            }
        }
    }

    // ---------------------------------- PRIVATES --------------------------------------- //

    private Double[][] getPieces() {
        Color[][] board = Utils.getColors(Utils.getScreenshot(r, startX, endX, startY, endY));
        Color[][] pboard = Utils.filterColors(board, Color.BLACK, light, dark);
        int w = (pboard[0].length / 8);
        int h = (pboard.length / 8);
        Double[][] pieces = new Double[64][4];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Color[][] tile = Utils.arraySubset(pboard, i * h, ((i + 1) * h) - 1, j * w, ((j + 1) * w) - 1);
                int tw = tile[0].length - 1;
                int th = tile.length - 1;
                pieces[(8 * i) + j][0] = Utils.imgavg(Utils.arraySubset(tile, 0, th/2, 0, tw/2));
                pieces[(8 * i) + j][1] = Utils.imgavg(Utils.arraySubset(tile, 0, th/2, tw/2, tw));
                pieces[(8 * i) + j][2] = Utils.imgavg(Utils.arraySubset(tile, th/2, th, 0, tw/2));
                pieces[(8 * i) + j][3] = Utils.imgavg(Utils.arraySubset(tile, th/2, th, tw/2, tw));
            }
        }
        return pieces;
    }

    private String guessBoardFen() {
        Double[][] pieces = getPieces();
        StringBuilder board = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            int empties = 0;
            for (int j = 0; j < 8; j++) {
                String guess = guessPiece(pieces[(8 * i) + j]);
                if (guess.equals(" ")) {
                    empties++;
                } else {
                    board.append((empties != 0) ? empties : "");
                    empties = 0;
                    board.append(guess);
                }
            }
            board.append((empties != 0) ? empties : "");
            board.append((i == 7) ? "" : "/");
        }
        if (!isWhite) {
            board.reverse();
        }
        board.append(isWhite ? " w" : " b");
        board.append(" - - 0 1");

        return board.toString();
    }

    private String guessPiece(Double[] piece) {
        String guess = "";
        double error = Double.MAX_VALUE;
        for (Entry<Double[], String> entry : pieceMap.entrySet()) {
            double err = 0;
            for (int i = 0; i < entry.getKey().length; i++) {
                err += Math.pow(entry.getKey()[i] - piece[i], 2);
            }
            if (err < error) {
                error = err;
                guess = entry.getValue();
            }
        }
        return guess;
    }

    private void move(String move, int patMillis) {
        int x_i = move.charAt(0) - 'a' + 1;
        int y_i = move.charAt(1) - '1' + 1;
        int x = move.charAt(2) - 'a' + 1;
        int y = move.charAt(3) - '1' + 1;

        int xScale = (endX - startX) / 8;
        int yScale = (endY - startY) / 8;

        x_i = startX + ((isWhite ? x_i : 9 - x_i) * xScale) - (xScale / 2);
        y_i = startY + ((!isWhite ? y_i : 9 - y_i) * yScale) - (yScale / 2);
        x = startX + ((isWhite ? x : 9 - x) * xScale) - (xScale / 2);
        y = startY + ((!isWhite ? y : 9 - y) * yScale) - (yScale / 2);

        r.setAutoDelay(patMillis);

        r.mouseMove(x_i, y_i);
        r.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        r.mouseMove(x, y);
        r.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        r.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        r.mouseMove(startX - 10, endY + 10);
    }
}