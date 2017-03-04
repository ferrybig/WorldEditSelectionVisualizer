/***
 * Reflection utilities test to make sure WESV remains cross-version compatible.
 */

package com.rojel.wesv;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;

import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;

import junit.framework.TestCase;

/**
* Tests for the Metrics class.
* @author martinambrus
*
*/
//@RunWith(PowerMockRunner.class)
@PrepareForTest(Metrics.class)
public class MetricsTest extends TestCase {
    /**
     * In order to run this with PoweMockito and get some
     * code coverage reports from JaCoCo, we need to use
     * the following Rule instead of using the "RunWith" annotation.
     */
    @Rule
    public PowerMockRule rule = new PowerMockRule();

    /**
     * A mock of the Plugin class.
     */
    private Plugin pluginMock;

    /**
     * A mock of the PluginManager class.
     */
    private PluginManager pManagerMock;

    /**
     * A mock of the Server class.
     */
    private Server serverMock;

    /**
     * An actual instance of the Metrics class to be tested.
     */
    private Metrics met;

    /**
     * This method will setup the testing with mocks of the Server, Plugin and PluginManager
     * classes, as well as an instance of the Metric class.
     *
     * @throws Exception if any of the classes cannot be mocked or Metrics cannot be instantiated.
     */
    @Override
    @Before
    public void setUp() throws Exception {
        this.pluginMock = PowerMockito.mock(Plugin.class);
        this.pManagerMock = PowerMockito.mock(PluginManager.class);

        this.serverMock = PowerMockito.mock(Server.class);
        PowerMockito.when(this.serverMock.getPluginManager()).thenReturn(this.pManagerMock);

        PowerMockito.when(this.pluginMock.getServer()).thenReturn(this.serverMock);
        PowerMockito.when(this.pluginMock.getDataFolder()).thenReturn(new File("plugin.yml"));

        // remove the PluginMetrics/config.yml file, so our tests will test its creation as well
        File f = new File("PluginMetrics/config.yml");
        f.delete();

        // also remove the folder
        f = new File("PluginMetrics");
        f.delete();

        this.met = new Metrics(this.pluginMock);
    }

    /**
     * Test that we can create a new graph.
     */
    @Test
    public void testCreateGraph() {
        assertThat("Test graph is not of class Metrics.Graph", this.met.createGraph("test"),
                instanceOf(Metrics.Graph.class));
    }

    /**
     * Test that we can add a new graph.
     */
    @Test
    public void testAddGraph() {
        try {
            this.met.addGraph(this.met.createGraph("test2"));
        } catch (final IllegalArgumentException ex) {
            fail("Adding a new graph was unsuccessful.");
        }
    }

    /**
     * Tests that the new graph retains its given name.
     */
    @Test
    public void testGraphName() {
        final Metrics.Graph graph = this.met.createGraph("test3");
        assertThat("Test graph did not retain its given name.", graph.getName(), is("test3"));
    }

    /**
     * Tests that the opt-out value is false by default.
     */
    @Test
    public void testOptOutDefault() {
        assertThat("Opt-Out value is not false by default.", this.met.isOptOut(), is(false));
    }
}