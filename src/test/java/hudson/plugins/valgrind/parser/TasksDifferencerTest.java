package hudson.plugins.valgrind.parser;

import hudson.plugins.analysis.core.AnnotationDifferencer;
import hudson.plugins.analysis.test.AnnotationDifferencerTest;
import hudson.plugins.analysis.util.model.FileAnnotation;
import hudson.plugins.analysis.util.model.Priority;
import hudson.plugins.valgrind.parser.Leak;


/**
 * Tests the {@link AnnotationDifferencer} for tasks.
 */
public class TasksDifferencerTest extends AnnotationDifferencerTest {
    /** {@inheritDoc} */
    @Override
    public FileAnnotation createAnnotation(final String fileName, final Priority priority, final String message, final String category,
            final String type, final int start, final int end) {
        Leak leak = new Leak(priority, start, type, message);
        leak.setFileName(fileName);
        return leak;
    }
}

