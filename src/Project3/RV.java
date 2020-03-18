package Project3;

import java.time.temporal.ChronoUnit;
import java.util.GregorianCalendar;

public class RV extends CampSite {

private int power;

    public RV() {
    }

    public RV(String guestName, GregorianCalendar checkIn, GregorianCalendar estimatedCheckOut, GregorianCalendar actualCheckOut, int power) {
        super(guestName, checkIn, estimatedCheckOut, actualCheckOut);
        this.power = power;
        }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    @Override
    public double getCost(GregorianCalendar checkOut) {
        int daysBetween = (int) ChronoUnit.DAYS.between(checkIn.toInstant(), checkOut.toInstant());
        double cost = 20;
        if (this.power>1000){
            cost+=10;
        }
        cost*=daysBetween;
        return cost+10;
    }

    @Override
    public String toString() {
        return "RV{" +
                "power=" + power +
                super.toString() +
                '}';
    }
}