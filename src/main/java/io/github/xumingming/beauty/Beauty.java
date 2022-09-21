package io.github.xumingming.beauty;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;
import java.util.function.Function;
import java.util.stream.Collectors;

import static io.github.xumingming.beauty.Column.column;
import static io.github.xumingming.beauty.Utils.padEnd;
import static io.github.xumingming.beauty.Utils.percentage;

public class Beauty
{
    /**
     * The separator string between columns
     */
    private static final String COLUMN_SEPARATOR = "  ";
    private static final Color[] BAR_CHART_COLORS = new Color[] {
            Color.YELLOW,
            Color.GREEN,
            Color.GRAY,
            Color.PURPLE,
            Color.CYAN
    };

    private static final SimpleDateFormat SHORT_DATE_FORMAT = new SimpleDateFormat("MM-dd HH:mm:ss");
    private static final SimpleDateFormat LONG_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private Beauty()
    {}

    public static <T> String table(List<T> items, List<Column<T>> columns)
    {
        return table(items, columns, x -> Color.NONE);
    }

    public static <T> String table(List<T> items,
            List<Column<T>> columns,
            Function<T, Color> contentLineColorProvider)
    {
        return table(items, columns, contentLineColorProvider, Color.WHITE);
    }

    public static <T> String table(List<T> items,
            List<Column<T>> columns,
            Function<T, Color> contentLineColorProvider,
            Color headColor)
    {
        // 先遍历一遍数据，获取数据的width
        List<Integer> maxColumnWidth = new ArrayList<>();
        for (int i = 0; i < columns.size(); i++) {
            maxColumnWidth.add(columns.get(i).getName().length());
        }

        for (T item : items) {
            for (int i = 0; i < columns.size(); i++) {
                Column<T> column = columns.get(i);
                Object content = column.getExtractor().apply(item);

                if (content instanceof Date && content != null) {
                    content = SHORT_DATE_FORMAT.format(content);
                }

                if (content == null) {
                    content = "-";
                }

                maxColumnWidth.set(i, Math.max(maxColumnWidth.get(i), content.toString().length()));
            }
        }

        // draw the headline
        StringBuilder ret = new StringBuilder();

        StringBuilder headline = new StringBuilder();
        for (int i = 0; i < columns.size(); i++) {
            Column<T> column = columns.get(i);
            headline.append(padEnd(column.getName(), maxColumnWidth.get(i), ' ')).append(COLUMN_SEPARATOR);
        }
        headline.append("\n");
        ret.append(Color.colorWith(headline, headColor));

        // draw the dash line
        StringBuilder dashline = new StringBuilder();
        for (int i = 0; i < columns.size(); i++) {
            dashline.append(padEnd("", maxColumnWidth.get(i), '-')).append(COLUMN_SEPARATOR);
        }
        dashline.append("\n");
        ret.append(Color.colorWith(dashline.toString(), headColor));

        for (T item : items) {
            StringBuilder contentLine = new StringBuilder();
            for (int i = 0; i < columns.size(); i++) {
                Column<T> column = columns.get(i);
                Object content = column.getExtractor().apply(item);

                if (content instanceof Date && content != null) {
                    content = SHORT_DATE_FORMAT.format(content);
                }

                if (content == null) {
                    content = "-";
                }

                contentLine.append(padEnd(content.toString(), maxColumnWidth.get(i), ' '))
                        .append(COLUMN_SEPARATOR);
            }
            contentLine.append("\n");
            Color lineColor = Color.NONE;
            if (contentLineColorProvider != null) {
                lineColor = contentLineColorProvider.apply(item);
            }

            ret.append(Color.colorWith(contentLine.toString(), lineColor));
        }

        return ret.toString();
    }

    public static <T> String detail(T item, List<Column<T>> columns)
    {
        return detail(item, columns, Color.GRAY);
    }

    public static <T> String detail(T item, List<Column<T>> columns, Color nameColor)
    {
        StringBuilder ret = new StringBuilder();

        Optional<Integer> nameWidth = columns.stream()
                .map(x -> x.getName().length())
                .max(Comparator.comparingInt(x -> x.intValue()));

        for (Column<T> column : columns) {
            // a separator line
            if (column == Column.SEPARATOR) {
                ret.append(padEnd("-", nameWidth.get(), '-'));
                ret.append("--");
                ret.append(padEnd("-", nameWidth.get(), '-'));
                ret.append("\n");
                continue;
            }

            // the name
            ret.append(Color.colorWith(padEnd(column.getName(), nameWidth.get(), ' '), nameColor)).append(": ");
            // the value
            ret.append(Color.colorWith(column.getExtractor().apply(item), column.getColor(item))).append("\n");
        }

        return ret.toString();
    }

    public static <T> String barChart(BarChart<T> barChart)
    {
        List<BarItem<T>> barItems = barChart.getItems();
        Function<BarItem<T>, Color> colorProvider = barChart.getColorProvider();

        // compute the total value & percentage
        T totalValue = barItems.stream()
                .map(BarItem::getValue)
                .reduce(barChart.getValueIdentitySupplier().get(), (left, right) -> {
                    return barChart.getValueAccumulator().apply(left, right);
                });

        barItems.forEach(barItem -> barItem.setPercentage(
                barChart.getValueDivider().apply(barItem.getValue(), totalValue)));

        return table(
                barItems
                        .stream()
                        .sorted(Comparator.comparing(BarItem<T>::getPercentage).reversed())
                        .collect(Collectors.toList()),
                Arrays.asList(
                        Column.column("Name", (BarItem<T> barItem) -> barItem.getName()),
                        Column.column("Value", (BarItem<T> barItem) -> barChart.getValueFormatter().apply(barItem.getValue())),
                        Column.column("Percentage", (BarItem<T> barItem) -> percentage(barItem.getPercentage())),
                        Column.column("Bar", (BarItem<T> barItem) -> barItem.getBar())),
                colorProvider);
    }

    public static final Color getColorByHash(Object object)
    {
        if (object == null) {
            return Color.GRAY;
        }

        return BAR_CHART_COLORS[Math.abs(object.hashCode() * 13 + 4) % BAR_CHART_COLORS.length];
    }

    static {
        SHORT_DATE_FORMAT.setTimeZone(TimeZone.getDefault());
        LONG_DATE_FORMAT.setTimeZone(TimeZone.getDefault());
    }
}
