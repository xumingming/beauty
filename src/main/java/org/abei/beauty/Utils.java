package org.abei.beauty;

import io.airlift.units.DataSize;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;

public class Utils
{
    private Utils()
    {}

    public static String dataSize(DataSize dataSize)
    {
        long v = dataSize.toBytes();
        if (v < 1024) {
            return v + "B";
        }
        int z = (63 - Long.numberOfLeadingZeros(v)) / 10;
        return format("%.1f %sB", (double) v / (1L << (z * 10)), " KMGTPE".charAt(z));
    }

    public static String duration(Duration duration)
    {
        List<String> parts = new ArrayList<>();
        long days = duration.toDays();
        if (days > 0) {
            parts.add(timeAndUnit(days, "d"));
        }
        int hours = duration.toHoursPart();
        if (hours > 0 || !parts.isEmpty()) {
            parts.add(timeAndUnit(hours, "h"));
        }
        int minutes = duration.toMinutesPart();
        if (minutes > 0 || !parts.isEmpty()) {
            parts.add(timeAndUnit(minutes, "m"));
        }
        int seconds = duration.toSecondsPart();
        parts.add(timeAndUnit(seconds, "s"));

        int millis = duration.toMillisPart();
        parts.add(timeAndUnit(millis, "ms"));
        return String.join(" ", parts);
    }

    private static String timeAndUnit(long num, String unit)
    {
        return num + "" + unit;
    }

    public static String twoDigitsDouble(double value)
    {
        return format("%.2f", value);
    }

    public static String percentage(double value)
    {
        return format("%.2f%%", value * 100);
    }

    public static String repeat(String string, int count)
    {
        if (string == null) {
            throw new IllegalArgumentException("input string should not be null!");
        }
        if (count <= 1) {
            if (count < 0) {
                throw new IllegalArgumentException("invalid count: " + count);
            }
            return count == 0 ? "" : string;
        }
        else {
            int len = string.length();
            long longSize = (long) len * (long) count;
            int size = (int) longSize;
            if ((long) size != longSize) {
                throw new ArrayIndexOutOfBoundsException((new StringBuilder(51)).append("Required array size too large: ").append(longSize).toString());
            }
            else {
                char[] array = new char[size];
                string.getChars(0, len, array, 0);

                int n;
                for (n = len; n < size - n; n <<= 1) {
                    System.arraycopy(array, 0, array, n, n);
                }

                System.arraycopy(array, 0, array, n, size - n);
                return new String(array);
            }
        }
    }

    public static String padEnd(String string, int minLength, char padChar)
    {
        if (string == null) {
            throw new IllegalArgumentException("input string should not be null!");
        }
        if (string.length() >= minLength) {
            return string;
        }
        else {
            StringBuilder sb = new StringBuilder(minLength);
            sb.append(string);

            for (int i = string.length(); i < minLength; ++i) {
                sb.append(padChar);
            }

            return sb.toString();
        }
    }
}
