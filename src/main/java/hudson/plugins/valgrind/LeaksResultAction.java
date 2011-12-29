package hudson.plugins.valgrind;

import hudson.model.AbstractBuild;
import hudson.plugins.analysis.core.AbstractResultAction;
import hudson.plugins.analysis.core.HealthDescriptor;
import hudson.plugins.analysis.core.PluginDescriptor;

/**
 * Controls the live cycle of the task scanner results. This action persists the
 * results of the task scanner of a build and displays the results on the
 * build page. The actual visualization of the results is defined in the
 * matching <code>summary.jelly</code> file.
 * <p>
 * Moreover, this class renders the tasks scanner result trend.
 * </p>
 *
 * @author Ulli Hafner
 */
public class LeaksResultAction extends AbstractResultAction<LeaksResult>  {
    /**
     * Creates a new instance of <code>LeaksResultAction</code>.
     *
     * @param owner
     *            the associated build of this action
     * @param healthDescriptor
     *            health descriptor to use
     * @param result
     *            the result in this build
     */
    public LeaksResultAction(final AbstractBuild<?, ?> owner, final HealthDescriptor healthDescriptor, final LeaksResult result) {
        super(owner, new LeaksHealthDescriptor(healthDescriptor), result);
    }

    /**
     * Creates a new instance of <code>LeaksResultAction</code>.
     *
     * @param owner
     *            the associated build of this action
     * @param healthDescriptor
     *            health descriptor to use
     */
    public LeaksResultAction(final AbstractBuild<?, ?> owner, final HealthDescriptor healthDescriptor) {
        super(owner, new LeaksHealthDescriptor(healthDescriptor));
    }

    /** {@inheritDoc} */
    public String getDisplayName() {
        return Messages.Valgrind_ProjectAction_Name();
    }

    /** {@inheritDoc} */
    @Override
    protected PluginDescriptor getDescriptor() {
        return new LeaksDescriptor();
    }

    /** {@inheritDoc} */
    @Override
    public String getMultipleItemsTooltip(final int numberOfItems) {
        return Messages.Valgrind_ResultAction_MultipleLeaks(numberOfItems);
    }

    /** {@inheritDoc} */
    @Override
    public String getSingleItemTooltip() {
        return Messages.Valgrind_ResultAction_OneLeak();
    }
}
