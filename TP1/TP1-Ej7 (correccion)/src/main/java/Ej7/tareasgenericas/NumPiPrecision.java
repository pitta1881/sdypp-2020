package Ej7.tareasgenericas;

import Ej7.rmi.Tarea;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class NumPiPrecision implements Tarea {

    private int decimales;

    public NumPiPrecision(int decimales) {
        this.decimales = decimales;
    }

    @Override
    public Object ejecutar() {
        return new BigDecimal(Math.PI).setScale(this.decimales, RoundingMode.DOWN).floatValue();
    }
}
