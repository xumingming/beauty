package io.github.xumingming.beauty;

import junit.framework.TestCase;
import org.junit.Test;

import java.time.Duration;

import static io.github.xumingming.beauty.Utils.duration;

public class UtilsTest
        extends TestCase
{
    @Test
    public void testDuration()
    {
        assertEquals("100ms", duration(Duration.ofMillis(100)));
        assertEquals("10s", duration(Duration.ofMillis(10000)));
    }
}
