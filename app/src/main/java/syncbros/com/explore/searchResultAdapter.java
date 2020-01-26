package syncbros.com.explore;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v4.util.Pair;
import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Random;

public class searchResultAdapter extends RecyclerView.Adapter<searchResultAdapter.MyViewHolder> {

    private List<Hotel> hotelList;
    private int[] imageIds;
    private Random rand;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView hotel_image;
        public TextView room_category;
        public TextView stars;
        public TextView price;
        public TextView location;
        public TextView hotel_name;


        public MyViewHolder(View view) {
            super(view);
            context = view.getContext();
            hotel_image = view.findViewById(R.id.hotel_image_search);
            room_category = view.findViewById(R.id.room_type);
            stars = view.findViewById(R.id.stars_search);
            price = view.findViewById(R.id.hotel_search_price);
            location = view.findViewById(R.id.hotelLocation);
            hotel_name = view.findViewById(R.id.hotel_name_search);
        }
    }

    public searchResultAdapter(List<Hotel> hotelList) {
        this.hotelList = hotelList;
        imageIds = new int[]{R.drawable.islamabad2, R.drawable.karachi, R.drawable.lahore, R.drawable.murree};
        rand = new Random();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_search2, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Hotel hotel = hotelList.get(position);
        holder.hotel_name.setText(hotel.name);
        holder.location.setText(hotel.location);
        holder.price.setText("Rs. " + hotel.price + " / Night");
        holder.room_category.setText(hotel.type);

        if( hotel.type.equals("GOLD") ){
            holder.room_category.setBackgroundResource(R.drawable.gold_shape);
        }else if(hotel.type.equals("SILVER")){
            holder.room_category.setBackgroundResource(R.drawable.silver_shape);
        }else{
            holder.room_category.setBackgroundResource(R.drawable.platnium_shape);
        }

        holder.stars.setText(hotel.stars + " Stars");


        int value = rand.nextInt(4);

        Glide.with(context)
                .load(imageIds[value])
                .centerCrop()
                .into(holder.hotel_image);

        holder.itemView.setOnClickListener(view -> {
            Intent i = new Intent(view.getContext(),hotel_main.class);
            i.putExtra("hotel_name",hotel.name);
            i.putExtra("hotel_price",hotel.price);
            i.putExtra("hotel_image",imageIds[value]);
            i.putExtra("hotel_location",hotel.location);
            i.putExtra("hotel_city", hotel.city);
            i.putExtra("hotel_stars",hotel.stars);
            i.putExtra("room_type",hotel.type);
            i.putExtra("feature_description", hotel.feature_description);

            Pair<View, String> p1 = Pair.create(holder.hotel_image, "transitionImage");
            Pair<View, String> p2 = Pair.create(holder.hotel_name, "transitionHotelName");
            Pair<View, String> p3 = Pair.create(holder.location, "transitionAddress");
            Pair<View, String> p4 = Pair.create(holder.price, "transitionPrice");
            Pair<View, String> p5 = Pair.create(holder.room_category, "transitionRoomCategory");

            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity)view.getContext(), p1, p2, p3, p4, p5);
            view.getContext().startActivity(i, options.toBundle());
        });

    }

    @Override
    public int getItemCount() {
        return hotelList.size();
    }
}