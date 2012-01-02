package hudson.plugins.valgrind.parser;

import hudson.plugins.analysis.core.ParserResult;
import hudson.util.IOException2;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * Parses a Valgrind XML report file.
 *
 * @author Sylvain Fargier
 */
public class LeakParser {
    private SAXParser parser;

    /**
     * Creates a new instance of {@link LeakParser}.
     */
    public LeakParser() {
        parser = null;
    }

    private void createParser() throws SAXException, ParserConfigurationException {
        if (parser == null) {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setValidating(false);
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            parser = factory.newSAXParser();
        }
    }

    public ParserResult parse(final File file, final ParserResult collect) throws IOException
    {
        ParserResult result;

        if (collect == null) {
            result = new ParserResult();
        }
        else {
            result = collect;
        }

        try {
            createParser();

            ValgrindXMLHandler handler = new ValgrindXMLHandler(result);
            parser.parse(file, handler);

        } catch (ParserConfigurationException e) {
            throw new IOException2("Cannot parse Valgrind results", e);
        } catch (SAXException e) {
            throw new IOException2("Cannot parse Valgrind results", e);
        }
        return result;
    }

    public ParserResult parse(final InputStream in, final ParserResult collect) throws IOException
    {
        ParserResult result;

        if (collect == null) {
            result = new ParserResult();
        }
        else {
            result = collect;
        }

        try {
            createParser();

            ValgrindXMLHandler handler = new ValgrindXMLHandler(result);
            parser.parse(in, handler);

        } catch (ParserConfigurationException e) {
            throw new IOException2("Cannot parse Valgrind results", e);
        } catch (SAXException e) {
            throw new IOException2("Cannot parse Valgrind results", e);
        }
        return result;
    }

    private enum XMLContext {
        ROOT,
        FRAME,
        XWHAT,
        ERROR
    }

    /**
     *
     * Sax element handler
     *
     * @author Sylvain Fargier
     */
    private class ValgrindXMLHandler extends DefaultHandler {
        private final ParserResult rootLeaks;
        private LinkedList<Frame> frame;
        private String unique;
        private String tid;
        private LeakType kind;
        private String message;
        private StringBuilder data;

        private XMLContext ctx;

        /**
         * Creates a new instance of {@link ValgrindXMLHandler}.
         * @param rootLeaks
         *      Leaks collection, parsed Leaks will be added to the collection.
         */
        public ValgrindXMLHandler(final ParserResult rootLeaks)
        {
            this.rootLeaks = rootLeaks;
        }

        public void clearCache()
        {
            frame = new LinkedList<Frame>();
            unique = null;
            tid = null;
            kind = null;
            message = null;
            data = null;
        }

        public void clearData() {
            if (data != null) {
                data = new StringBuilder();
            }
        }

        public String getData() {
            if (data != null) {
                return data.toString().trim();
            }
            else {
                return null;
            }
        }

        public void setDataCollect(final boolean collect) {
            if (collect) {
                data = new StringBuilder();
            }
            else {
                data = null;
            }
        }

        @Override
        public void startDocument() throws SAXException {
            ctx = XMLContext.ROOT;
            clearCache();
        }

        @Override
        public void endDocument() throws SAXException {
            super.endDocument();
            clearCache();
        }

        @Override
        public void characters(final char[] ch, final int start, final int length) throws SAXException {
            if (data != null) {
                data.append(new String(ch, start, length));
            }
        }
        @Override
        public void startElement(final String uri, final String localName, final String qName, final Attributes attributes) throws SAXException {
            super.startElement(uri, localName, qName, attributes);

            if (ctx == XMLContext.ROOT) {
                if (qName.equals("error")) {
                    ctx = XMLContext.ERROR;
                }
            }
            else if (ctx == XMLContext.ERROR) {
                if (qName.equals("frame")) {
                    ctx = XMLContext.FRAME;
                    frame.addLast(new Frame());
                    setDataCollect(true);
                }
                else if (qName.equals("xwhat")) {
                    ctx = XMLContext.XWHAT;
                    setDataCollect(true);
                }
                else if (qName.equals("unique")
                        || qName.equals("tid")
                        || qName.equals("kind")
                        || qName.equals("what"))
                {
                    setDataCollect(true);
                }
            }
            else if (ctx == XMLContext.FRAME || ctx == XMLContext.XWHAT)
            {
                clearData();
            }

        }

        @Override
        public void endElement(final String uri, final String localName, final String qName) throws SAXException {
            super.endElement(uri, localName, qName);
            switch (ctx)
            {
                case FRAME:
                    if (frame == null || frame.isEmpty()) {
                        throw new SAXException("Corrupted frame element");
                    }

                    if (qName.equals("frame")) {
                        ctx = XMLContext.ERROR;
                        setDataCollect(false);
                    }
                    else if (qName.equals("ip")) {
                        frame.getLast().setIp(getData());
                    }
                    else if (qName.equals("obj")) {
                        frame.getLast().setObj(getData());
                    }
                    else if (qName.equals("fn")) {
                        frame.getLast().setFn(getData());
                    }
                    else if (qName.equals("dir")) {
                        frame.getLast().setDir(getData());
                    }
                    else if (qName.equals("file")) {
                        frame.getLast().setFile(getData());
                    }
                    else if (qName.equals("line")) {
                        try {
                            frame.getLast().setLine(Integer.parseInt(getData()));
                        } catch (NumberFormatException e) {
                            throw new SAXException("Wrong line number in frame", e);
                        }
                    }
                    clearData();
                    break;
                case XWHAT:
                    if (qName.equals("xwhat")) {
                        ctx = XMLContext.ERROR;
                        setDataCollect(false);
                    }
                    else if (qName.equals("text")) {
                        message = getData();
                    }
                    clearData();
                    break;

                case ERROR:
                    if (qName.equals("error")) {
                        rootLeaks.addAnnotation(new Leak(message, kind, frame));
                        ctx = XMLContext.ROOT;
                        clearCache();
                    }
                    else if ("unique".equals(qName)) {
                        unique = getData();
                        setDataCollect(false);
                    }
                    else if ("tid".equals(qName)) {
                        tid = getData();
                        setDataCollect(false);
                    }
                    else if ("kind".equals(qName)) {
                        kind = LeakType.fromString(getData());
                        setDataCollect(false);
                    }
                    else if ("what".equals(qName)) {
                        message = getData();
                        setDataCollect(false);
                    }
                    break;

                case ROOT:
                    break;
            }
        }
    }
}

