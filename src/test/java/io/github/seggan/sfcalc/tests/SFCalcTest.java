package io.github.seggan.sfcalc.tests;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import io.github.seggan.sfcalc.SFCalc;
import io.github.seggan.sfcalc.StringRegistry;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunPlugin;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

public class SFCalcTest {

    private SFCalc plugin;
    private SlimefunPlugin slimefun;
    private ServerMock server;

    @Before
    public void setUp() {
        server = MockBukkit.mock();
        slimefun = MockBukkit.load(SlimefunPlugin.class);
        plugin = MockBukkit.load(SFCalc.class);
    }

    @Test
    public void testFormatting() {
        Assert.assertEquals(StringRegistry.format("Hi %2 %1", 1, "hi"), "Hi hi 1");
        Assert.assertEquals(StringRegistry.format("Hi %2 %1 %3", 1, "hi", Collections.singletonList("hey")), "Hi hi 1 [hey]");
    }

    @After
    public void tearDown() {
        MockBukkit.unmock();
    }
}
