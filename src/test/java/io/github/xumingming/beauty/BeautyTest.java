package io.github.xumingming.beauty;

import org.junit.Test;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static io.github.xumingming.beauty.BarChart.durationBarChart;
import static io.github.xumingming.beauty.Beauty.barChartAsString;
import static io.github.xumingming.beauty.Beauty.draw;
import static org.junit.Assert.assertEquals;

public class BeautyTest
{
    @Test
    public void testTable()
    {
        String str = Beauty.table(
                persons(),
                Arrays.asList(
                        Column.column("Name", (Person p) -> p.getName()),
                        Column.column("Age", (Person p) -> p.getAge()),
                        Column.column("Gender", (Person p) -> p.isMale() ? "Male" : "Female")),
                Color.NONE);

        System.out.println(str);
        assertEquals(
                "┏━━━━━━━━┳━━━━━┳━━━━━━━━┓\n" +
                        "┃ Name   ┃ Age ┃ Gender ┃\n" +
                        "┡━━━━━━━━╇━━━━━╇━━━━━━━━┩\n" +
                        "│ james  │ 10  │ Male   │\n" +
                        "│ bond   │ 20  │ Male   │\n" +
                        "│ john   │ 30  │ Male   │\n" +
                        "│ jack   │ 40  │ Male   │\n" +
                        "│ lee    │ 50  │ Male   │\n" +
                        "│ steven │ 60  │ Male   │\n" +
                        "│ lily   │ 70  │ Female │\n" +
                        "│ lucy   │ 80  │ Female │\n" +
                        "│ rachel │ 90  │ Female │\n" +
                        "│ grace  │ 100 │ Female │\n" +
                        "└────────┴─────┴────────┘\n",
                str);
    }

    @Test
    public void testClickHouseTable()
    {
        String str = Beauty.table(
                persons(),
                Arrays.asList(
                        Column.column("Name", (Person p) -> p.getName()),
                        Column.column("Age", (Person p) -> p.getAge()),
                        Column.column("Gender", (Person p) -> p.isMale() ? "Male" : "Female")));

        System.out.println(str);
    }

    @Test
    public void testDetail()
    {
        String str = Beauty.detail(
                persons().get(0),
                Arrays.asList(
                        Column.column("Name", (Person p) -> p.getName()),
                        Column.column("Age", (Person p) -> p.getAge()),
                        Column.column("Gender", (Person p) -> p.isMale() ? "Male" : "Female")),
                Color.NONE);

        System.out.println(str);
        assertEquals(
                "Name  : james\n" +
                        "Age   : 10\n" +
                        "Gender: Male\n",
                str);
    }

    @Test
    public void testBarChart()
    {
        BarChart<Integer> barChart = BarChart.intBarChart(Arrays.asList(
                new BarItem<>("Male", persons().stream().mapToInt(x -> x.isMale() ? 1 : 0).sum()),
                new BarItem<>("Female", persons().stream().mapToInt(x -> x.isMale() ? 0 : 1).sum())));
        barChart.setColorProvider(x -> Color.NONE);
        String str = barChartAsString(barChart);

        System.out.println(" == barchart == ");
        System.out.println(str);
        assertEquals("┏━━━━━━━━┳━━━━━━━┳━━━━━━━━━━━━┳━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓\n" +
                        "┃ Name   ┃ Value ┃ Percentage ┃ Bar                                                          ┃\n" +
                        "┡━━━━━━━━╇━━━━━━━╇━━━━━━━━━━━━╇━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┩\n" +
                        "│ Male   │ 6     │ 60.00%     │ ████████████████████████████████████████████████████████████ │\n" +
                        "│ Female │ 4     │ 40.00%     │ ████████████████████████████████████████                     │\n" +
                        "└────────┴───────┴────────────┴──────────────────────────────────────────────────────────────┘\n",
                str);
    }

    @Test
    public void showBeautifulQueries()
    {
        BarChart<Duration> barChart = durationBarChart(queries()
                .stream()
                .map(q -> new BarItem<Duration>(q.getQueryName(), q.getElapseTime()))
                .collect(Collectors.toList()));

        draw(barChartAsString(barChart));
    }

    private List<Person> persons()
    {
        Person person0 = new Person("james", 10, true);
        Person person1 = new Person("bond", 20, true);
        Person person2 = new Person("john", 30, true);
        Person person3 = new Person("jack", 40, true);
        Person person4 = new Person("lee", 50, true);
        Person person5 = new Person("steven", 60, true);

        Person person6 = new Person("lily", 70, false);
        Person person7 = new Person("lucy", 80, false);
        Person person8 = new Person("rachel", 90, false);
        Person person9 = new Person("grace", 100, false);

        return Arrays.asList(
                person0, person1, person2, person3, person4,
                person5, person6, person7, person8, person9);
    }

    private static class Person
    {
        private final String name;
        private final int age;
        private final boolean male;

        public Person(String name, int age, boolean male)
        {
            this.name = name;
            this.age = age;
            this.male = male;
        }

        public String getName()
        {
            return name;
        }

        public int getAge()
        {
            return age;
        }

        public boolean isMale()
        {
            return male;
        }
    }

    private List<Query> queries()
    {
        return Arrays.asList(
                new Query("q1", Duration.ofSeconds(60)),
                new Query("q2", Duration.ofSeconds(70)),
                new Query("q3", Duration.ofSeconds(80)),
                new Query("q4", Duration.ofSeconds(90)),
                new Query("q5", Duration.ofSeconds(40)),
                new Query("q6", Duration.ofSeconds(50)),
                new Query("q7", Duration.ofSeconds(70)),
                new Query("q8", Duration.ofSeconds(30)));
    }

    public static class Query
    {
        private String queryName;
        private Duration elapseTime;

        public Query(String queryName, Duration elapseTime)
        {
            this.queryName = queryName;
            this.elapseTime = elapseTime;
        }

        public String getQueryName()
        {
            return queryName;
        }

        public void setQueryName(String queryName)
        {
            this.queryName = queryName;
        }

        public Duration getElapseTime()
        {
            return elapseTime;
        }

        public void setElapseTime(Duration elapseTime)
        {
            this.elapseTime = elapseTime;
        }
    }
}
