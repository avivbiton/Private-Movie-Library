package com.biton.aviv.movieproject;

import android.os.AsyncTask;
import android.support.annotation.IntRange;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class HttpRequest extends AsyncTask<String, Void, String> {

    public final static int GET = 1;
    public final static int POST = 2;

    AsyncCallback callback;
    int method;
    String errorMessage;

    public HttpRequest(@IntRange(from = 1, to = 2) int method, AsyncCallback callback) {
        this.method = method;
        this.callback = callback;
    }


    @Override
    protected String doInBackground(String... strings) {
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;

        try {
            URL url = new URL(strings[0]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            if (method == POST) {
                String parameters = strings[1]; // Url Parameters
                byte[] parameterBytes = parameters.getBytes();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Length", Integer.toString(parameterBytes.length));
                DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
                dataOutputStream.write(parameterBytes);
            }

            int httpStatusCode = connection.getResponseCode();

            if (httpStatusCode != HttpURLConnection.HTTP_OK) {
                errorMessage = connection.getResponseMessage();
                return null;
            }

            inputStream = connection.getInputStream();
            inputStreamReader = new InputStreamReader(inputStream);
            bufferedReader = new BufferedReader(inputStreamReader);

            String downloadedText = "";

            String oneLine = bufferedReader.readLine();

            while (oneLine != null) {
                downloadedText += oneLine + "\n";
                oneLine = bufferedReader.readLine();
            }

            return downloadedText;
        } catch (Exception ex) {
            errorMessage = ex.getMessage();
            return null;
        } finally {
            if (bufferedReader != null) try {
                bufferedReader.close();
            } catch (Exception e) {
            }
            if (inputStreamReader != null) try {
                inputStreamReader.close();
            } catch (Exception e) {
            }
            if (inputStream != null) try {
                inputStream.close();
            } catch (Exception e) {
            }
        }
    }

    @Override
    protected void onPreExecute() {
        if (callback != null) {
            callback.OnPreExecute();
        }
    }

    @Override
    protected void onPostExecute(String s) {
        if (callback != null) {
            callback.OnFinishExecuting(s);
        }
    }
}
