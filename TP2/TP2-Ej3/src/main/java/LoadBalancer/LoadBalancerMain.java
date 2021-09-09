package LoadBalancer;

import NodoServer.Nodo;
import log.Log;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class LoadBalancerMain {

    public static void main(String[] args) {
        Log myLog = null;
        final int PORT = 9000;
        ArrayList<Nodo> listaNodos = new ArrayList<Nodo>();

        try {
            myLog = new Log("./src/main/resources/log/LoadBalancer/myLog.log");
            myLog.addToLog("Iniciando Servidor", "INFO");
            try {
                Registry serverRMI = LocateRegistry.createRegistry(PORT);
                LoadBalancerImplementer si = new LoadBalancerImplementer(myLog, listaNodos);

                RemoteIntBalancer serviceA = (RemoteIntBalancer) UnicastRemoteObject.exportObject(si, 8000);

                serverRMI.rebind("Operaciones-Services", serviceA);
                myLog.addToLog("Servicio Iniciado", "INFO");

                System.out.println("Presione Enter para terminar el Servidor");
                System.in.read();
                try {
                    serverRMI.unbind("Operaciones-Services");
                    UnicastRemoteObject.unexportObject(si, true);
                    myLog.addToLog("Servidor Terminado", "INFO");
                } catch (NotBoundException e3) {
                    System.out.println("No se encuentrar el Servicio para cerrar.");
                }
            } catch (IOException e2) {
                myLog.addToLog("Servidor No Iniciado. El puerto " + PORT + " ya está en uso.", "SEVERE");
            }
        } catch (SecurityException | IOException e1) {
            myLog.addToLog("No tiene permisos de Read/Write en el archivo .log","SEVERE");
        }
    }

}

