package ServerSide;

import java.io.Serializable;

public class ClienteBanco implements Serializable {

    int id;
    float saldo;

    public ClienteBanco(int id, float saldoInicial){
        this.id = id;
        this.saldo = saldoInicial;
    }

    public float getSaldo() {
        return saldo;
    }

    public void setSaldo(float saldo) {
        this.saldo = saldo;
    }
}
