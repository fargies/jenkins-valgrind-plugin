package hudson.plugins.valgrind.parser;

import java.io.Serializable;

/**
 * An element of a stack frame (used by Leak objects).
 *
 * @author Sylvain Fargier
 */
public class Frame implements Serializable {
    /** Unique identifier of this class. */
    private static final long serialVersionUID = -7251447912916231327L;
    private String ip;
    private String obj;
    private String fn;
    private String dir;
    private String file;
    private int line;

    /**
     * Creates a new instance of {@link Frame}.
     * @param ip
     * @param obj
     * @param fn
     * @param dir
     * @param file
     * @param line
     */
    public Frame(final String ip, final String obj,
            final String fn, final String dir,
            final String file, final int line) {
        super();
        this.ip = ip;
        this.obj = obj;
        this.fn = fn;
        this.dir = dir;
        this.file = file;
        this.line = line;
    }

    public Frame() {
        super();
        ip = "";
        line = -1;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((dir == null) ? 0 : dir.hashCode());
        result = prime * result + ((file == null) ? 0 : file.hashCode());
        result = prime * result + ((fn == null) ? 0 : fn.hashCode());
        result = prime * result + line;
        result = prime * result + ((obj == null) ? 0 : obj.hashCode());
        return result;
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
        Frame other = (Frame)obj;
        if (dir == null) {
            if (other.dir != null) {
                return false;
            }
        }
        else if (!dir.equals(other.dir)) {
            return false;
        }
        if (file == null) {
            if (other.file != null) {
                return false;
            }
        }
        else if (!file.equals(other.file)) {
            return false;
        }
        if (fn == null) {
            if (other.fn != null) {
                return false;
            }
        }
        else if (!fn.equals(other.fn)) {
            return false;
        }
        if (line != other.line) {
            return false;
        }
        if (this.obj == null) {
            if (other.obj != null) {
                return false;
            }
        }
        else if (!this.obj.equals(other.obj)) {
            return false;
        }
        return true;
    }

    /**
     * Returns the obj.
     *
     * @return the obj
     */
    public String getObj() {
        return obj;
    }


    /**
     * Sets the obj to the specified value.
     *
     * @param obj the value to set
     */
    public void setObj(final String obj) {
        this.obj = obj;
    }


    /**
     * Returns the fn.
     *
     * @return the fn
     */
    public String getFn() {
        return fn;
    }


    /**
     * Sets the fn to the specified value.
     *
     * @param fn the value to set
     */
    public void setFn(final String fn) {
        this.fn = fn;
    }


    /**
     * Returns the dir.
     *
     * @return the dir
     */
    public String getDir() {
        return dir;
    }


    /**
     * Sets the dir to the specified value.
     *
     * @param dir the value to set
     */
    public void setDir(final String dir) {
        this.dir = dir;
    }


    /**
     * Returns the file.
     *
     * @return the file
     */
    public String getFile() {
        return file;
    }


    /**
     * Sets the file to the specified value.
     *
     * @param file the value to set
     */
    public void setFile(final String file) {
        this.file = file;
    }


    /**
     * Returns the line.
     *
     * @return the line
     */
    public int getLine() {
        return line;
    }


    /**
     * Sets the line to the specified value.
     *
     * @param line the value to set
     */
    public void setLine(final int line) {
        this.line = line;
    }


    /**
     * Returns the ip.
     *
     * @return the ip
     */
    public String getIp() {
        return ip;
    }


    /**
     * Sets the ip to the specified value.
     *
     * @param ip the value to set
     */
    public void setIp(final String ip) {
        this.ip = ip;
    }
}

