package hudson.plugins.valgrind;

import hudson.model.AbstractProject;
import hudson.plugins.analysis.core.ResultAction;
import hudson.plugins.analysis.core.AbstractProjectAction;

/**
 * Entry point to visualize the task scanner trend graph. Drawing of the graph is
 * delegated to the associated {@link LeaksResultAction}.
 *
 * @author Ulli Hafner
 */
public class LeaksProjectAction extends AbstractProjectAction<ResultAction<LeaksResult>> {
    /**
     * Instantiates a new {@link LeaksProjectAction}.
     *
     * @param project
     *            the project that owns this action
     */
    public LeaksProjectAction(final AbstractProject<?, ?> project) {
        this(project, LeaksResultAction.class);
    }

    /**
     * Instantiates a new {@link LeaksProjectAction}.
     *
     * @param project
     *            the project that owns this action
     * @param type
     *            the result action type
     */
    public LeaksProjectAction(final AbstractProject<?, ?> project,
            final Class<? extends ResultAction<LeaksResult>> type) {
        super(project, type, new LeaksDescriptor());
    }

    /** {@inheritDoc} */
    public String getDisplayName() {
        return Messages.Valgrind_ProjectAction_Name();
    }

    /** {@inheritDoc} */
    @Override
    public String getTrendName() {
        return Messages.Valgrind_Trend_Name();
    }
}

