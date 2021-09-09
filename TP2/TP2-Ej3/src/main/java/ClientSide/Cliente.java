package ClientSide;

import LoadBalancer.RemoteIntBalancer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Cliente implements Runnable {

    String servicio;

    public Cliente(String servicio) {
        this.servicio = servicio;
    }

    @Override
    public void run() {
        boolean conectado = false;
        String nombreUsuario = null;
        final int PORT = 9000;
        try {
            nombreUsuario = InetAddress.getLocalHost().getHostName();
            try {
                Registry clientRMI = null;
                RemoteIntBalancer operacionServices = null;
                clientRMI = LocateRegistry.getRegistry("localhost", PORT);
                operacionServices = (RemoteIntBalancer) clientRMI.lookup("Operaciones-Services");
                switch (servicio){
                    case "Clima":
                        System.out.println("Servidor > Clima: " + operacionServices.getClima());
                        break;
                    case "Numero Primo":
                        System.out.println("Servidor > Numero Primo: " + operacionServices.getPrimo());
                        break;
                    case "FechaHora":
                        System.out.println("Servidor > " + operacionServices.getFechaHora());
                        break;
                    case "Direccion Aleatorio":
                        System.out.println("Servidor > Direccion: " + operacionServices.getDireccionRandom());
                        break;
                    case "Nombre Aleatorio":
                        System.out.println("Servidor > Nombres y Apellidos: " + operacionServices.getNombreRandom());
                        break;
                    case "Sobel":
                        System.out.println("Servidor > Sobel: " + operacionServices.getConversion());
                        break;
                    default:
                        break;
                }

            } catch (IOException e2) {
                if (conectado) {
                    // en caso que ya se haya conectado al HOST pero luego éste no responda mas
                    System.out.println(nombreUsuario + " > El servidor no responde. Terminando Programa.");
                } else {
                    // en caso que no se pueda conectar al HOST al primer intento.
                    System.out.println(nombreUsuario + " > No se puede conectar con el Servidor. Verifique IP y Puerto.");
                }
            } catch (NotBoundException e) {
                e.printStackTrace();
            }
        } catch (UnknownHostException e1) {
            System.out.println("No se pudo encontrar el nombre del usuario. Host desconocido.");
        }
    }
}
