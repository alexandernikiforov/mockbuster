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

import ch.alni.mockbuster.core.domain.PrincipalRepository;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

public class InMemoryPrincipalRepositoryFactoryTest {
    private InMemoryPrincipalRepositoryFactory factory;

    @Before
    public void setUp() throws Exception {
        File baseDir = new File(getClass().getResource("/database/database.properties").getFile()).getParentFile();

        factory = new InMemoryPrincipalRepositoryFactory(baseDir, "database.properties");
    }

    @Test
    public void create() throws Exception {
        PrincipalRepository principalRepository = factory.create();

        assertThat(principalRepository.findAll()).hasSize(2);
    }

}