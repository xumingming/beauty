package io.github.xumingming.beauty;

import java.time.Duration;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import static io.github.xumingming.beauty.Utils.duration;

public class BarChart<T>
{
    private List<BarItem<T>> items;
    private Function<BarItem<T>, Color> colorProvider;

    private Supplier<T> valueIdentitySupplier;
    private BiFunction<T, T, T> valueAccumulator;
    private BiFunction<T, T, Double> valueDivider;
    private Function<T, String> valueFormatter = x -> x == null ? "-" : x.toString();

    public static BarChart<Integer> intBarChart(List<BarItem<Integer>> barItems)
    {
        BarChart<Integer> barChart = new BarChart<>();
        barChart.setItems(barItems);
        barChart.setValueAccumulator((Integer x, Integer y) -> x + y);
        barChart.setValueDivider((x, y) -> x * 1.0 / y);
        barChart.setValueIdentitySupplier(() -> 0);
        barChart.setColorProvider((BarItem<Integer> x) -> Beauty.getColorByHash(x.getName()));

        return barChart;
    }

    public static BarChart<Long> longBarChart(List<BarItem<Long>> barItems)
    {
        BarChart<Long> barChart = new BarChart<>();
        barChart.setItems(barItems);
        barChart.setValueAccumulator((Long x, Long y) -> x + y);
        barChart.setValueDivider((x, y) -> x * 1.0 / y);
        barChart.setValueIdentitySupplier(() -> 0L);
        barChart.setColorProvider((BarItem<Long> x) -> Beauty.getColorByHash(x.getName()));

        return barChart;
    }

    public static BarChart<Duration> durationBarChart(List<BarItem<Duration>> barItems)
    {
        BarChart<Duration> barChart = new BarChart<>();
        barChart.setItems(barItems);
        barChart.setValueAccumulator((Duration x, Duration y) -> x.plus(y));
        barChart.setValueDivider((x, y) -> x.toMillis() * 1.0 / y.toMillis());
        barChart.setValueIdentitySupplier(() -> Duration.ZERO);
        barChart.setValueFormatter(x -> duration(x));
        barChart.setColorProvider((BarItem<Duration> x) -> Beauty.getColorByHash(x.getName()));

        return barChart;
    }

    public List<BarItem<T>> getItems()
    {
        return items;
    }

    public void setItems(List<BarItem<T>> items)
    {
        this.items = items;
    }

    public Function<BarItem<T>, Color> getColorProvider()
    {
        return colorProvider;
    }

    public void setColorProvider(Function<BarItem<T>, Color> colorProvider)
    {
        this.colorProvider = colorProvider;
    }

    public Supplier<T> getValueIdentitySupplier()
    {
        return valueIdentitySupplier;
    }

    public void setValueIdentitySupplier(Supplier<T> valueIdentitySupplier)
    {
        this.valueIdentitySupplier = valueIdentitySupplier;
    }

    public BiFunction<T, T, T> getValueAccumulator()
    {
        return valueAccumulator;
    }

    public void setValueAccumulator(BiFunction<T, T, T> valueAccumulator)
    {
        this.valueAccumulator = valueAccumulator;
    }

    public BiFunction<T, T, Double> getValueDivider()
    {
        return valueDivider;
    }

    public void setValueDivider(BiFunction<T, T, Double> valueDivider)
    {
        this.valueDivider = valueDivider;
    }

    public Function<T, String> getValueFormatter()
    {
        return valueFormatter;
    }

    public void setValueFormatter(Function<T, String> valueFormatter)
    {
        this.valueFormatter = valueFormatter;
    }
}
