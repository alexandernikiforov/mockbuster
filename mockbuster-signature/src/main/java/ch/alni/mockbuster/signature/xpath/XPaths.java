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

package ch.alni.mockbuster.signature.xpath;

import javax.xml.namespace.QName;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.format;

public final class XPaths {

    private XPaths() {
    }

    public static String toAbsolutePath(QName... qNames) {
        return Stream.of(qNames)
                .map(XPaths::toLocalPath)
                .map(value -> "/" + value)
                .collect(Collectors.joining());
    }

    public static String toLocalPath(QName qName) {
        return format("*[local-name()='%s' and namespace-uri()='%s']", qName.getLocalPart(), qName.getNamespaceURI());
    }

}
