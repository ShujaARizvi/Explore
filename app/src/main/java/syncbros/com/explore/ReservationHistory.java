package syncbros.com.explore;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ReservationHistory extends AppCompatActivity {

    SwipeMenuListView reservationsListView;
    ArrayList<Reservation> reservations;
    ReservationAdapter reservationsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_history);

        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My Reservations");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        reservationsListView = findViewById(R.id.reservationsListView);

        reservations = new ArrayList<>();
        populateReservationsList(reservations);

        reservationsAdapter = new ReservationAdapter(this, reservations);
        reservationsListView.setAdapter(reservationsAdapter);


        SwipeMenuCreator creator = menu -> {

            // create "cancel" item
            SwipeMenuItem openItem = new SwipeMenuItem(
                    getApplicationContext());
            // set item background c9302c
            openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0x30,
                    0x2C)));
            // set item width
            openItem.setWidth(360);
            // set item title
            openItem.setTitle("Cancel");
            // set item title font-size
            openItem.setTitleSize(18);
            // set item title font color
            openItem.setTitleColor(Color.WHITE);
            // add to menu
            menu.addMenuItem(openItem);

        };

        reservationsListView.setMenuCreator(creator);

        reservationsListView.setOnMenuItemClickListener((position, menu, index) -> {
            switch (index) {
                case 0:
                    // cancel
                    queryWebServer(reservations.get(position).getReservationNumber(), position);
                    break;
            }
            // false : close the menu; true : not close the menu
            return false;
        });
    }

    private void populateReservationsList(ArrayList<Reservation> reservations){

        SharedPreferences sharedPreferences = getSharedPreferences(getResources().getString(R.string.reservation_preference), MODE_PRIVATE);

        Set<String> reservationSet = sharedPreferences.getStringSet("reservation_set", new HashSet<>());

        Log.v("Reservation_Set", reservationSet.toArray().toString());

        for (String reservation : reservationSet){

            String[] splitReservation = reservation.split(",");
            reservations.add(new Reservation(splitReservation[0], splitReservation[4], splitReservation[5], splitReservation[3], splitReservation[1],
                    splitReservation[2], splitReservation[6], splitReservation[7]));

        }
    }

    public void queryWebServer(String reservationNumber, int position){

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = getResources().getString(R.string.ip) + "/reservation/" + reservationNumber;

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url,
                response -> {

                    Log.v("response in web api: ", response);
                    onSearchOccurred(response, position, reservationNumber);
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

    public void onSearchOccurred(String response, int position, String reservationNumber){
        try{
            JSONObject responseBody = new JSONObject(response);

            boolean success = responseBody.getBoolean("cancellationResult");

            if (!success){
                Toast.makeText(this, "Reservation already accepted, please contact the hotel to cancel your reservation", Toast.LENGTH_LONG).show();
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
            reservations.remove(position);
            reservationsAdapter.notifyDataSetChanged();

        } catch (Exception e){
            Toast.makeText(this, "Error processing request, please try again", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


}
