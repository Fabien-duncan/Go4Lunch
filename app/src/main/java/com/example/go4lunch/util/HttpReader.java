package com.example.go4lunch.util;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
/**
 * Utility class for reading data line by line from an HTTP URL.
 */
public class HttpReader {
    /**
     * Reads data from the given HTTP URL and returns it as a string.
     *
     * @param httpUrl The URL to read data from.
     * @return The data retrieved from the URL as a string.
     * @throws IOException If an I/O error occurs while reading the URL.
     */
    public String read(String httpUrl) throws IOException {
        String httpData = "";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL(httpUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();
            inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuffer = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }
            httpData = stringBuffer.toString();
            bufferedReader.close();
        } catch (Exception e) {
            Log.d("ExceptionReadingHttpUrl", e.toString());
        } finally {
            assert inputStream != null;
            inputStream.close();
            httpURLConnection.disconnect();
        }
        return httpData;
    }
}
