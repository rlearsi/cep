package com.rlearsi.apps.zipcode.whatsthezipcode;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpConnections {
    //método get
    public static String get(String urlString){
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resposta = null;
        try {
            URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            //urlConnection.setRequestProperty("Content-Type","application/json");
            urlConnection.setRequestProperty("Accept","application/json");
            urlConnection.connect();

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());

            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            StringBuilder buffer = new StringBuilder();
            //StringBuffer buffer = new StringBuffer();
            while ((line = reader.readLine()) != null){
                buffer.append(line);
            }
            resposta = buffer.toString();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (urlConnection != null){
                urlConnection.disconnect();
            }
            try {
                assert reader != null;
                reader.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return resposta;
    }
}