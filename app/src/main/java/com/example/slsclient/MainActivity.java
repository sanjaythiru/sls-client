package com.example.slsclient;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private ScheduledExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                String url = "http://4.213.124.227:8381/status";

                JsonObjectRequest statusRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                TextView deviceStatusTextView = findViewById(R.id.deviceStatusTextViewValue);
                                String status;
                                try {
                                    status = response.getString("status");
                                    deviceStatusTextView.setText(status);
                                } catch(JSONException ex) {
                                    Toast toast = Toast.makeText(MainActivity.this, "Unable to parse the response", Toast.LENGTH_SHORT);
                                    toast.show();
                                }
                            }}, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        TextView deviceStatusTextView = findViewById(R.id.deviceStatusTextViewValue);
                        deviceStatusTextView.setText("ERROR");
                    }
                });

                queue.add(statusRequest);
            }
        }, 0, 5, TimeUnit.SECONDS);
    }

    public void onForceOnButtonClick(View view) {
        sendRequest("FORCE_ON");
    }

    public void onForceOffButtonClick(View view) {
        sendRequest("FORCE_OFF");
    }

    public void onDetectionBasedButtonClick(View view) {
        sendRequest("DETECTION_BASED");
    }

    private void sendRequest(String deviceMode) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://4.213.124.227:8381/devicemode";
        HashMap<String, String> params = new HashMap<>();
        params.put("deviceMode", deviceMode);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast toast = Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT);
                        toast.show();
                    }}, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast toast = Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        queue.add(request);

    }
}