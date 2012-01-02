package hudson.plugins.valgrind.tokens;

import hudson.Extension;
import hudson.plugins.analysis.tokens.AbstractFixedAnnotationsTokenMacro;
import hudson.plugins.valgrind.LeaksResultAction;

/**
 * Provides a token that evaluates to the number of fixed tasks.
 *
 * @author Ulli Hafner
 */
@Extension(optional = true)
public class FixedLeaksTokenMacro extends AbstractFixedAnnotationsTokenMacro {
    /**
     * Creates a new instance of {@link FixedLeaksTokenMacro}.
     */
    @SuppressWarnings("unchecked")
    public FixedLeaksTokenMacro() {
        super("LEAKS_FIXED", LeaksResultAction.class);
    }
}

