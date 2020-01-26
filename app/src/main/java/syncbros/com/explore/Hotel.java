package syncbros.com.explore;

public class Hotel {

    public String name;
    public String type;
    public int stars;
    public int image;
    public long price;
    public String location;
    public String city;
    public String feature_description;

    public Hotel(String name, String type, int stars, int image, long price, String location, String city, String feature_description) {
        this.name = name;
        this.type = type;
        this.stars = stars;
        this.image = image;
        this.price = price;
        this.location = location;
        this.city = city;
        this.feature_description = feature_description;
    }



}
