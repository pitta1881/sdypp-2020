package Ej7.tareasgenericas;

import Ej7.rmi.Tarea;

public class NumAleatorio implements Tarea {

    @Override
    public Object ejecutar() {
        int resu = 0;
        try {
            resu = (int) (Math.random() * 10000);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return resu;
    }
}
