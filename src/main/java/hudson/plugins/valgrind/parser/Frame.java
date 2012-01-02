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

    public Frame() {
        ip = "";
        line = -1;
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

