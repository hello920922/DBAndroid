package hongik.android.project.best;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Administrator on 2015-11-12.
 */
public class URLConnector extends Thread {
    private String result;
    private String url;
    private String method;
    private String query;

    public URLConnector(String url, String method, String query) {
        this.url = url;
        this.method = method;
        this.query = query.replace(" ", "%20");
    }

    @Override
    public void run(){
        StringBuilder output = new StringBuilder();
        try{

            // If Method is GET, Append Query After The URL
            if(method.equals("GET"))
                url += "?"+ URLEncoder.encode(query,"utf-8");

            Log.i("SampleHTTP", "URL : " + url);

            URL u = new URL(url);
            HttpURLConnection conn = (HttpURLConnection)u.openConnection();

            if(conn != null){
                conn.setConnectTimeout(10000);
                conn.setRequestMethod(method);
                conn.setDoInput(true);
                conn.setDoOutput(true);

                Log.i("SampleHTTP", "METHOD : " + method);

                // If Method is POST, Send Query to Server
                if(method.equals("POST")) {
                    PrintWriter writer = new PrintWriter(conn.getOutputStream());
                    writer.print(query);
                    writer.close();

                    Log.i("SampleHTTP", "QUERY : " + query);
                }

                int resCode = conn.getResponseCode();
                if(resCode == HttpURLConnection.HTTP_OK){
                    Log.i("SampleHTTP", "HTTP_OK...");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String line = null;
                    while(true){
                        line = reader.readLine();
                        Log.i("SampleHTTP", "LINE : " + line);
                        if(line == null){
                            break;
                        }
                        output.append(line);
                    }
                    Log.i("SampleHTTP", "OUTPUT : " + output.toString());

                    reader.close();
                    conn.disconnect();
                }
            }
        }catch(Exception ex){
            Log.e("SampleHTTP", "Exception in processing response.",ex);
            ex.printStackTrace();
        }
        result = output.toString();
    }

    public String getResult(){
        return result;
    }
}