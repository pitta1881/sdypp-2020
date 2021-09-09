package ClientSide;

public class ClienteMain {

    public static void main(String[] args) {
        int totalClientes = 4000;
        Cliente miCliente;
        String[] servicio = {"Clima", "Numero Primo", "Direccion Aleatorio", "Nombre Aleatorio", "FechaHora"};
        for (int i = 0; i < totalClientes; i++) {
            try {
                int indexServicio = (int) Math.floor(Math.random() * servicio.length);
                int sleepRandom;
                if (i < (2 * totalClientes / 100)) {                //los PRIMEROS 2% comienza con solicitudes separadas
                    sleepRandom = (int) Math.floor((Math.random() * 50) + 50);
                } else if (i > totalClientes - (5 * totalClientes / 100)) {        //los ultimos 5% aumentan todavia mas el tiempo entre peticiones
                    sleepRandom = (int) Math.floor((Math.random() * 50) + 75);
                } else if (i > totalClientes - (10 * totalClientes / 100)) {        //los ultimos 10% aumentan el tiempo entre peticiones
                    sleepRandom = (int) Math.floor((Math.random() * 50) + 50);
                } else if (i > totalClientes - (60 * totalClientes / 100)) {        //los ultimos 60% disminuyan el tiempo entre peticiones
                    sleepRandom =  (int) Math.floor((Math.random() * 10) + 10);
                } else {
                    sleepRandom =  (int) Math.floor((Math.random() * 5) + 20);
                }
                Thread.sleep(sleepRandom);
                if(i == 200) {
                    miCliente = new Cliente("Sobel");
                } else {
                    miCliente = new Cliente(servicio[indexServicio]);
                }
                new Thread(miCliente).start();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
