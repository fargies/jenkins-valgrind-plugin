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
 * A serializable Java Bean class representing an Memory Leak.
 *
 * @author Sylvain Fargier
 */
public final class Leak implements FileAnnotation, Serializable {
    /** Unique identifier of this class. */
    private static final long serialVersionUID = -7610946922529535828L;
    /** Current key of this annotation. */
    private static long currentKey;
    /** Temporary directory holding the workspace files. */
    public static final String WORKSPACE_FILES = "workspace-files";

    /** unique identifier */
    private final String message;
    private final LeakType type;
    private final LinkedList<Frame> frame;
    private final int frame_index;
    private final long key;
    /**
     * Context hash code of this annotation. This hash code is used to decide if
     * two annotations are equal even if the equals method returns <code>false</code>.
     */
    private transient long contextHashCode;

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
     * @param frame_index
     *            the frame index where the leak is (in the workspace).
     */
    public Leak(final String message, final LeakType type, final LinkedList<Frame> frame, final int frame_index) {
        this.message = message;
        this.type = type;
        this.frame = frame;
        this.frame_index = frame_index;
        key = currentKey++;
        contextHashCode = currentKey; /* will be overridden soon */
    }

    /**
     * Creates a new instance of {@link Leak}.
     *
     * @details no frame in the stack could be associated with this error.
     *
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
        frame_index = -1;
        key = currentKey++;
        contextHashCode = hashCode();
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

    /** {@inheritDoc} */
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
        if (frame == null) {
            if (other.frame != null) {
                return false;
            }
        }
        else if (!frame.equals(other.frame)) {
            return false;
        }
        if (frame_index != other.frame_index) {
            return false;
        }
        if (message == null) {
            if (other.message != null) {
                return false;
            }
        }
        else if (!message.equals(other.message)) {
            return false;
        }
        if (type != other.type) {
            return false;
        }
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((frame == null) ? 0 : frame.hashCode());
        result = prime * result + frame_index;
        result = prime * result + ((message == null) ? 0 : message.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
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
        if (frame_index != -1) {
            return frame.get(frame_index);
        }
        else {
            return null;
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
        return type.getPriority();
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
            return EXTERNAL_MODULE;
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

    /**
     * @details
     * If there is no frame associated with this Leak the hashCode is regenrated rather than set.
     */
    public void setContextHashCode(final long contextHashCode) {
        if (frame_index == -1) {
            this.contextHashCode = hashCode();
        }
        else {
            this.contextHashCode = contextHashCode;
        }
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

