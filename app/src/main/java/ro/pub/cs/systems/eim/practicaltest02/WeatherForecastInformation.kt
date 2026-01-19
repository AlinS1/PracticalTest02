package ro.pub.cs.systems.eim.practicaltest02

import androidx.annotation.NonNull


class WeatherForecastInformation(
    val temperature: String?,
    val windSpeed: String?,
    val condition: String?,
    val pressure: String?,
    val humidity: String?
) {
    @NonNull
    override fun toString(): String {
        return "WeatherForecastInformation{" +
                "temperature='" + temperature + '\'' +
                ", windSpeed='" + windSpeed + '\'' +
                ", condition='" + condition + '\'' +
                ", pressure='" + pressure + '\'' +
                ", humidity='" + humidity + '\'' +
                '}'
    }
}