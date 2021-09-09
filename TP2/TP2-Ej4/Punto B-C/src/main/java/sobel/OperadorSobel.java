package sobel;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

public class OperadorSobel {

    //private static final Logger logger = LoggerFactory.getLogger(OperadorSobel.class);
    private RenderedImage image;

    public OperadorSobel (RenderedImage image) {
        this.image = image;
    }

   public String ejecutar (int orden) throws IOException {

       int x = image.getWidth();
       int y = image.getHeight();

       int[][] edgeColors = new int[x][y];
       int maxGradient = -1;

       for (int i = 1; i < x - 1; i++) {
           for (int j = 1; j < y - 1; j++) {

               int val00 = getGrayScale(((BufferedImage)image).getRGB(i - 1, j - 1));
               int val01 = getGrayScale(((BufferedImage)image).getRGB(i - 1, j));
               int val02 = getGrayScale(((BufferedImage)image).getRGB(i - 1, j + 1));

               int val10 = getGrayScale(((BufferedImage)image).getRGB(i, j - 1));
               int val11 = getGrayScale(((BufferedImage)image).getRGB(i, j));
               int val12 = getGrayScale(((BufferedImage)image).getRGB(i, j + 1));

               int val20 = getGrayScale(((BufferedImage)image).getRGB(i + 1, j - 1));
               int val21 = getGrayScale(((BufferedImage)image).getRGB(i + 1, j));
               int val22 = getGrayScale(((BufferedImage)image).getRGB(i + 1, j + 1));

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

               ((BufferedImage)image).setRGB(i, j, edgeColor);
           }
       }

       File outputfile = new File("./sobel_salida"+orden+".png");
       ImageIO.write(image, "png", outputfile);

       return outputfile.getAbsolutePath();
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