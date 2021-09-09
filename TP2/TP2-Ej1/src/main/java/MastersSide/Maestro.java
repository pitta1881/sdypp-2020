package MastersSide;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import log.Log;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Enumeration;

public class Maestro {
    private String ipServer;
    private int portServer;
    ArrayList<Extremo> listaExtremos;

    private Maestro(String ipServer, int portServer) {
        this.ipServer = ipServer;
        this.portServer = portServer;
    }

    private boolean inicializar(Log myLog, File fileExtremos, File fileMaestros) {
        boolean retorno = false;
        ServerSocket servidor = null;
        String ipCliente = null;
        int portCliente = 0;
        try {
            servidor = new ServerSocket(portServer);
            myLog.addToLog("Servidor Iniciado en el Puerto " + portServer, "INFO");
            listaExtremos = recibirListadoExtremos(myLog, fileExtremos, fileMaestros);
            retorno = true;

            while (true) {
                Socket cliente = servidor.accept();
                ipCliente = cliente.getInetAddress().getHostAddress();
                boolean local = false;
                if (ipCliente.equals("127.0.0.1")) {
                    ipCliente = getLocalAddress().getHostAddress();
                    local = true;
                }
                portCliente = cliente.getPort();
                ExtremoHandler clientSock = new ExtremoHandler(cliente, ipCliente, portCliente, myLog, fileExtremos, fileMaestros, listaExtremos, getLocalAddress().getHostAddress(), portServer);
                if (local) {
                    myLog.addToLog("Extremo/Maestro Conectado -> " + ipCliente + ":" + portCliente + " -> (localhost)", "INFO");
                } else {
                    myLog.addToLog("Extremo/Maestro Conectado -> " + ipCliente + ":" + portCliente, "INFO");
                }

                new Thread(clientSock).start();
            }
        } catch (IOException e2) {
            myLog.addToLog("Servidor No Iniciado. El puerto " + portServer + " ya está en uso.", "SEVERE");
        }
        return retorno;
    }

    /**
     * Finds a local, non-loopback, IPv4 address
     *
     * @return The first non-loopback IPv4 address found, or
     * <code>null</code> if no such addresses found
     * @throws SocketException If there was a problem querying the network
     *                         interfaces
     */
    public static InetAddress getLocalAddress() throws SocketException {
        Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces();
        while (ifaces.hasMoreElements()) {
            NetworkInterface iface = ifaces.nextElement();
            Enumeration<InetAddress> addresses = iface.getInetAddresses();

            while (addresses.hasMoreElements()) {
                InetAddress addr = addresses.nextElement();
                if (addr instanceof Inet4Address && !addr.isLoopbackAddress()) {
                    return addr;
                }
            }
        }
        return null;
    }

    public String getIpServer() {
        return ipServer;
    }

    public int getPortServer() {
        return portServer;
    }

    private void setPort(int newPortServer) {
        this.portServer = newPortServer;
    }

    private ArrayList<Extremo> recibirListadoExtremos(Log myLog, File fileExtremos, File fileMaestros) {
        Gson gson = new Gson();
        ArrayList<Extremo> listaExtremos = new ArrayList<Extremo>();
        try {
            Reader readerFile = Files.newBufferedReader(Paths.get(fileMaestros.getPath()));
            ArrayList<Maestro> listaMaestros = gson.fromJson(readerFile, new TypeToken<ArrayList<Maestro>>() {
            }.getType());
            int indexListaMaestro = 0;
            boolean conecte = false;
            while ((!conecte) && (indexListaMaestro < listaMaestros.size())) {
                indexListaMaestro++;
                try {
                    String HOST = listaMaestros.get(indexListaMaestro - 1).getIpServer();
                    int PORT = listaMaestros.get(indexListaMaestro - 1).getPortServer();
                    if((HOST.equals("127.0.0.1"))) {
                       HOST = ipServer;
                    }
                        if (((ipServer.equals(HOST)) && !(portServer == PORT)) || (!(ipServer.equals(HOST)))) {
                            Socket conexion = new Socket();
                            conexion.connect(new InetSocketAddress(HOST, PORT), 1000);
                            conecte = true;
                            PrintWriter outServer = new PrintWriter(conexion.getOutputStream(), true);
                            outServer.println("MAESTRO-RecibirListaExtremo");
                            outServer.println(portServer);
                            BufferedReader in = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
                            String gsonFicheros = in.readLine();
                            FileWriter myWriter = new FileWriter(fileExtremos);
                            myWriter.write(gsonFicheros);
                            myWriter.close();
                            conexion.close();
                            listaExtremos = new Gson().fromJson(gsonFicheros, new TypeToken<ArrayList<Extremo>>() {
                            }.getType());

                            myLog.addToLog("Listado Extremos recibida de " + HOST + ":" + PORT, "INFO");
                        }

                } catch (SocketTimeoutException e2) {

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return listaExtremos;
    }

    public static void main(String[] args) {
        String ipServer = null;
        Log myLog = null;
        try {
            ipServer = getLocalAddress().getHostAddress();
            int portServer = 10000;
            Maestro miMaestro = new Maestro(ipServer, portServer);
            Long nombreCarpeta = System.currentTimeMillis();
            File directory = new File("./src/main/resources/Maestros/Server" + nombreCarpeta);
            File fileExtremos = new File(directory + "/ListaExtremos.json");
            try {
                File fileMaestros = new File(directory + "/inicializacion-ListaMaestros.json");
                if (!directory.exists()) {
                    directory.mkdirs();
                    fileExtremos.createNewFile();
                    Files.copy(Paths.get("./src/main/resources/inicializacion-ListaMaestros.json"), Paths.get(directory + "/inicializacion-ListaMaestros.json"), StandardCopyOption.REPLACE_EXISTING);
                }
                myLog = new Log(directory + "/myLog.log");
                myLog.addToLog("Iniciando Servidor", "INFO");
                ;
                while (!miMaestro.inicializar(myLog, fileExtremos, fileMaestros)) {
                    miMaestro.setPort(miMaestro.getPortServer() + 1);
                }
            } catch (SocketException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (SecurityException e2) {
            myLog.addToLog("No tiene permisos de Read/Write en el archivo .log", "SEVERE");
        }
    }


}
