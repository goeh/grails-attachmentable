/*
 *  Copyright 2010 Goran Ehrsson.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */
package com.macrobit.grails.plugins.attachmentable.compass;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.compass.core.converter.ConversionException;
import org.compass.core.converter.basic.AbstractBasicConverter;
import org.compass.core.mapping.ResourcePropertyMapping;
import org.compass.core.marshall.MarshallingContext;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.exception.TikaException;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.apache.tika.sax.BodyContentHandler;

/**
 *
 * @author Goran Ehrsson
 */
public class FileContentConverter extends AbstractBasicConverter {

    /**
     * Max length of returned string from document parser.
     */
    private static final int MAX_STRING_LENGTH = 2*1024*1024; // 2 MB limit

    public static String extractText(InputStream is) throws IOException, SAXException, TikaException {
        ContentHandler textHandler = new BodyContentHandler(MAX_STRING_LENGTH);
        Metadata metadata = new Metadata();
        AutoDetectParser parser = new AutoDetectParser();
        parser.parse(is, textHandler, metadata);
        return textHandler.toString();
    }

    @Override
    protected String doToString(Object o, ResourcePropertyMapping resourcePropertyMapping, MarshallingContext context) {
        InputStream input = null;
        try {
            input = new FileInputStream((String)o);
            return FileContentConverter.extractText(input);
        } catch (Exception ex) {
            System.err.println("Error while extracting text from " + o + ": " + ex.getMessage());
            return "";
        } finally {
            if(input != null) {
                try {
                    input.close();
                } catch(IOException e) { /* ignore */ }
            }
        }
    }

    @Override
    protected Object doFromString(String arg0, ResourcePropertyMapping arg1, MarshallingContext arg2) throws ConversionException {
        return null;
    }
}
