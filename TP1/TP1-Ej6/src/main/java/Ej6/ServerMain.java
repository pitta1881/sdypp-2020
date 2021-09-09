package Ej6;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ServerMain {

	public static void main(String[] args) {
		String carpeta = ServerMain.class.getPackageName();
		Log myLog = null;
		final int PORT = 9000;

		try {
			myLog = new Log("./src/main/java/" + carpeta + "/log" + carpeta + ".log");
			myLog.addToLog("Iniciando Servidor", "INFO");
			try {
				Registry serverRMI = LocateRegistry.createRegistry(PORT);
				ServerImplementer si = new ServerImplementer(myLog);				

				RemoteInt serviceA = (RemoteInt) UnicastRemoteObject.exportObject(si, 8000);
				serverRMI.rebind("Vectores-Services", serviceA);
				myLog.addToLog("Servicio Iniciado", "INFO");

				System.out.println("Presione Enter para terminar el Servidor");
				System.in.read();
				try {
					serverRMI.unbind("Vectores-Services");
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
