package syncbros.com.explore;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import org.florescu.android.rangeseekbar.RangeSeekBar;

public class FullScreenDialog extends DialogFragment {

    public static String TAG = "FullScreenDialog";
    EditText hotelName;
    Button applyButton;
    RangeSeekBar priceRangeSeekBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.filters_sort, container, false);

        getDialog().getWindow()
                .getAttributes().windowAnimations = R.style.FullScreenDialogStyle;

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.close);
        toolbar.setNavigationOnClickListener(view1 -> finishDialog());
        toolbar.setTitle("Filters");
        toolbar.inflateMenu(R.menu.filter_menu);

        toolbar.setOnMenuItemClickListener(menuItem -> {

            hotelName.setText("");
            clearHotelCategories();
            clearRoomCategories();
            priceRangeSeekBar.resetSelectedValues();

            return false;
        });

        return view;
    }

    private void clearRoomCategories() {

        Button goldButton = getDialog().findViewById(R.id.gold_select);
        Button silverButton = getDialog().findViewById(R.id.silver_select);
        Button platinumButton = getDialog().findViewById(R.id.platinum_select);

        goldButton.setTextColor(Color.BLACK);
        silverButton.setTextColor(Color.BLACK);
        platinumButton.setTextColor(Color.BLACK);
        goldButton.setBackgroundResource(R.drawable.button_shape_default);
        silverButton.setBackgroundResource(R.drawable.button_shape_default);
        platinumButton.setBackgroundResource(R.drawable.button_shape_default);

    }

    private void clearHotelCategories() {

        Button threeButton = getDialog().findViewById(R.id.three_select);
        Button fiveButton = getDialog().findViewById(R.id.five_select);
        Button sevenButton = getDialog().findViewById(R.id.seven_select);

        threeButton.setTextColor(Color.BLACK);
        fiveButton.setTextColor(Color.BLACK);
        sevenButton.setTextColor(Color.BLACK);
        threeButton.setBackgroundResource(R.drawable.button_shape_default);
        fiveButton.setBackgroundResource(R.drawable.button_shape_default);
        sevenButton.setBackgroundResource(R.drawable.button_shape_default);

    }

    private void finishDialog() {
        hotelName.setText("");
        clearHotelCategories();
        clearRoomCategories();
        priceRangeSeekBar.resetSelectedValues();
        this.getDialog().cancel();
    }


    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();

        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }

        hotelName = getDialog().findViewById(R.id.hotel_name_select);
        applyButton = getDialog().findViewById(R.id.apply_button);

        priceRangeSeekBar = getDialog().findViewById(R.id.priceRange);
        priceRangeSeekBar.setTextAboveThumbsColorResource(R.color.colorPrimary);
        priceRangeSeekBar.setRangeValues(5000, 60000);


        applyButton.setOnClickListener(view -> {

            EditDialogListener activity = (EditDialogListener) getActivity();

            Button goldButton =  getDialog().findViewById(R.id.gold_select);
            Button silverButton = getDialog().findViewById(R.id.silver_select);
            Button platinumButton = getDialog().findViewById(R.id.platinum_select);
            Button threeButton =  getDialog().findViewById(R.id.three_select);
            Button fiveButton = getDialog().findViewById(R.id.five_select);
            Button sevenButton = getDialog().findViewById(R.id.seven_select);
            String hcategoryButton = "";
            String rcategoryButton = "";

            if(goldButton.getCurrentTextColor() == Color.WHITE){
                rcategoryButton = "GOLD";
            }else if(silverButton.getCurrentTextColor() == Color.WHITE){
                rcategoryButton = "SILVER";
            }else if(platinumButton.getCurrentTextColor() == Color.WHITE){
                rcategoryButton = "PLATINUM";
            }

            if(threeButton.getCurrentTextColor() == Color.WHITE){
                hcategoryButton = "THREE";
            }else if(fiveButton.getCurrentTextColor() == Color.WHITE){
                hcategoryButton = "FIVE";
            }else if(sevenButton.getCurrentTextColor() == Color.WHITE){
                hcategoryButton = "SEVEN";
            }

            activity.updateResult(hotelName.getText().toString(),
                    priceRangeSeekBar.getSelectedMinValue().toString(),
                    priceRangeSeekBar.getSelectedMaxValue().toString(),
                    hcategoryButton,
                    rcategoryButton);

            getDialog().cancel();

        });

        int hotelCategory = (int)getArguments().getSerializable("hotel_category");
        String roomCategory = (String)getArguments().getSerializable("room_category");
        String hotel_name = (String)getArguments().getSerializable("hotelName");
        int priceMax1 = (int)getArguments().getSerializable("priceMax");
        int priceMin1 = (int)getArguments().getSerializable("priceMin");

        switch (hotelCategory) {
            case 3:
                onHotelCategoryClick("THREE");
                break;
            case 5:
                onHotelCategoryClick("FIVE");
                break;
            case 7:
                onHotelCategoryClick("SEVEN");
                break;
        }

        switch (roomCategory) {
            case "GOLD":
                onCategoryClick("GOLD");
                break;
            case "SILVER":
                onCategoryClick("SILVER");
                break;
            case "PLATINUM":
                onCategoryClick("PLATINUM");
                break;
        }

        hotelName.setText(hotel_name);

        priceRangeSeekBar.setSelectedMinValue(priceMin1);
        priceRangeSeekBar.setSelectedMaxValue(priceMax1);

    }

    public void onHotelCategoryClick(String roomType){

        Button threeButton = getDialog().findViewById(R.id.three_select);
        Button fiveButton = getDialog().findViewById(R.id.five_select);
        Button sevenButton = getDialog().findViewById(R.id.seven_select);

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

    public void onCategoryClick(String roomType){

        Button goldButton = getDialog().findViewById(R.id.gold_select);
        Button silverButton = getDialog().findViewById(R.id.silver_select);
        Button platinumButton = getDialog().findViewById(R.id.platinum_select);

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

    public interface EditDialogListener {
        void updateResult(String hotelName,String priceMin,String priceMax,String hotelCategory,String roomCategory);
    }



}
