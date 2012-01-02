package hudson.plugins.valgrind.parser;

import hudson.model.AbstractBuild;
import hudson.plugins.analysis.util.model.FileAnnotation;
import hudson.plugins.analysis.util.model.LineRange;
import hudson.plugins.analysis.util.model.Priority;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Formatter;
import java.util.LinkedList;
import java.util.ListIterator;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;

/**
 * A serializable Java Bean class representing an open task.
 *
 * @author Ulli Hafner
 */
public class Leak implements FileAnnotation, Serializable {
    /** Unique identifier of this class. */
    private static final long serialVersionUID = -7610946922529535828L;
    /** Current key of this annotation. */
    private static long currentKey;
    /** Temporary directory holding the workspace files. */
    public static final String WORKSPACE_FILES = "workspace-files";

    /** unique identifier */
    private final String message;
    private final Priority priority;
    private final LeakType type;
    private final LinkedList<Frame> frame;
    private final long key;
    /**
     * Context hash code of this annotation. This hash code is used to decide if
     * two annotations are equal even if the equals method returns <code>false</code>.
     */
    private long contextHashCode;

    /** Origin of the annotation. */
    public static final String ORIGIN = "valgrind";

    /** text used for external files (the topFrame is in an external library) */
    public static final String EXTERNAL_MODULE = "external";

    /**
     * Creates a new instance of {@link Leak}.
     * @param message
     *            the associated message.
     * @param type
     *            the message type.
     * @param frame
     *            the associated stackframe.
     */
    public Leak(final String message, final LeakType type, final LinkedList<Frame> frame) {
        this.message = message;
        this.type = type;
        this.frame = frame;
        priority = Priority.NORMAL;
        contextHashCode = key = currentKey++;
    }

    /** {@inheritDoc} */
    public int compareTo(final FileAnnotation o) {
        int result;

        result = getMessage().compareTo(o.getMessage());
        if (result != 0) {
            return result;
        }
        result = getType().compareTo(o.getType());
        if (result != 0) {
            return result;
        }

        return hashCode() - o.hashCode(); // fallback
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Leak other = (Leak)obj;
        if (message != other.message) {
            return false;
        }

        if (type != other.type) {
            return false;
        }

        if (getFirstFrame() != other.getFirstFrame()) {
            return false;
        }

        return true;
    }

    /** {@inheritDoc} */
    public int getPrimaryLineNumber() {
        Frame f = getFirstFrame();

        if (f != null) {
            return f.getLine();
        }
        else {
            return -1;
        }
    }

    private Frame getFirstFrame() {
        if (frame == null || frame.isEmpty()) {
            return null;
        }
        else if (type == LeakType.Leak_DefinitelyLost ||
                type == LeakType.Leak_IndirectlyLost ||
                type == LeakType.Leak_PossiblyLost ||
                type == LeakType.Leak_StillReachable)
        {
            return frame.getLast();
        }
        else {
            return frame.getFirst();
        }
    }

    /** {@inheritDoc} */
    public Collection<LineRange> getLineRanges() {
        ArrayList<LineRange> range = new ArrayList<LineRange>();
        range.add(new LineRange(getPrimaryLineNumber()));

        return range;
    }

    /** {@inheritDoc} */
    public long getKey() {
        return key;
    }

    /** {@inheritDoc} */
    public Priority getPriority() {
        return priority;
    }

    /** {@inheritDoc} */
    public String getFileName() {
        Frame f = getFirstFrame();

        if (f != null && f.getFile() != null && f.getDir() != null) {
            return f.getDir() + "/" + f.getFile();
        }
        else {
            return EXTERNAL_MODULE;
        }
    }

    /** {@inheritDoc} */
    public String getLinkName() {
        return getFileName();
    }

    /** {@inheritDoc} */
    public String getTempName(final AbstractBuild<?, ?> owner) {
        String fileName = getFileName();
        if (fileName != null) {
            return owner.getRootDir().getAbsolutePath()
                    + "/" + WORKSPACE_FILES
                    + "/" + Integer.toHexString(fileName.hashCode()) + ".tmp";
        }
        return StringUtils.EMPTY;
    }

    /** {@inheritDoc} */
    public void setFileName(final String fileName) {
    	/* can't set filename on those Annotations (the first Frame fileName is used). */
    }

    /** {@inheritDoc} */
    public void setPathName(final String workspacePath) {
    	/* can't set pathName on those Annotations. */
    }

    /** {@inheritDoc} */
    public boolean canDisplayFile(final AbstractBuild<?, ?> owner) {
        Frame f = getFirstFrame();

        return (f != null && f.getFile() != null);
    }

    /** {@inheritDoc} */
    public String getShortFileName() {
        Frame f = getFirstFrame();

        if (f != null) {
            return FilenameUtils.getName(f.getFile());
        }
        else {
            return
                    EXTERNAL_MODULE;
        }
    }

    /** {@inheritDoc} */
    public String getModuleName() {
        return "Default Module";
    }

    /** {@inheritDoc} */
    public void setModuleName(final String moduleName) {
    	/* this plugin is C/C++ specific, no moduleName with those languages */
    }

    /** {@inheritDoc} */
    public String getPackageName() {
        return "Default Package";
    }

    /** {@inheritDoc} */
    public boolean hasPackageName() {
        return false;
    }

    /** {@inheritDoc} */
    public String getPathName() {
        return null;
    }

    /** {@inheritDoc} */
    public String getOrigin() {
        return ORIGIN;
    }

    /** {@inheritDoc} */
    public String getCategory() {
        return null;
    }

    /** {@inheritDoc} */
    public String getType() {
        return type.toString();
    }

    /** {@inheritDoc} */
    public long getContextHashCode() {
        return contextHashCode;
    }

    /** {@inheritDoc} */
    public void setContextHashCode(final long contextHashCode) {
        this.contextHashCode = contextHashCode;
    }

    /** {@inheritDoc} */
    public String getMessage() {
        return message;
    }

    private void dumpStack(final StringBuilder sb) {
        ListIterator<Frame> it = frame.listIterator();

        if (!it.hasNext()) {
            return;
        }

        Frame topFrame = it.next();
        Formatter form = new Formatter(sb);
        form.format("&nbsp;at %s: %s ",
                topFrame.getIp(),
                (topFrame.getFn() != null) ? topFrame.getFn() : "???");
        if (topFrame.getFile() != null) {
            form.format("(%s:%d)<br/>", topFrame.getFile(), topFrame.getLine());
        }
        else {
            form.format("(in %s)<br/>", topFrame.getObj());
        }

        while (it.hasNext()) {
            topFrame = it.next();

            form.format("&nbsp;by %s: %s ",
                    topFrame.getIp(),
                    (topFrame.getFn() != null) ? topFrame.getFn() : "???");
            if (topFrame.getFile() != null) {
                form.format("(%s:%d)<br/>", topFrame.getFile(), topFrame.getLine());
            }
            else {
                form.format("(in %s)<br/>", topFrame.getObj());
            }
        }
    }

    /** {@inheritDoc} */
    public String getToolTip() {
        StringBuilder tooltip = new StringBuilder();
        tooltip.append(message + "<br/>");
        dumpStack(tooltip);
        return tooltip.toString();
    }

}

