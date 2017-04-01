/*
 * Mockbuster SAML2 IDP
 * Copyright (C) 2016  Alexander Nikiforov
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ch.alni.mockbuster.saml2;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

public final class InstantAdapter {
    InstantAdapter() {
    }

    public static Instant parseDateTime(String dateTimeValue) {
        try {
            XMLGregorianCalendar xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(dateTimeValue);
            return Instant.ofEpochMilli(xmlGregorianCalendar.toGregorianCalendar().getTimeInMillis());

        } catch (DatatypeConfigurationException e) {
            throw new IllegalStateException(e);
        }
    }

    public static String toDateTime(Instant instant) {
        return DateTimeFormatter.ISO_INSTANT.format(instant);
    }
}
