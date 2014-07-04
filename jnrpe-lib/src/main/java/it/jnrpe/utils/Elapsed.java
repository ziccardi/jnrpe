package it.jnrpe.utils;

public class Elapsed {

    private long seconds;
    private long minutes;
    private long hours;
    private long days;

    public Elapsed(final long qty, final TimeUnit unit) {
        init(qty, unit);
    }

    private void init(final long qty, final TimeUnit unit) {
        long millis = unit.convert(qty);
        seconds = TimeUnit.MILLISECOND.convert(millis, TimeUnit.SECOND) % 60;
        minutes = TimeUnit.MILLISECOND.convert(millis, TimeUnit.MINUTE) % 60;
        hours = TimeUnit.MILLISECOND.convert(millis, TimeUnit.HOUR) % 24;
        days = TimeUnit.MILLISECOND.convert(millis, TimeUnit.DAY);
    }

    public long getSeconds() {
        return seconds;
    }

    public long getMinutes() {
        return minutes;
    }

    public long getHours() {
        return hours;
    }

    public long getDays() {
        return days;
    }
}
