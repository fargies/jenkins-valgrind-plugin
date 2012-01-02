package hudson.plugins.valgrind.parser;

import static org.junit.Assert.*;
import hudson.plugins.analysis.core.AnnotationDifferencer;
import hudson.plugins.analysis.util.model.FileAnnotation;

import java.util.LinkedList;

import org.junit.Test;


/**
 * Tests the {@link AnnotationDifferencer} for tasks.
 */
public class TasksDifferencerTest {
    /** Error message. */
    private static final String ANNOTATIONS_ARE_NOT_EQUAL = "Annotations are not equal.";
    /** Indicates a wrong calculation of warnings. */
    private static final String WARNINGS_COUNT_ERROR = "Wrong warnings count.";

    /**
     * Checks whether equals works for warnings.
     */
    @Test
    public void testExternalLeak() {
        FileAnnotation annotation  = new Leak("LeakMessage1", LeakType.InvalidFree, null);

        assertEquals(ANNOTATIONS_ARE_NOT_EQUAL, Leak.EXTERNAL_MODULE, annotation.getFileName());
        assertEquals(ANNOTATIONS_ARE_NOT_EQUAL, Leak.EXTERNAL_MODULE, annotation.getShortFileName());
    }

    /**
     * Checks whether equals works for warnings.
     */
    @Test
    public void testLeakEquals() {
        LinkedList<Frame> firstFrame = new LinkedList<Frame>();
        firstFrame.add(new Frame("ip1", "obj1", "fn1", "dir1",
                "file1", 1));
        firstFrame.add(new Frame("ip2", "obj2", "fn2", "dir2",
                "file2", 2));
        FileAnnotation first  = new Leak("Leak1",
                LeakType.ClientCheck, firstFrame);

        LinkedList<Frame> secondFrame = new LinkedList<Frame>();
        secondFrame.add(new Frame("ip1", "obj1", "fn1", "dir1",
                "file1", 1));
        secondFrame.add(new Frame("ip2", "obj2", "fn2", "dir2",
                "file2", 2));
        FileAnnotation second = new Leak("Leak1",
                LeakType.ClientCheck, secondFrame);

        assertEquals(ANNOTATIONS_ARE_NOT_EQUAL, first, second);

        second = new Leak("Leak2", LeakType.ClientCheck, secondFrame);
        assertFalse("Annotations are equal.", first.equals(second));

        second = new Leak("Leak1", LeakType.InvalidFree, secondFrame);
        assertFalse("Annotations are equal.", first.equals(second));

        secondFrame.remove(1);
        second = new Leak("Leak1", LeakType.ClientCheck, secondFrame);
        assertFalse("Annotations are equal.", first.equals(second));
    }

    /**
     * Checks whether differencing detects single changes (new and fixed).
     */
/*    @Test
    public void testDifferencer() {
        Set<FileAnnotation> actual = new HashSet<FileAnnotation>();
        Set<FileAnnotation> previous = new HashSet<FileAnnotation>();

        FileAnnotation annotation = createAnnotation(STRING, Priority.HIGH, STRING, STRING, STRING, 2, 3);
        actual.add(annotation);

        annotation = createAnnotation(STRING, Priority.HIGH, STRING, STRING, STRING, 2, 3);
        previous.add(annotation);

        assertEquals(WARNINGS_COUNT_ERROR, 0, AnnotationDifferencer.getFixedAnnotations(actual, previous).size());

        annotation = createAnnotation(STRING, Priority.HIGH, "type2", STRING, STRING, 2, 3);
        previous.add(annotation);

        assertEquals(WARNINGS_COUNT_ERROR, 0, AnnotationDifferencer.getNewAnnotations(actual, previous).size());
        assertEquals(WARNINGS_COUNT_ERROR, 1, AnnotationDifferencer.getFixedAnnotations(actual, previous).size());

        annotation = createAnnotation(STRING, Priority.HIGH, "type2", STRING, STRING, 2, 3);
        actual.add(annotation);

        assertEquals(WARNINGS_COUNT_ERROR, 0, AnnotationDifferencer.getNewAnnotations(actual, previous).size());
        assertEquals(WARNINGS_COUNT_ERROR, 0, AnnotationDifferencer.getFixedAnnotations(actual, previous).size());

        annotation = createAnnotation(STRING, Priority.HIGH, "type3", STRING, STRING, 2, 3);
        actual.add(annotation);

        assertEquals(WARNINGS_COUNT_ERROR, 1, AnnotationDifferencer.getNewAnnotations(actual, previous).size());
        assertEquals(WARNINGS_COUNT_ERROR, 0, AnnotationDifferencer.getFixedAnnotations(actual, previous).size());
    } */

    /**
     * Checks whether the hash codes are evaluated if similar warnings are part of new and fixed.
     */
/* FIXME   @Test
    public void testHashCodes() {
        Set<FileAnnotation> actual = new HashSet<FileAnnotation>();
        Set<FileAnnotation> previous = new HashSet<FileAnnotation>();

        FileAnnotation current = createAnnotation(STRING, Priority.HIGH, STRING, STRING, STRING, 3, 4);
        actual.add(current);

        FileAnnotation old = createAnnotation(STRING, Priority.HIGH, STRING, STRING, STRING, 2, 3);
        previous.add(old);

        assertEquals(WARNINGS_COUNT_ERROR, 1, AnnotationDifferencer.getFixedAnnotations(actual, previous).size());
        assertEquals(WARNINGS_COUNT_ERROR, 1, AnnotationDifferencer.getNewAnnotations(actual, previous).size());

        ((AbstractAnnotation)current).setContextHashCode(0);
        ((AbstractAnnotation)old).setContextHashCode(0);

        assertEquals(WARNINGS_COUNT_ERROR, 0, AnnotationDifferencer.getFixedAnnotations(actual, previous).size());
        assertEquals(WARNINGS_COUNT_ERROR, 0, AnnotationDifferencer.getNewAnnotations(actual, previous).size());
    }*/

}

