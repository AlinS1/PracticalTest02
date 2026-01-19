package ro.pub.cs.systems.eim.practicaltest02

import android.util.Log
import java.io.IOException
import java.net.Socket
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

class CommunicationThread(private val serverThread: ServerThread, private val socket: Socket) :
    Thread() {


    override fun run() {
        try {
            Log.v(
                Constants.TAG,
                "Connection opened to " + socket.getLocalAddress() + ":" + socket.getLocalPort() + " from " + socket.getInetAddress()
            )
            var bufferedReader = Utilities.getReader(socket)
            var printWriter = Utilities.getWriter(socket)

            var city : String = bufferedReader.readLine()
            var infoType : String = bufferedReader.readLine()

            if (city == null || city.isEmpty() || infoType == null || infoType.isEmpty()) {
                Log.e(Constants.TAG, "Error receiving parameters from client (city / information type)")
                socket.close()
                return
            }

            val data = serverThread.getData()
            var weatherForecastInformation: WeatherForecastInformation? = null
            if(data != null) {
                if (data.containsKey(city)) {
                    weatherForecastInformation = data[city]
                }
            }
            if (weatherForecastInformation == null) {
                val httpClient = OkHttpClient()

                val getStr = Constants.WEB_SERVICE_ADDRESS + "?q=" + city + "&appid=" +
                        Constants.API_KEY + "&units=metric"
                var request: Request = Request.Builder()
                    .url(getStr)
                    .build()


                val response = httpClient.newCall(request).execute()
                var pageSourceCode = response.body!!.string()
                val json = JSONObject(pageSourceCode)

                val weatherArray = json.optJSONArray("weather")
                val weatherMain = weatherArray?.optJSONObject(0)?.optString("main", "") ?: ""

                val mainObj = json.optJSONObject("main")
                val temp = mainObj?.optString("temp", "")
                val pressure = mainObj?.optString("pressure", "")
                val humidity = mainObj?.optString("humidity", "")

                val windObj = json.optJSONObject("wind")
                val windSpeed = windObj?.optString("speed", "")

                weatherForecastInformation = WeatherForecastInformation(
                    temp, windSpeed,weatherMain, pressure, humidity
                )
                serverThread.setData(city, weatherForecastInformation)
            }

            var result: String = when (infoType) {
                "temp" -> weatherForecastInformation.temperature ?: "no data"
                "vant" -> weatherForecastInformation.windSpeed ?: "no data"
                "stare" -> weatherForecastInformation.condition ?: "no data"
                "presiune" -> weatherForecastInformation.pressure ?: "no data"
                "umiditate" -> weatherForecastInformation.humidity ?: "no data"
                else -> weatherForecastInformation.toString()
            }

            printWriter.println(result)
            printWriter.flush()


        } catch (ioException: IOException) {
            Log.e(Constants.TAG, "An exception has occurred: " + ioException.message)
        } finally {
            socket.close()
            Log.v(Constants.TAG, "Connection closed")
        }
    }
}