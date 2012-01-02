package hudson.plugins.valgrind;

import hudson.Launcher;
import hudson.matrix.MatrixAggregator;
import hudson.matrix.MatrixRun;
import hudson.matrix.MatrixBuild;
import hudson.model.BuildListener;
import hudson.plugins.analysis.core.AnnotationsAggregator;
import hudson.plugins.analysis.core.HealthDescriptor;
import hudson.plugins.analysis.core.ParserResult;

import java.io.IOException;

/**
 * Aggregates {@link LeaksResultAction}s of {@link MatrixRun}s into
 * {@link MatrixBuild}.
 *
 * @author Ulli Hafner
 */

public class LeaksAnnotationsAggregator extends MatrixAggregator {
    private final ParserResult totals = new ParserResult();
    private final HealthDescriptor healthDescriptor;
    private final String defaultEncoding;

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
            }
        }
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public boolean endBuild() throws InterruptedException, IOException {
        LeaksResult result = new LeaksResult(build, defaultEncoding, totals);

        build.addAction(new LeaksResultAction(build, healthDescriptor, result));

        return true;
    }
}

