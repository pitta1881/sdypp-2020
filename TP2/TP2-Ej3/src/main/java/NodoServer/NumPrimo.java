package NodoServer;

public class NumPrimo {

    int numeroPrimo;

    public NumPrimo()  {
        int numero = 0;
        boolean primo = false;
        while (!primo) {
            numero = (int) (Math.random() * 10000);
            primo = esPrimo(numero);
        }
        this.numeroPrimo = numero;
    }

    private boolean esPrimo(int numero) {
        int contador = 2;
        boolean primo = true;
        while ((primo) && (contador != numero)) {
            if (numero % contador == 0)
                primo = false;
            contador++;
        }
        return primo;
    }

    public int getPrimo(){
        return this.numeroPrimo;
    }
}

