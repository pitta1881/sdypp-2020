package NodoServer;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteNodoServ extends Remote {

    String getClima() throws RemoteException;

    int getPrimo() throws RemoteException;

    String getFechaHora() throws RemoteException;

    String getDireccionRandom() throws RemoteException;

    String getNombreRandom() throws RemoteException;

    byte[] getImageSobel(byte[] toByteArray) throws RemoteException;

}