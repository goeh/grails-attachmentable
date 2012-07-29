/* Copyright 2010 Mihai Cazacu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.macrobit.grails.plugins.attachmentable.compass;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.compass.core.converter.ConversionException;
import org.compass.core.converter.basic.AbstractBasicConverter;
import org.compass.core.mapping.ResourcePropertyMapping;
import org.compass.core.marshall.MarshallingContext;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;

/**
 * Convert file content to text using Apache Tika.
 * Used by Compass (searchable plugin) to index attachments.
 * @author Goran Ehrsson
 */
public class FileContentConverter extends AbstractBasicConverter {

		private static final Log LOG = LogFactory.getLog("com.macrobit.grails.plugins.attachmentable.compass.FileContentConverter");

		private static final int MAX_STRING_LENGTH = 2 * 1024 * 1024; // 2 MB

    public static String extractText(InputStream input) throws IOException, TikaException {
        Tika tika = new Tika();
		    tika.setMaxStringLength(MAX_STRING_LENGTH);
				return tika.parseToString(input);
    }

    @Override
    protected String doToString(Object o, ResourcePropertyMapping resourcePropertyMapping, MarshallingContext context) {
        try {
        	  File f = new File((String)o);
   			    if(f.exists()) {
   			        Tika tika = new Tika();
   			    		tika.setMaxStringLength(MAX_STRING_LENGTH);
   			    		return tika.parseToString(f);
   			    }
        } catch (Exception ex) {
            if(LOG.isDebugEnabled()) {
            	LOG.debug("Error while parsing " + o, ex);
            } else {
            	System.err.println("Error while parsing " + o + ": " + ex.getMessage());
            }
        }
        return "";
    }

    @Override
    protected Object doFromString(String arg0, ResourcePropertyMapping arg1, MarshallingContext arg2) throws ConversionException {
        return null;
    }
}
