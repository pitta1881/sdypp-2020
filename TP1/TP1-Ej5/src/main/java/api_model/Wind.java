package api_model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Wind {
    private double speed;
    private long deg;

    @JsonProperty("speed")
    public double getSpeed() { return speed; }
    @JsonProperty("speed")
    public void setSpeed(double value) { this.speed = value; }

    @JsonProperty("deg")
    public long getDeg() { return deg; }
    @JsonProperty("deg")
    public void setDeg(long value) { this.deg = value; }
}