/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.catalina.util;

import java.io.IOException;
import java.io.Writer;

import org.apache.tomcat.util.security.Escape;

/**
 * XMLWriter helper class.
 */
public class XMLWriter {

    // -------------------------------------------------------------- Constants

    /**
     * Opening tag.
     */
    public static final int OPENING = 0;


    /**
     * Closing tag.
     */
    public static final int CLOSING = 1;


    /**
     * Element with no content.
     */
    public static final int NO_CONTENT = 2;


    // ----------------------------------------------------- Instance Variables

    /**
     * Buffer.
     */
    protected StringBuilder buffer = new StringBuilder();


    /**
     * Writer.
     */
    protected final Writer writer;


    protected boolean lastWriteWasOpen;


    // ----------------------------------------------------------- Constructors

    /**
     * New XML writer utility that will store its data in an internal buffer.
     */
    public XMLWriter() {
        this(null);
    }


    /**
     * New XML writer utility that will store its data in an internal buffer and can write it to the specified writer.
     * <p>
     * See {@link #sendData()}
     *
     * @param writer The writer to use
     */
    public XMLWriter(Writer writer) {
        this.writer = writer;
    }


    // --------------------------------------------------------- Public Methods

    /**
     * Retrieve generated XML.
     *
     * @return String containing the generated XML
     */
    @Override
    public String toString() {
        return buffer.toString();
    }


    /**
     * Write property to the XML.
     *
     * @param namespace Namespace
     * @param name      Property name
     * @param value     Property value
     */
    public void writeProperty(String namespace, String name, String value) {
        writeElement(namespace, name, OPENING);
        buffer.append(value);
        writeElement(namespace, name, CLOSING);
    }


    /**
     * Write an element.
     *
     * @param name      Element name
     * @param namespace Namespace abbreviation
     * @param type      Element type
     */
    public void writeElement(String namespace, String name, int type) {
        writeElement(namespace, null, name, type);
    }


    /**
     * Write an element.
     *
     * @param namespace     Namespace abbreviation
     * @param namespaceInfo Namespace info
     * @param name          Element name
     * @param type          Element type
     */
    public void writeElement(String namespace, String namespaceInfo, String name, int type) {
        if ((namespace != null) && (!namespace.isEmpty())) {
            switch (type) {
                case OPENING:
                    if (lastWriteWasOpen) {
                        buffer.append('\n');
                    }
                    if (namespaceInfo != null) {
                        buffer.append("<").append(namespace).append(":").append(name).append(" xmlns:")
                            .append(namespace).append("=\"").append(namespaceInfo).append("\">");
                    } else {
                        buffer.append("<").append(namespace).append(":").append(name).append(">");
                    }
                    lastWriteWasOpen = true;
                    break;
                case CLOSING:
                    buffer.append("</").append(namespace).append(":").append(name).append(">\n");
                    lastWriteWasOpen = false;
                    break;
                case NO_CONTENT:
                default:
                    if (lastWriteWasOpen) {
                        buffer.append('\n');
                    }
                    if (namespaceInfo != null) {
                        buffer.append("<").append(namespace).append(":").append(name).append(" xmlns:")
                            .append(namespace).append("=\"").append(namespaceInfo).append("\"/>\n");
                    } else {
                        buffer.append("<").append(namespace).append(":").append(name).append("/>\n");
                    }
                    lastWriteWasOpen = false;
                    break;
            }
        } else if ((namespaceInfo != null) && (!namespaceInfo.isEmpty())) {
            switch (type) {
                case OPENING:
                    if (lastWriteWasOpen) {
                        buffer.append('\n');
                    }
                    buffer.append("<").append(name).append(" xmlns=\"").append(namespaceInfo).append("\">");
                    lastWriteWasOpen = true;
                    break;
                case CLOSING:
                    buffer.append("</").append(name).append(">\n");
                    lastWriteWasOpen = false;
                    break;
                case NO_CONTENT:
                default:
                    if (lastWriteWasOpen) {
                        buffer.append('\n');
                    }
                    buffer.append("<").append(name).append(" xmlns=\"").append(namespaceInfo).append("\"/>\n");
                    lastWriteWasOpen = false;
                    break;
            }
        } else {
            switch (type) {
                case OPENING:
                    if (lastWriteWasOpen) {
                        buffer.append('\n');
                    }
                    buffer.append("<").append(name).append(">");
                    lastWriteWasOpen = true;
                    break;
                case CLOSING:
                    buffer.append("</").append(name).append(">\n");
                    lastWriteWasOpen = false;
                    break;
                case NO_CONTENT:
                default:
                    if (lastWriteWasOpen) {
                        buffer.append('\n');
                    }
                    buffer.append("<").append(name).append("/>\n");
                    lastWriteWasOpen = false;
                    break;
            }
        }
    }


    /**
     * Write text.
     *
     * @param text Text to append
     */
    public void writeText(String text) {
        buffer.append(Escape.xml(text));
    }


    /**
     * Write raw XML data.
     *
     * @param raw Raw XML to append
     */
    public void writeRaw(String raw) {
        buffer.append(raw);
    }


    /**
     * Write data.
     *
     * @param data Data to append
     */
    public void writeData(String data) {
        buffer.append("<![CDATA[").append(data).append("]]>");
    }


    /**
     * Write XML Header.
     */
    public void writeXMLHeader() {
        buffer.append("<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n");
    }


    /**
     * Send data and reinitializes buffer, if a writer has been specified.
     *
     * @throws IOException Error writing XML data
     */
    public void sendData() throws IOException {
        if (writer != null) {
            writer.write(buffer.toString());
            buffer = new StringBuilder();
        }
    }


}
