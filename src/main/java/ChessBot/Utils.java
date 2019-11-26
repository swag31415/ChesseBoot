package ChessBot;

import java.lang.reflect.Array;
import java.util.function.Function;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import java.awt.image.BufferedImage;
import java.awt.Color;

public class Utils {
    public static <T> T[][] arraySubset(T[][] array, int startY, int endY, int startX, int endX) {
        int d1 = endY - startY + 1;
        int d2 = endX - startX + 1;
        @SuppressWarnings("unchecked")
        T[][] newArray = (T[][]) Array.newInstance(array.getClass().getComponentType().getComponentType(), d1, d2);
        for (int i = 0; i < d1; i++) {
            for (int j = 0; j < d2; j++) {
                newArray[i][j] = array[startY + i][startX + j];
            }
        }
        return newArray;
    }

    public static <T> T[] flipArray(T[] array) {
        int len = array.length;
        @SuppressWarnings("unchecked")
        T[] newArray = (T[]) Array.newInstance(array.getClass().getComponentType(), len);
        for (int i = 0; i < len; i++) {
            newArray[len - 1 - i] = array[i];
        }
        return newArray;
    }

    public static <T> T[][] transpose(T[][] array) {
        int d1 = array.length;
        int d2 = array[0].length;
        @SuppressWarnings("unchecked")
        T[][] newArray = (T[][]) Array.newInstance(array.getClass().getComponentType().getComponentType(), d2, d1);
        for (int y = 0; y < d1; y++) {
            for (int x = 0; x < d2; x++) {
                newArray[x][y] = array[y][x];
            }
        }
        return newArray;
    }

    public static <T> int getFirstIndex(T[] array, Function<T, Boolean> tester) {
        for (int i = 0; i < array.length; i++) {
            if (tester.apply(array[i])) {
                return i;
            }
        }
        return -1;
    }

    public static BufferedImage getImage(Color[][] clrs) {
        BufferedImage image = new BufferedImage(clrs[0].length, clrs.length, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < clrs.length; y++) {
            for (int x = 0; x < clrs[y].length; x++) {
                image.setRGB(x, y, clrs[y][x].getRGB());
            }
        }

        return image;
    }

    public static Color[][] getColors(BufferedImage img) {
        int h = img.getHeight();
        int w = img.getWidth();
        Color[][] colors = new Color[h][w];

        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                int clr = img.getRGB(x, y);
                colors[y][x] = Color.decode(clr + "");
            }
        }

        return colors;
    }

    public static void dispImage(BufferedImage img) {
        JFrame frame = new JFrame();
        frame.getContentPane().add(new JLabel(new ImageIcon(img)));
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void dispImage(Color[][] img) {
        dispImage(getImage(img));
    }

    public static Color[][] filterColors(Color[][] img, Color replacement, Color... filters) {
        int d1 = img.length;
        int d2 = img[0].length;
        Color[][] newImg = new Color[d1][d2];
        for (int i = 0; i < d1; i++) {
            for (int j = 0; j < d2; j++) {

                boolean isBackground = false;
                for (Color filter : filters) {
                    if (img[i][j].getRGB() == filter.getRGB()) {
                        isBackground = true;
                    }
                }

                if (isBackground) {
                    newImg[i][j] = replacement;
                } else {
                    newImg[i][j] = img[i][j];
                }
            }
        }
        return newImg;
    }

    public static double imgavg(Color[][] img) {
        double sum = 0;
        int count = img.length * img[0].length * 3;
        for (Color[] colors : img) {
            for (Color color : colors) {
                sum += (double) (color.getRed() + color.getGreen() + color.getBlue()) / count;
            }
        }
        return sum;
    }
}