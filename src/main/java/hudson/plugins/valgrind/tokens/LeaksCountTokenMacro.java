package hudson.plugins.valgrind.tokens;

import hudson.Extension;
import hudson.plugins.analysis.tokens.AbstractAnnotationsCountTokenMacro;
import hudson.plugins.valgrind.LeaksResultAction;

/**
 * Provides a token that evaluates to the number of open tasks.
 *
 * @author Ulli Hafner
 */
@Extension(optional = true)
public class LeaksCountTokenMacro extends AbstractAnnotationsCountTokenMacro {
    /**
     * Creates a new instance of {@link LeaksCountTokenMacro}.
     */
    @SuppressWarnings("unchecked")
    public LeaksCountTokenMacro() {
        super("LEAKS_COUNT", LeaksResultAction.class);
    }
}

