package NodoServer;

import com.github.javafaker.Faker;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Clima implements Serializable {

    public String getClima() {
        Faker faker = new Faker();
        return faker.weather().description();
    }

}
