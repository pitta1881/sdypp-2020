package Ej6;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {

	public static void main(String[] args) {
		boolean conectado = false;
		String nombreUsuario = null;
		final int PORT = 9000;
		try {
			nombreUsuario = InetAddress.getLocalHost().getHostName();
			try {
				Registry clientRMI = LocateRegistry.getRegistry("localhost", PORT);
				System.out.println(nombreUsuario + " > Conectando al Servidor");
				conectado = true;
				
				try {
					RemoteInt vectoresServices = (RemoteInt) clientRMI.lookup("Vectores-Services");
					int largo = 10;
					int[] v1 = new int[largo];
					int[] v2 = new int[largo];
					int[] v3 = new int[largo];
					int[] v4 = new int[largo];
					for (int i = 0; i < largo; i++) {
						v1[i] = (int) ((Math.random() * 100) +1);
						v2[i] = (int) ((Math.random() * 100) +1);
					}
					
					System.out.println("Servidor > V1 ANTES = " + vectoresServices.mostrarVector(v1));
					System.out.println("Servidor > V2 ANTES = " + vectoresServices.mostrarVector(v2));
					v3 = vectoresServices.sumaVectores(v1,v2);
					v4 = vectoresServices.restaVectores(v1,v2);
					System.out.println("Servidor >   V1+V2  = " + vectoresServices.mostrarVector(v3));
					System.out.println("Servidor >   V1-V2  = " + vectoresServices.mostrarVector(v4));
					System.out.println("Servidor > V1 DESP. = " + vectoresServices.mostrarVector(v1));
					System.out.println("Servidor > V2 DESP. = " + vectoresServices.mostrarVector(v2));
					
					System.out.println(nombreUsuario + " > Conexion terminada");
				} catch (NotBoundException e3) {
					System.out.println("No se encontró el servicio solicitado.");
				}
			} catch (IOException e2) {
				if (conectado) {
					// en caso que ya se haya conectado al HOST pero luego éste no responda mas
					System.out.println(nombreUsuario + " > El servidor no responde. Terminando Programa.");
				} else {
					// en caso que no se pueda conectar al HOST al primer intento.
					System.out.println(nombreUsuario + " > No se puede conectar con el Servidor. Verifique IP y Puerto.");
				}
			}
		} catch (UnknownHostException e1) {
			System.out.println("No se pudo encontrar el nombre del usuario. Host desconocido.");
		}
	}

}
