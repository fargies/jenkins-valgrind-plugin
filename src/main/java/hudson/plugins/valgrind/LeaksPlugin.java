package hudson.plugins.valgrind;

import hudson.Plugin;
import hudson.plugins.analysis.views.DetailFactory;

/**
 * Registers the task scanner plug-in publisher.
 *
 * @author Ulli Hafner
 */
public class LeaksPlugin extends Plugin {
    /** {@inheritDoc} */
    @Override
    public void start() {
        LeaksDetailBuilder detailBuilder = new LeaksDetailBuilder();
        DetailFactory.addDetailBuilder(LeaksResultAction.class, detailBuilder);
    }
}
