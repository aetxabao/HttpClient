package com.pmdm.httpclient;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    protected Button btnSend;
    protected EditText txtUrl;
    protected EditText txtOutput;

    protected TextView weather;
    //http://home.openweathermap.org/users/sign_up
    protected String strUrl = "http://api.openweathermap.org/data/2.5/weather?mode=json&lang=sp&units=metric&q=Pamplona&appid=XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX";

    protected URL url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSend = (Button) findViewById(R.id.BtnSend);
        txtUrl = (EditText) findViewById(R.id.TxtUrl);
        txtOutput = (EditText) findViewById(R.id.TxtHtml);

        weather = (TextView) findViewById(R.id.LblWeather);

        txtUrl.setText(strUrl);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtOutput.setText(R.string.txtWaiting);
                try {
                    url = new URL(txtUrl.getText().toString());
                } catch (MalformedURLException e) {
                    txtOutput.setText(R.string.txtErrorUrl);
                    return;
                }
                new HttpGetTask().execute();
            }
        });
    }

    public String parseJson(String in){
        try{
            JSONObject reader = new JSONObject(in);
            String nombre = reader.getString("name");
            JSONArray tiempo = reader.getJSONArray("weather");
            JSONObject obj0 = tiempo.getJSONObject(0);
            String desc = obj0.getString("description");
            return nombre + " - " + desc;
        }catch(Exception e){
            return "Error parser: " + e;
        }
    }

    //http://developer.android.com/reference/android/os/AsyncTask.html
    private class HttpGetTask extends AsyncTask<Void, Void, String>{

        @Override
        protected String doInBackground(Void... params) {
            try{
                //http://developer.android.com/intl/es/reference/java/net/HttpURLConnection.html
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("User-Agent", "");
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.connect();
                //https://es.wikipedia.org/wiki/Anexo:C%C3%B3digos_de_estado_HTTP
                Log.d("Cliente HTTP", "respuesta " + conn.getResponseCode());
                InputStream inputStream = new BufferedInputStream(conn.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder sb = new StringBuilder("");
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }
                bufferedReader.close();
                return sb.toString();
            }catch (Exception e){
                return "Error: " + e;
            }
        }

        @Override
        protected void onPostExecute(String result){
            weather.setText(parseJson(result));
            txtOutput.setText(result);
        }
    }
}
