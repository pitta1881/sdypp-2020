package NodoServer;

import com.github.javafaker.Faker;

public class NombreRandom {

    public String getNombreRandom() {
        Faker faker = new Faker();

        String nombre = faker.dragonBall().character();
        String segundoNombre = faker.pokemon().name();

        return nombre + " " + segundoNombre;
    }

}
