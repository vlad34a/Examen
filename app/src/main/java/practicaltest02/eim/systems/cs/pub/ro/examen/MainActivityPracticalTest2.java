package practicaltest02.eim.systems.cs.pub.ro.examen;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivityPracticalTest2 extends AppCompatActivity {

    ServerThread serverThread;
    EditText serverPortEditText;// = (EditText) findViewById(R.id.server_port_edit_text);
    Button connect;// = (Button) findViewById(R.id.connect_button);


    private class ConnectButtonClickListener implements Button.OnClickListener {

        @Override
        public void onClick(View view) {
            String serverPort = serverPortEditText.getText().toString();
            if (serverPort == null || serverPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Server port should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }
            serverThread = new ServerThread(Integer.parseInt(serverPort));
          /*  if (serverThread.getServerSocket() == null) {
                Log.e(Constants.TAG, "[MAIN ACTIVITY] Could not create server thread!");
                return;
            }*/
            serverThread.start();
        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_practical_test2);

        serverPortEditText = (EditText) findViewById(R.id.server_port_edit_text);
        connect = (Button) findViewById(R.id.connect_button);
        connect.setOnClickListener(new ConnectButtonClickListener());

        final View addressEditText = findViewById(R.id.client_address_edit_text);
        final View clientPortEditText = findViewById(R.id.client_port_edit_text);
        final View resultTextView = findViewById(R.id.city_edit_text);
        final View optionsSpinner = findViewById(R.id.information_type_spinner);
        final View cityB = findViewById(R.id.city_edit_text);

        View getForecastButton = findViewById(R.id.get_weather_forecast_button);
        getForecastButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ipAddress = ((EditText)addressEditText).getText().toString();
                int port = Integer.parseInt(((EditText)clientPortEditText).getText().toString());
                String tag = ((Spinner)optionsSpinner).getSelectedItem().toString();
                String city = ((EditText)cityB).getText().toString();

               ClientThread clientThread = new ClientThread(ipAddress, port, resultTextView, tag, city);
                clientThread.start();
            }
        });
    }
    @Override
    public void onDestroy() {
        if (serverThread != null) {
            serverThread.stopServer();
        }
        super.onDestroy();
    }
}
