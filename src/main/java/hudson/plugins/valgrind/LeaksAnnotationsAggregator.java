package hudson.plugins.valgrind;

import hudson.Launcher;
import hudson.matrix.MatrixAggregator;
import hudson.matrix.MatrixRun;
import hudson.matrix.MatrixBuild;
import hudson.model.BuildListener;
import hudson.plugins.analysis.core.AnnotationsAggregator;
import hudson.plugins.analysis.core.HealthDescriptor;
import hudson.plugins.analysis.util.model.Priority;
import hudson.plugins.valgrind.parser.LeaksParserResult;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;

/**
 * Aggregates {@link LeaksResultAction}s of {@link MatrixRun}s into
 * {@link MatrixBuild}.
 *
 * @author Ulli Hafner
 */

public class LeaksAnnotationsAggregator extends MatrixAggregator {
    private final LeaksParserResult totals = new LeaksParserResult();
    private final HealthDescriptor healthDescriptor;
    private final String defaultEncoding;

    /** Tag identifiers indicating high priority. */
    private String highTags = StringUtils.EMPTY;
    /** Tag identifiers indicating normal priority. */
    private String normalTags = StringUtils.EMPTY;
    /** Tag identifiers indicating low priority. */
    private String lowTags = StringUtils.EMPTY;

    /**
     * Creates a new instance of {@link AnnotationsAggregator}.
     *
     * @param build
     *            the matrix build
     * @param launcher
     *            the launcher
     * @param listener
     *            the build listener
     * @param healthDescriptor
     *            health descriptor
     * @param defaultEncoding
     *            the default encoding to be used when reading and parsing files
     */
    public LeaksAnnotationsAggregator(final MatrixBuild build, final Launcher launcher, final BuildListener listener,
            final HealthDescriptor healthDescriptor, final String defaultEncoding) {
        super(build, launcher, listener);

        this.healthDescriptor = healthDescriptor;
        this.defaultEncoding = defaultEncoding;
    }

    /** {@inheritDoc} */
    @Override
    public boolean endRun(final MatrixRun run) throws InterruptedException, IOException {
        if (totals.hasNoAnnotations()) {
            LeaksResultAction action = run.getAction(LeaksResultAction.class);
            if (action != null) {
                LeaksResult result = action.getResult();
                totals.addAnnotations(result.getAnnotations());
                totals.addScannedFiles(result.getNumberOfFiles());
                highTags = result.getTags(Priority.HIGH);
                normalTags = result.getTags(Priority.NORMAL);
                lowTags = result.getTags(Priority.LOW);
            }
        }
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public boolean endBuild() throws InterruptedException, IOException {
        LeaksResult result = new LeaksResult(build, defaultEncoding, totals,
                highTags, normalTags, lowTags);

        build.addAction(new LeaksResultAction(build, healthDescriptor, result));

        return true;
    }
}

