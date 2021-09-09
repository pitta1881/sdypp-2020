package ServerSide;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteInt extends Remote{

	public float consultaSaldo(int idCliente, String nombrePersona) throws RemoteException;

	public float operacionExtraccion(int idCliente, float montoOperacion, String nombrePersona) throws RemoteException;

	public float operacionDeposito(int idCliente, float montoOperacion, String nombrePersona) throws RemoteException;


}
