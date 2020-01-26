package syncbros.com.explore;


// This class is for populating the main page when no room search is performed.

public class City {

    private String mCityName;
    private String mCityDescription;
    private int mImageResourceId;

    public City(String cityName, String cityDescription, int imageResourceId){
        this.mCityName = cityName;
        this.mCityDescription = cityDescription;
        this.mImageResourceId = imageResourceId;
    }

    public String getCityDescription() {
        return mCityDescription;
    }

    public String getCityName() {
        return mCityName;
    }

    public int getImageResourceId() {
        return mImageResourceId;
    }
}
