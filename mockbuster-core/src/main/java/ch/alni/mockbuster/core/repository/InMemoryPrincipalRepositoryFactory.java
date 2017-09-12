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

package ch.alni.mockbuster.core.repository;

import ch.alni.mockbuster.core.domain.NameId;
import ch.alni.mockbuster.core.domain.Principal;
import ch.alni.mockbuster.core.domain.PrincipalRepository;
import org.slf4j.Logger;

import java.io.*;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Create an in-memory principal repository from the files found in the filesystem.
 */
public class InMemoryPrincipalRepositoryFactory {
    private static final Logger LOG = getLogger(InMemoryPrincipalRepositoryFactory.class);

    private final File baseDir;
    private final String databaseFileName;

    public InMemoryPrincipalRepositoryFactory(File baseDir, String databaseFileName) {
        this.baseDir = baseDir;
        this.databaseFileName = databaseFileName;
    }

    public PrincipalRepository create() {
        File databaseFile = new File(baseDir, databaseFileName);

        Properties databaseProperties = loadDatabase(databaseFile);

        List<Principal> principalList = PropertyNames.selectNames(databaseProperties.stringPropertyNames()).stream()
                .map(propertyName -> new Principal(
                        new NameId(
                                databaseProperties.getProperty(propertyName + ".name_id"),
                                databaseProperties.getProperty(propertyName + ".name_id_format")
                        ),
                        databaseProperties.getProperty(propertyName + ".display_name"),
                        databaseProperties.getProperty(propertyName + ".attribute_statement")
                ))
                .collect(Collectors.toList());

        return new InMemoryPrincipalRepository(principalList);
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
