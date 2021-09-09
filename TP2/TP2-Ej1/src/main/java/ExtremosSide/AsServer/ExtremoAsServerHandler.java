package ExtremosSide.AsServer;

import MastersSide.Extremo;
import log.Log;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ExtremoAsServerHandler  implements Runnable {
    private final Socket cliente;
    private final String ipCliente;
    private final int portCliente;
    Log myLog;
    String folderResources;

    public ExtremoAsServerHandler(Socket socket, String _ipCliente, int _portCliente, Log _myLog, String folderResources) {
        this.cliente = socket;
        this.ipCliente = _ipCliente;
        this.portCliente = _portCliente;
        this.myLog = _myLog;
        this.folderResources = folderResources;
    }

    @Override
    public void run() {
        try {
            BufferedReader inServerResource = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
            String nombreRecurso = inServerResource.readLine();
            File archivo = new File(folderResources+nombreRecurso);
            myLog.addToLog("Petición recibida -> Archivo: " + nombreRecurso, "INFO");
            if (sendFile(archivo)){
                myLog.addToLog("Archivo enviado correctamente -> " + nombreRecurso, "INFO");
            } else {
                myLog.addToLog("Error al enviar el Archivo: " + nombreRecurso, "SEVERE");
            }
            System.out.print("> ");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public boolean sendFile(File inputFile) {
        boolean retorno = true;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(inputFile);
            BufferedInputStream bis = new BufferedInputStream(fis);
            //Get socket's output stream
            OutputStream os = cliente.getOutputStream();
            //Read File Contents into contents array
            byte[] contents;
            long fileLength = inputFile.length();
            long current = 0;
            while(current!=fileLength){
                int size = 10000;
                if(fileLength - current >= size)
                    current += size;
                else{
                    size = (int)(fileLength - current);
                    current = fileLength;
                }
                contents = new byte[size];
                bis.read(contents, 0, size);
                os.write(contents);
            }
            os.flush();
            //File transfer done. Close the socket connection!
            cliente.close();
        } catch (FileNotFoundException e) {
            retorno = false;
        } catch (IOException e) {
            retorno = false;
        }
        return retorno;
    }


}
