package Ej3;

public class Mensaje {
	private String origen;
	private String destinatario;
	private String mensaje;
	
	public Mensaje(String origen, String destinatario, String mensaje) {
		this.origen = origen;
		this.destinatario = destinatario;
		this.mensaje = mensaje;
	}
	
	public String getDestinatario() {
		return this.destinatario;
	}
	
	@Override
	public String toString() {
		return 	"De: " + this.origen + "\n" + 
				"Para: " + this.destinatario + "\n" +
				"Mensaje: " + this.mensaje;
	}
}
