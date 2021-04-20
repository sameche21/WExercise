package com.example.webserviceexercise;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;

public class DataRunnable implements Runnable {

    private static final String TAG = "TAG_DATA";
    private MainActivity mainActivity;
    private static final String DATA_URL = "https://api.npoint.io/12558c846f10b1a477e0";

    public DataRunnable(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void run() {
        Uri dataUri = Uri.parse(DATA_URL);
        String urlToUse = dataUri.toString();
        StringBuilder sb = new StringBuilder();

        Log.d(TAG, "URL -> " + DATA_URL);
        try {
            java.net.URL url = new URL(urlToUse);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                processResults(null);
                return;
            }

            InputStream is = connection.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
            Log.d(TAG, "sb -> " + sb.toString());

        } catch (Exception e) {
            processResults(null);
            return;
        }

        processResults(sb.toString());
    }

    private void processResults(String s) {
        final ArrayList<Payer> payers = parseJSON(s);

        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                assert payers != null;
                mainActivity.gatherData(payers);
            }
        });
    }

    private ArrayList<Payer> parseJSON(String s) {
        ArrayList<Payer> tmp = new ArrayList<>();
        try {
            JSONObject jObjMain = new JSONObject(s);
            JSONArray accounts = jObjMain.getJSONArray("accounts");

            for (int i = 0; i < accounts.length(); i++) {
                JSONObject jObj = (JSONObject) accounts.get(i);

                String payer = jObj.getString("payer");
                int points = jObj.getInt("points");
                SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                Date date = new Date();

                String curr_date = formatter.format(date);

                tmp.add(new Payer(payer, points, curr_date));
            }

            return tmp;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}

