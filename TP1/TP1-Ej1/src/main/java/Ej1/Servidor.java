package Ej1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {

	public static void main(String[] args) {
		Log myLog = null;
		String carpeta = Servidor.class.getPackageName();
		ServerSocket servidor = null;
		Socket cliente = null;
		BufferedReader in;
		PrintWriter out;

		String mensajeCliente;
		String mensajeServidor;
		boolean salir = true;

		final int PORT = 10000;

		String datosCliente = null;

		try {
			myLog = new Log("./src/main/java/" + carpeta + "/log" + carpeta + ".log");
			myLog.addToLog("Iniciando Servidor", "INFO");
			try {
				servidor = new ServerSocket(PORT);
				myLog.addToLog("Servidor Iniciado", "INFO");
				cliente = servidor.accept();
				try {
					datosCliente = cliente.getInetAddress().getCanonicalHostName() + ":" + cliente.getPort();
					myLog.addToLog("Cliente Conectado -> " + datosCliente, "INFO");
					while (salir) {

						in = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
						out = new PrintWriter(cliente.getOutputStream(), true);

						mensajeCliente = in.readLine();

						myLog.addToLog("Mensaje Recibido de -> " + datosCliente, "INFO");
						mensajeServidor = mensajeCliente;
						if (mensajeCliente.equals("Exit")) {
							cliente.close();
							myLog.addToLog("Cliente Desconectado -> " + datosCliente, "INFO");
							salir = false;
						} else {
							out.println(mensajeServidor);
							myLog.addToLog("Mensaje Enviado a -> " + datosCliente, "INFO");
						}
					}
				} catch (IOException e3) {
					// el cliente se desconecta sin escribir "Exit"
					myLog.addToLog("Cliente Desconectado -> " + datosCliente, "INFO");
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
