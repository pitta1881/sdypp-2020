package RMI;

import java.io.IOException;
import java.rmi.Remote;

public interface RemoteInterface extends Remote {

    byte[] getConversion(byte[] toByteArray, int orden) throws IOException;
}

