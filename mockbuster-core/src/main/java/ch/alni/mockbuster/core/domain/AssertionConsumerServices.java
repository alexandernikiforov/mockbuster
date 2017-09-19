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

package ch.alni.mockbuster.core.domain;

import org.apache.commons.lang.StringUtils;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Functions around AssertionConsumerService
 */
public final class AssertionConsumerServices {
    private AssertionConsumerServices() {
    }

    public static boolean containsAssertionServiceUrl(Stream<AssertionConsumerService> assertionConsumerServices,
                                                      String assertionServiceUrl) {
        return assertionConsumerServices.anyMatch(byUrl(assertionServiceUrl));
    }

    public static boolean containsAssertionServiceIndex(Stream<AssertionConsumerService> assertionConsumerServices,
                                                        int assertionServiceIndex) {
        return assertionConsumerServices.anyMatch(byIndex(assertionServiceIndex));
    }

    public static Optional<AssertionConsumerService> findByUrlOrIndex(Stream<AssertionConsumerService> assertionConsumerServices,
                                                                      String assertionServiceUrl, int assertionServiceIndex) {
        return assertionConsumerServices.filter(byUrl(assertionServiceUrl).or(byIndex(assertionServiceIndex))).findFirst();
    }

    private static Predicate<AssertionConsumerService> byUrl(String assertionServiceUrl) {
        if (null == assertionServiceUrl) {
            return assertionConsumerService -> false;
        } else {
            return assertionConsumerService -> StringUtils.equals(assertionServiceUrl, assertionServiceUrl);
        }
    }

    private static Predicate<AssertionConsumerService> byIndex(Integer assertionServiceIndex) {
        if (null == assertionServiceIndex) {
            return assertionConsumerService -> false;
        } else {
            return assertionConsumerService -> assertionServiceIndex == assertionConsumerService.getIndex();
        }
    }

}

