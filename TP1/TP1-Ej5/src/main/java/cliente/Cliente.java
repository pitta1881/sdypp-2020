package cliente;

import RMI.RemoteInterface;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Cliente {

    public static void main(String[] args)  {
        try {
            Registry registro = LocateRegistry.getRegistry("127.0.0.1",9000);
            RemoteInterface ri = (RemoteInterface) registro.lookup("Clima");

            System.out.println( ri.getClima() );


        } catch (Exception e) {
            System.out.println("Error : "+e.getMessage());
            e.printStackTrace();
        }
    }
    
}
