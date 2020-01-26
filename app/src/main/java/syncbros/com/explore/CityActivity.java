package syncbros.com.explore;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;

public class CityActivity extends AppCompatActivity {

    SliderLayout imagesSlider;
    TextView nameTextView, descriptionTextView, roomInfoTextView, attractionsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city);

        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imagesSlider = findViewById(R.id.cityImagesSlider);
        nameTextView = findViewById(R.id.name_text_view);
        descriptionTextView = findViewById(R.id.description_text_view);
        roomInfoTextView = findViewById(R.id.room_info);
        attractionsTextView = findViewById(R.id.tourist_attractions_text_view);

        Intent intent = getIntent();
        String cityName = intent.getStringExtra("city_name");
        String cityDescription = cityName + "_Details";
        String cityRoomInfo = cityName + "_room_info";
        String cityTouristAttractions = cityName + "_tourist_attractions";

        nameTextView.setText(cityName);
        descriptionTextView.setText(getResources().getString(getIdentifier(this, cityDescription, "string")));
        roomInfoTextView.setText(getResources().getString(getIdentifier(this, cityRoomInfo, "string")));

        String touristAttractions = getResources().getString(getIdentifier(this, cityTouristAttractions, "string"));
        StringBuilder touristAttractionList = new StringBuilder();

        for (String attraction : touristAttractions.split(",")){
            touristAttractionList.append("- " + attraction.trim());
            touristAttractionList.append(System.getProperty("line.separator"));
        }
        attractionsTextView.setText(touristAttractionList);

        imagesSlider.stopAutoCycle();

        for (int i = 1; i <= 5; i++){

            String imageResourceString = cityName.toLowerCase() + "_" + i;

            TextSliderView textSliderView = new TextSliderView(this);

            // initialize a SliderLayout
            textSliderView
                    .image(getIdentifier(this, imageResourceString, "drawable"))
                    .setScaleType(BaseSliderView.ScaleType.CenterCrop);

            imagesSlider.addSlider(textSliderView);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public static int getIdentifier(Context context, String name, String resourceType) {
        return context.getResources().getIdentifier(name, resourceType, context.getPackageName());
    }
}
