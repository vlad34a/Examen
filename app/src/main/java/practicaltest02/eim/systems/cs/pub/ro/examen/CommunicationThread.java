package practicaltest02.eim.systems.cs.pub.ro.examen;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.ResponseHandler;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.BasicResponseHandler;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import cz.msebera.android.httpclient.protocol.HTTP;

/**
 * Created by vlad on 5/20/18.
 */

public class CommunicationThread extends Thread{


    Socket clientSocket;
    PrintWriter printWriter;
    BufferedReader bufferedReader;
    ServerThread serverThread;


    public CommunicationThread(ServerThread serverThread, Socket clientSocket)
    {
        this.serverThread = serverThread;
        this.clientSocket = clientSocket;
    }

       /* @Override
        public void run() {
            try {
                Log.v(Constants.TAG, "Connection opened with "+clientSocket.getInetAddress()+":"+clientSocket.getLocalPort());

                BufferedReader bufferedReader = Utilities.getReader(clientSocket);
                PrintWriter printWriter = Utilities.getWriter(clientSocket);


               // printWriter.println(serverTextEditText.getText().toString());


                clientSocket.close();
                Log.v(Constants.TAG, "Connection closed");
            } catch (IOException ioException) {
                Log.e(Constants.TAG, "An exception has occurred: "+ioException.getMessage());
                if (Constants.DEBUG) {
                    ioException.printStackTrace();
                }
            }
        }*/

    public void run() {
        if (clientSocket == null) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] Socket is null!");
            return;
        }
        try {
            BufferedReader bufferedReader = Utilities.getReader(clientSocket);
            PrintWriter printWriter = Utilities.getWriter(clientSocket);
            if (bufferedReader == null || printWriter == null) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Buffered Reader / Print Writer are null!");
                return;
            }
            Log.i(Constants.TAG, "[COMMUNICATION THREAD] Waiting for parameters from client (city / information type!");

            String city = bufferedReader.readLine();
            String informationType = bufferedReader.readLine();
            if (city == null || city.isEmpty() || informationType == null || informationType.isEmpty()) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error receiving parameters from client (city / information type!");
                return;
            }


            Map<String, WeatherForecastInformation> data = serverThread.getData();
            WeatherForecastInformation weatherForecastInformation = null;

            if (data.containsKey(city)) {
                Log.i(Constants.TAG, "[COMMUNICATION THREAD] Getting the information from the cache...");
                weatherForecastInformation = data.get(city);
            } else {
                Log.i(Constants.TAG, "[COMMUNICATION THREAD] Getting the information from the webservice...");
                HttpClient httpClient = new DefaultHttpClient();


                //cerere POst
                HttpPost httpPost = new HttpPost(Constants.WEB_SERVICE_ADDRESS);
                List<NameValuePair> params = new ArrayList<>();

                params.add(new BasicNameValuePair("query", city));
                UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
                httpPost.setEntity(urlEncodedFormEntity);
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                String pageSourceCode = httpClient.execute(httpPost, responseHandler);
                if (pageSourceCode == null) {
                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error getting the information from the webservice!");
                    return;
                }

                //parsare continut
                Document document = Jsoup.parse(pageSourceCode);
                Element element = document.child(0);
                Elements elements = element.getElementsByTag("script");
                for (Element script: elements) {
                    String scriptData = script.data();
                    if (scriptData.contains("wui.api_dat")) {
                        int position = scriptData.indexOf("wui.api_dat") + "wui.api_dat".length();
                        scriptData = scriptData.substring(position);

                        JSONObject content = new JSONObject(scriptData);
                        JSONObject currentObservation = content.getJSONObject("current_observation");
                        String temperature = currentObservation.getString("temperature");
                        String windSpeed = currentObservation.getString("wind_speed");
                        String condition = currentObservation.getString("condition");
                        String pressure = currentObservation.getString("pressure");
                        String humidity = currentObservation.getString("humidity");
                        weatherForecastInformation = new WeatherForecastInformation(
                                temperature, windSpeed, condition, pressure, humidity);
                        serverThread.setData(city, weatherForecastInformation);
                        break;
                    }
                }
            }
            if (weatherForecastInformation == null) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Weather Forecast Information is null!");
                return;
            }
            String result = null;
            switch(informationType) {
                case "all":
                    result = weatherForecastInformation.toString();
                    break;
                case "temperature":
                    result = weatherForecastInformation.getTemperature();
                    break;
                case "wind_speed":
                    result = weatherForecastInformation.getWindSpeed();
                    break;
                case "condition":
                    result = weatherForecastInformation.getCondition();
                    break;
                case "humidity":
                    result = weatherForecastInformation.getHumidity();
                    break;
                case "pressure":
                    result = weatherForecastInformation.getPressure();
                    break;
                default:
                    result = "[COMMUNICATION THREAD] Wrong information type (all / temperature / wind_speed / condition / humidity / pressure)!";
            }
            printWriter.println(result);
            printWriter.flush();

        } catch (IOException ioException) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        } catch (JSONException jsonException) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + jsonException.getMessage());
            if (Constants.DEBUG) {
                jsonException.printStackTrace();
            }
        } finally {
            if (clientSocket != null) {
                try {
                    clientSocket.close();
                } catch (IOException ioException) {
                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
                    if (Constants.DEBUG) {
                        ioException.printStackTrace();
                    }
                }
            }
        }
    }


}
