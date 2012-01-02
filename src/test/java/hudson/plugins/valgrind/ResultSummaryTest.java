package hudson.plugins.valgrind;

import static org.mockito.Mockito.*;
import hudson.plugins.analysis.test.AbstractEnglishLocaleTest;
import junit.framework.Assert;

import org.junit.Test;

/**
 * Tests the class {@link ResultSummary}.
 */
public class ResultSummaryTest extends AbstractEnglishLocaleTest {
    /**
     * Checks the text for no leaks. The delta is > 0.
     */
    @Test
    public void test0LeaksAndPositiveDelta() {
        checkSummaryText(0, 1, 10, "Memory Leaks: 0 memory leaks");
    }

    /**
     * Checks the text for no leaks. The delta is < 0.
     */
    @Test
    public void test0LeaksAndNegativeDelta() {
        checkSummaryText(0, 1, -5, "Memory Leaks: 0 memory leaks");
    }

    /**
     * Checks the text for no leaks. The delta is 0.
     */
    @Test
    public void test0Leaks() {
        checkSummaryText(0, 1, 0, "Memory Leaks: 0 memory leaks");
    }

    /**
     * Checks the text for 1 leak. The delta is 0.
     */
    @Test
    public void test1Leak() {
        checkSummaryText(1, 2, 0, "Memory Leaks: <a href=\"valgrindResult\">1 memory leak</a>");
    }

    /**
     * Checks the text for 5 leaks. The delta is 0.
     */
    @Test
    public void test5Leaks() {
        checkSummaryText(5, 1, 0, "Memory Leaks: <a href=\"valgrindResult\">5 memory leaks</a>");
    }

    /**
     * Parameterized test case to check the message text for the specified
     * number of open tasks and files.
     *
     * @param numberOfWarnings
     *            the number of open tasks
     * @param numberOfFiles
     *            the number of files
     * @param delta
     *            delta between the last run
     * @param expectedMessage
     *            the expected message
     */
    private void checkSummaryText(final int numberOfWarnings, final int numberOfFiles, final int delta, final String expectedMessage) {
        LeaksResult result = mock(LeaksResult.class);
        when(result.getNumberOfAnnotations()).thenReturn(numberOfWarnings);
        when(result.getDelta()).thenReturn(delta);

        Assert.assertEquals("Wrong summary message created.", expectedMessage, ResultSummary.createSummary(result));
    }
    /**
     * Checks the delta message for no new and no closed tasks.
     */
    @Test
    public void testNoDelta() {
        checkDeltaText(0, 0, "");
    }

    /**
     * Checks the delta message for 1 new and no closed leak.
     */
    @Test
    public void testOnly1New() {
        checkDeltaText(0, 1, "<li><a href=\"valgrindResult/new\">1 new memory leak</a></li>");
    }

    /**
     * Checks the delta message for 5 new and no closed leaks.
     */
    @Test
    public void testOnly5New() {
        checkDeltaText(0, 5, "<li><a href=\"valgrindResult/new\">5 new memory leaks</a></li>");
    }

    /**
     * Checks the delta message for 1 fixed and no new open leaks.
     */
    @Test
    public void testOnly1Fixed() {
        checkDeltaText(1, 0, "<li><a href=\"valgrindResult/fixed\">1 fixed leak</a></li>");
    }

    /**
     * Checks the delta message for 5 fixed and no new open leaks.
     */
    @Test
    public void testOnly5Fixed() {
        checkDeltaText(5, 0, "<li><a href=\"valgrindResult/fixed\">5 fixed leaks</a></li>");
    }

    /**
     * Checks the delta message for 5 fixed and 5 new open leaks.
     */
    @Test
    public void test5New5Fixed() {
        checkDeltaText(5, 5,
                "<li><a href=\"valgrindResult/new\">5 new memory leaks</a></li>"
                + "<li><a href=\"valgrindResult/fixed\">5 fixed leaks</a></li>");
    }

    /**
     * Checks the delta message for 5 fixed and 5 new open leaks.
     */
    @Test
    public void test5New1Fixed() {
        checkDeltaText(1, 5,
        "<li><a href=\"valgrindResult/new\">5 new memory leaks</a></li>"
        + "<li><a href=\"valgrindResult/fixed\">1 fixed leak</a></li>");
    }

    /**
     * Checks the delta message for 5 fixed and 5 new open leaks.
     */
    @Test
    public void test1New5Fixed() {
        checkDeltaText(5, 1,
                "<li><a href=\"valgrindResult/new\">1 new memory leak</a></li>"
                + "<li><a href=\"valgrindResult/fixed\">5 fixed leaks</a></li>");
    }

    /**
     * Checks the delta message for 5 fixed and 5 new open leaks.
     */
    @Test
    public void test1New1Fixed() {
        checkDeltaText(1, 1,
                "<li><a href=\"valgrindResult/new\">1 new memory leak</a></li>"
                + "<li><a href=\"valgrindResult/fixed\">1 fixed leak</a></li>");
    }

    /**
     * Parameterized test case to check the message text for the specified
     * number of open tasks and files.
     *
     * @param numberOfFixedWarnings
     *            the number of closed tasks
     * @param numberOfNewWarnings
     *            the number of new open tasks
     * @param expectedMessage
     *            the expected message
     */
    private void checkDeltaText(final int numberOfFixedWarnings, final int numberOfNewWarnings, final String expectedMessage) {
        LeaksResult result = mock(LeaksResult.class);
        when(result.getNumberOfFixedWarnings()).thenReturn(numberOfFixedWarnings);
        when(result.getNumberOfNewWarnings()).thenReturn(numberOfNewWarnings);

        Assert.assertEquals("Wrong delta message created.", expectedMessage, ResultSummary.createDeltaMessage(result));
    }
}

