package hudson.plugins.valgrind;

/**
 * Represents the result summary of the open tasks parser. This summary will be
 * shown in the summary.jelly script of the warnings result action.
 *
 * @author Ulli Hafner
 */
public final class ResultSummary {
    /**
     * Returns the message to show as the result summary.
     *
     * @param result
     *            the result
     * @return the message
     */
    // CHECKSTYLE:CONSTANTS-OFF
    public static String createSummary(final LeaksResult result) {
        StringBuilder summary = new StringBuilder();
        int tasks = result.getNumberOfAnnotations();

        summary.append(Messages.Valgrind_ResultAction_Summary());
        summary.append(" ");
        if (tasks > 0) {
            summary.append("<a href=\"leaksResult\">");
        }
        if (tasks == 1) {
            summary.append(Messages.Valgrind_ResultAction_OneLeak());
        }
        else {
            summary.append(Messages.Valgrind_ResultAction_MultipleLeaks(tasks));
        }
        if (tasks > 0) {
            summary.append("</a>");
        }
        return summary.toString();
    }
    // CHECKSTYLE:CONSTANTS-ON

    /**
     * Returns the message to show as the result summary.
     *
     * @param result
     *            the result
     * @return the message
     */
    // CHECKSTYLE:CONSTANTS-OFF
    public static String createDeltaMessage(final LeaksResult result) {
        StringBuilder summary = new StringBuilder();
        if (result.getNumberOfNewWarnings() > 0) {
            summary.append("<li><a href=\"leaksResult/new\">");
            if (result.getNumberOfNewWarnings() == 1) {
                summary.append(Messages.Valgrind_ResultAction_OneNewLeak());
            }
            else {
                summary.append(Messages.Valgrind_ResultAction_MultipleNewLeaks(result.getNumberOfNewWarnings()));
            }
            summary.append("</a></li>");
        }
        if (result.getNumberOfFixedWarnings() > 0) {
            summary.append("<li><a href=\"leaksResult/fixed\">");
            if (result.getNumberOfFixedWarnings() == 1) {
                summary.append(Messages.Valgrind_ResultAction_OneFixedLeak());
            }
            else {
                summary.append(Messages.Valgrind_ResultAction_MultipleFixedLeaks(result.getNumberOfFixedWarnings()));
            }
            summary.append("</a></li>");
        }

        return summary.toString();
    }
    // CHECKSTYLE:CONSTANTS-ON

    /**
     * Instantiates a new result summary.
     */
    private ResultSummary() {
        // prevents instantiation
    }
}

