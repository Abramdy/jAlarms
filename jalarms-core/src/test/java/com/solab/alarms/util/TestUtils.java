package com.solab.alarms.util;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Enrique Zamudio
 *         Date: 11/24/16 10:22 AM
 */
public class TestUtils {

    @Test
    public void testReplaceAllBuilder() {
        StringBuilder sb = new StringBuilder("SUB and then SUB and finally SUB");
        Utils.replaceAll(sb, "SUB", "REPL");
        Assert.assertEquals("REPL and then REPL and finally REPL", sb.toString());
        sb.setLength(0);
        sb.append("SUB then SUB and SUB");
        Utils.replaceAll(sb, "SUB", "LSUB");
        Assert.assertEquals("LSUB then LSUB and LSUB", sb.toString());
        sb.setLength(0);
        sb.append("SUB then SUB and SUB");
        Utils.replaceAll(sb, "SUB", "SUBR");
        Assert.assertEquals("SUBR then SUBR and SUBR", sb.toString());
    }

    @Test
    public void testReplaceAllString() {
        Assert.assertEquals("REPL and then REPL and finally REPL",
                Utils.replaceAll("SUB", "REPL", "SUB and then SUB and finally SUB"));
        Assert.assertEquals("LSUB then LSUB and LSUB",
                Utils.replaceAll("SUB", "LSUB", "SUB then SUB and SUB"));
        Assert.assertEquals("SUBR then SUBR and SUBR",
                Utils.replaceAll("SUB", "SUBR", "SUB then SUB and SUB"));
    }
}
