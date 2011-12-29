package hudson.plugins.valgrind;

import hudson.model.AbstractBuild;
import hudson.plugins.analysis.core.BuildResult;
import hudson.plugins.analysis.core.ResultAction;
import hudson.plugins.valgrind.parser.LeaksParserResult;

/**
 * Represents the aggregated results of the PMD analysis in m2 jobs.
 *
 * @author Ulli Hafner
 */
public class LeaksReporterResult extends LeaksResult {
    private static final long serialVersionUID = 3803699268659365514L;

    /**
     * Creates a new instance of {@link LeaksReporterResult}.
     *
     * @param build
     *            the current build as owner of this action
     * @param defaultEncoding
     *            the default encoding to be used when reading and parsing files
     * @param result
     *            the parsed annotations
     * @param highTags
     *            tag identifiers indicating high priority
     * @param normalTags
     *            tag identifiers indicating normal priority
     * @param lowTags
     *            tag identifiers indicating low priority
     */
    public LeaksReporterResult(final AbstractBuild<?, ?> build, final String defaultEncoding, final LeaksParserResult result,
            final String highTags, final String normalTags, final String lowTags) {
        super(build, defaultEncoding, result, highTags, normalTags, lowTags);
    }

    @Override
    protected Class<? extends ResultAction<? extends BuildResult>> getResultActionType() {
        return LeaksMavenResultAction.class;
    }
}

