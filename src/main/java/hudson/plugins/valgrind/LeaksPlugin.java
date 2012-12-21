package hudson.plugins.valgrind;

import hudson.Plugin;
import hudson.plugins.analysis.views.DetailFactory;

/**
 * Registers the valgrind memory-leaks publisher.
 *
 * @author Sylvain Fargier
 */
public class LeaksPlugin extends Plugin {
    /** {@inheritDoc} */
    @Override
    public void start() {
        LeaksDetailBuilder detailBuilder = new LeaksDetailBuilder();
        DetailFactory.addDetailBuilder(LeaksResultAction.class, detailBuilder);
    }
}
