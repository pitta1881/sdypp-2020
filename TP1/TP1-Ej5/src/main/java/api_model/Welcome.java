package api_model;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.text.DecimalFormat;
import java.util.List;

public class Welcome {
    private Coord coord;
    private List<Weather> weather;
    private String base;
    private Main main;
    private long visibility;
    private Wind wind;
    private Clouds clouds;
    private long dt;
    private Sys sys;
    private long timezone;
    private long id;
    private String name;
    private long cod;

    @JsonProperty("coord")
    public Coord getCoord() { return coord; }
    @JsonProperty("coord")
    public void setCoord(Coord value) { this.coord = value; }

    @JsonProperty("weather")
    public List<Weather> getWeather() { return weather; }
    @JsonProperty("weather")
    public void setWeather(List<Weather> value) { this.weather = value; }

    @JsonProperty("base")
    public String getBase() { return base; }
    @JsonProperty("base")
    public void setBase(String value) { this.base = value; }

    @JsonProperty("main")
    public Main getMain() { return main; }
    @JsonProperty("main")
    public void setMain(Main value) { this.main = value; }

    @JsonProperty("visibility")
    public long getVisibility() { return visibility; }
    @JsonProperty("visibility")
    public void setVisibility(long value) { this.visibility = value; }

    @JsonProperty("wind")
    public Wind getWind() { return wind; }
    @JsonProperty("wind")
    public void setWind(Wind value) { this.wind = value; }

    @JsonProperty("clouds")
    public Clouds getClouds() { return clouds; }
    @JsonProperty("clouds")
    public void setClouds(Clouds value) { this.clouds = value; }

    @JsonProperty("dt")
    public long getDt() { return dt; }
    @JsonProperty("dt")
    public void setDt(long value) { this.dt = value; }

    @JsonProperty("sys")
    public Sys getSys() { return sys; }
    @JsonProperty("sys")
    public void setSys(Sys value) { this.sys = value; }

    @JsonProperty("timezone")
    public long getTimezone() { return timezone; }
    @JsonProperty("timezone")
    public void setTimezone(long value) { this.timezone = value; }

    @JsonProperty("id")
    public long getID() { return id; }
    @JsonProperty("id")
    public void setID(long value) { this.id = value; }

    @JsonProperty("name")
    public String getName() { return name; }
    @JsonProperty("name")
    public void setName(String value) { this.name = value; }

    @JsonProperty("cod")
    public long getCod() { return cod; }
    @JsonProperty("cod")
    public void setCod(long value) { this.cod = value; }

    @Override
    public String toString() {
        DecimalFormat df = new DecimalFormat("#.00");
        return "Informe de : "+name+"\n{" +
                "\n\tClima = " + weather.get(0).getMain() + " | " + weather.get(0).getDescription() +
                "\n\tTemperatura = " + df.format(main.getTemp()-273.15)+"??C | Sensasi??n t??rmica:" + df.format(main.getFeelsLike()-275.15) +
                "\n\tM??nima y M??xima = " +df.format(main.getTempMin()-273.15) + "??C | " + df.format(main.getTempMax()-273.15)+" ??C" +
                "\n\tVisibilidad = " + visibility/1000 + " km" +
                "\n\tViento Velocidad = " + wind.getSpeed() + "km/h"+
                "\n}";
    }
}
