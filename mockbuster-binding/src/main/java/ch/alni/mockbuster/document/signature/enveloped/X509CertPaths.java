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

package ch.alni.mockbuster.document.signature.enveloped;

import org.slf4j.Logger;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.X509Data;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Iterator over X509 certificate paths in the given KeyInfo.
 */
public class X509CertPaths implements Iterable<List<X509Certificate>> {
    private static final Logger LOG = getLogger(X509CertPaths.class);

    private final List<List<X509Certificate>> certPathList = new ArrayList<>();

    public X509CertPaths(KeyInfo keyInfo) {
        certPathList.addAll(toX509CertPathList(keyInfo));
    }

    static List<List<X509Certificate>> toX509CertPathList(KeyInfo keyInfo) {
        List<List<X509Certificate>> result = new ArrayList<>();

        for (Object xmlStructure : keyInfo.getContent()) {
            if (xmlStructure instanceof X509Data) {
                result.add(toX509CertificateList((X509Data) xmlStructure));
                continue;
            }

            LOG.warn("do not support xml structure of type {} for signature validation", xmlStructure.getClass().getName());
        }

        return result;
    }

    static List<X509Certificate> toX509CertificateList(X509Data x509Data) {
        List<X509Certificate> result = new ArrayList<>();
        for (Object xmlStructure : x509Data.getContent()) {
            if (xmlStructure instanceof X509Certificate) {
                result.add((X509Certificate) xmlStructure);
                continue;
            }

            LOG.warn("do not support X509Data structure of type {} for signature validation", xmlStructure.getClass().getName());
        }

        return result;
    }

    public Stream<List<X509Certificate>> stream() {
        return StreamSupport.stream(this.spliterator(), false);
    }

    @Override
    public Iterator<List<X509Certificate>> iterator() {
        return createListIterator();
    }

    private Iterator<List<X509Certificate>> createListIterator() {
        final Iterator<List<X509Certificate>> iterator = certPathList.iterator();
        return new Iterator<List<X509Certificate>>() {

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public List<X509Certificate> next() {
                return iterator.next();
            }
        };
    }

}
