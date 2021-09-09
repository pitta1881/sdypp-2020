package log;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LogVolatil {

    private String hoy;
    private String tipo;
    private String mensaje;

    public LogVolatil(String tipo, String mensaje) {
        this.hoy = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        this.tipo = tipo;
        this.mensaje = mensaje;
    }

    @Override
    public String toString() {
        return "[" + this.hoy + "] " + "[" + this.tipo + "] " + this.mensaje;
    }

    public String getHoy() {
        return hoy;
    }

    public String getTipo() {
        return tipo;
    }

    public String getMensaje() {
        return mensaje;
    }

}
