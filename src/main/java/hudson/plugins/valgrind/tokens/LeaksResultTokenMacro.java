package hudson.plugins.valgrind.tokens;

import hudson.Extension;
import hudson.plugins.analysis.tokens.AbstractResultTokenMacro;
import hudson.plugins.valgrind.LeaksResultAction;

/**
 * Provides a token that evaluates to the tasks scanner build result.
 *
 * @author Ulli Hafner
 */
@Extension(optional = true)
public class LeaksResultTokenMacro extends AbstractResultTokenMacro {
    /**
     * Creates a new instance of {@link LeaksResultTokenMacro}.
     */
    @SuppressWarnings("unchecked")
    public LeaksResultTokenMacro() {
        super("LEAKS_RESULT", LeaksResultAction.class);
    }
}

