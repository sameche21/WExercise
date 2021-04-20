package com.example.webserviceexercise;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.provider.MediaStore;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MAINTAG";

    private HashMap<String, String> map = new HashMap<>();
    private Payer payer;
    private TextView balance;
    private TextView transactions;
    private EditText spend;
    private DatabaseHandler databaseHandler;
    private EditText add;
    private String currently_selected = "DANNON";
    private ArrayList<String[]> list;
    private RadioButton first;
    private RadioButton second;
    private RadioButton third;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        balance = findViewById(R.id.balance);
        balance.setMovementMethod(new ScrollingMovementMethod());
        add = findViewById(R.id.editTextNumber);
        transactions = findViewById(R.id.transactions);
        transactions.setMovementMethod(new ScrollingMovementMethod());
        spend = findViewById(R.id.editText);
        first = findViewById(R.id.radioButton);
        second = findViewById(R.id.radioButton2);
        third = findViewById(R.id.radioButton3);

        databaseHandler = new DatabaseHandler(this);

        list = databaseHandler.loadAccounts();

        if (list == null) {
            DataRunnable dataRunnable = new DataRunnable(this);
            new Thread(dataRunnable).start();
        }
    }

    public void gatherData(ArrayList<Payer> payers) {
        for (Payer p : payers) {
            map.put(p.getPayer(), String.valueOf(p.getPoints()));
            databaseHandler.addPayers(p);
        }
    }

    public void addPoints(View v) {

        if (first.isChecked()) {
            currently_selected = "DANNON";
        }
        if (second.isChecked()) {
            currently_selected = "UNILEVER";
        }
        if (third.isChecked()) {
            currently_selected = "MILLER COORS";
        }

        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date date = new Date();
        String current_date = formatter.format(date);
        int numPointsAdded = Integer.parseInt(add.getText().toString());

        transactions.append("payer: " + currently_selected + " | points: " + numPointsAdded + " | timestamp: " + current_date);
        transactions.append("\n");

        for (String[] tmp : list) {
            if (tmp[0].equals(currently_selected)) {
                numPointsAdded += Integer.parseInt(tmp[1]);
            }
        }

        payer = new Payer(currently_selected, numPointsAdded, current_date);

        databaseHandler.updatePayers(payer);
    }

    public void onCheckBalance(View v) {
        balance.setText("");
        list = databaseHandler.loadAccounts();
        if (list.size() > 0) {
            for (String[] tmp : list) {
                balance.append("Payer -> " + tmp[0] + " | Points -> " + tmp[1]);
                balance.append("\n");
            }
        }
    }

    public void spendPoints(View v) throws ParseException {
        list = databaseHandler.loadAccounts();
        SimpleDateFormat parser = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        int current_index = 0;
        int total_points = Integer.parseInt(list.get(0)[1]);
        if (list.size() > 0) {
            Date current_oldest = parser.parse(list.get(0)[2]);
            for (int i = 1; i < list.size(); i++) {
                Date current = parser.parse(list.get(i)[2]);
                if (current_oldest.compareTo(current) > 0) {
                    current_oldest = current;
                    current_index = i;
                }
                total_points += Integer.parseInt(list.get(i)[1]);
            }
        }

        int spent = Integer.parseInt(spend.getText().toString());
        int outcome = total_points - spent;
        if (total_points - spent < 0) {
            notEnoughPoints();
        } else {
            calculatePoints(spent, current_index);
        }
    }

    private void calculatePoints(int spent, int index) {
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date date = new Date();
        String current_date = formatter.format(date);

        String name = list.get(index)[0];
        int current_points = Integer.parseInt(list.get(index)[1]);

        if (current_points == 0) {
            notEnoughPoints();
        }
        else if (spent - current_points == 0) {
            payer = new Payer(name, 0, current_date);
            transactions.append("payer: " + name + " | points: -" +  current_points + " | timestamp: " + current_date);
            transactions.append("\n");
        } else if (spent - current_points < 0) {
            payer = new Payer(name, current_points - spent, current_date);
            transactions.append("payer: " + name + " | points: -" + (current_points - spent) + " | timestamp: " + current_date);
            transactions.append("\n");
        } else if (current_points - spent < 0) {
            payer = new Payer(name, 0, current_date);
            transactions.append("payer: " + name + " | points: -" + current_points + " | timestamp: " + current_date);
            transactions.append("\n");
        }

        databaseHandler.updatePayers(payer);
    }

    public void notEnoughPoints() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Not Enough points");
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onDestroy() {
        databaseHandler.shutDown();
        super.onDestroy();
    }
}