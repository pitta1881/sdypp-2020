package Ej7.tareasgenericas;

import Ej7.rmi.Tarea;

public class NumAleatorioPrimo implements Tarea {
    int numAleatorio;

    public NumAleatorioPrimo () { numAleatorio = 0; }

    @Override
    public Object ejecutar() {

        int numero = (int) (Math.random() * 10000);
        boolean isPrimo = getIsPrimo(numero);

        while (!isPrimo) {
            numero = (int) (Math.random() * 10000);
            isPrimo = getIsPrimo(numero);
        }

        return numero;

    }


    public boolean getIsPrimo (int numero){
        int contador = 2;
        boolean primo = true;
        while ((primo) && (contador != numero)) {
            if (numero % contador == 0)
                primo = false;
            contador++;
        }
        return primo;
    }
}
