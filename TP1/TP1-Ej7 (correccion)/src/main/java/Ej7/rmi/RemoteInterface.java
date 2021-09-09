package Ej7.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteInterface extends Remote {

    Object ejecutar (Tarea tarea) throws RemoteException;
}
