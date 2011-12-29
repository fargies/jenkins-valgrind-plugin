package hudson.plugins.valgrind;

import hudson.maven.MavenAggregatedReport;
import hudson.maven.MavenBuild;
import hudson.maven.MavenModule;
import hudson.maven.MavenModuleSet;
import hudson.maven.MavenModuleSetBuild;
import hudson.model.Action;
import hudson.plugins.analysis.core.HealthDescriptor;
import hudson.plugins.analysis.core.MavenResultAction;
import hudson.plugins.valgrind.parser.LeaksParserResult;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

/**
 * A {@link LeaksResultAction} for native Maven jobs. This action
 * additionally provides result aggregation for sub-modules and for the main
 * project.
 *
 * @author Ulli Hafner
 */
public class LeaksMavenResultAction extends MavenResultAction<LeaksResult> {
    /** Tag identifiers indicating high priority. */
    private String high;
    /** Tag identifiers indicating normal priority. */
    private String normal;
    /** Tag identifiers indicating low priority. */
    private String low;

    /**
     * Creates a new instance of {@link LeaksMavenResultAction}. This instance
     * will have no result set in the beginning. The result will be set
     * successively after each of the modules are build.
     *
     * @param owner
     *            the associated build of this action
     * @param healthDescriptor
     *            health descriptor to use
     * @param defaultEncoding
     *            the default encoding to be used when reading and parsing files
     * @param high
     *            tag identifiers indicating high priority
     * @param normal
     *            tag identifiers indicating normal priority
     * @param low
     *            tag identifiers indicating low priority
     */
    public LeaksMavenResultAction(final MavenModuleSetBuild owner, final HealthDescriptor healthDescriptor,
            final String defaultEncoding, final String high, final String normal, final String low) {
        super(new LeaksResultAction(owner, healthDescriptor), defaultEncoding, "TASKS");

        initializeFields(high, normal, low);
    }

    /**
     * Creates a new instance of {@link LeaksMavenResultAction}.
     *
     * @param owner
     *            the associated build of this action
     * @param healthDescriptor
     *            health descriptor to use
     * @param defaultEncoding
     *            the default encoding to be used when reading and parsing files
     * @param high
     *            tag identifiers indicating high priority
     * @param normal
     *            tag identifiers indicating normal priority
     * @param low
     *            tag identifiers indicating low priority
     * @param result
     *            the result in this build
     */
    public LeaksMavenResultAction(final MavenBuild owner, final HealthDescriptor healthDescriptor,
            final String defaultEncoding, final String high, final String normal, final String low, final LeaksResult result) {
        super(new LeaksResultAction(owner, healthDescriptor, result), defaultEncoding, "TASKS");

        initializeFields(high, normal, low);
    }

    /**
     * Initializes the fields of this action.
     * @param high
     *            tag identifiers indicating high priority
     * @param normal
     *            tag identifiers indicating normal priority
     * @param low
     *            tag identifiers indicating low priority
     */
    // CHECKSTYLE:OFF
    @SuppressWarnings("hiding")
    private void initializeFields(final String high, final String normal, final String low) {
        this.high = high;
        this.normal = normal;
        this.low = low;
    }
    // CHECKSTYLE:ON

    /** {@inheritDoc} */
    public MavenAggregatedReport createAggregatedAction(final MavenModuleSetBuild build, final Map<MavenModule, List<MavenBuild>> moduleBuilds) {
        return new LeaksMavenResultAction(build, getHealthDescriptor(), getDefaultEncoding(), high, normal, low);
    }

    /** {@inheritDoc} */
    public Action getProjectAction(final MavenModuleSet moduleSet) {
        return new LeaksProjectAction(moduleSet, LeaksMavenResultAction.class);
    }

    @Override
    public Class<? extends MavenResultAction<LeaksResult>> getIndividualActionType() {
        return LeaksMavenResultAction.class;
    }

    /** {@inheritDoc} */
    @Override
    protected LeaksResult createResult(final LeaksResult existingResult, final LeaksResult additionalResult) {
        return new LeaksReporterResult(getOwner(), additionalResult.getDefaultEncoding(),
                aggregate(existingResult, additionalResult), high, normal, low);
    }

    /** {@inheritDoc} */
    @Override
    protected LeaksParserResult aggregate(final LeaksResult existingResult, final LeaksResult additionalResult) {
        LeaksParserResult aggregatedAnnotations = new LeaksParserResult();

        List<LeaksResult> results = Lists.newArrayList();
        if (existingResult != null) {
            results.add(existingResult);
        }
        results.add(additionalResult);

        for (LeaksResult result : results) {
            aggregatedAnnotations.addAnnotations(result.getAnnotations());
            aggregatedAnnotations.addModules(result.getModules());
            aggregatedAnnotations.addErrors(result.getErrors());
            aggregatedAnnotations.addScannedFiles(result.getNumberOfFiles());
        }

        return aggregatedAnnotations;
    }
}

