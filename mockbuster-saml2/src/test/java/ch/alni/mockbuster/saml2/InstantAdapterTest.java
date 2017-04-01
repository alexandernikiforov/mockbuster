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

import org.junit.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

public class InstantAdapterTest {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(InstantAdapterTest.class);

    @Test
    public void parseDateTime() throws Exception {
        Instant instant = InstantAdapter.parseDateTime("2002-05-30T09:30:10");
        assertThat(instant).isNotNull();

        String result = InstantAdapter.toDateTime(instant);

        LOG.debug(result);

    }

}