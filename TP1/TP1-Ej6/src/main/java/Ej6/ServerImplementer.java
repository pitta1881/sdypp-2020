package Ej6;

import java.rmi.RemoteException;
import java.rmi.server.RemoteServer;
import java.rmi.server.ServerNotActiveException;

public class ServerImplementer implements RemoteInt {
	Log myLog;

	public ServerImplementer(Log _myLog) {
		this.myLog = _myLog;
	}

	public int[] sumaVectores(int[] v1, int[] v2) throws RemoteException {
		addLog();
		int[] v3 = new int[v1.length];;
		for (int i = 0; i < v1.length; i++) {
			v3[i] = v1[i] + v2[i];
		}		
		v1 = null;
		v2 = null;
		return v3;
	}
	
	public int[] restaVectores(int[] v1, int[] v2) throws RemoteException {
		addLog();
		int[] v3 = new int[v1.length];;
		for (int i = 0; i < v1.length; i++) {
			v3[i] = v1[i] - v2[i];
		}		
		return v3;
	}
	
	public String mostrarVector(int[] v) {
		addLog();
		String cadena = "";
		for (int i = 0; i < v.length; i++) {
			cadena += "|" + v[i] + "|\t";
		}		
		return cadena;
	}
	
	private void addLog() {
		try {
			String datosCliente = RemoteServer.getClientHost();
			myLog.addToLog("Mensaje Recibido de " + datosCliente, "INFO");
		} catch (ServerNotActiveException e) {
			e.printStackTrace();
		}
	}

	
}
