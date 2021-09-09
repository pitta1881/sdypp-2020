package Ej2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
	private final Socket cliente;
	private final String datosCliente;
	private Log myLog;

	public ClientHandler(Socket _socket, String _datosCliente, Log _myLog) {
		this.cliente = _socket;
		this.datosCliente = _datosCliente;
		this.myLog = _myLog;
	}

	public void run() {
		String mensajeCliente;
		String mensajeServidor;
		boolean salir = true;		

		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
			PrintWriter out = new PrintWriter(cliente.getOutputStream(), true);
			while (salir) {
				mensajeCliente = in.readLine();
				myLog.addToLog("Mensaje Recibido de " + datosCliente, "INFO");
				if (mensajeCliente.equals("Exit")) {
					cliente.close();
					myLog.addToLog("Cliente Desconectado -> " + datosCliente, "INFO");
					salir = false;
				} else {
					mensajeServidor = mensajeCliente;
					out.println(mensajeServidor);
					myLog.addToLog("Mensaje Enviado a " + datosCliente, "INFO");
				}
			}
		} catch (IOException e) {
			// el cliente se desconecta sin escribir "Exit"
			myLog.addToLog("Cliente Desconectado -> " + datosCliente, "INFO");
		}

	}
}
