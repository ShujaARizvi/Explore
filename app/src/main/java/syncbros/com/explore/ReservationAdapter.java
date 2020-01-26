package syncbros.com.explore;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import java.util.ArrayList;

public class ReservationAdapter extends ArrayAdapter<Reservation> {

    Context context;

    public ReservationAdapter(Activity context, ArrayList<Reservation> reservations){
        super(context,0,reservations);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.reservation_item, parent, false);
        }

        Reservation currentReservation = getItem(position);

        TextView reservationTimeTextView = listItemView.findViewById(R.id.reservation_time);
        reservationTimeTextView.setText(currentReservation.getDateTime());

        TextView roomTypeTextView = listItemView.findViewById(R.id.room_type);
        String roomType = currentReservation.getRoomCategory();

        roomTypeTextView.setText(roomType);

        if (roomType.compareTo("GOLD") == 0){
            roomTypeTextView.setBackground(context.getResources().getDrawable(R.drawable.gold_shape));
        } else if (roomType.compareTo("SILVER") == 0){
            roomTypeTextView.setBackground(context.getResources().getDrawable(R.drawable.silver_shape));
        } else {
            roomTypeTextView.setBackground(context.getResources().getDrawable(R.drawable.platnium_shape));
        }

        TextView priceTextView = listItemView.findViewById(R.id.price);
        priceTextView.setText(currentReservation.getPrice());

        TextView hotelNameTextView = listItemView.findViewById(R.id.hotel_name);
        hotelNameTextView.setText(currentReservation.getHotelName());

        TextView timeSpanTextView = listItemView.findViewById(R.id.time_span);
        timeSpanTextView.setText(currentReservation.getTimeSpan());


        return listItemView;
    }


}
