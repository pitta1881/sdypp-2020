package ServerSide;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import log.Log;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class ServerMain {

	public static void main(String[] args) {
		Log myLog = null;
		final int PORT = 9000;
		String pathArchivoSaldos = "./src/main/resources/data/Saldos.json";


		try {
			myLog = new Log("./src/main/resources/log/myLog.log");
			myLog.addToLog("Iniciando Servidor", "INFO");
			Reader reader = Files.newBufferedReader(Paths.get(pathArchivoSaldos));
			ArrayList<ClienteBanco> listaClientes = new Gson().fromJson(reader, new TypeToken<ArrayList<ClienteBanco>>() {
			}.getType());
			reader.close();
			try {
				Registry serverRMI = LocateRegistry.createRegistry(PORT);
				ServerImplementer si = new ServerImplementer(myLog, listaClientes);
				ServerImplementer si2 = new ServerImplementer(myLog, listaClientes);

				RemoteInt serviceA = (RemoteInt) UnicastRemoteObject.exportObject(si, 8000);
				RemoteInt serviceB = (RemoteInt) UnicastRemoteObject.exportObject(si2, 8001);
				serverRMI.rebind("Deposito-Services", serviceA);
				serverRMI.rebind("Extraccion-Services", serviceB);
				myLog.addToLog("Servicio Iniciado", "INFO");

				System.out.println("Presione Enter para terminar el Servidor");
				System.in.read();
				try {
					serverRMI.unbind("Deposito-Services");
					serverRMI.unbind("Extraccion-Services");
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
