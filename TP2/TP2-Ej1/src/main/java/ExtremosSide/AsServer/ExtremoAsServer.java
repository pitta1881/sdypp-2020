package ExtremosSide.AsServer;

import MastersSide.Maestro;
import log.Log;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ExtremoAsServer implements Runnable {

    private String ipServer;
    private int portServer;
    private String dirExtremo;
    private boolean shutdownRequested = false;
    ServerSocket servidor = null;

    public ExtremoAsServer(String ipServer, int portServer, String newDirExtremo) {
        this.ipServer = ipServer;
        this.portServer = portServer;
        this.dirExtremo = newDirExtremo;
    }

    public boolean iniciar() {
        boolean retorno = false;
        String ipCliente = null;
        int portCliente = 0;
        Log myLog = null;
        try {
            File folderLog = new File(dirExtremo + "log/");
            folderLog.mkdir();
            myLog = new Log(dirExtremo + "log/myLog.log");
            myLog.addToLog("Iniciando Extremo como Servidor", "INFO");
            try {
                servidor = new ServerSocket(portServer);
                myLog.addToLog("Extremo como Servidor Iniciado en el Puerto " + portServer, "INFO");
                retorno = true;

                while (true) {
                    Socket cliente = servidor.accept();
                    if (shutdownRequested == false) {
                        ipCliente = cliente.getInetAddress().getCanonicalHostName();
                        portCliente = cliente.getPort();
                        ExtremoAsServerHandler clientSock = new ExtremoAsServerHandler(cliente, ipCliente, portCliente, myLog, dirExtremo + "data/");
                        System.out.println();
                        myLog.addToLog("Cliente Conectado -> " + ipCliente + ":" + portCliente, "INFO");

                        new Thread(clientSock).start();
                    } else {
                        break;
                    }

                }
            } catch (IOException e2) {
                myLog.addToLog("Servidor No Iniciado. El puerto " + portServer + " ya está en uso.", "SEVERE");
            }
        } catch (SecurityException | IOException e1) {
            myLog.addToLog("No tiene permisos de Read/Write en el archivo .log", "SEVERE");
        }
        return retorno;
    }

    @Override
    public void run() {
        while (!iniciar()) {
            setPort(getPortServer()+1);
        }
    }

    public int getPortServer(){
        return this.portServer;
    }

    private void setPort(int portServer){
        this.portServer = portServer;
    }

    public void shutdown() {
        shutdownRequested = true;
        try {
            new Socket(servidor.getInetAddress(),servidor.getLocalPort()).close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
