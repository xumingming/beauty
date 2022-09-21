package io.github.xumingming.beauty;

import static io.github.xumingming.beauty.Utils.repeat;

/**
 * Represents the information of a bar in a bar chart.
 */
public class BarItem<T>
{
    /**
     * Name of the bar.
     */
    private final String name;
    /**
     * value for this bar.
     */
    private final T value;
    /**
     * percentage of the value measured versus total value.
     */
    private transient double percentage;

    public BarItem(String name, T value)
    {
        this.name = name;
        this.value = value;
    }

    public String getName()
    {
        return name;
    }

    public double getPercentage()
    {
        return percentage;
    }

    public void setPercentage(double percentage)
    {
        this.percentage = percentage;
    }

    public T getValue()
    {
        return value;
    }

    public String getBar()
    {
        int barCount = (int) (percentage * 100);
        int blankCount = 100 - barCount;
        return repeat("█", barCount)
                + repeat(" ", blankCount);
    }
}
