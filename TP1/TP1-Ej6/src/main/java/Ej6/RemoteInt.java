package Ej6;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteInt extends Remote{
	
	public int[] sumaVectores(int[] v1, int[] v2) throws RemoteException;
	
	public int[] restaVectores(int[] v1, int[] v2) throws RemoteException;

	public String mostrarVector(int[] v) throws RemoteException;

}
