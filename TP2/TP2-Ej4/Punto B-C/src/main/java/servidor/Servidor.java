package servidor;

import log.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Servidor implements Serializable {
    protected int port;
    protected int particiones;
    protected HashMap<Integer,Boolean> portEstados;


    public Servidor (int port) {
        this.port = port;
        portEstados = new HashMap<>();
    }


    public void inicar() {
        try {

            ServerSocket ss = new ServerSocket(this.port);

            Log myLog = null;
            myLog = new Log("./src/main/resources/log/servidor.log");
            myLog.addToLog("Iniciando Servidor en puerto: " +this.port, "INFO");

            while (true) {
                Socket sc = ss.accept();
                myLog.addToLog("Cliente conectado : " + sc.getInetAddress().getCanonicalHostName() + ":" + sc.getPort(), "INFO");
                BufferedReader input = new BufferedReader(new InputStreamReader(sc.getInputStream()));
                String inputMsg = input.readLine();

                this.particiones = Integer.parseInt(inputMsg);
                final int MAX_PORTS = 9001+this.particiones;

                myLog.addToLog("El cliente desea procesar la imagen en  : " + this.particiones +" partes.", "INFO");
                myLog.addToLog("Se necesitarán : " +(MAX_PORTS-9001) + " PROCESOS", "INFO");


                // Creamos un array de sv
                List<ServidorImpl> svImpList = new ArrayList<>();
                for (int i=0; i<this.particiones;i++) {
                    myLog.addToLog("Crendo nueva : " + i + " instancia de ServidorImpl" , "INFO");
                    svImpList.add(new ServidorImpl());
                }

                // Creamos un array de registros
                List<Registry> registryList = new ArrayList<>();
                for (int i=9001; i<MAX_PORTS;i++) {
                    myLog.addToLog("Registrando en puerto : " + i , "INFO");
                    registryList.add(LocateRegistry.createRegistry(i));

                    portEstados.put(i,true); // Almacenamos el puerto y valor de estado
                }

                int puertoActual = 9001;
                // En funcion al N de particiones creamos N servicios
                for (int i=0; i<this.particiones; i++){
                    String estadoPort = portEstados.get(puertoActual) ? "OK":"FALLA";
                    myLog.addToLog("Estado del puerto :" + puertoActual +" es : " + estadoPort, "INFO");

                    if (portEstados.get(puertoActual)) { // si estado es true -> puerto funcionando
                        registryList.get(i).rebind("Sobel", svImpList.get(i));
                    } else {
                        myLog.addToLog("Fallo un proceso, se solicita proceso auxiliar", "INFO");
                        procesoAuxiliar(this.particiones, svImpList.get(i), registryList, myLog);

                    }
                    puertoActual ++;

                    if(puertoActual == 9002) { // modificamos el estado del puerto
                        myLog.addToLog("Forzando la falla de un proceso", "INFO");
                        portEstados.replace(puertoActual, false);
                    }
                }
                myLog.addToLog("Finalizo de procesar la imagen" , "INFO");
            }
        } catch (Exception e) {
            System.out.println("Error : " + e.getMessage());
        }
    }

    public static void procesoAuxiliar(int cant_particiones, ServidorImpl svImpl, List<Registry> registryList, Log myLog) {

        int portAux = 9000+cant_particiones+1;
        myLog.addToLog("Proceso auxiliar en port: " + portAux, "INFO");
        try {

            registryList.add(LocateRegistry.createRegistry(portAux));
            registryList.get(1).rebind("Sobel", svImpl);
            myLog.addToLog("Se proceso parte de imagen en proceso auxiliar", "INFO");
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        Servidor s = new Servidor(8000);
        s.inicar();
    }
}