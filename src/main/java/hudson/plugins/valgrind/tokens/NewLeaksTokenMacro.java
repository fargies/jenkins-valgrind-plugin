package hudson.plugins.valgrind.tokens;

import hudson.Extension;
import hudson.plugins.analysis.tokens.AbstractNewAnnotationsTokenMacro;
import hudson.plugins.valgrind.LeaksResultAction;

/**
 * Provides a token that evaluates to the number of new open tasks.
 *
 * @author Ulli Hafner
 */
@Extension(optional = true)
public class NewLeaksTokenMacro extends AbstractNewAnnotationsTokenMacro {
    /**
     * Creates a new instance of {@link NewLeaksTokenMacro}.
     */
    @SuppressWarnings("unchecked")
    public NewLeaksTokenMacro() {
        super("LEAKS_NEW", LeaksResultAction.class);
    }
}

