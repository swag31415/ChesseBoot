/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package ChessBot;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Robot;
import java.util.Scanner;

import xyz.niflheim.stockfish.StockfishClient;
import xyz.niflheim.stockfish.engine.enums.Option;
import xyz.niflheim.stockfish.engine.enums.Variant;
import xyz.niflheim.stockfish.exceptions.StockfishInitException;

public class App {
    public static void main(String[] args) throws AWTException, StockfishInitException {
        Color light = new Color(238, 238, 210);
        Color dark = new Color(118, 150, 86);
        Color light_selected = new Color(246, 246, 130);
        Color dark_selected = new Color(186, 202, 68);
        Robot r = new Robot();

        StockfishClient client = new StockfishClient.Builder()
                .setInstances(4)
                .setOption(Option.Minimum_Thinking_Time, 50) // Minimum thinking time Stockfish will take
                .setOption(Option.Skill_Level, 20) // Stockfish skill level 0-20
                .setVariant(Variant.BMI2).build();

        ChessBot chessBot = new ChessBot(light, dark, light_selected, dark_selected, r, client);
    }
}
