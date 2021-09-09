package ExtremosSide.AsClient;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLOutput;
import java.util.*;

import ExtremosSide.AsServer.ExtremoAsServer;
import MastersSide.Extremo;
import MastersSide.Maestro;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class ExtremoAsClient {

    int portAsServer = 20000;  //<---- cambiar este valor x cada extremo que creemos
    String nombreUsuario;
    Socket cliente;
    String HOST;
    int PORT;
    String dirAllFiles = "./src/main/resources/Extremos/TodosLosArchivos/";
    String archivoInicializacion = "./src/main/resources/inicializacion-ListaMaestros.json";
    String newDirExtremo;
    ExtremoAsServer newExtremoServer;
    PrintWriter out;
    BufferedReader in;


    private boolean inicializarCliente() {
        Gson gson = new Gson();
        boolean conectado = false;
        try {
            this.nombreUsuario = InetAddress.getLocalHost().getHostName();
            Long carpetaExtremo = System.currentTimeMillis();
            newDirExtremo = "./src/main/resources/Extremos/Extremo" + carpetaExtremo + "/";
            File directory = new File(newDirExtremo);
            directory.mkdir();
            Files.copy(Paths.get(archivoInicializacion), Paths.get(newDirExtremo + "inicializacion-ListaMaestros.json"), StandardCopyOption.REPLACE_EXISTING);
            Reader readerFile = Files.newBufferedReader(Paths.get(newDirExtremo + "inicializacion-ListaMaestros.json"));
            ArrayList<Maestro> listaMaestros = gson.fromJson(readerFile, new TypeToken<ArrayList<Maestro>>() {
            }.getType());
            int indexListaMaestro = 0;
            while ((!conectado) && (indexListaMaestro < listaMaestros.size())) {
                indexListaMaestro++;
                try {
                    System.out.println("Intentando conectar al Maestro Nº" + indexListaMaestro + " ...");
                    HOST = listaMaestros.get(indexListaMaestro - 1).getIpServer();
                    PORT = listaMaestros.get(indexListaMaestro - 1).getPortServer();
                    this.cliente = new Socket(HOST, PORT);
                    out = new PrintWriter(cliente.getOutputStream(), true);
                    in = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
                    conectado = true;
                    System.out.println("Conectado correctamente al Servidor Maestro Nº" + indexListaMaestro);

                    File directoryData = new File(newDirExtremo + "data");
                    directoryData.mkdir();
                    File dir = new File(dirAllFiles);
                    ArrayList<String> archivos = new ArrayList<String>(Arrays.asList(dir.list()));
                    Random r = new Random();
                    int maxArchivos = r.nextInt(archivos.size());
                    for (int i = 0; i < maxArchivos; i++) {
                        int indexRnd = r.nextInt(archivos.size());
                        Files.copy(Paths.get(dirAllFiles + archivos.get(indexRnd)), Paths.get(newDirExtremo + "data/" + archivos.get(indexRnd)), StandardCopyOption.REPLACE_EXISTING);
                        archivos.remove(indexRnd);
                    }
                } catch (IOException e2) {
                    System.out.println(nombreUsuario + " > No se puede conectar con el Servidor Maestro Nº" + indexListaMaestro);
                }
            }
            if (!conectado) {
                System.out.println("No se pudo conectar con ningun Servidor Maestro.");
            }
        } catch (UnknownHostException e1) {
            System.out.println("No se pudo encontrar el nombre del usuario. Host desconocido.");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return conectado;
    }

    private void enviarListadoRecursos() {
        Gson gson = new Gson();
        File dir = new File(newDirExtremo + "data/");
        ArrayList<String> ficheros = new ArrayList<String>(Arrays.asList(dir.list()));

        String jsonString = gson.toJson(ficheros);
        out.println("EXTREMO");
        out.println(jsonString);
        out.println(portAsServer);
    }


    private void menu() {
        boolean salir = true;
        String opcion;
        Scanner reader = new Scanner(System.in);
        while (salir) {
            System.out.println("===MENU===");
            System.out.println("1. OBTENER LISTADO DE RECURSOS");
            System.out.println("2. DESCARGAR RECURSO");
            System.out.println("0. SALIR");
            System.out.println("==========");
            System.out.print(nombreUsuario + " > ");
            opcion = reader.nextLine();
            out.println(opcion);
            switch (opcion) {
                case "1":
                    listadoRecursos();
                    break;
                case "2":
                    descargarRecursos();
                    break;
                case "0":
                    salir = false;
                    reader.close();
                    newExtremoServer.shutdown();
                    try {
                        cliente.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println(nombreUsuario + " > Conexion terminada");
                    break;
                default:
                    System.out.println("Servidor > Opcion incorrecta");
                    break;
            }
        }
    }

    private ArrayList<String> listadoRecursos() {
        Gson gson = new Gson();
        String gsonFicheros = null;
        ArrayList<String> listaRecursos = null;
        try {
            gsonFicheros = in.readLine();
            listaRecursos = gson.fromJson(gsonFicheros, new TypeToken<ArrayList<String>>() {
            }.getType());
            if (!listaRecursos.isEmpty()) {
                int index = 1;
                for (String recurso : listaRecursos) {
                    System.out.println(index++ + " - " + recurso);
                }
            } else {
                System.out.println("Servidor > No hay ningun recurso disponible");
            }
        } catch (SocketException e) {
            System.out.println("El servidor no responde. Intente mas tarde.");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return listaRecursos;
    }

    private void descargarRecursos() {
        ArrayList<String> lista = listadoRecursos();
        Scanner reader = new Scanner(System.in);
        Gson gson = new Gson();
        if (!(lista == null) && !(lista.isEmpty())) {
            boolean eligio = false;
            int maxRecursos = lista.size();
            while (!eligio) {
                System.out.print("Servidor > Seleccione el recurso deseado para descargar (Exit para salir): ");
                try {
                    int seleccion = reader.nextInt();
                    if ((seleccion > 0) && (seleccion <= maxRecursos)) {
                        eligio = true;
                        out.println(true);
                        String fileName = lista.get(seleccion - 1);
                        out.println(fileName);

                        String gsonFicheros = null;
                        try {
                            gsonFicheros = in.readLine();
                            ArrayList<Extremo> listWhoHaveResources = gson.fromJson(gsonFicheros, new TypeToken<ArrayList<Extremo>>() {
                            }.getType());
                            boolean conectado = false;
                            int index = 0;
                            while ((!conectado) && (index < listWhoHaveResources.size())) {
                                String ipServerHasResource = listWhoHaveResources.get(index).getIp();
                                int portServerHasResource = listWhoHaveResources.get(index).getPortAsServer();
                                System.out.println(nombreUsuario + " > Conectando al Server que contiene el recurso... -> " + ipServerHasResource + ":" + portServerHasResource);
                                try {
                                    Socket conexion = new Socket(ipServerHasResource, portServerHasResource);
                                    System.out.println("Servidor > Conexion establecida..");
                                    conectado = true;
                                    for (int i = 0; i < 5; i++) {
                                        try {
                                            Thread.sleep(300);
                                            System.out.println(nombreUsuario + " > Descargado Recurso..");
                                        } catch (InterruptedException e) {
                                            //siempre pasa (o deberia)
                                        }
                                    }

                                    PrintWriter outServerResource = new PrintWriter(conexion.getOutputStream(), true);
                                    outServerResource.println(fileName);
                                    byte[] contents = new byte[10000];
                                    //Initialize the FileOutputStream to the output file's full path.
                                    FileOutputStream fos = new FileOutputStream(newDirExtremo + "data/" + fileName);
                                    BufferedOutputStream bos = new BufferedOutputStream(fos);
                                    InputStream is = conexion.getInputStream();
                                    //No of bytes read in one read() call
                                    int bytesRead = 0;
                                    while ((bytesRead = is.read(contents)) != -1)
                                        bos.write(contents, 0, bytesRead);
                                    bos.flush();
                                    fos.close();
                                    conexion.close();
                                    System.out.println(nombreUsuario + " > Archivo descargado correctamente!");
                                } catch (IOException e) {
                                    System.out.println(nombreUsuario + " > No se pudo conectar con el Servidor..");
                                    if (index < (listWhoHaveResources.size() - 1)) {
                                        System.out.println(nombreUsuario + " > Intentando proximo Servidor..");
                                    } else {
                                        System.out.println(nombreUsuario + " > Descarga cancelada.. Ningun Servidor responde..");
                                    }
                                }
                                index++;
                            }
                            if (conectado) {
                                out.println(true);
                                enviarListadoRecursos();
                            } else {
                                out.println(false);
                            }
                        } catch (SocketException e) {
                            System.out.println("El servidor no responde. Intente mas tarde.");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("Servidor > No es una opcion válida.");
                    }
                }catch (InputMismatchException e){
                    eligio = true;
                    out.println(false);
                }
            }
        }
    }


    private boolean inicializarServer() {
        newExtremoServer = new ExtremoAsServer("127.0.0.1", portAsServer, newDirExtremo);
        Thread threadServer = new Thread(newExtremoServer);
        threadServer.start();
        try {
            threadServer.join(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        portAsServer = newExtremoServer.getPortServer();
        return threadServer.isAlive();
    }

    public static void main(String[] args) {
        ExtremoAsClient miExtremo = new ExtremoAsClient();
        if ((miExtremo.inicializarCliente()) && (miExtremo.inicializarServer())) {
            miExtremo.enviarListadoRecursos();
            miExtremo.menu();
        }
    }


}
