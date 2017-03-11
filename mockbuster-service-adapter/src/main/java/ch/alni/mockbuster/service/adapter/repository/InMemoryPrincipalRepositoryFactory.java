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

package ch.alni.mockbuster.service.adapter.repository;

import org.oasis.saml2.assertion.AttributeStatementType;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import ch.alni.mockbuster.core.Principal;
import ch.alni.mockbuster.core.PrincipalRepository;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Create an in-memory principal repository from the files found in the filesystem.
 */
public class InMemoryPrincipalRepositoryFactory implements PrincipalRepositoryFactory {
    private static final Logger LOG = getLogger(InMemoryPrincipalRepositoryFactory.class);

    private final File baseDir;
    private final String databaseFileName;

    public InMemoryPrincipalRepositoryFactory(File baseDir, String databaseFileName) {
        this.baseDir = baseDir;
        this.databaseFileName = databaseFileName;
    }

    @Override
    public PrincipalRepository create() {
        File databaseFile = new File(baseDir, databaseFileName);

        Properties databaseProperties = loadDatabase(databaseFile);

        List<Principal> principalList = PropertyNames.selectNames(databaseProperties.stringPropertyNames()).stream()
                .map(propertyName -> new Principal(
                        databaseProperties.getProperty(propertyName + ".id"),
                        databaseProperties.getProperty(propertyName + ".display_name"),
                        readAttributeStatementType(databaseProperties.getProperty(propertyName + ".attribute_statement"))
                ))
                .collect(Collectors.toList());

        return new InMemoryPrincipalRepository(principalList);
    }

    private AttributeStatementType readAttributeStatementType(String reference) {
        File attrinbuteStatementFile = new File(baseDir, reference);
        try (InputStream inputStream = new FileInputStream(attrinbuteStatementFile)) {
            return AttributeStatements.toAttributeStatementType(inputStream);
        } catch (IOException e) {
            throw new IllegalStateException("cannot load attribute statement " + reference, e);
        }
    }

    private Properties loadDatabase(File databaseFile) {
        Properties database = new Properties();

        try (Reader reader = new InputStreamReader(new FileInputStream(databaseFile), Charset.forName("UTF-8"))) {
            database.load(reader);
        } catch (IOException e) {
            throw new IllegalStateException("cannot load principal database", e);
        }

        return database;
    }
}
