package MastersSide;

import java.util.ArrayList;

public class Extremo {
    final String ip;
    final int portAsCliente;
    final int portAsServer;
    ArrayList<String> ficheros;

    public Extremo(String ip, int portAsCliente, int portAsServer, ArrayList<String> ficheros){
        this.ip = ip;
        this.portAsCliente = portAsCliente;
        this.portAsServer = portAsServer;
        this.ficheros = ficheros;
    }

    public String getIp() {
        return this.ip;
    }

    public int getPortAsCliente() {
        return this.portAsCliente;
    }

    public int getPortAsServer() {
        return this.portAsServer;
    }

    public ArrayList<String> getFicheros(){
        return this.ficheros;
    }

    public void setFicheros(ArrayList<String> ficheros) {
        this.ficheros = ficheros;
    }
}
