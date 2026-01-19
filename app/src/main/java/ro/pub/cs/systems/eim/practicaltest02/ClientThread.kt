package ro.pub.cs.systems.eim.practicaltest02

import android.util.Log
import android.widget.TextView
import java.io.BufferedReader
import java.net.Socket

class ClientThread(address: String, port: Int, city: String, infoType: String, weather: TextView) :
    Thread() {

    private val address: String = address
    private val port: Int = port
    private val city: String = city
    private val infoType: String = infoType
    private val weather: TextView = weather

    private var isRunning = false

    override fun run() {
        // Client thread logic goes here
        var socket: Socket? = null
        try {
            socket = Socket(address, port)
            var bufferedReader = Utilities.getReader(socket)
            var printWriter = Utilities.getWriter(socket)
            printWriter.println(city)
            printWriter.flush()
            printWriter.println(infoType)
            printWriter.flush()

            Log.v(Constants.TAG, "Client sent: $city and $infoType")

            var weatherInfo = bufferedReader.readLine()
            weather.post {
                weather.text = weatherInfo
            }

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            socket?.close()
        }
    }


    fun stopClient() {
    }
}