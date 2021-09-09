package Ej1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Cliente {

	public static void main(String[] args) {
		Scanner reader = new Scanner(System.in);
		String mensajeCliente;
		String mensajeServidor;
		final String HOST = "localhost";
		final int PORT = 10000;
		boolean salir = true;
		boolean conectado = false;
		String nombreUsuario = null;

		try {
			nombreUsuario = InetAddress.getLocalHost().getHostName();
			try {
				Socket cliente = new Socket(HOST, PORT);
				BufferedReader in = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
				PrintWriter out = new PrintWriter(cliente.getOutputStream(), true);
				conectado = true;
				System.out.println(nombreUsuario + " > Conectando al Servidor");
				System.out.println(
						"Servidor > Escriba algo para recibir el ECHO del Servidor. Escriba Exit para salir. ");
				while (salir) {
					System.out.print(nombreUsuario + " > ");
					mensajeCliente = reader.nextLine();
					out.println(mensajeCliente);
					if (!mensajeCliente.equals("Exit")) {
						mensajeServidor = in.readLine();
						System.out.println("Servidor > " + mensajeServidor);
					} else {
						salir = false;
						System.out.println(nombreUsuario + " > Conexion terminada.");
						reader.close();
						cliente.close();
					}
				}
			} catch (IOException e2) {
				if (conectado) {
					// en caso que ya se haya conectado al HOST pero luego éste no responda mas
					System.out.println(nombreUsuario + " > El servidor no responde. Terminando Programa.");
				} else {
					// en caso que no se pueda conectar al HOST al primer intento.
					System.out
							.println(nombreUsuario + " > No se puede conectar con el Servidor. Verifique IP y Puerto.");
				}
			}
		} catch (UnknownHostException e1) {
			System.out.println("No se pudo encontrar el nombre del usuario. Host desconocido.");
		}
	}

}
