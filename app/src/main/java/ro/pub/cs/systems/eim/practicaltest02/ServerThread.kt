package ro.pub.cs.systems.eim.practicaltest02

import android.util.Log
import java.io.IOException
import java.net.InetAddress
import java.net.ServerSocket


class ServerThread(private val serverPort: Int) : Thread() {

    private var serverSocket: ServerSocket? = null

    private var data: HashMap<String?, WeatherForecastInformation?> = HashMap()

    init {
        try {
            serverSocket = ServerSocket(serverPort, 50, InetAddress.getByName("0.0.0.0"))
            Log.v(Constants.TAG, "Server socket created on port $serverPort")
        } catch (e: IOException) {
            Log.e(Constants.TAG, "Could not create server socket: ${e.message}")
            serverSocket = null
        }
    }

    fun startServer() {
        start()
        Log.v(Constants.TAG, "startServer() method was invoked")
    }

    fun stopServer() {
        try {
            serverSocket!!.close()
        } catch (ioException: IOException) {
            Log.e(Constants.TAG, "An exception has occurred: " + ioException.message)
        }
        Log.v(Constants.TAG, "stopServer() method was invoked")
    }

    @Synchronized
    fun setData(city: String?, weatherForecastInformation: WeatherForecastInformation?) {
        if (city != null && weatherForecastInformation != null) {
            data[city] = weatherForecastInformation
        }
    }

    @Synchronized
    fun getData(): HashMap<String?, WeatherForecastInformation?>? {
        return data
    }

    fun getServerSocket(): ServerSocket? {
        return serverSocket
    }

    override fun run() {
        try {
            if (serverSocket == null){
                serverSocket = ServerSocket(serverPort, 50, InetAddress.getByName("0.0.0.0"))
            }
            while (!Thread.currentThread().isInterrupted) {
                val socket = serverSocket!!.accept()
                Log.v(Constants.TAG, "accept()-ed: " + socket.getInetAddress())
                val communicationThread = CommunicationThread(this, socket)
                communicationThread.start()
            }
        } catch (ioException: IOException) {
            Log.e(Constants.TAG, "An exception has occurred: " + ioException.message)
        }
    }
}

