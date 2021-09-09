package Ej7.servidor;

import Ej7.log.Log;
import Ej7.rmi.RemoteInterface;
import Ej7.rmi.Tarea;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ServidorImplementer extends UnicastRemoteObject implements RemoteInterface {
    Log log;

    public ServidorImplementer (Log log) throws RemoteException {
        this.log = log;
    }

    @Override
    public Object ejecutar(Tarea tarea) throws RemoteException {

        log.addToLog("Ejecutando " + tarea.getClass().getSimpleName(), "INFO");
        Object resu = tarea.ejecutar();

        log.addToLog("Resulado obtenido de : "+ tarea.getClass().getSimpleName()+
                " : " + resu.toString(), "INFO");

        return resu;
    }
}
