package org.uct.cs.hough.util;

public class Timer implements AutoCloseable
{
    private static final long NANOSECONDS_PER_MINUTE = 60_000_000_000L;
    private static final long NANOSECONDS_PER_SECOND = 1_000_000_000L;
    private static final long NANOSECONDS_PER_MILLISECONDS = 1_000_000L;
    private static final long NANOSECONDS_PER_MICROSECOND = 1_000L;

    private final String text;
    private final long starttime;
    private long lastprint;

    public Timer()
    {
        this("Timer");
    }

    public Timer(String text)
    {
        this.text = text;
        this.starttime = System.nanoTime();
        this.lastprint = this.starttime;
    }

    public long getElapsed()
    {
        return System.nanoTime() - this.starttime;
    }

    public void print(String text)
    {
        long now = System.nanoTime();
        long subElapsed = now - this.lastprint;
        long elapsed = now - this.starttime;
        System.out.printf("Timer : %-25s : + %-15s [%s] %n", text, formatTime(subElapsed), formatTime(elapsed));

        this.lastprint = now;
    }

    @Override
    public void close()
    {
        this.print(this.text);
    }

    public static String formatTime(long ns)
    {
        if (ns > NANOSECONDS_PER_MINUTE) return String.format("%.2f min", ns / (double) NANOSECONDS_PER_MINUTE);
        if (ns > NANOSECONDS_PER_SECOND) return String.format("%.2f sec", ns / (double) NANOSECONDS_PER_SECOND);
        if (ns > NANOSECONDS_PER_MILLISECONDS)
            return String.format("%.2f ms", ns / (double) NANOSECONDS_PER_MILLISECONDS);
        if (ns > NANOSECONDS_PER_MICROSECOND)
            return String.format("%.2f us", ns / (double) NANOSECONDS_PER_MICROSECOND);
        return String.format("%d ns", ns);
    }

}

