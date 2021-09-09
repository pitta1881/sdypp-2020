package NodoServer;

import log.Log;

import java.io.File;
import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Nodo implements Runnable{

    private int conexionesActivas = 0;
    private int pesoActual = 0;
    private int umbralPeso = 100;
    private int umbralConexiones = 10;
    private int port;
    private String nombreNodo;
    private String estado = "Sin Carga";
    private boolean esPrimario;
    private RemoteNodoServ operacionesServicios;

    public Nodo(int port, String nombreNodo, boolean esPrimario){
        this.port = port;
        this.nombreNodo = nombreNodo;
        this.esPrimario = esPrimario;
    }

    @Override
    public void run() {
        Log myLogNodo = null;
        try {
            long carpeta = System.currentTimeMillis();
            File directory = new File("./src/main/resources/log/Nodo" + carpeta);
            directory.mkdirs();
            myLogNodo = new Log( directory + "/myLog.log");
            myLogNodo.addToLog("Iniciando Servidor ", "INFO");
            try {
                Registry serverRMI = LocateRegistry.createRegistry(port);
                NodoImplementerServ si = new NodoImplementerServ(myLogNodo);
                RemoteNodoServ serviceA = (RemoteNodoServ) UnicastRemoteObject.exportObject(si, port+2000);

                serverRMI.rebind("Operaciones-Services", serviceA);
                myLogNodo.addToLog("Servicio Iniciado " + nombreNodo, "INFO");
            } catch (IOException e2) {
                myLogNodo.addToLog("Servidor No Iniciado. El puerto " + port + " ya está en uso.", "SEVERE");
            }
        } catch (SecurityException | IOException e1) {
            myLogNodo.addToLog("No tiene permisos de Read/Write en el archivo .log","SEVERE");
        }
    }

    public int getConexionesActivas() {
        return this.conexionesActivas;
    }

    private void sumConexionesActivas() {
        this.conexionesActivas += 1;
    }

    private void resConexionesActivas() { this.conexionesActivas -= 1; }

    public int getPeso(){
        return this.pesoActual;
    }

    public void setEstado(String newEstado) {
        this.estado = newEstado;
    }

    public String getEstado() {
        return this.estado;
    }

    private void verificarEstado() {
        int pesoActual = this.getPeso();
        int conexionesActuales = this.getConexionesActivas();
        int alertaPeso = 60 * umbralPeso / 100; //60%
        int alertaConexiones = 60 * umbralConexiones / 100; //60%
        int criticoPeso = 80 * umbralPeso / 100; //80%
        int criticoConexiones = 80 * umbralConexiones / 100; //80%
        if ((pesoActual >= criticoPeso) || (conexionesActuales >= criticoConexiones)){
            this.setEstado("Critico");
        } else
        if ((pesoActual >= alertaPeso) || (conexionesActuales >= alertaConexiones)){
            this.setEstado("Alerta");
        } else
        if ((pesoActual > 0) || (conexionesActuales > 0)){
            this.setEstado("Normal");
        }
        if((pesoActual == 0) && (conexionesActuales == 0)){
            this.setEstado("Sin Carga");
        }
    }

    public float porcentajeUso(){
        return this.getPeso()*100/this.umbralPeso;
    }

    public void sumPeso(int pesoNuevo){
        this.pesoActual += pesoNuevo;
        this.sumConexionesActivas();
        this.verificarEstado();
    }

    public void resPeso(int pesoViejo){
        this.pesoActual -= pesoViejo;
        this.resConexionesActivas();
        this.verificarEstado();
    }

    public String getNombreNodo() {
        return this.nombreNodo;
    }

    public boolean esPrimario() {
        return this.esPrimario;
    }

    public RemoteNodoServ getOperaciones(){
        return this.operacionesServicios;
    }

    public void setOperaciones(RemoteNodoServ operacionesServices) {
        this.operacionesServicios = operacionesServices;
    }

    public boolean aceptaCliente(int pesoNuevo) {
        boolean retorno = false;
        if ((!this.getEstado().equals("Critico")) && (this.getPeso()+pesoNuevo <= this.umbralPeso)){
            retorno = true;
        }
        return retorno;
    }

    @Override
    public String toString() {
        return this.getNombreNodo() + "\t\t" + this.getConexionesActivas() + "\t\t\t" + this.getPeso() + "\t\t\t" + this.getEstado();
    }
}
