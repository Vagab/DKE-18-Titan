package support;

import body.Body;

import java.util.Calendar;

public class LaunchData implements Comparable<LaunchData> {
    public final Body target;
    public final String name;
    public final double progradeVelocity;
    public final double towardsSunVelocity;
    public final double perpendicularVelocity;
    public final Calendar launchDate;
    public final long launchTimeMillis;

    public LaunchData(
        Body target,
        String name,
        double progradeVelocity,
        double towardsSunVelocity,
        double perpendicularVelocity,
        int year, int month, int day,
        int hour, int minute, int second
    ) {
        this.target = target;
        this.name = name;
        this.progradeVelocity = progradeVelocity;
        this.towardsSunVelocity = towardsSunVelocity;
        this.perpendicularVelocity = perpendicularVelocity;

        launchDate = Calendar.getInstance();
        launchDate.set(Calendar.YEAR, year);
        launchDate.set(Calendar.MONTH, month);
        launchDate.set(Calendar.DAY_OF_MONTH, day);
        launchDate.set(Calendar.HOUR_OF_DAY, hour);
        launchDate.set(Calendar.MINUTE, minute);
        launchDate.set(Calendar.SECOND, second);
        launchDate.set(Calendar.MILLISECOND, 0);

        launchTimeMillis = launchDate.getTimeInMillis();
    }

    public int compareTo(LaunchData other) {
        return launchDate.compareTo(other.launchDate);
    }
}
