package servidor;

import RMI.RemoteInterface;
import api_model.Welcome;
import com.fasterxml.jackson.databind.ObjectMapper;
import log.Log;

import java.io.*;
import java.rmi.RemoteException;
import java.rmi.server.RemoteServer;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;



public class ServidorImplementer extends UnicastRemoteObject implements RemoteInterface {
    Log log;
    String ubicacionServer;
    String apiKey = "0140e253bb0e87bc193e07cac07b2497";

    public ServidorImplementer(Log log) throws RemoteException {
        super();
        this.log = log;
        this.ubicacionServer = "Luján,AR";
    }

    public String getClima() throws IOException {

            String urlApi="https://api.openweathermap.org/data/2.5/weather?q="+ubicacionServer+"&appid="+apiKey;

            log.addToLog("Voy a consultar: " +urlApi, "INFO");

            // Esto es lo que vamos a devolver
            StringBuilder resultado = new StringBuilder();

            // Crear un objeto de tipo URL
            URL url = new URL(urlApi);

            // Abrir la conexión e indicar que será de tipo GET
            HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
            conexion.setRequestMethod("GET");

            // Búferes para leer
            BufferedReader rd = new BufferedReader(new InputStreamReader(conexion.getInputStream()));

            String linea;

            log.addToLog("Comenzando a procesar respuesta de :" +url, "INFO");
            // Mientras el BufferedReader se pueda leer, agregar contenido a resultado
            while ((linea = rd.readLine()) != null) {
                resultado.append(linea);
            }
            log.addToLog("Finalizo de procesar respuesta", "INFO");

            // Cerrar el BufferedReader
            rd.close();

            //Clima clima = new Clima();
            log.addToLog("Conviertiendo a Objecto" +resultado, "INFO");
            ObjectMapper mapper = new ObjectMapper();
            Welcome resu = mapper.readValue(resultado.toString(), Welcome.class);
            log.addToLog("Convertido con éxito el clima para: " +resu.getName(), "INFO");

            return resu.toString();

    }

    private void addLog() {
        try {
            String datosCliente = RemoteServer.getClientHost();
            log.addToLog("Mensaje Recibido de " + datosCliente, "INFO");
        } catch (ServerNotActiveException e) {
            e.printStackTrace();
        }
    }

}
