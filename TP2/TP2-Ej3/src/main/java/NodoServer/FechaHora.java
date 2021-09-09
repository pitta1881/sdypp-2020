package NodoServer;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressWarnings("serial")
public class FechaHora implements Serializable {

    public String getFechaHora() {
        Date date = new Date();
        DateFormat hourdateFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
        return "La Fecha y Hora actual es " + hourdateFormat.format(date);
    }

}
