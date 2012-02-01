package hudson.plugins.valgrind.parser;

import static org.junit.Assert.*;
import hudson.FilePath;
import hudson.plugins.analysis.core.ParserResult;
import hudson.plugins.analysis.util.model.FileAnnotation;
import hudson.plugins.analysis.util.model.Priority;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import org.junit.Test;

/**
 * Tests the class {@link LeakScanner}.
 */
public class LeakScannerTest {
    /** Error message. */
    private static final String WRONG_MESSAGE_ERROR = "Wrong message returned.";
    /** Error message. */
    private static final String WRONG_NUMBER_OF_TASKS_ERROR = "Wrong number of tasks found.";

    /**
     * Checks whether we can parse a simple report.
     *
     * @throws IOException if we can't read the file
     */
    @Test
    public void scanFileWithWords() throws IOException {
        InputStream file = LeakScannerTest.class.getResourceAsStream("file-with-leaks.xml");

        ParserResult result = new LeakParser().parse(file, null, null, null);
        assertEquals(WRONG_NUMBER_OF_TASKS_ERROR, 4, result.getNumberOfAnnotations());

        assertEquals(WRONG_NUMBER_OF_TASKS_ERROR, 4, result.getNumberOfAnnotations(Priority.HIGH));
        assertEquals(WRONG_NUMBER_OF_TASKS_ERROR, 0, result.getNumberOfAnnotations(Priority.NORMAL));
        assertEquals(WRONG_NUMBER_OF_TASKS_ERROR, 0, result.getNumberOfAnnotations(Priority.LOW));
    }

    /**
     * Check that it can parse a real stack.
     *
     * @throws IOException if we can't read the file
     */
    @Test
    public void scanFileWithCompleteStack() throws IOException {
        InputStream file = LeakScannerTest.class.getResourceAsStream("sample-file1.xml");
        FilePath workspace = new FilePath(new File("/opt/jenkins/workspace"));

        ParserResult result = new LeakParser().parse(file, null, workspace, null);
        assertEquals(WRONG_NUMBER_OF_TASKS_ERROR, 1, result.getNumberOfAnnotations());

        Iterator<FileAnnotation> anotations = result.getAnnotations().iterator();
        assertTrue(WRONG_NUMBER_OF_TASKS_ERROR, anotations.hasNext());

        Leak leak = (Leak) anotations.next();
        Frame frame = leak.getFirstFrame();
        assertTrue("Failed to find the right frame", frame != null);
    }

    /**
     * Checks whether we set the type of the task to the actual tag.
     *
     * @throws IOException if we can't read the file
     */
    /** FIXME @Test
    public void testTagsIdentification() throws IOException {
        String text = "FIXME: this is a fixme";
        Collection<Leak> result = new LeakScanner("FIXME,TODO", null, null, false).scan(new StringReader(text));
        assertEquals(WRONG_NUMBER_OF_TASKS_ERROR, 1, result.size());
        Leak leak = result.iterator().next();
        assertEquals("Type is not the found token", FIXME, leak.getType());

        result = new LeakScanner(null, "XXX, HELP, FIXME, TODO", null, false).scan(new StringReader(text));
        assertEquals(WRONG_NUMBER_OF_TASKS_ERROR, 1, result.size());

        leak = result.iterator().next();
        assertEquals("Type is not the found token", FIXME, leak.getType());
    }*/

    @Test
    public void scanFailure() throws IOException {
        InputStream file = LeakScannerTest.class.getResourceAsStream("corrupted-file.xml");

        try {
            ParserResult result = new LeakParser().parse(file, null, null, null);
            fail("An assertion should have been raised");
        }
        catch (IOException e)
        {
            return;
        }
    }

    /**
     * Checks whether we find no leaks in the report file.
     *
     * @throws IOException if we can't read the file
     */
    /** FIXME @Test */
    public void scanFileWithoutTasks() throws IOException {
        InputStream file = LeakScannerTest.class.getResourceAsStream("clean-report.xml");

        ParserResult result = new LeakParser().parse(file, null, null, null);
        assertEquals(WRONG_NUMBER_OF_TASKS_ERROR, 0, result.getNumberOfAnnotations());
    }
}

