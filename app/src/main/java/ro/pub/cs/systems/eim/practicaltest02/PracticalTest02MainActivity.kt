package ro.pub.cs.systems.eim.practicaltest02

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class PracticalTest02MainActivity : AppCompatActivity() {
    var ss_port: EditText? = null
    var start_server_button: Button? = null

    var cl_address: EditText? = null
    var cl_port: EditText? = null
    var city: EditText? = null
    var information_type: Spinner? = null
    var get_weather_forecast_button: Button? = null
    var weather_forecast_text_view: TextView? = null


    var serverThread: ServerThread? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_practical_test02_main)

        ss_port = findViewById<EditText>(R.id.sv_port_text)
        start_server_button = findViewById<Button>(R.id.start_server_button)

        cl_address = findViewById<EditText>(R.id.addresss_edit_text)
        cl_port = findViewById<EditText>(R.id.client_port_edit_text)
        city = findViewById<EditText>(R.id.city_edit_text)
        information_type = findViewById<Spinner>(R.id.type_info_spinner)
        get_weather_forecast_button = findViewById<Button>(R.id.get_weather_button)

        weather_forecast_text_view = findViewById<TextView>(R.id.weather_forecast_text_view)

        start_server_button!!.setOnClickListener {
            if(serverThread != null) {
                Toast.makeText(this, "Server is already started!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            var svPort: String = ss_port!!.text.toString()
            if (svPort.isEmpty()) {
                Toast.makeText(this, "Server port should be filled!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            serverThread = ServerThread(svPort.toInt())
            if (serverThread?.getServerSocket() == null) {
                Toast.makeText(this, "Could not create server thread!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            serverThread!!.start()
        }


        get_weather_forecast_button!!.setOnClickListener {
            var addr = cl_address?.text.toString()
            var portStr = cl_port?.text.toString()
            var cityStr = city?.text.toString()
            var infoTypeStr = information_type?.selectedItem.toString()
            if (addr.isEmpty() || portStr.isEmpty() || cityStr.isEmpty() || infoTypeStr.isEmpty()) {
                Toast.makeText(this, "All client parameters should be filled!", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            if (serverThread == null || serverThread?.getServerSocket() == null) {
                Toast.makeText(this, "There is no server to connect to!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            weather_forecast_text_view?.text = ""

            var clientThread = ClientThread(
                addr,
                portStr.toInt(),
                cityStr,
                infoTypeStr,
                weather_forecast_text_view!!
            )
            clientThread.start()

        }
    }

    override fun onDestroy() {
        serverThread?.stopServer()
        super.onDestroy()
    }
}