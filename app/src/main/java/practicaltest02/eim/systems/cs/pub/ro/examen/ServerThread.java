package practicaltest02.eim.systems.cs.pub.ro.examen;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.io.UTFDataFormatException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.ResponseHandler;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.BasicResponseHandler;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;

import cz.msebera.android.httpclient.protocol.HTTP;
/**
 * Created by vlad on 5/20/18.
 */

public class ServerThread extends Thread{
    ServerSocket serverSocket;

    public ServerSocket getServerSocket() {
        return serverSocket;
    }
    private boolean isRunning;

    //private ServerSocket serverSocket;

    public void startServer() {
        isRunning = true;
        start();
    }

    public void setServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    Map<String, WeatherForecastInformation> data = new HashMap<String, WeatherForecastInformation>();
    int port;
    public ServerThread(int port){
        this.port = port;
        /*try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    public synchronized void setData(String city, WeatherForecastInformation weatherForecastInformation) {
        this.data.put(city, weatherForecastInformation);
    }

    public synchronized Map<String, WeatherForecastInformation> getData() {
        return data;
    }

    public String getInformation(String tag)
    {
        if (data.containsKey(tag))
        {
            Log.d("QQ","[Server] Info found in hashtable / cached");
            return data.get(tag).toString();
        }
        else
        {
            Log.d("QQ","[Server] Info not found - http requesting");

            HttpClient httpClient = new DefaultHttpClient();
            ResponseHandler<String> responseHandler = new BasicResponseHandler();

            // -- cerere GET
            HttpGet httpGet = new HttpGet("https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20weather.forecast%20where%20woeid%20in%20(select%20woeid%20from%20geo.places(1)%20where%20text%3D%22Bucuresti%22)&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys");
            try {
                String response = httpClient.execute(httpGet, responseHandler);

                JSONObject content = new JSONObject(response);
                response = content.getJSONObject("query").getJSONObject("results").getJSONObject("channel").getJSONObject("wind").getString("direction");


                // -- cerere POST
                HttpPost httpPost = new HttpPost("http://www.codingvision.net");

                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("attribute1", "value1"));
                UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(params, HTTP.UTF_8);

                httpPost.setEntity(urlEncodedFormEntity);

                String response2 = httpClient.execute(httpPost, responseHandler);
                Document document = Jsoup.parse(response2);

                response = (document.getElementsByTag("script").get(0)).data();

                //data.put(tag, response);

                return data.get(tag).toString();

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }




        }
    }
    public void stopThread() {
        interrupt();
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException ioException) {
                Log.e(Constants.TAG, "[SERVER THREAD] An exception has occurred: " + ioException.getMessage());
                if (Constants.DEBUG) {
                    ioException.printStackTrace();
                }
            }
        }
    }
    public void stopServer() {
        isRunning = false;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (serverSocket != null) {
                        serverSocket.close();
                    }
                    Log.v(Constants.TAG, "stopServer() method invoked "+serverSocket);
                } catch(IOException ioException) {
                    Log.e(Constants.TAG, "An exception has occurred: "+ioException.getMessage());
                    if (Constants.DEBUG) {
                        ioException.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    public void run() {

        ArrayList<CommunicationThread> clientThreads = new ArrayList<CommunicationThread>();
        try {
            serverSocket = new ServerSocket(port);

            while (!Thread.currentThread().isInterrupted()) {
                Log.i(Constants.TAG, "[SERVER THREAD] Waiting for a client invocation...");
                Socket socket = serverSocket.accept();
                Log.i(Constants.TAG, "[SERVER THREAD] A connection request was received from " + socket.getInetAddress() + ":" + socket.getLocalPort());
                CommunicationThread communicationThread = new CommunicationThread(this,socket);
                communicationThread.start();

                clientThreads.add(communicationThread);
            }
        } catch (IOException ioException) {
            Log.e(Constants.TAG, "[SERVER THREAD] An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        }
    }

}
