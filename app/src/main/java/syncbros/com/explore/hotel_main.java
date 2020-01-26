package syncbros.com.explore;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class hotel_main extends AppCompatActivity {

    SliderLayout imagesSlider;
    TextView hotel_name_view, hotel_price_view, hotel_address_view, feature_desc;
    ImageView hotel_stars_view;
    Button reserveButton;
    LinearLayout availabilityLayout;
    RequestQueue queue;

    String hotel_name, room_type, hotel_location, cityName, feature_description, startDate, endDate;
    int hotel_image, hotel_stars;
    long hotel_price, days;
    boolean isRoomAvailable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_main);

        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Room Details");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        hotel_name = getIntent().getStringExtra("hotel_name");
        room_type = getIntent().getStringExtra("room_type");
        hotel_price = getIntent().getLongExtra("hotel_price", -1);
        hotel_image = getIntent().getIntExtra("hotel_image",-1);
        hotel_location = getIntent().getStringExtra("hotel_location");
        cityName = getIntent().getStringExtra("hotel_city");
        hotel_stars = getIntent().getIntExtra("hotel_stars", 3);
        feature_description = getIntent().getStringExtra("feature_description");
        isRoomAvailable = true;

        SharedPreferences sharedPreferences = getSharedPreferences(getResources().getString(R.string.preference), MODE_PRIVATE);
        startDate = sharedPreferences.getString("reservationStartDate", "");
        endDate = sharedPreferences.getString("reservationEndDate", "");
        days = sharedPreferences.getLong("daysOfStay", 0);

//        listView.setAdapter(adapter);

        imagesSlider = findViewById(R.id.imagesSlider);

        imagesSlider.stopAutoCycle();

        ArrayList<Integer> hotelImages = new ArrayList<>();
        hotelImages.add(hotel_image);

        for (Integer id: hotelImages) {
            TextSliderView textSliderView = new TextSliderView(this);
            // initialize a SliderLayout
            textSliderView
                    .image(id)
                    .setScaleType(BaseSliderView.ScaleType.CenterCrop);

            imagesSlider.addSlider(textSliderView);
        }

        hotel_name_view = findViewById(R.id.hotel_name_main);
        hotel_price_view = findViewById(R.id.price_main);
        hotel_address_view = findViewById(R.id.address_main);
        hotel_stars_view = findViewById(R.id.stars_main);
        feature_desc = findViewById(R.id.feature_description);
        availabilityLayout = findViewById(R.id.room_availability);
        reserveButton = findViewById(R.id.reserve_button);

        updateUI();

        onRoomTypeClick(room_type);

    }

    public void onRoomTypeClick(String roomType){

        Button goldButton = findViewById(R.id.gold);
        Button silverButton = findViewById(R.id.silver);
        Button platinumButton = findViewById(R.id.platinum);

        if(roomType.equals("GOLD")){

            goldButton.setTextColor(Color.WHITE);
            silverButton.setTextColor(Color.BLACK);
            platinumButton.setTextColor(Color.BLACK);
            goldButton.setBackgroundResource(R.drawable.button_shape_pressed);
            silverButton.setBackgroundResource(R.drawable.button_shape_default);
            platinumButton.setBackgroundResource(R.drawable.button_shape_default);

        }else if(roomType.equals("SILVER")){

            goldButton.setTextColor(Color.BLACK);
            silverButton.setTextColor(Color.WHITE);
            platinumButton.setTextColor(Color.BLACK);
            goldButton.setBackgroundResource(R.drawable.button_shape_default);
            silverButton.setBackgroundResource(R.drawable.button_shape_pressed);
            platinumButton.setBackgroundResource(R.drawable.button_shape_default);

        }else{

            goldButton.setTextColor(Color.BLACK);
            silverButton.setTextColor(Color.BLACK);
            platinumButton.setTextColor(Color.WHITE);
            goldButton.setBackgroundResource(R.drawable.button_shape_default);
            silverButton.setBackgroundResource(R.drawable.button_shape_default);
            platinumButton.setBackgroundResource(R.drawable.button_shape_pressed);
        }

    }

    public void goldButtonClick(View v){

        queryWebServer("GOLD");
        onRoomTypeClick("GOLD");
    }

    public void silverButtonClick(View v){

        queryWebServer("SILVER");
        onRoomTypeClick("SILVER");
    }

    public void platinumButtonClick(View v){

        queryWebServer("PLATINUM");
        onRoomTypeClick("PLATINUM");

    }

    public void onReserveClick(View v){

        Intent intent = new Intent(this, ReservationActivity.class);
        intent.putExtra("hotel_name", hotel_name);
        intent.putExtra("city_name", cityName);
        intent.putExtra("room_category", room_type);
        intent.putExtra("reservation_start_date", startDate);
        intent.putExtra("reservation_end_date", endDate);
        intent.putExtra("price", String.valueOf(hotel_price * days));
        startActivity(intent);

    }

    public void queryWebServer(String roomType){

        String url = getResources().getString(R.string.ip) + "/specified_room";

        JSONObject jsonBody = new JSONObject();

        try {
            jsonBody.put("reservationStartDate",startDate);
            jsonBody.put("reservationEndDate",endDate);

            JSONObject roomInfo = new JSONObject();
            roomInfo.put("roomCategory", roomType);
            roomInfo.put("hotelName", hotel_name);
            roomInfo.put("cityName", cityName);

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

    private void onSearchOccurred(String response){

        try {
            JSONObject responseBody = new JSONObject(response);

            isRoomAvailable = responseBody.getBoolean("availabilityStatus");

            JSONObject hotelInfo = responseBody.getJSONObject("hotelInfo");
            JSONObject roomInfo = responseBody.getJSONObject("roomInfo");

            hotel_name = hotelInfo.getString("hotelName");
            hotel_stars = hotelInfo.getInt("hotelStars");
            hotel_location = hotelInfo.getString("hotelLocation");

            hotel_price = Integer.parseInt(roomInfo.getString("roomCost"));
            room_type = roomInfo.getString("roomType");
            feature_description = roomInfo.getString("featureDescription");

            updateUI();

        }catch (Exception e){
            Toast.makeText(this, "There was some error completing your request, please try again", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUI(){

        hotel_name_view.setText(hotel_name);

        if (hotel_stars == 3){
            hotel_stars_view.setImageResource(R.drawable.star_3);
        } else {
            hotel_stars_view.setImageResource(R.drawable.star_5);
        }

        String priceString = "Rs. " + hotel_price * days + " for " + days + " night(s)";
        hotel_price_view.setText(priceString);
        hotel_address_view.setText(hotel_location);

        String[] features = feature_description.split("\\.");
        StringBuilder featureList = new StringBuilder();

        for (String feature : features){
            featureList.append("- " + feature.trim());
            featureList.append(System.getProperty("line.separator"));
        }
        feature_desc.setText(featureList);

        if (isRoomAvailable){
            availabilityLayout.setVisibility(View.GONE);
            reserveButton.setVisibility(View.VISIBLE);
        } else {
           availabilityLayout.setVisibility(View.VISIBLE);
           reserveButton.setVisibility(View.GONE);
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
