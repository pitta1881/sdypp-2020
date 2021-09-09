package servidor;

import RMI.RemoteInterface;

import sobel.OperadorSobel;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;


public class ServidorImpl extends UnicastRemoteObject implements RemoteInterface, Serializable{
    String pathSalida = "";
    private OperadorSobel opSobel;
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );


    public ServidorImpl() throws RemoteException {
        super();
    }

    public byte[] getConversion(byte[] toByteArray, int orden) throws IOException {
        RenderedImage image = ImageIO.read(new ByteArrayInputStream(toByteArray));
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        opSobel = new OperadorSobel(image);

        this.pathSalida = opSobel.ejecutar(orden);

        // Levanta la parte que convirtio y la retorna
        File file = new File(pathSalida);
        BufferedImage imageReturn = ImageIO.read(file);

        ImageIO.write(imageReturn, "PNG", bytes);

        return bytes.toByteArray();
    }

}
