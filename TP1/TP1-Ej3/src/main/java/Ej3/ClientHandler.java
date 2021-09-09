package Ej3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import com.google.gson.Gson;

public class ClientHandler implements Runnable {
	private final Socket cliente;
	private final String datosCliente;
	private final ArrayList<Mensaje> colaMensajes;
	Gson gson = new Gson();
	Log myLog;

	public ClientHandler(Socket socket, String _datosCliente, ArrayList<Mensaje> colaMensajes, Log _myLog) {
		this.cliente = socket;
		this.datosCliente = _datosCliente;
		this.colaMensajes = colaMensajes;
		this.myLog = _myLog;
	}

	public void run() {
		boolean salir = true;

		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
			String nickCliente = in.readLine();
			String opcion;
			while (salir) {
				cantidadMensajesPendientes(nickCliente);
				opcion = in.readLine();
				myLog.addToLog("Mensaje Recibido de " + datosCliente, "INFO");
				switch (opcion) {
				case "1":
					leerMensajesPendientes(nickCliente);
					break;
				case "2":
					addMensajeToCola();
					break;
				case "0":
					salir = false;
					cliente.close();
					myLog.addToLog("Cliente Desconectado -> " + datosCliente, "INFO");
					break;
				default:
					break;
				}
			}
		} catch (IOException e) {
			// el cliente se desconecta sin escribir "Exit"
			myLog.addToLog("Cliente Desconectado -> " + datosCliente, "INFO");
		}
	}

	private void cantidadMensajesPendientes(String nickCliente) throws IOException {
		String destinatario;
		int cantidad = 0;
		PrintWriter out = new PrintWriter(cliente.getOutputStream(), true);
		for (Mensaje mensaje : colaMensajes) {
			destinatario = mensaje.getDestinatario();
			if ((destinatario.equals(nickCliente))) {
				cantidad++;
			}
		}
		out.println(Integer.toString(cantidad));
		myLog.addToLog("Mensaje Enviado a " + datosCliente, "INFO");
	}

	private void leerMensajesPendientes(String nickCliente) throws IOException {
		String destinatario;
		ArrayList<Mensaje> colaParticular = new ArrayList<Mensaje>();
		PrintWriter out = new PrintWriter(cliente.getOutputStream(), true);
		for (Mensaje mensaje : colaMensajes) {
			destinatario = mensaje.getDestinatario();
			if ((destinatario.equals(nickCliente))) {
				colaParticular.add(mensaje);
			}
		}
		String colaJson = gson.toJson(colaParticular);
		out.println(colaJson);
		myLog.addToLog("Mensaje Enviado a " + datosCliente, "INFO");
	}

	private void addMensajeToCola() throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
		String seguroEnviar = in.readLine();
		myLog.addToLog("Mensaje Recibido de " + datosCliente, "INFO");
		if ((seguroEnviar.equals("Y")) || (seguroEnviar.equals("y"))) {
			String mensajeCliente = in.readLine();
			myLog.addToLog("Mensaje Recibido de " + datosCliente, "INFO");
			Mensaje miMensaje = gson.fromJson(mensajeCliente, Mensaje.class);
			colaMensajes.add(miMensaje);
			myLog.addToLog("Mensaje guardado en cola.", "INFO");
		}

	}
}
