package Ej2;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {

	public static void main(String[] args) {
		String carpeta = Servidor.class.getPackageName();
		Log myLog = null;
		ServerSocket servidor = null;

		final int PORT = 10000;

		String datosCliente = null;

		try {
			myLog = new Log("./src/main/java/" + carpeta + "/log" + carpeta + ".log");
			myLog.addToLog("Iniciando Servidor", "INFO");
			try {
			servidor = new ServerSocket(PORT);
				myLog.addToLog("Servidor Iniciado", "INFO");

				while (true) {
					Socket cliente = servidor.accept();
					datosCliente = cliente.getInetAddress().getCanonicalHostName() + ":" + cliente.getPort();
					ClientHandler clientSock = new ClientHandler(cliente, datosCliente, myLog);
					myLog.addToLog("Cliente Conectado -> " + datosCliente, "INFO");

					new Thread(clientSock).start();
				}

			} catch (IOException e2) {
				myLog.addToLog("Servidor No Iniciado. El puerto " + PORT + " ya está en uso.", "SEVERE");
			}
		} catch (SecurityException | IOException e1) {
			myLog.addToLog("No tiene permisos de Read/Write en el archivo .log","SEVERE");
		}
		myLog.addToLog("Servidor Terminado", "INFO");

	}

}
