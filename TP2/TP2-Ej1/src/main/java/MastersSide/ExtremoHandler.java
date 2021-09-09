package MastersSide;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import log.Log;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class ExtremoHandler implements Runnable {
    private final Socket cliente;
    private final String ipCliente;
    private final String ipServerMaestro;
    private final int portExtremoAsClient;
    private final int portServerMaestro;
    private int portExtremoAsServer;
    Log myLog;
    private final File fileExtremos;
    private final File fileMaestros;
    ArrayList<Extremo> listaExtremos;
    PrintWriter out;
    BufferedReader in;

    public ExtremoHandler(Socket socket, String _ipCliente, int _portCliente, Log _myLog, File fileExtremos, File fileMaestros, ArrayList<Extremo> listaExtremos, String ipServerMaestro, int portServerMaestro) {
        this.cliente = socket;
        this.ipCliente = _ipCliente;
        this.portExtremoAsClient = _portCliente;
        this.myLog = _myLog;
        this.fileExtremos = fileExtremos;
        this.fileMaestros = fileMaestros;
        this.listaExtremos = listaExtremos;
        this.ipServerMaestro = ipServerMaestro;
        this.portServerMaestro = portServerMaestro;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
            out = new PrintWriter(cliente.getOutputStream(), true);
            int portNewServerMaestro;
            String tipo = in.readLine();
            if(tipo.equals("EXTREMO")) {
                addExtremoToFile();
                menu();
            } else if (tipo.equals("MAESTRO-Novedad")){
                portNewServerMaestro = Integer.parseInt(in.readLine());
                novedadExtremo();
                myLog.addToLog("Novedad de Extremo recibida de Maestro " + ipCliente + " : " + portNewServerMaestro, "INFO");
            } else if (tipo.equals("MAESTRO-RecibirListaExtremo")){
                Gson gson = new Gson();
                portNewServerMaestro = Integer.parseInt(in.readLine());
                out.println(gson.toJson(listaExtremos));
                myLog.addToLog("Listado Extremos enviada a " + ipCliente + ":" + portNewServerMaestro, "INFO");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void novedadExtremo() {
        try {
            Gson gson = new Gson();
            boolean addOrRemove = Boolean.parseBoolean(in.readLine());
            String gsonFicheros = in.readLine();
            Extremo extremoToRefresh = gson.fromJson(gsonFicheros, Extremo.class);
            boolean encontre = false;
            if(addOrRemove) {
                int index = 0;
                while ((!encontre) && (index < listaExtremos.size())) {
                    if ((listaExtremos.get(index).getIp().equals(extremoToRefresh.getIp())) && (listaExtremos.get(index).getPortAsCliente() == extremoToRefresh.getPortAsCliente())) {
                        encontre = true;
                        listaExtremos.get(index).setFicheros(extremoToRefresh.getFicheros());
                    }
                    index++;
                }
                if(!encontre){
                    listaExtremos.add(extremoToRefresh);
                }
            } else {
                Reader reader = null;
                int index = 0;
                while ((!encontre) && (index < listaExtremos.size())) {
                    if ((listaExtremos.get(index).getIp().equals(extremoToRefresh.getIp())) && (listaExtremos.get(index).getPortAsCliente() == extremoToRefresh.getPortAsCliente())) {
                        listaExtremos.remove(index);
                        encontre = true;
                    }
                    index++;
                }
            }
            String listaJson = gson.toJson(listaExtremos);
            FileWriter myWriter = new FileWriter(fileExtremos);
            myWriter.write(listaJson);
            myWriter.close();
        } catch (Exception e) {

        }
    }

    private void addExtremoToFile() {
        try {
            Gson gson = new Gson();
            String gsonFicheros = in.readLine();
            ArrayList<String> ficheros = gson.fromJson(gsonFicheros, new TypeToken<ArrayList<String>>() {
            }.getType());
            portExtremoAsServer = Integer.parseInt(in.readLine());
            Extremo extremo = new Extremo(ipCliente, portExtremoAsClient, portExtremoAsServer, ficheros);
            listaExtremos.add(extremo);
            String listaJson = gson.toJson(listaExtremos);
            FileWriter myWriter = new FileWriter(fileExtremos);
            myWriter.write(listaJson);
            myWriter.close();
            avisarDemasMaestros(listaExtremos.get(listaExtremos.size()-1), true);
        } catch (Exception e) {

        }
    }

    private void avisarDemasMaestros(Extremo novedadExtremo, boolean addOrRemove) {
        Gson gson = new Gson();
        try {
            Reader readerFile = Files.newBufferedReader(Paths.get(fileMaestros.getPath()));
            ArrayList<Maestro> listaMaestros = gson.fromJson(readerFile, new TypeToken<ArrayList<Maestro>>() {
            }.getType());
            int indexListaMaestro = 0;
            while (indexListaMaestro < listaMaestros.size()) {
                indexListaMaestro++;
                try {
                    String HOST = listaMaestros.get(indexListaMaestro - 1).getIpServer();
                    int PORT = listaMaestros.get(indexListaMaestro - 1).getPortServer();
                    if((HOST.equals("127.0.0.1"))) {
                        HOST = ipServerMaestro;
                    }
                    if (((ipServerMaestro.equals(HOST)) && !(portServerMaestro == PORT)) || (!(ipServerMaestro.equals(HOST)))) {
                        Socket conexion = new Socket();
                        conexion.connect(new InetSocketAddress(HOST, PORT), 1000);
                        PrintWriter outServer = new PrintWriter(conexion.getOutputStream(), true);
                        outServer.println("MAESTRO-Novedad");
                        outServer.println(portServerMaestro);
                        outServer.println(addOrRemove);
                        outServer.println(gson.toJson(novedadExtremo));
                        conexion.close();
                        myLog.addToLog("Novedad de Extremo enviado a Maestro " + HOST + ":" + PORT, "INFO");
                    }
                } catch (SocketTimeoutException e2){

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void menu() {
        boolean salir = true;
        try {
            String opcion;
            while (salir) {
                opcion = in.readLine();
                myLog.addToLog("Mensaje Recibido de " + ipCliente + ":" + portExtremoAsClient, "INFO");
                switch (opcion) {
                    case "1":
                        listadoRecursos();
                        break;
                    case "2":
                        descargarRecursos();
                        break;
                    case "0":
                        salir = false;
                        removeExtremoFromFile();
                        break;
                    default:
                        break;
                }
            }
        } catch (IOException e) {
            // el cliente se desconecta sin escribir "Exit"
            removeExtremoFromFile();
        }
    }

    private ArrayList<String> listadoRecursos() {
        Reader reader = null;
        ArrayList<String> listaRecursosFinal = null;
        try {
            reader = Files.newBufferedReader(Paths.get(fileExtremos.getPath()));
            ArrayList<Extremo> archivo = new Gson().fromJson(reader, new TypeToken<ArrayList<Extremo>>() {
            }.getType());
            Gson gson = new Gson();
            ArrayList<String> listaRecursos = new ArrayList<String>();
            ArrayList<String> listaRecursosCliente = new ArrayList<String>();
            int index = 0;
            while (index < archivo.size()) {
                if (!(archivo.get(index).getPortAsCliente() == this.portExtremoAsClient)) {
                    for (String recurso : archivo.get(index).getFicheros()) {
                        listaRecursos.add(recurso);
                    }
                } else {
                    listaRecursosCliente = archivo.get(index).getFicheros();
                }
                index++;
            }

            //elimino los duplicados
            ArrayList<String> listaRecursosSinDuplicados = new ArrayList<String>();
            for (String element : listaRecursos) {
                if (!listaRecursosSinDuplicados.contains(element)) {
                    listaRecursosSinDuplicados.add(element);
                }
            }

            //elimino los que ya tiene el cliente
            listaRecursosFinal = new ArrayList<String>();
            for (String element : listaRecursosSinDuplicados) {
                if (!listaRecursosCliente.contains(element)) {
                    listaRecursosFinal.add(element);
                }
            }

            String listaJson = gson.toJson(listaRecursosFinal);
            out.println((listaJson));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return listaRecursosFinal;
    }

    private void descargarRecursos() {
        try {
            ArrayList<String> lista = listadoRecursos();
            if (!lista.isEmpty()) {
                boolean entre = Boolean.parseBoolean(in.readLine());
                if (entre) {
                    String nombreRecursoBuscado = in.readLine();
                    ArrayList<Extremo> listaEnviar = new ArrayList<Extremo>();
                    try {
                        Reader reader = Files.newBufferedReader(Paths.get(fileExtremos.getPath()));
                        ArrayList<Extremo> miExtremo = new Gson().fromJson(reader, new TypeToken<ArrayList<Extremo>>() {
                        }.getType());
                        for (int i = 0; i < miExtremo.size(); i++) {
                            if (!(miExtremo.get(i).getPortAsCliente() == this.portExtremoAsClient)) {
                                for (String recurso : miExtremo.get(i).getFicheros()) {
                                    if (recurso.equals(nombreRecursoBuscado)) {
                                        listaEnviar.add(miExtremo.get(i));
                                    }
                                }
                            }
                        }
                        Gson gson = new Gson();
                        String listaJson = gson.toJson(listaEnviar);
                        out.println((listaJson));
                        boolean pudoDescargar = Boolean.parseBoolean(in.readLine());
                        if (pudoDescargar) {
                            actualizarListadoExtremo();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void removeExtremoFromFile() {
        Reader reader = null;
        try {
            reader = Files.newBufferedReader(Paths.get(fileExtremos.getPath()));
            ArrayList<Extremo> archivo = new Gson().fromJson(reader, new TypeToken<ArrayList<Extremo>>() {
            }.getType());
            int index = 0;
            boolean encontre = false;
            Extremo extremoParaPasar = null;
            while ((!encontre) && (index < archivo.size())) {
                if ((archivo.get(index).getIp().equals(this.ipCliente)) && (archivo.get(index).getPortAsCliente() == this.portExtremoAsClient)) {
                    extremoParaPasar = listaExtremos.get(index);
                    listaExtremos.remove(index);
                    FileWriter myWriter = new FileWriter(this.fileExtremos);
                    Gson gson = new Gson();
                    String listaJson = gson.toJson(listaExtremos);
                    myWriter.write(listaJson);
                    myWriter.close();
                    encontre = true;
                }
                index++;
            }
            myLog.addToLog("Extremo Desconectado -> " + ipCliente + ":" + portExtremoAsClient, "INFO");
            cliente.close();
            avisarDemasMaestros(extremoParaPasar,false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void actualizarListadoExtremo() {
        try {
            Gson gson = new Gson();
            in.readLine();
            String gsonFicheros = in.readLine();
            ArrayList<String> ficheros = gson.fromJson(gsonFicheros, new TypeToken<ArrayList<String>>() {
            }.getType());
            in.readLine();
            boolean encontre = false;
            int index = 0;
            while ((!encontre) && (index < listaExtremos.size())) {
                if ((listaExtremos.get(index).getIp().equals(ipCliente)) && (listaExtremos.get(index).getPortAsCliente() == portExtremoAsClient)) {
                    encontre = true;
                    listaExtremos.get(index).setFicheros(ficheros);
                }
                index++;
            }
            String listaJson = gson.toJson(listaExtremos);
            FileWriter myWriter = new FileWriter(fileExtremos);
            myWriter.write(listaJson);
            myWriter.close();
            avisarDemasMaestros(listaExtremos.get(index-1), true);
        } catch (Exception e) {
            System.out.println(e);
        }
    }


}