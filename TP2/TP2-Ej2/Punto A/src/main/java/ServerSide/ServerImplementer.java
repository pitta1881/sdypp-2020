package ServerSide;

import log.Log;

import java.rmi.RemoteException;
import java.rmi.server.RemoteServer;
import java.rmi.server.ServerNotActiveException;
import java.util.ArrayList;

public class ServerImplementer implements RemoteInt {
    Log myLog;
    ArrayList<ClienteBanco> listaClientes;

    public ServerImplementer(Log _myLog, ArrayList<ClienteBanco> listaClientes) {
        this.myLog = _myLog;
        this.listaClientes = listaClientes;
    }

    @Override
    public float consultaSaldo(int idCliente, String nombrePersona) throws RemoteException {
        addLog("Consulta Saldo Cliente " + idCliente, true, nombrePersona);
        float retorno;
      //  synchronized (listaClientes) {
            addLog("Entro Operacion Consulta Cliente " + idCliente, false, nombrePersona);
            retorno = listaClientes.get(idCliente - 1001).getSaldo();
            addLog("Salgo Operacion Consulta Cliente " + idCliente, false, nombrePersona);
       // }
        return retorno;
    }

    @Override
    public float operacionExtraccion(int idCliente, float montoOperacion, String nombrePersona) throws RemoteException {
        addLog("Operacion Extraccion Cliente " + idCliente, true, nombrePersona);
       // synchronized (listaClientes) {
            addLog("Entro Operacion Extraccion Cliente " + idCliente, false, nombrePersona);
            float saldoActual = listaClientes.get(idCliente - 1001).getSaldo();
            if (saldoActual - montoOperacion >= 0) {
                try {
                    Thread.sleep(1800);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                listaClientes.get(idCliente - 1001).setSaldo(saldoActual - montoOperacion);
            }
            addLog("Salgo Operacion Extraccion Cliente " + idCliente, false, nombrePersona);
       // }
        return listaClientes.get(idCliente - 1001).getSaldo();
    }

    @Override
    public float operacionDeposito(int idCliente, float montoOperacion, String nombrePersona) throws RemoteException {
        addLog("Operacion Deposito Cliente " + idCliente, true, nombrePersona);
        float saldoActual = listaClientes.get(idCliente - 1001).getSaldo();
        //synchronized (listaClientes) {
            addLog("Entro Operacion Deposito Cliente " + idCliente, false, nombrePersona);
            try {
                Thread.sleep(1400);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            listaClientes.get(idCliente - 1001).setSaldo(saldoActual + montoOperacion);
            addLog("Salgo Operacion Deposito Cliente " + idCliente, false, nombrePersona);
       // }
        return listaClientes.get(idCliente - 1001).getSaldo();
    }


    private void addLog(String aLoguear, boolean intExt, String nombrePersona) {      //true:externo      //false:interno
        try {
            String datosCliente = RemoteServer.getClientHost();
            if (intExt) {
                myLog.addToLog("Mensaje Recibido de " + datosCliente + " (" + nombrePersona + ") -> " + aLoguear, "INFO");
            } else {
                myLog.addToLog("Operacion Interna  -> " + aLoguear, "INFO");
            }
        } catch (ServerNotActiveException e) {
            e.printStackTrace();
        }
    }
}
