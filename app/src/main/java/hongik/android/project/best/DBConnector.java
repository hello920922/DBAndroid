package hongik.android.project.best;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Administrator on 2015-12-03.
 */
public class DBConnector extends Thread{
    private JSONObject result;
    private String query;

    public DBConnector(String query) {
        this.query = query.replace(" ", "%20");
    }

    @Override
    public void run(){
        String output="";
        try{

            Log.i("SampleHTTP", "URL : " + Constant.QueryURL);

            URL u = new URL(Constant.QueryURL);
            HttpURLConnection conn = (HttpURLConnection)u.openConnection();

            if(conn != null){
                conn.setConnectTimeout(10000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                PrintWriter writer = new PrintWriter(conn.getOutputStream());
                writer.print(query);
                writer.close();

                Log.i("SampleHTTP", "QUERY : " + query);

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
                        output = line;
                    }

                    reader.close();
                    conn.disconnect();
                }
            }
        }catch(Exception ex){
            Log.e("SampleHTTP", "Exception in processing response.",ex);
            ex.printStackTrace();
        }
        try {
            Log.i("SampleHTTP", "OUTPUT : " + output);
            result = new JSONObject(output);
        } catch (JSONException e) {
            e.printStackTrace();}
    }

    public JSONObject getResult(){
        return result;
    }
}