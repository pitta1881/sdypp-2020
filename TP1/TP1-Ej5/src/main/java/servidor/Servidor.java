package servidor;

import log.Log;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;


public class Servidor {

    public static void main(String[] args) {
        String nombreArchivo = "log";
        Log log = null;
        try {
            log = new Log("./src/main/java/log/" + nombreArchivo + ".log");
            log.addToLog("Iniciando Servidor", "INFO");

            try {
                Registry registro = LocateRegistry.createRegistry(9000);
                registro.rebind("Clima", new ServidorImplementer(log));
                log.addToLog("Servicio Iniciado", "INFO");
            } catch (Exception e) {
                System.out.println("error : " + e.getMessage());
            }
        }catch(Exception e){
            log.addToLog("No tiene permisos de Lectura/Escritura en el archivo .log","SEVERE");
        }
    }
}
