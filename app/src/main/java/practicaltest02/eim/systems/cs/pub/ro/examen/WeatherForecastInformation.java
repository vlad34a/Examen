package practicaltest02.eim.systems.cs.pub.ro.examen;

/**
 * Created by vlad on 5/21/18.
 */

class WeatherForecastInformation {

    private String temperature;
    private String windSpeed;
    private String condition;
    private String pressure;
    private String humidity;

    public WeatherForecastInformation(String temperature, String windSpeed, String condition, String pressure, String humidity) {
        this.temperature = temperature;
        this.windSpeed = windSpeed;
        this.condition = condition;
        this.pressure = pressure;
        this.humidity = humidity;
       // this.general = general;

       }

    public String getCondition() {
        return condition;
    }public void setCondition(String condition) {
        this.condition = condition;
    }
    @Override
    public String toString() {
        return "WeatherForecastInformation{" +
                "temperature='" + temperature + '\'' +
                ", windSpeed='" + windSpeed + '\'' +
                ", pressure='" + pressure + '\'' +
                ", humidity='" + humidity + '\'' +
                ", general='" + general + '\'' +
                '}';
    }

    public String getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(String windSpeed) {
        this.windSpeed = windSpeed;
    }

    public String getPressure() {
        return pressure;
    }

    public void setPressure(String pressure) {
        this.pressure = pressure;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getGeneral() {
        return general;
    }

    public void setGeneral(String general) {
        this.general = general;
    }

    private String general;

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }
}
