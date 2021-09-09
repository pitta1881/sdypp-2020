package Ej7.servidor;

import Ej7.log.Log;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Servidor {

    public static void main(String[] args) {
        String nombreArchivo = "log";
        final int port = 9001;
        Log log = null;

        try {
            log = new Log("./src/main/java/Ej7/log/" + nombreArchivo + ".log");
            log.addToLog("Iniciando Servidor", "INFO");

            try {
                Registry registro = LocateRegistry.createRegistry(port);
                registro.rebind("Ejercicio7", new ServidorImplementer(log));
                log.addToLog("Servidor Iniciado, escuchando en puerto: " + port, "INFO");
            } catch (Exception e) {
                log.addToLog("Error al iniciar servidor en puerto : " +port, "SEVERE");
                System.out.println("error : " + e.getMessage());
            }
            while(true){}

        }catch(Exception e){
            log.addToLog("No tiene permisos de Lectura/Escritura en el archivo .log","SEVERE");
        }
    }
}
