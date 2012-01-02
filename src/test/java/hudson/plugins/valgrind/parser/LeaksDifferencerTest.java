package hudson.plugins.valgrind.parser;

import static org.junit.Assert.*;
import hudson.plugins.analysis.core.AnnotationDifferencer;
import hudson.plugins.analysis.util.model.FileAnnotation;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.junit.Test;


/**
 * Tests the {@link AnnotationDifferencer} for tasks.
 */
public class LeaksDifferencerTest {
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
        firstFrame.add(new Frame("ip1", "obj1", "fn1", "dir1", "file1", 1));
        firstFrame.add(new Frame("ip2", "obj2", "fn2", "dir2", "file2", 2));
        FileAnnotation first  = new Leak("Leak1",
                LeakType.ClientCheck, firstFrame);

        LinkedList<Frame> secondFrame = new LinkedList<Frame>();
        secondFrame.add(new Frame("ip1", "obj1", "fn1", "dir1", "file1", 1));
        secondFrame.add(new Frame("ip2", "obj2", "fn2", "dir2", "file2", 2));
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
    @Test
    public void testDifferencer() {
        Set<FileAnnotation> actual = new HashSet<FileAnnotation>();
        Set<FileAnnotation> previous = new HashSet<FileAnnotation>();

        FileAnnotation annotation = new Leak("Leak1", LeakType.InvalidJump, null);
        actual.add(annotation);

        annotation = new Leak("Leak1", LeakType.InvalidJump, null);
        previous.add(annotation);

        assertEquals(WARNINGS_COUNT_ERROR, 0, AnnotationDifferencer.getFixedAnnotations(actual, previous).size());

        annotation = new Leak("Leak2", LeakType.InvalidJump, null);
        previous.add(annotation);

        assertEquals(WARNINGS_COUNT_ERROR, 0, AnnotationDifferencer.getNewAnnotations(actual, previous).size());
        assertEquals(WARNINGS_COUNT_ERROR, 1, AnnotationDifferencer.getFixedAnnotations(actual, previous).size());

        annotation = new Leak("Leak2", LeakType.InvalidJump, null);
        actual.add(annotation);

        assertEquals(WARNINGS_COUNT_ERROR, 0, AnnotationDifferencer.getNewAnnotations(actual, previous).size());
        assertEquals(WARNINGS_COUNT_ERROR, 0, AnnotationDifferencer.getFixedAnnotations(actual, previous).size());

        annotation = new Leak("Leak3", LeakType.InvalidJump, null);
        actual.add(annotation);

        assertEquals(WARNINGS_COUNT_ERROR, 1, AnnotationDifferencer.getNewAnnotations(actual, previous).size());
        assertEquals(WARNINGS_COUNT_ERROR, 0, AnnotationDifferencer.getFixedAnnotations(actual, previous).size());
    }

    /**
     * Checks whether the hash codes are evaluated if similar warnings are part of new and fixed.
     */
    @Test
    public void testHashCodes() {
        Set<FileAnnotation> actual = new HashSet<FileAnnotation>();
        Set<FileAnnotation> previous = new HashSet<FileAnnotation>();

        LinkedList<Frame> currentFrame = new LinkedList<Frame>();
        currentFrame.add(new Frame("ip1", "obj1", "fn1", "dir1", "file1", 1));
        currentFrame.add(new Frame("ip2", "obj2", "fn2", "dir2", "file2", 2));
        FileAnnotation current = new Leak("Leak1", LeakType.ClientCheck, currentFrame);
        actual.add(current);

        LinkedList<Frame> oldFrame = new LinkedList<Frame>(currentFrame);
        oldFrame.remove(1);
        FileAnnotation old = new Leak("Leak1", LeakType.ClientCheck, oldFrame);
        previous.add(old);

        assertEquals(WARNINGS_COUNT_ERROR, 1, AnnotationDifferencer.getFixedAnnotations(actual, previous).size());
        assertEquals(WARNINGS_COUNT_ERROR, 1, AnnotationDifferencer.getNewAnnotations(actual, previous).size());

        currentFrame.remove(1);
        current.setContextHashCode(0);

        assertEquals(WARNINGS_COUNT_ERROR, 0, AnnotationDifferencer.getFixedAnnotations(actual, previous).size());
        assertEquals(WARNINGS_COUNT_ERROR, 0, AnnotationDifferencer.getNewAnnotations(actual, previous).size());
    }

}

