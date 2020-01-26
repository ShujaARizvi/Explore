package syncbros.com.explore;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class ReservationActivity extends AppCompatActivity {

    private String reservationStartDate, reservationEndDate, roomCategory, hotelName, cityName, price; // other info
    private String customerName, CNIC, email, phoneNumber, address; // customer info

    private EditText firstName, lastName, emailField, cnicField, numberField, addressField;

    ArrayList<EditText> errorFields;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Reservation");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        firstName = findViewById(R.id.first_name);
        lastName = findViewById(R.id.last_name);
        emailField = findViewById(R.id.email);
        cnicField = findViewById(R.id.cnic);
        numberField = findViewById(R.id.phone);
        addressField = findViewById(R.id.address);

        errorFields = new ArrayList<>();
        errorFields.add(firstName);
        errorFields.add(lastName);
        errorFields.add(emailField);
        errorFields.add(cnicField);
        errorFields.add(numberField);
        errorFields.add(addressField);

        SharedPreferences sharedPreferences = getSharedPreferences(getResources().getString(R.string.preference), MODE_PRIVATE);
        reservationStartDate = sharedPreferences.getString("reservationStartDate","");
        reservationEndDate = sharedPreferences.getString("reservationEndDate","");

        Intent intent = getIntent();
        roomCategory = intent.getStringExtra("room_category");
        hotelName = intent.getStringExtra("hotel_name");
        cityName = intent.getStringExtra("city_name");
        price = intent.getStringExtra("price");


        for (EditText errorField : errorFields){
            errorField.setOnTouchListener((v, event) -> {
                revertWarning();
                return false;
            });
        }
    }

    public void confirmReservation(View view){

        if (!performCheck()){
            return;
        }

        customerName = firstName.getText().toString() + " " + lastName.getText().toString();
        CNIC = cnicField.getText().toString();
        email = emailField.getText().toString();
        phoneNumber = numberField.getText().toString();
        address = addressField.getText().toString();

        queryWebServer();


    }

    public void queryWebServer(){

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = getResources().getString(R.string.ip) + "/reservation";

        JSONObject jsonBody = new JSONObject();

        try {

            jsonBody.put("reservationStartDate",reservationStartDate);
            jsonBody.put("reservationEndDate",reservationEndDate);

            JSONObject customerInfo = new JSONObject();
            customerInfo.put("name", customerName);
            customerInfo.put("email", email);
            customerInfo.put("phoneNumber", phoneNumber);
            customerInfo.put("address", address);
            customerInfo.put("CNIC", CNIC);

            JSONObject roomInfo = new JSONObject();
            roomInfo.put("roomCategory", roomCategory);
            roomInfo.put("hotelName", hotelName);
            roomInfo.put("cityName", cityName);

            jsonBody.put("customerInfo", customerInfo);
            jsonBody.put("roomInfo", roomInfo);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String requestBody = jsonBody.toString();

        Log.v("reqeyt", requestBody);

        // Request a string response from the provided URL.

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {

                        Log.v("response in web api: ", response);
                        onSearchOccurred(response);
                }, error -> {
                    Log.v("error in web api: ", error.toString());
                    Toast.makeText(getApplicationContext(),"An error occurred fetching results",Toast.LENGTH_SHORT).show();
                }){
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                    return null;
                }
            }
        };

        queue.add(stringRequest);

    }

    private void onSearchOccurred(String response) {

        try {

            JSONObject responseBody = new JSONObject(response);

            boolean success = responseBody.getBoolean("reservationSuccess");
            String reservationNumber = responseBody.getString("reservationNumber");

            if (success){
                String reservationInfoSharedPreferenceString = getResources().getString(R.string.reservation_preference);
                SharedPreferences sharedPreferences = getSharedPreferences(reservationInfoSharedPreferenceString, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                Date datetime = Calendar.getInstance().getTime();
                String reservationString = String.format("%s,%s,%s,%s,%s,%s,%s,%s", datetime.toString(), reservationStartDate,reservationEndDate, roomCategory, hotelName, cityName, price, reservationNumber);

                Set<String> reservationSet = new HashSet<>(sharedPreferences.getStringSet("reservation_set", new HashSet<>()));
                reservationSet.add(reservationString);

                editor.putStringSet("reservation_set", reservationSet);
                editor.commit();

                Intent intent = new Intent(this, ReservationSuccess.class);
                intent.putExtra("reservationNumber", reservationNumber);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }else{
                Toast.makeText(this, "There was some error processing your request. Please try again", Toast.LENGTH_LONG).show();
            }

        } catch (Throwable t) {
            Log.e("Parsing error", t.toString());
            Toast.makeText(getApplicationContext(),"Error parsing results",Toast.LENGTH_SHORT).show();
            Log.v("My App", "Could not parse malformed JSON: \"" + response + "\"");
        }

    }



    private boolean performCheck(){

        ArrayList<EditText> errorFields = new ArrayList<>();

        if (firstName.getText().toString().compareTo("") == 0){
            errorFields.add(firstName);
        }
        if (lastName.getText().toString().compareTo("") == 0){
            errorFields.add(lastName);
        }
        if (!emailField.getText().toString().matches("^([a-zA-Z0-9_\\-\\\\.]+)@([a-zA-Z0-9_\\-\\\\.]+)\\.([a-zA-Z]{2,5})$")){
            errorFields.add(emailField);
        }
        if (cnicField.getText().toString().length() != 13){
            errorFields.add(cnicField);
        }
        if (!numberField.getText().toString().matches("^[0][3][\\d]{2}[\\d]{7}$")){
            errorFields.add(numberField);
        }
        if (addressField.getText().toString().compareTo("") == 0){
            errorFields.add(addressField);
        }

        for (EditText errorField : errorFields){
            errorField.setText("");
            errorField.setHint("Correction required");
        }

        if (errorFields.size() > 0){
            Toast.makeText(this, "Field(s) empty or not satisfied", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;

    }

    private void revertWarning(){

        for (EditText errorField : errorFields){
            errorField.setHint("");
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
