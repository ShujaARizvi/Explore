package syncbros.com.explore;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Set;

import cdflynn.android.library.checkview.CheckView;

public class ReservationSuccess extends AppCompatActivity {

    String reservationNumber;
    Button browse, cancel;
    CheckView checkView;

    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_success);

        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Reservation");

        Intent intent = getIntent();
        reservationNumber = intent.getStringExtra("reservationNumber");

        cancel = findViewById(R.id.cancel_button);
        browse = findViewById(R.id.browse_button);
        checkView = findViewById(R.id.reservation_complete);

        checkView.check();

        cancel.setOnClickListener(v -> queryWebServer());
        browse.setOnClickListener(v -> {
            Intent intent1 = new Intent(ReservationSuccess.this, MainActivity.class);
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent1);
        });
    }

    public void queryWebServer(){

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = getResources().getString(R.string.ip) + "/reservation/" + reservationNumber;

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url,
                response -> {

                    Log.v("response in web api: ", response);
                    onSearchOccurred(response);
                }, error -> {
            Toast.makeText(getApplicationContext(),"An error occurred fetching results",Toast.LENGTH_SHORT).show();
        }){
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
        };

        queue.add(stringRequest);

    }

    public void onSearchOccurred(String response){
        try{
            JSONObject responseBody = new JSONObject(response);

            boolean success = responseBody.getBoolean("cancellationResult");

            if (!success){
                Toast.makeText(this, "Error processing request, please try again", Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPreferences sharedPreferences = getSharedPreferences(getResources().getString(R.string.reservation_preference), MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            Set<String> reservationSet = new HashSet<>(sharedPreferences.getStringSet("reservation_set", new HashSet<>()));

            for (String reservation : reservationSet){
                if (reservation.split(",")[7].compareTo(reservationNumber) == 0){
                    reservationSet.remove(reservation);
                    break;
                }
            }
            editor.putStringSet("reservation_set", reservationSet);
            editor.commit();

            Toast.makeText(this, "Reservation Cancelled", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        } catch (Exception e){
            Toast.makeText(this, "Error processing request, please try again", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(() -> doubleBackToExitPressedOnce=false, 2000);
    }
}
