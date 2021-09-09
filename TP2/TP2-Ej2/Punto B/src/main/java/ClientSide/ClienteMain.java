package ClientSide;

public class ClienteMain {

    public static void main(String[] args) {    //true: extraccion  //false: deposito
        Cliente miCliente = new Cliente(1001, 500, false, "Persona 1");
        Cliente miCliente2 = new Cliente(1001, 250, true, "Persona 2");
        new Thread(miCliente).start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        new Thread(miCliente2).start();
    }
}
