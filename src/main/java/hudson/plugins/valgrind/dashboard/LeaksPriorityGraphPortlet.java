package hudson.plugins.valgrind.dashboard;

import hudson.Extension;
import hudson.model.Descriptor;
import hudson.plugins.analysis.core.AbstractProjectAction;
import hudson.plugins.analysis.dashboard.AbstractWarningsGraphPortlet;
import hudson.plugins.analysis.graph.BuildResultGraph;
import hudson.plugins.analysis.graph.PriorityGraph;
import hudson.plugins.valgrind.LeaksProjectAction;
import hudson.plugins.valgrind.Messages;
import hudson.plugins.view.dashboard.DashboardPortlet;

import org.kohsuke.stapler.DataBoundConstructor;

/**
 * A portlet that shows the warnings trend graph by priority.
 *
 * @author Ulli Hafner
 */
public final class LeaksPriorityGraphPortlet extends AbstractWarningsGraphPortlet {
    /**
     * Creates a new instance of {@link LeaksPriorityGraphPortlet}.
     *
     * @param name
     *            the name of the portlet
     * @param width
     *            width of the graph
     * @param height
     *            height of the graph
     * @param dayCountString
     *            number of days to consider
     */
    @DataBoundConstructor
    public LeaksPriorityGraphPortlet(final String name, final String width, final String height, final String dayCountString) {
        super(name, width, height, dayCountString);

        configureGraph(getGraphType());
    }

    /** {@inheritDoc} */
    @Override
    protected Class<? extends AbstractProjectAction<?>> getAction() {
        return LeaksProjectAction.class;
    }

    /** {@inheritDoc} */
    @Override
    protected String getPluginName() {
        return "valgrind";
    }

    /** {@inheritDoc} */
    @Override
    protected BuildResultGraph getGraphType() {
        return new PriorityGraph();
    }

    /**
     * Extension point registration.
     *
     * @author Ulli Hafner
     */
    @Extension(optional = true)
    public static class WarningsGraphDescriptor extends Descriptor<DashboardPortlet> {
        @Override
        public String getDisplayName() {
            return Messages.Portlet_LeaksPriorityGraph();
        }
    }
}

