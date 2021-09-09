package LoadBalancer;

import NodoServer.Nodo;
import NodoServer.RemoteNodoServ;
import log.Log;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RemoteServer;
import java.rmi.server.ServerNotActiveException;
import java.util.ArrayList;

public class LoadBalancerImplementer implements RemoteIntBalancer {
    Log myLog;
    ArrayList<Nodo> listaNodos;
    int puertoNodo = 20010;
    int numeroNodo = 1;
    boolean tokenCreandoNewNodo;

    public LoadBalancerImplementer(Log myLog, ArrayList<Nodo> listaNodos) {
        this.myLog = myLog;
        this.listaNodos = listaNodos;
        //inicio dos Nodos Primarios
        crearNuevoNodo(true);
        crearNuevoNodo(true);
    }

    private void crearNuevoNodo(boolean primario) {
        tokenCreandoNewNodo = true;
        Nodo newNodo = new Nodo(puertoNodo, "Nodo " + this.numeroNodo++, primario);
        new Thread(newNodo).start();
        Registry clientRMI;
        RemoteNodoServ operacionesServices = null;
        try {
            clientRMI = LocateRegistry.getRegistry("localhost", puertoNodo);
            operacionesServices = (RemoteNodoServ) clientRMI.lookup("Operaciones-Services");
            newNodo.setOperaciones(operacionesServices);
            synchronized (listaNodos) {
                this.listaNodos.add(newNodo);
                listaNodos.notifyAll();
            }
        } catch (NotBoundException | RemoteException e) {
            e.printStackTrace();
        }
        tokenCreandoNewNodo = false;
        this.puertoNodo += 10;
    }

    private Nodo balancearCarga(int pesoNuevo) {
        Nodo nodoRetorno = null;
        boolean encontre = false;
        System.out.println("Nombre\t\tConex.\t\tPesoAct.\tEstado");
        for (Nodo miNodo:listaNodos){
            System.out.println(miNodo.toString());
            if (!encontre && miNodo.aceptaCliente(pesoNuevo)) {
                encontre = true;
                nodoRetorno = miNodo;
            }
        }
        return nodoRetorno;
    }

    private boolean verificarCreacionNewNodo() {
        boolean retorno = false;
        float sumaPromedios = 0;
        Nodo nodoTempSinCarga = null;
        for (Nodo miNodo:listaNodos){
            sumaPromedios += miNodo.porcentajeUso();
            if (miNodo.getEstado().equals("Sin Carga") && !miNodo.esPrimario()) {
                nodoTempSinCarga = miNodo;
            }
        }
        float totalUso = sumaPromedios / listaNodos.size();
        if (totalUso >= 55) {
            System.out.println("Soy mayor a 55% - creo un nuevo nodo");
            retorno = true;
        } else if (totalUso <= 13) {
            if (nodoTempSinCarga != null) {
                listaNodos.remove(nodoTempSinCarga);
                System.out.println("Soy menor a 13% - inactivo un nodo sin carga");
            }
        }
        return retorno;
    }

    private Nodo preProceso(String servicio, int peso) throws RemoteException {
        addLog("Servicio de " + servicio);
        Nodo nodoToWork = null;
        boolean flagNewNodo = false;
        boolean encontre = false;
        synchronized (listaNodos) {
            while (!encontre) {
                nodoToWork = balancearCarga(peso);
                if (nodoToWork != null) {
                    encontre = true;
                    nodoToWork.sumPeso(peso);
                    System.out.println("Soy " + servicio + ", mi peso es de " + peso + " y entré en el " + nodoToWork.getNombreNodo());
                    if (!tokenCreandoNewNodo) {
                        flagNewNodo = verificarCreacionNewNodo();
                    }
                } else {
                    try {
                        // System.out.println("me dormi!");
                        listaNodos.wait(0); //infinito hasta q lo despierten por creacion de nuevo nodo o xq se termino una tarea
                        // System.out.println("me despertaron!");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        if (flagNewNodo && !tokenCreandoNewNodo) {
            crearNuevoNodo(false);
        }
        return nodoToWork;
    }

    private void postProceso(Nodo nodo, int peso) {
        synchronized (listaNodos) {
            nodo.resPeso(peso);
            listaNodos.notify();
        }
    }


    @Override
    public String getClima() throws RemoteException {
        int peso = 20;
        Nodo nodoToWork = preProceso("Clima", peso);
        String retorno = nodoToWork.getOperaciones().getClima();
        postProceso(nodoToWork, peso);
        return retorno;
    }

    @Override
    public int getPrimo() throws RemoteException {
        int peso = 40;
        Nodo nodoToWork = preProceso("Numero Primo", peso);
        int retorno = nodoToWork.getOperaciones().getPrimo();
        postProceso(nodoToWork, peso);
        return retorno;
    }

    @Override
    public String getFechaHora() throws RemoteException {
        int peso = 30;
        Nodo nodoToWork = preProceso("Fecha y Hora", peso);
        String retorno = nodoToWork.getOperaciones().getFechaHora();
        postProceso(nodoToWork, peso);
        return retorno;
    }

    @Override
    public String getDireccionRandom() throws RemoteException {
        int peso = 35;
        Nodo nodoToWork = preProceso("Direccion Random", peso);
        String retorno = nodoToWork.getOperaciones().getDireccionRandom();
        postProceso(nodoToWork, peso);
        return retorno;
    }

    @Override
    public String getNombreRandom() throws RemoteException {
        int peso = 25;
        Nodo nodoToWork = preProceso("Nombre Random", peso);
        String retorno = nodoToWork.getOperaciones().getNombreRandom();
        postProceso(nodoToWork, peso);
        return retorno;
    }

    @Override
    public byte[] getConversion()  throws RemoteException {
        int peso = 40;
        Nodo nodoToWork = preProceso("Sobel", peso);
        BufferedImage bImageFromConvert = null;
        byte[] imageInByte = null;

        try {
            File file = new File("./src/main/resources/img/image2.png");
            BufferedImage image = ImageIO.read(file);

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            ImageIO.write(image, "PNG", bytes);

            imageInByte = nodoToWork.getOperaciones().getImageSobel(bytes.toByteArray());

            outputStream.write (imageInByte);
            byte imagenByteFinal[] = outputStream.toByteArray();
            InputStream inFinal = new ByteArrayInputStream(imagenByteFinal);
            bImageFromConvert = ImageIO.read(inFinal);

            File output = new File("./src/main/java/ClientSide/sobel_resultado.png");
            ImageIO.write(bImageFromConvert, "png", output);

        } catch ( IOException e) {
            e.printStackTrace();
        }
        postProceso(nodoToWork, peso);
        return new byte[0];
    }


    private void addLog(String aLoguear) {
        try {
            String datosCliente = RemoteServer.getClientHost();
            myLog.addToLog("Mensaje Recibido de " + datosCliente + " -> " + aLoguear, "INFO");
        } catch (ServerNotActiveException e) {
            e.printStackTrace();
        }
    }
}




