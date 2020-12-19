package com.geek.mockesp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MainActivity extends AppCompatActivity {


    private ProgressDialog dialog;

    private SwitchCompat sLed1;
    private SwitchCompat sLed2;

    private static final String BASE_URL = "http://192.168.1.9/";

    private static final String LED_1_ON = "LED1 Status: ON";
    private static final String LED_2_ON = "LED2 Status: ON";
    private static final String LED_1_OFF = "LED1 Status: OFF";
    private static final String LED_2_OFF = "LED2 Status: OFF";

    private String ledOneState;
    private String ledTwoState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sLed1 = findViewById(R.id.sLed1);
        sLed2 = findViewById(R.id.sLed2);

        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setMessage("Loading...");


        new Thread(() -> {
            try {
                runOnUiThread(() -> dialog.show());
                Document document = Jsoup.connect(BASE_URL).get();

                runOnUiThread(() -> {
                    dialog.dismiss();
                    for (Element p : document.select("p")) {
                        switch (p.html()) {
                            case LED_1_ON:
                                sLed1.setChecked(true);
                                break;
                            case LED_1_OFF:
                                sLed1.setChecked(false);
                                break;
                            default:
                                ledOneState = p.html();
                                Log.e("elements", p.html());
                        }

                        switch (p.html()) {
                            case LED_2_ON:
                                sLed2.setChecked(true);
                                break;
                            case LED_2_OFF:
                                sLed2.setChecked(false);
                                break;
                            default:
                                ledTwoState = p.html();
                                Log.e("elements", p.html());
                        }
                    }

                    Log.e("led 1", ledOneState);
                    Log.e("led 2", ledTwoState);
                });
            } catch (Exception e) {
                Log.e("Document", "eee");
            }
        }).start();


        sLed1.setOnClickListener(v -> {
            dialog.show();
            String url = BASE_URL + (ledOneState.equals(LED_1_ON) ? "led1off" : "led1on");
            Log.e("url", url);
            Volley.newRequestQueue(this).add(
                    new StringRequest(Request.Method.GET, url, response -> {
                        dialog.dismiss();
                        ledOneState = sLed1.isChecked() ? LED_1_ON : LED_1_OFF;
                        Log.e("response", response);
                    }, error -> {
                        dialog.dismiss();
                        Log.e("volley error", error.getMessage());
                    }));
        });

        sLed2.setOnClickListener(v -> {
            dialog.show();
            String url = BASE_URL + (ledTwoState.equals(LED_2_ON) ? "led2off" : "led2on");
            Log.e("url", url);
            Volley.newRequestQueue(this).add(
                    new StringRequest(Request.Method.GET, url, response -> {
                        dialog.dismiss();
                        ledTwoState = sLed2.isChecked() ? LED_2_ON : LED_2_OFF;
                        Log.e("response", response);
                    }, error -> {
                        dialog.dismiss();
                        Log.e("volley error", error.getMessage());
                    }));
        });
    }
}