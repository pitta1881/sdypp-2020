package Ej7.cliente;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.InputMismatchException;
import java.util.Scanner;

import Ej7.rmi.RemoteInterface;
import Ej7.tareasgenericas.NumAleatorio;
import Ej7.tareasgenericas.NumAleatorioPrimo;
import Ej7.tareasgenericas.NumPiPrecision;


public class Cliente {

    public static void main(String[] args) {
        String nombreUsuario = "";
        try {
            nombreUsuario = InetAddress.getLocalHost().getHostName();

            try {
                Registry registro = LocateRegistry.getRegistry("127.0.0.1", 9001);
                RemoteInterface ri = (RemoteInterface) registro.lookup("Ejercicio7");

                boolean salir = true;
                Scanner reader = new Scanner(System.in);
                int opcion = 0;
                while (salir) {
                    System.out.println("===MENU===");
                    System.out.println("1. NUMERO RANDOM");
                    System.out.println("2. NUMERO PRIMO RANDOM");
                    System.out.println("3. NUMERO PI con PRECISION");
                    System.out.println("0. SALIR");
                    System.out.println("==========");
                    System.out.print(nombreUsuario + " > ");
                    try {
                        opcion = reader.nextInt();
                        switch (opcion) {
                            case 1:
                                NumAleatorio numAleatorio = new NumAleatorio();
                                System.out.println("Numero aleatorio : " + ri.ejecutar(numAleatorio).toString());
                                break;
                            case 2:
                                NumAleatorioPrimo numAleatorioPrimo = new NumAleatorioPrimo();
                                System.out.println("Numero aleatorio primo: " + ri.ejecutar(numAleatorioPrimo).toString());
                                break;
                            case 3:
                                System.out.print("Ingrese la cantidad de decimales que desea: ");
                                int decimales = reader.nextInt();

                                NumPiPrecision numPi = new NumPiPrecision(decimales);
                                System.out.println("Numero PI con "+decimales+" decimales de precision : " + ri.ejecutar(numPi).toString());
                                break;
                            case 0:
                                salir = false;
                                reader.close();
                                System.out.println(nombreUsuario + " > Conexion terminada");
                                break;
                            default:
                                System.out.println("Servidor > Opcion incorrecta");
                                break;
                        }
                    } catch (InputMismatchException e4) {
                        System.out.println("Servidor > Opcion incorrecta");
                        reader.next();
                    }
                }

            } catch (RemoteException | NotBoundException e) {
                System.out.println("Error : " + e.getMessage());
                e.printStackTrace();
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
