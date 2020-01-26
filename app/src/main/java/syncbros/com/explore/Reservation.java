package syncbros.com.explore;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Reservation {

    private String dateTime, hotelName, cityName, roomCategory, dateFrom, dateTo, price, reservationNumber;

    public Reservation(String dateTime, String hotelName, String cityName, String roomCategory,
                       String dateFrom, String dateTo, String price, String reservationNumber) {
        this.dateTime = dateTime;
        this.hotelName = hotelName;
        this.cityName = cityName;
        this.roomCategory = roomCategory;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.price = price;
        this.reservationNumber = reservationNumber;
    }

    public String getDateTime() {
        String[] splitDateTime = dateTime.split(" ");

        return splitDateTime[1] + " " + splitDateTime[2] + ", " + splitDateTime[5] + " " + splitDateTime[3];
    }

    public String getRoomCategory(){
        return this.roomCategory;
    }

    public String getHotelName() {
        return String.format("%s, %s", hotelName, cityName);
    }

    public String getTimeSpan() {
        SimpleDateFormat inputFormatter = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat outputFormatter = new SimpleDateFormat("MMM dd, yyyy");
        try {
            Date startDate = inputFormatter.parse(dateFrom);
            dateFrom = outputFormatter.format(startDate);

            Date endDate = inputFormatter.parse(dateTo);
            dateTo = outputFormatter.format(endDate);

            return dateFrom + " - " + dateTo;

        }catch (Exception e){
            Log.v("Date_Error", e.toString());
            return dateFrom + " - " + dateTo;
        }

    }

    public String getPrice() {
        return "PKR " + price;
    }

    public String getReservationNumber(){
        return this.reservationNumber;
    }

}
