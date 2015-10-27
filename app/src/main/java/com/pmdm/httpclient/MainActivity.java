package com.pmdm.httpclient;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

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

    public String parseRss(String input)  {
        DocumentBuilderFactory factory;
        DocumentBuilder builder;
        Document doc;
        XPathFactory xpf;
        XPath xp;
        XPathExpression xpe;
        NodeList items;
        int n;
        String str;
        StringBuilder sb = new StringBuilder();
        try {
            factory = DocumentBuilderFactory.newInstance();
            builder = factory.newDocumentBuilder();
//            //permitir trabajar sin conexión, evitar la resolución de la dtd por la red
//            //<!DOCTYPE rss SYSTEM "http://www.validome.org/check/RSS_validator/errorFiles/DTDs/entity.dtd">
//            builder.setEntityResolver(new EntityResolver() {
//                @Override
//                public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
//                    return new InputSource(new StringReader(""));
//                }
//            });
            doc = builder.parse(new ByteArrayInputStream(input.getBytes()));
            doc.getDocumentElement().normalize();

            xpf = XPathFactory.newInstance();
            xp = xpf.newXPath();

            xpe = xp.compile("/rss/channel/item");
            items = (NodeList) xpe.evaluate(doc.getDocumentElement(), XPathConstants.NODESET);
            n = items.getLength();
            for (int i = 0; i < n; i++) {
                Element item = (Element) items.item(i);
                str = xp.evaluate("title", item);
                sb.append(str);
                sb.append("\n");
                str = xp.evaluate("pubDate", item);
                sb.append(str);
                sb.append("\n\n");
            }
            return sb.toString();
        }catch(Exception e){
            return "Error in parseRss: " + e;
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
            txtOutput.setText(parseRss(result));
        }
    }
}
