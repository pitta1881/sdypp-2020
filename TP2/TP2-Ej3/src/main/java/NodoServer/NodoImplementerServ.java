package NodoServer;

import log.Log;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;

public class NodoImplementerServ implements RemoteNodoServ {
    Log myLog;


    public NodoImplementerServ(Log _myLog) {
        this.myLog = _myLog;
    }



    @Override
    public String getClima() throws RemoteException {
        Clima c = new Clima();
        try {
            Thread.sleep(30);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return c.getClima();
    }

    @Override
    public int getPrimo() throws RemoteException {
        NumPrimo n = new NumPrimo();
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return n.getPrimo();
    }

    @Override
    public String getFechaHora() throws RemoteException {
        FechaHora fh = new FechaHora();
        try {
            Thread.sleep(35);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return fh.getFechaHora();
    }

    @Override
    public String getDireccionRandom() throws RemoteException {
        DireccionRandom n = new DireccionRandom();
        try {
            Thread.sleep(35);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return n.getDirRandom();
    }

    @Override
    public String getNombreRandom() throws RemoteException {
        NombreRandom nr = new NombreRandom();
        try {
            Thread.sleep(35);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return nr.getNombreRandom();
    }

    @Override
    public byte[] getImageSobel(byte[] toByteArray) throws RemoteException {
        ByteArrayOutputStream bytes = null;
        try {
            RenderedImage image = ImageIO.read(new ByteArrayInputStream(toByteArray));
            bytes = new ByteArrayOutputStream();

            OperadorSobel opSobel = new OperadorSobel(image);
            String path = opSobel.ejecutar();

            File file = new File(path);
            BufferedImage bi = ImageIO.read(file);

            ImageIO.write(bi, "PNG", bytes);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return bytes.toByteArray();
    }

    /*
    private void addLog(String aLoguear) {
        try {
            String datosCliente = RemoteServer.getClientHost();
            myLog.addToLog("Mensaje Recibido de " + datosCliente + " -> " + aLoguear, "INFO");
        } catch (ServerNotActiveException e) {
            e.printStackTrace();
        }
    }
    */

}




