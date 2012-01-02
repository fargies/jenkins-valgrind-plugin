package hudson.plugins.valgrind;

import hudson.FilePath;
import hudson.Launcher;
import hudson.matrix.MatrixAggregator;
import hudson.matrix.MatrixBuild;
import hudson.model.Action;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.plugins.analysis.core.BuildResult;
import hudson.plugins.analysis.core.HealthAwarePublisher;
import hudson.plugins.analysis.core.ParserResult;
import hudson.plugins.analysis.util.PluginLogger;
import hudson.plugins.valgrind.parser.LeakParser;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Publishes the results of the task scanner (freestyle project type).
 *
 * @author Ulli Hafner
 */
public class LeaksPublisher extends HealthAwarePublisher {
    /** Unique ID of this class. */
    private static final long serialVersionUID = 3787892530045641806L;

    public static final ValgrindReportFilenameFilter VALGRIND_FILENAME_FILTER = new ValgrindReportFilenameFilter();
    /** Ant reportFiles-set pattern of files to work with. */
    private final String reportFiles;

    /**
     * Creates a new instance of <code>LeaksPublisher</code>.
     *
     * @param reportFiles
     *            The Valgrind output reportFiles to parse
     * @param healthy
     *            Report health as 100% when the number of open tasks is less
     *            than this value
     * @param unHealthy
     *            Report health as 0% when the number of open tasks is greater
     *            than this value
     * @param thresholdLimit
     *            determines which warning priorities should be considered when
     *            evaluating the build stability and health
     * @param useDeltaValues
     *            determines whether the absolute annotations delta or the
     *            actual annotations set difference should be used to evaluate
     *            the build stability
     * @param unstableTotalAll
     *            annotation threshold
     * @param unstableTotalHigh
     *            annotation threshold
     * @param unstableTotalNormal
     *            annotation threshold
     * @param unstableTotalLow
     *            annotation threshold
     * @param unstableNewAll
     *            annotation threshold
     * @param unstableNewHigh
     *            annotation threshold
     * @param unstableNewNormal
     *            annotation threshold
     * @param unstableNewLow
     *            annotation threshold
     * @param failedTotalAll
     *            annotation threshold
     * @param failedTotalHigh
     *            annotation threshold
     * @param failedTotalNormal
     *            annotation threshold
     * @param failedTotalLow
     *            annotation threshold
     * @param failedNewAll
     *            annotation threshold
     * @param failedNewHigh
     *            annotation threshold
     * @param failedNewNormal
     *            annotation threshold
     * @param failedNewLow
     *            annotation threshold
     * @param canRunOnFailed
     *            determines whether the plug-in can run for failed builds, too
     * @param shouldDetectModules
     *            determines whether module names should be derived from Maven POM or Ant build files
     */
    // CHECKSTYLE:OFF
    @SuppressWarnings("PMD.ExcessiveParameterList")
    @DataBoundConstructor
    public LeaksPublisher(final String healthy, final String unHealthy, final String thresholdLimit,
            final String defaultEncoding, final boolean useDeltaValues,
            final String unstableTotalAll, final String unstableTotalHigh, final String unstableTotalNormal, final String unstableTotalLow,
            final String unstableNewAll, final String unstableNewHigh, final String unstableNewNormal, final String unstableNewLow,
            final String failedTotalAll, final String failedTotalHigh, final String failedTotalNormal, final String failedTotalLow,
            final String failedNewAll, final String failedNewHigh, final String failedNewNormal, final String failedNewLow,
            final boolean canRunOnFailed, final boolean shouldDetectModules, final boolean canComputeNew,
            final String reportFiles) {
        super(healthy, unHealthy, thresholdLimit, defaultEncoding, useDeltaValues,
                unstableTotalAll, unstableTotalHigh, unstableTotalNormal, unstableTotalLow,
                unstableNewAll, unstableNewHigh, unstableNewNormal, unstableNewLow,
                failedTotalAll, failedTotalHigh, failedTotalNormal, failedTotalLow,
                failedNewAll, failedNewHigh, failedNewNormal, failedNewLow,
                canRunOnFailed, shouldDetectModules, canComputeNew, "LEAKS");
        this.reportFiles = reportFiles;
    }
    // CHECKSTYLE:ON

    /**
     * Returns the Valgrind XML reportFiles to work with.
     *
     * @return Valgrind XML reportFiles to work with.
     */
    public String getReportFiles() {
        return reportFiles;
    }


    /** {@inheritDoc} */
    @Override
    public Action getProjectAction(final AbstractProject<?, ?> project) {
        return new LeaksProjectAction(project);
    }

    /** {@inheritDoc} */
    @Override
    protected BuildResult perform(final AbstractBuild<?, ?> build, final PluginLogger logger) throws InterruptedException, IOException {
        logger.log("Publishing Valgrind report...");
        final FilePath[] moduleRoots = build.getModuleRoots();
        final FilePath root =
                (moduleRoots != null && moduleRoots.length > 1) ?
                        build.getWorkspace() : build.getModuleRoot();
        ParserResult project = new ParserResult();

        FilePath[] reports = new FilePath[0];

        // throws IOExceptions on error
        if (reportFiles != null) {
            reports = root.list(reportFiles);
        }

        if (reports.length == 0) {
            logger.log("No Valgrind XML report reportFiles were found using the pattern '" +
                    reportFiles + "' relative to '" + root.getRemote() + "'.");
            throw new IOException("Error: no Valgrind XML report reportFiles found.");
        }

        LeakParser parser = new LeakParser();
        for (int i = 0; i < reports.length; ++i) {
            parser.parse(reports[i].read(), project);
        }

        logger.logLines(project.getLogMessages());
        logger.log(String.format("Found %d memory leaks.", project.getNumberOfAnnotations()));

        LeaksResult result = new LeaksResult(build, getDefaultEncoding(), project);
        build.getActions().add(new LeaksResultAction(build, this, result));

        return result;
    }

    /**
     * Gets the stored valgrind report files for the given build.
     */
    static File[] getValgrindReports(final AbstractBuild<?,?> build) {
        return build.getRootDir().listFiles(VALGRIND_FILENAME_FILTER);
    }

    /** {@inheritDoc} */
    @Override
    public LeaksDescriptor getDescriptor() {
        return (LeaksDescriptor)super.getDescriptor();
    }

    /** {@inheritDoc} */
    public MatrixAggregator createAggregator(final MatrixBuild build, final Launcher launcher,
            final BuildListener listener) {
        return new LeaksAnnotationsAggregator(build, launcher, listener, this, getDefaultEncoding());
    }

    private static class ValgrindReportFilenameFilter implements FilenameFilter {

        public boolean accept(final File dir, final String name) {
            return name.startsWith("valgrind") && name.endsWith(".xml");
        }
    }

}
