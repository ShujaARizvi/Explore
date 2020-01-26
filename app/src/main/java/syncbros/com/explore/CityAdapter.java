package syncbros.com.explore;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class CityAdapter extends ArrayAdapter<City> {

    Context context;

    public CityAdapter(Activity context, ArrayList<City> cities){
        super(context,0,cities);
        this.context = context;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item_main, parent, false);
        }

        City currentCity = getItem(position);

        TextView cityNameTextView = listItemView.findViewById(R.id.name_text_view);
        cityNameTextView.setText(currentCity.getCityName());

        TextView cityDescriptionTextView = listItemView.findViewById(R.id.description_text_view);
        cityDescriptionTextView.setText(currentCity.getCityDescription());

        ImageView cityImage = listItemView.findViewById(R.id.image);

        Glide.with(context)
                .load(currentCity.getImageResourceId())
                .centerCrop()
                .into(cityImage);

        listItemView.setOnClickListener(v -> {

            Intent i = new Intent(context, CityActivity.class);

            i.putExtra("city_name", currentCity.getCityName());
            i.putExtra("city_description", currentCity.getCityDescription());

            Pair<View, String> p1 = Pair.create(cityImage, "city_image");
            Pair<View, String> p2 = Pair.create(cityNameTextView, "city_name");
            Pair<View, String> p3 = Pair.create(cityDescriptionTextView, "city_description");

            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity)context, p1, p2, p3);
            context.startActivity(i, options.toBundle());
        });

        return listItemView;
    }
}
