package ClientSide;

import ServerSide.RemoteInt;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Cliente implements Runnable {

    int idCliente;
    int montoAOperar;
    boolean tipoOperacion;
    String nombreCliente;

    public Cliente(int idCliente, int montoAOperar, boolean tipoOperacion, String nombreCliente) {
        this.idCliente = idCliente;
        this.montoAOperar = montoAOperar;
        this.tipoOperacion = tipoOperacion;
        this.nombreCliente = nombreCliente;
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
                RemoteInt operacionServices = null;
                clientRMI = LocateRegistry.getRegistry("localhost", PORT);
                if (tipoOperacion) {
                    operacionServices = (RemoteInt) clientRMI.lookup("Extraccion-Services");
                } else {
                    operacionServices = (RemoteInt) clientRMI.lookup("Deposito-Services");
                }
                float saldoCliente = operacionServices.consultaSaldo(idCliente, nombreCliente);
                System.out.println(nombreCliente + " > El saldo Actual del Cliente " + idCliente + " es: $" + saldoCliente);
                if (tipoOperacion) {                                                    //true:extraccion //false:deposito
                    if (saldoCliente - montoAOperar >= 0) {
                        System.out.println(nombreCliente + " > Extrayendo dinero..");
                        float nuevoSaldo = operacionServices.operacionExtraccion(idCliente, montoAOperar, nombreCliente);
                        System.out.println(nombreCliente + " > Su Extraccion de $" + montoAOperar + " fue realizada correctamente.");
                        System.out.println(nombreCliente + " > Su nuevo saldo es: $" + nuevoSaldo);

                    } else {
                        System.out.println(nombreCliente + " > Error al Extraer dinero. No dispone de saldo disponible en su cuenta.");
                    }
                } else {
                    System.out.println(nombreCliente + " > Depositando dinero..");
                    float nuevoSaldo = operacionServices.operacionDeposito(idCliente, montoAOperar, nombreCliente);
                    System.out.println(nombreCliente + " > Su Deposito de $" + montoAOperar + " fue realizado correctamente.");
                    System.out.println(nombreCliente + " > Su nuevo saldo es: $" + nuevoSaldo);
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
