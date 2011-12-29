package hudson.plugins.valgrind;

import hudson.model.AbstractBuild;
import hudson.plugins.analysis.util.model.FileAnnotation;
import hudson.plugins.analysis.views.DetailFactory;
import hudson.plugins.analysis.views.FixedWarningsDetail;

import java.util.Collection;

/**
 * Result object to visualize the fixed tasks in a build.
 *
 * @author Ulli Hafner
 */
public class FixedLeaksDetail extends FixedWarningsDetail {
    /** Unique ID of this class. */
    private static final long serialVersionUID = -8592850365611555429L;

    /**
     * Creates a new instance of {@link FixedLeaksDetail}.
     *
     * @param owner
     *            the current results object as owner of this action
     * @param fixedTasks
     *            all fixed tasks in this build
     * @param defaultEncoding
     *            the default encoding to be used when reading and parsing files
     * @param header
     *            header to be shown on detail page
     */
    public FixedLeaksDetail(final AbstractBuild<?, ?> owner, final Collection<FileAnnotation> fixedTasks, final String defaultEncoding, final String header) {
        super(owner, new DetailFactory(), fixedTasks, defaultEncoding, header);
    }

    /** {@inheritDoc} */
    @Override
    public String getDisplayName() {
        return Messages.FixedLeaksDetail_Name();
    }
}

