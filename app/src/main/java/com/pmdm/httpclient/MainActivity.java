package com.pmdm.httpclient;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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

    protected URL url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSend = (Button) findViewById(R.id.BtnSend);
        txtUrl = (EditText) findViewById(R.id.TxtUrl);
        txtOutput = (EditText) findViewById(R.id.TxtHtml);

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
                StringBuffer sb = new StringBuffer("");
                String line = "";
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
            txtOutput.setText(result);
        }
    }
}
