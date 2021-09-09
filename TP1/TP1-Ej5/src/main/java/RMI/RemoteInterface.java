package RMI;

import java.io.IOException;
import java.rmi.Remote;

public interface RemoteInterface extends Remote {

    String getClima () throws IOException;
}
