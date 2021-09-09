package NodoServer;

import com.github.javafaker.Faker;

public class DireccionRandom {

    public String getDirRandom() {
        Faker faker = new Faker();

        String ciudad = faker.address().cityName();
        String calle = faker.address().streetName();
        String calleNumero = faker.address().streetAddressNumber();

        return ciudad+" - "+calle+" "+calleNumero;
    }

}
