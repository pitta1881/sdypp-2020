
/*

El operador de Sobel es una máscara que, aplicada a una imagen, permite detectar (resaltar) bordes.
Este operador es una operación matemática que, aplicada a cada pixel y teniendo en cuenta los
pixeles que lo rodean, obtiene un nuevo valor (color) para ese pixel. Aplicando la operación a
cada pixel, se obtiene una nueva imagen que resalta los bordes.

A) Desarrollar un proceso centralizado que tome una imagen, aplique una máscara, y genere un
nuevo archivo con el resultado. 

 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.IIOException;
import javax.imageio.ImageIO;


public class OperadorSobel {

    //private static final Logger logger = LoggerFactory.getLogger(OperadorSobel.class);

    public static void main(String args[]) throws IOException {

        String filename = "./src/main/resources/image2.png";

        try {
            File file = new File(filename);
            BufferedImage image = ImageIO.read(file);

            // Tiempo de inicio
            long startTime = System.currentTimeMillis();

            int x = image.getWidth();
            int y = image.getHeight();

            int[][] edgeColors = new int[x][y];
            int maxGradient = -1;

            for (int i = 1; i < x - 1; i++) {
                for (int j = 1; j < y - 1; j++) {

                    int val00 = getGrayScale(image.getRGB(i - 1, j - 1));
                    int val01 = getGrayScale(image.getRGB(i - 1, j));
                    int val02 = getGrayScale(image.getRGB(i - 1, j + 1));

                    int val10 = getGrayScale(image.getRGB(i, j - 1));
                    int val11 = getGrayScale(image.getRGB(i, j));
                    int val12 = getGrayScale(image.getRGB(i, j + 1));

                    int val20 = getGrayScale(image.getRGB(i + 1, j - 1));
                    int val21 = getGrayScale(image.getRGB(i + 1, j));
                    int val22 = getGrayScale(image.getRGB(i + 1, j + 1));

                    int gx =  ((-1 * val00) + (0 * val01) + (1 * val02))
                            + ((-2 * val10) + (0 * val11) + (2 * val12))
                            + ((-1 * val20) + (0 * val21) + (1 * val22));

                    int gy =  ((-1 * val00) + (-2 * val01) + (-1 * val02))
                            + ((0 * val10) + (0 * val11) + (0 * val12))
                            + ((1 * val20) + (2 * val21) + (1 * val22));

                    double gval = Math.sqrt((gx * gx) + (gy * gy));
                    int g = (int) gval;

                    if(maxGradient < g) {
                        maxGradient = g;
                    }

                    edgeColors[i][j] = g;
                }
            }

            double scale = 255.0 / maxGradient;

            for (int i = 1; i < x - 1; i++) {
                for (int j = 1; j < y - 1; j++) {
                    int edgeColor = edgeColors[i][j];
                    edgeColor = (int)(edgeColor * scale);
                    edgeColor = 0xff000000 | (edgeColor << 16) | (edgeColor << 8) | edgeColor;

                    image.setRGB(i, j, edgeColor);
                }
            }
            long endTime = System.currentTimeMillis() - startTime; // tiempo en que se ejecuta el proceso sobel
            System.out.println("Tiempo Operador Sobel centralizado: " + endTime + " ms");

            File outputfile = new File("sobel_salida.png");
            ImageIO.write(image, "png", outputfile);

            //System.out.println("max : " + maxGradient);
            System.out.println("Se guardo el resultado en : " + outputfile.getAbsolutePath());

        } catch (IIOException e) {
            System.err.println("Error : " + e.getMessage());
        }
    }

    public static int  getGrayScale(int rgb) {
        int r = (rgb >> 16) & 0xff;
        int g = (rgb >> 8) & 0xff;
        int b = (rgb) & 0xff;

        // Calculo sacado de https://en.wikipedia.org/wiki/Grayscale.
        // Conversión a escala de grises
        int gray = (int)(0.2126 * r + 0.7152 * g + 0.0722 * b);

        return gray;
    }
}