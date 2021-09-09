package Ej4;

public class Mensaje {
	private int id;
	private String origen;
	private String destinatario;
	private String mensaje;
	private boolean borrado;
	
	public Mensaje(String origen, String destinatario, String mensaje) {
		this.origen = origen;
		this.destinatario = destinatario;
		this.mensaje = mensaje;		
		this.borrado = false;
	}
	
	public String getDestinatario() {
		return this.destinatario;
	}
	
	@Override
	public String toString() {
		return 	"De: " + this.origen + "\n" + 
				"Para: " + this.destinatario + "\n" +
				"Mensaje: " + this.mensaje + "\n";
	}

	public void setId(int ultimoId) {
		this.id = ultimoId+1;		
	}

	public int getId() {		
		return this.id;
	}

	public void borrar() {
		this.borrado = true;		
	}
	
	public boolean isBorrado() {
		return this.borrado;
	}
}
