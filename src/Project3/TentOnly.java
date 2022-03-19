package Project3;

import java.time.temporal.ChronoUnit;
import java.util.GregorianCalendar;

public class TentOnly extends CampSite {

    private int numberOfTenters;

    public TentOnly() {
    }

    public TentOnly(String guestName,
                    GregorianCalendar checkIn,
                    GregorianCalendar estimatedCheckOut,
                    GregorianCalendar actualCheckOut,
                    int numberOfTenters) {
        super(guestName, checkIn, estimatedCheckOut, actualCheckOut);
        this.numberOfTenters = numberOfTenters;
    }

    public int getNumberOfTenters() {
        return numberOfTenters;
    }

    public void setNumberOfTenters(int numberOfTenters) {
        if (numberOfTenters < 1){
            throw new IllegalArgumentException("Must have a positive amount of tenters");
        }
        this.numberOfTenters = numberOfTenters;
    }

    @Override
    public double getCost(GregorianCalendar checkOut) {

        int daysBetween = (int) ChronoUnit.DAYS.between(checkIn.toInstant(), checkOut.toInstant());

        if (daysBetween < 1)
            throw new IllegalArgumentException("Checkout cannot be on the day of CheckIn or before");

        double cost = 10;
        if (this.numberOfTenters > 10){
            cost+=10;
        }
        cost*=daysBetween;
        return cost;
    }

    @Override
    public String toString() {
        return "TentOnly{" +
                "numberOfTenters=" + numberOfTenters +
                super.toString() +
                '}';
    }

}
