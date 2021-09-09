package Ej4;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Cliente {

	public static void main(String[] args) {
		Scanner reader = new Scanner(System.in);
		final String HOST = "localhost";
		final int PORT = 10000;
		boolean conectado = false;
		String nombreUsuario = null;
		try {
			nombreUsuario = InetAddress.getLocalHost().getHostName();
			try {
				Socket cliente = new Socket(HOST, PORT);
				PrintWriter out = new PrintWriter(cliente.getOutputStream(), true);
				conectado = true;
				System.out.println(nombreUsuario + " > Conectando al Servidor");
				System.out.println("Servidor > Escriba su Nick para identificarse.");
				System.out.print(nombreUsuario + " > ");
				String nickCliente = reader.nextLine();
				out.println(nickCliente);
				ClienteSingleMenu unCliente = new ClienteSingleMenu(cliente, nickCliente, nombreUsuario);
				unCliente.menu();
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
		reader.close();
	}

	private static class ClienteSingleMenu {

		private Socket cliente;
		private String nickCliente;
		private String nombreUsuario;
		Gson gson = new Gson();

		private ClienteSingleMenu(Socket cliente, String nickCliente, String nombreUsuario) {
			this.cliente = cliente;
			this.nickCliente = nickCliente;
			this.nombreUsuario = nombreUsuario;
		}


		private void menu() throws IOException {
			boolean salir = true;
			PrintWriter out = new PrintWriter(cliente.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
			Scanner reader = new Scanner(System.in);
			String opcion;
			String cantidadMensajesPend;
			while (salir) {
				cantidadMensajesPend = in.readLine();
				System.out.println("===MENU===");
				System.out.println("1. VER MENSAJES " + "(" + cantidadMensajesPend + ")");
				System.out.println("2. ESCRIBIR MENSAJE");
				System.out.println("3. BORRAR MENSAJES");
				System.out.println("4. ACTUALIZAR BANDEJA");
				System.out.println("0. SALIR");
				System.out.println("==========");
				System.out.print(nombreUsuario + " > ");
				opcion = reader.nextLine();
				out.println(opcion);
				switch (opcion) {
				case "1":
					leerMensajesPendientes();
					break;
				case "2":
					escribirNuevoMensaje();
					break;
				case "3":
					borrarMensajes();
					break;
				case "4":
					break;
				case "0":
					salir = false;
					reader.close();
					cliente.close();
					System.out.println(nombreUsuario + " > Conexion terminada");
					break;
				default:
					System.out.println("Servidor > Opcion incorrecta");
					break;
				}
			}
		}

		private void leerMensajesPendientes() throws IOException {
			Scanner reader = new Scanner(System.in);
			BufferedReader in = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
			String colaMensajesParaMi = in.readLine();
			Type mensajeListType = new TypeToken<ArrayList<Mensaje>>() {
			}.getType();
			ArrayList<Mensaje> miCola = gson.fromJson(colaMensajesParaMi, mensajeListType);
			if (!miCola.isEmpty()) {
				int cantMensajes = miCola.size();
				int indice = 1;
				System.out.println("Servidor > Usted tiene " + cantMensajes + " mensaje/s nuevo/s");
				for (Mensaje mensaje : miCola) {
					System.out.println("=======" + indice++ + "º mensaje nuevo =======");
					System.out.println(mensaje.toString());
					System.out.println("===============================");
					if (indice <= cantMensajes) {
						System.out.println("\nServidor > Presione Enter para continuar.\n");
						reader.nextLine();
					}
				}
			} else {
				System.out.println("Servidor > Usted no tiene nuevos mensajes.");
			}
		}

		private void escribirNuevoMensaje() throws IOException {
			Scanner reader = new Scanner(System.in);
			BufferedReader in = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
			PrintWriter out = new PrintWriter(cliente.getOutputStream(), true);
			String seguroEnviar;
			Mensaje mensaje;
			// escribir el destinatario
			System.out.println("Servidor > Escriba el Destinatario:");
			System.out.print(nombreUsuario + " > ");
			String mensajeDestinatario = reader.nextLine();
			// escribir el mensaje
			System.out.println("Servidor > Escriba el Mensaje:");
			System.out.print(nombreUsuario + " > ");
			String mensajeMensaje = reader.nextLine();
			// hago un json con todos los datos y lo envio
			System.out.println("Servidor > ¿Esta seguro que desea enviar el mensaje? Y/N");
			System.out.print(nombreUsuario + " > ");
			seguroEnviar = reader.nextLine();
			out.println(seguroEnviar);
			if ((seguroEnviar.equals("Y")) || (seguroEnviar.equals("y"))) {
				mensaje = new Mensaje(nickCliente, mensajeDestinatario, mensajeMensaje);
				String jsonString = gson.toJson(mensaje);
				out.println(jsonString);
				Boolean ACK = Boolean.parseBoolean(in.readLine());
				if (ACK == true) {
					System.out.println("Servidor > Mensaje enviado correctamente");
				}
			} else {
				System.out.println("Servidor > Mensaje cancelado.");
			}
		}

		private void borrarMensajes() throws IOException {
			Scanner reader = new Scanner(System.in);
			BufferedReader in = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
			PrintWriter out = new PrintWriter(cliente.getOutputStream(), true);
			String colaMensajesParaMi = in.readLine();
			Type mensajeListType = new TypeToken<ArrayList<Mensaje>>() {
			}.getType();
			ArrayList<Mensaje> miCola = gson.fromJson(colaMensajesParaMi, mensajeListType);
			int cantMensajes = miCola.size();
			out.println(cantMensajes);
			if (!miCola.isEmpty()) {
				int indice = 1;
				System.out.println("Servidor > Usted tiene " + cantMensajes + " mensaje/s nuevo/s");
				for (Mensaje mensaje : miCola) {
					System.out.println("=======" + indice++ + "º mensaje =======");
					System.out.println(mensaje.toString());
					System.out.println("==========================");
					System.out.println("\nServidor > ¿Desea eliminar este mensaje? Y/N");
					System.out.print(nombreUsuario + " > ");
					String eliminar = reader.nextLine();
					if ((eliminar.equals("Y")) || (eliminar.equals("y"))) {
						String idMensaje = Integer.toString(mensaje.getId());
						out.println(idMensaje);
						Boolean ACK = Boolean.parseBoolean(in.readLine());
						if (ACK == true) {
							System.out.println("Servidor > Mensaje borrado correctamente");
						}
					} else {
						out.println(-1);
					}
				}
			} else {
				System.out.println("Servidor > Usted no tiene mensajes para borrar.");
			}
		}

	}
}
