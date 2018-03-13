package we.retail.core.model;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class DummyTest {

    @Test
    public void testOne() {
        Assert.assertTrue(true);
    }

    @Ignore
    @Test
    public void skipped() {
        Assert.assertTrue(true);
    }
}
