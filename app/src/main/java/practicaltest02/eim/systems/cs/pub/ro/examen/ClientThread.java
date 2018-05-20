package practicaltest02.eim.systems.cs.pub.ro.examen;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

//import static practicaltest02.eim.systems.cs.pub.ro.examen.R.string.city;

/**
 * Created by vlad on 5/21/18.
 */

public class ClientThread extends Thread {

    String ipAddress;
    int port;
    PrintWriter printWriter;
    BufferedReader bufferedReader;
    View weatherForecastTextView;
    String city;
    View informationType;
    String tag;

    public ClientThread(String ipAddress, int port, View weatherForecastTextView, String tag,String city)
    {
        this.ipAddress = ipAddress;
        this.port = port;
        this.weatherForecastTextView = weatherForecastTextView;
        this.tag = tag;
        this.city = city;
        this.informationType = informationType;
    }
@Override
public void run() {
    Socket socket = null;
    try {
        socket = new Socket(ipAddress, port);
        if (socket == null) {
            Log.e(Constants.TAG, "[CLIENT THREAD] Could not create socket!");
            return;
        }
        BufferedReader bufferedReader = Utilities.getReader(socket);
        PrintWriter printWriter = Utilities.getWriter(socket);
        if (bufferedReader == null || printWriter == null) {
            Log.e(Constants.TAG, "[CLIENT THREAD] Buffered Reader / Print Writer are null!");
            return;
        }
        printWriter.println(city);
        printWriter.flush();
        printWriter.println(tag);
        printWriter.flush();
        String weatherInformation;
        while ((weatherInformation = bufferedReader.readLine()) != null) {
            final String finalizedWeateherInformation = weatherInformation;
            weatherForecastTextView.post(new Runnable() {
                @Override
                public void run() {
                    ((TextView)weatherForecastTextView).setText(finalizedWeateherInformation);
                }
            });
        }
    } catch (IOException ioException) {
        Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
        if (Constants.DEBUG) {
            ioException.printStackTrace();
        }
    } finally {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException ioException) {
                Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
                if (Constants.DEBUG) {
                    ioException.printStackTrace();
                }
            }
        }
    }
}
}
