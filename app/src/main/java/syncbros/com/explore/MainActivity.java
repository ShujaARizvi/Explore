package syncbros.com.explore;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
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

import org.florescu.android.rangeseekbar.RangeSeekBar;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements FullScreenDialog.EditDialogListener {


    RecyclerView rv;
    ListView lv;
    searchResultAdapter adapter;
    private CityAdapter cityAdapter;
    ArrayList<Hotel> hotelList;
    ArrayList<City> cityList;
    SearchView searchView;
    private DatePicker datePicker;
    public static TextView startDate;
    public static TextView endDate;
    FullScreenDialog dialog;
    LinearLayout progressBarLayout;
    TextView noResult;
    ImageView dateFromImageView, dateToImageView;

    private boolean doubleBackToExitPressedOnce = false;

    long days;
    boolean searchOccured = false;
    public static String cityName = "";
    String roomCategory = "", hotelName = "";
    int hotelCategory = 0, priceMax = 60000, priceMin = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Explore");

        rv = findViewById(R.id.rv_view_main);
        lv = findViewById(R.id.list_view_main);
        hotelList = new ArrayList<>();
        adapter = new searchResultAdapter(hotelList);

        searchView = findViewById(R.id.search_view);
        startDate = findViewById(R.id.start_date);
        endDate = findViewById(R.id.end_date);
        noResult = findViewById(R.id.no_result);

        try {
            Calendar calendar = Calendar.getInstance();

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            String startDateDate = sdf.format(calendar.getTime());

            calendar.add(Calendar.DAY_OF_YEAR, 1);

            String endDateDate = sdf.format(calendar.getTime());

            startDate.setText(startDateDate);
            endDate.setText(endDateDate);

        }catch (Exception e){
            Log.v("Didnt", "Didn't work date work" + e.toString());
        }

        dateFromImageView = findViewById(R.id.date_from_image);
        dateToImageView = findViewById(R.id.date_to_image);

        cityList = new ArrayList<>();

        cityList.add(new City("Karachi", getResources().getString(R.string.Karachi), R.drawable.karachi));
        cityList.add(new City("Islamabad", getResources().getString(R.string.Islamabad), R.drawable.islamabad2));
        cityList.add(new City("Lahore", getResources().getString(R.string.Lahore), R.drawable.lahore));
        cityList.add(new City("Murree", getResources().getString(R.string.Murree), R.drawable.murree));

        cityAdapter = new CityAdapter(this, cityList);
        lv.setAdapter(cityAdapter);

        searchView.setOnClickListener(v -> searchView.setIconified(false));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                queryWebServer();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                cityName = s;
                return false;
            }
        });

        new Handler().postDelayed(() -> searchView.clearFocus(), 200);

    }

    private void onSearchOccured(String response) {

        try {

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            Date startDateDate = sdf.parse(startDate.getText().toString());
            Date endDateDate = sdf.parse(endDate.getText().toString());

            long diff = endDateDate.getTime() - startDateDate.getTime();

            if(diff <= 0){

                Toast.makeText(getApplicationContext(),"Please select valid dates.",Toast.LENGTH_SHORT).show();
                return;

            }

            days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
            SharedPreferences sharedPreferences = getSharedPreferences(getResources().getString(R.string.preference), MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putLong("daysOfStay", days);
            editor.commit();

        }catch (Throwable t){

            Log.e("date issue", t.toString());
            Toast.makeText(getApplicationContext(),"date issue",Toast.LENGTH_SHORT).show();

        }

        try {

            JSONObject obj = new JSONObject(response);

            JSONArray rooms = obj.getJSONArray("rooms");

            for (int i=0;i<rooms.length();i++) {

                JSONObject currentRoom  = rooms.getJSONObject(i);

                hotelList.add(new Hotel(currentRoom.getString("hotelName"),currentRoom.getString("roomType"),currentRoom.getInt("hotelStars"),-1,currentRoom.getInt("roomCost"),currentRoom.getString("hotelLocation"), cityName, currentRoom.getString("featureDescription")));
            }

            LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(mLayoutManager);

        adapter = new searchResultAdapter(hotelList);

        rv.setAdapter(adapter);

        } catch (Throwable t) {
            Log.e("ParsingError", t.toString());
            Toast.makeText(getApplicationContext(),"Error parsing results",Toast.LENGTH_SHORT).show();
            Log.v("MyApp", "Could not parse malformed JSON: \"" + response + "\"");
        }

    }

    public void onStartDateClick(View v){

        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, monthOfYear, dayOfMonth) -> {
                    String dateString = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                    startDate.setText(dateString);
                    String calendarImage = "calendar_" + dayOfMonth;
                    dateFromImageView.setImageResource(getIdentifier(this, calendarImage, "drawable"));

                    }, mYear, mMonth, mDay);

        datePickerDialog.show();

        Long msInADay = Long.valueOf(86400000);
        Long msInAWeek = msInADay * 7;

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

        Date endDateDate = new Date();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            endDateDate = sdf.parse(endDate.getText().toString());

        }catch (Exception e){}
        datePickerDialog.getDatePicker().setMaxDate(endDateDate.getTime() - 86400000);
    }

    public void onEndDateClick(View v){

        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, monthOfYear, dayOfMonth) -> {

                    String dateString = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                    endDate.setText(dateString);
                    String calendarImage = "calendar_" + dayOfMonth;
                    dateToImageView.setImageResource(getIdentifier(this, calendarImage, "drawable"));

                }, mYear, mMonth, mDay);
        datePickerDialog.show();

        Long msInADay = new Long(86400000);
        Long msInAMonth = msInADay * 30;

        Date startDateDate = new Date();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            startDateDate = sdf.parse(startDate.getText().toString());

        }catch (Exception e){}

        datePickerDialog.getDatePicker().setMinDate(startDateDate.getTime() + msInADay);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() + msInAMonth);

    }

    public void onFilterClick(View view) {

        dialog = new FullScreenDialog();
        FragmentManager fm = this.getSupportFragmentManager();

        Bundle bundle = new Bundle();

        bundle.putSerializable("hotel_category",hotelCategory);
        bundle.putSerializable("room_category",roomCategory);
        bundle.putSerializable("hotelName",hotelName);
        bundle.putSerializable("priceMax",priceMax);
        bundle.putSerializable("priceMin",priceMin);

        dialog.setArguments(bundle);
        dialog.show(fm,FullScreenDialog.TAG);

    }

    public void onCategoryClick(String roomType){

        Button goldButton = dialog.getDialog().findViewById(R.id.gold_select);
        Button silverButton = dialog.getDialog().findViewById(R.id.silver_select);
        Button platinumButton = dialog.getDialog().findViewById(R.id.platinum_select);

        Log.e("here", goldButton + " " + silverButton + " " + platinumButton);

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

    public void onPlatinumSelectClick(View v){

        roomCategory = "PLATINUM";
        onCategoryClick("Platinum");

    }

    public void onSilverSelectClick(View v){

        onCategoryClick("SILVER");
        roomCategory = "SILVER";

    }

    public void onGoldSelectClick(View v){

        roomCategory = "GOLD";
        onCategoryClick("GOLD");

    }

    public void onHotelCategoryClick(String roomType){

        Button threeButton = dialog.getDialog().findViewById(R.id.three_select);
        Button fiveButton = dialog.getDialog().findViewById(R.id.five_select);
        Button sevenButton = dialog.getDialog().findViewById(R.id.seven_select);

        Log.e("here", threeButton + " " + fiveButton + " " + sevenButton);

        if(roomType.equals("THREE")){

            threeButton.setTextColor(Color.WHITE);
            fiveButton.setTextColor(Color.BLACK);
            sevenButton.setTextColor(Color.BLACK);
            threeButton.setBackgroundResource(R.drawable.button_shape_pressed);
            fiveButton.setBackgroundResource(R.drawable.button_shape_default);
            sevenButton.setBackgroundResource(R.drawable.button_shape_default);

        }else if(roomType.equals("FIVE")){

            threeButton.setTextColor(Color.BLACK);
            fiveButton.setTextColor(Color.WHITE);
            sevenButton.setTextColor(Color.BLACK);
            threeButton.setBackgroundResource(R.drawable.button_shape_default);
            fiveButton.setBackgroundResource(R.drawable.button_shape_pressed);
            sevenButton.setBackgroundResource(R.drawable.button_shape_default);

        }else{

            threeButton.setTextColor(Color.BLACK);
            fiveButton.setTextColor(Color.BLACK);
            sevenButton.setTextColor(Color.WHITE);
            threeButton.setBackgroundResource(R.drawable.button_shape_default);
            fiveButton.setBackgroundResource(R.drawable.button_shape_default);
            sevenButton.setBackgroundResource(R.drawable.button_shape_pressed);
        }

    }

    public void onSevenSelectClick(View v){

        onHotelCategoryClick("SEVEN");
        hotelCategory = 7;

    }

    public void onFiveSelectClick(View v){

        onHotelCategoryClick("FIVE");
        hotelCategory = 5;

    }

    public void onThreeSelectClick(View v){

        onHotelCategoryClick("THREE");
        hotelCategory = 3;

    }

    public void queryWebServer(){


        if(startDate.getText().toString().equals("mm-dd-yyyy")){
            Toast.makeText(getApplicationContext(),"Please enter valid dates",Toast.LENGTH_SHORT).show();
            return;
        }else if(endDate.getText().toString().equals("mm-dd-yyyy")){
            Toast.makeText(getApplicationContext(),"Please enter valid dates",Toast.LENGTH_SHORT).show();
            return;
        }
        closeKeyboard();

        if(cityName.equals("")){

            Toast.makeText(getApplicationContext(),"Please enter City Name",Toast.LENGTH_SHORT).show();
            return;

        }

        hotelList.clear();
        adapter.notifyDataSetChanged();
        progressBarLayout = findViewById(R.id.indeterminateBar);
        lv.setVisibility(View.GONE);
        progressBarLayout.setVisibility(View.VISIBLE);
        noResult.setVisibility(View.GONE);


        Log.v("in func ", "before req");

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = getResources().getString(R.string.ip) + "/room";


        String[] startDateSplitted = startDate.getText().toString().split("-");
        String startDateStr = startDateSplitted[2] + "-" + startDateSplitted[1] + "-" + startDateSplitted[0];

        String[] endDateSplitted = endDate.getText().toString().split("-");
        String endDateStr = endDateSplitted[2] + "-" + endDateSplitted[1] + "-" + endDateSplitted[0];

        SharedPreferences sharedPreferences = getSharedPreferences(getResources().getString(R.string.preference), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("reservationStartDate", startDateStr);
        editor.putString("reservationEndDate", endDateStr);
        editor.commit();

        JSONObject jsonBody = new JSONObject();

        try {

            JSONObject filters = new JSONObject();
            jsonBody.put("reservationStartDate",startDateStr);
            jsonBody.put("reservationEndDate",endDateStr);
            filters.put("City",cityName);

            if(!hotelName.equals("")){
                filters.put("Hotel_Name",hotelName);
            }

            if(!roomCategory.equals("")){
                filters.put("Room_Category",roomCategory);
            }

            if(hotelCategory != 0){
                filters.put("Hotel_Category",hotelCategory);
            }

            JSONObject pricing = new JSONObject();
            pricing.put("Lower_Bound",priceMin);
            pricing.put("Upper_Bound",priceMax);
            filters.put("Pricing",pricing);

            jsonBody.put("filters",filters);



        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String requestBody = jsonBody.toString();

        Log.v("reqeyt", requestBody);

        // Request a string response from the provided URL.

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {

                    Log.v("response in web api: ", response);

                    progressBarLayout.setVisibility(View.GONE);
                    lv.setVisibility(View.GONE);
                    rv.setVisibility(View.VISIBLE);

                    if (response.length() == 12){
                        Log.v("Triggered", "True");
                        noResult.setVisibility(View.VISIBLE);
                    }else{
                        noResult.setVisibility(View.GONE);
                    }

                    onSearchOccured(response);
                    searchOccured = true;
                }, error -> {
                    //mTextView.setText("That didn't work!");
                    Log.v("error in web api: ", error.toString());
                    Toast.makeText(getApplicationContext(),"An error occurred fetching results",Toast.LENGTH_SHORT).show();
                    progressBarLayout.setVisibility(View.GONE);
                    noResult.setVisibility(View.VISIBLE);
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

        //            @Override
        //            protected Response<String> parseNetworkResponse(NetworkResponse response) {
        //                String responseString = "";
        //                if (response != null) {
        //                    responseString = String.valueOf(response.statusCode);
        //                    // can get more details such as response.headers
        //                }
        //                return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
        //            }
                };

//        stringRequest.setRetryPolicy(new DefaultRetryPolicy( 1000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Add the request to the RequestQueue.
        queue.add(stringRequest);

    }


    @Override
    public void updateResult(String hotelName,String priceMin,String priceMax,String hotelCategoryParam,String roomCategoryParam ) {

        this.hotelName = hotelName;

        this.priceMax = Integer.parseInt(priceMax);
        this.priceMin = Integer.parseInt(priceMin);

        if(hotelCategoryParam.equals("")){
           hotelCategory = 0;
        }else if(hotelCategoryParam.equals("THREE")){
            hotelCategory = 3;
        }else if(hotelCategoryParam.equals("FIVE")){
            hotelCategory = 5;
        }else{
            hotelCategory = 7;
        }

        roomCategory = roomCategoryParam;

        queryWebServer();



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_reservations:
                Intent intent = new Intent(this, ReservationHistory.class);
                startActivity(intent);
                break;
            case R.id.go_button:
                queryWebServer();
                break;
            default:
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            try {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }catch (Exception e){

            }
        }
    }

    public static int getIdentifier(Context context, String name, String resourceType) {
        return context.getResources().getIdentifier(name, resourceType, context.getPackageName());
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
