package LoadBalancer;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteIntBalancer extends Remote{

    String getClima() throws RemoteException;

    int getPrimo() throws RemoteException;

    String getFechaHora() throws RemoteException;

    String getDireccionRandom() throws RemoteException;

    String getNombreRandom() throws RemoteException;

    // Proceso Sobel
    byte[] getConversion()  throws RemoteException;
}
