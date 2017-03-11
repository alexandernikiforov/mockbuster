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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import ch.alni.mockbuster.core.Principal;
import ch.alni.mockbuster.core.PrincipalRepository;

/**
 * In-memory principal repository.
 */
public class InMemoryPrincipalRepository implements PrincipalRepository {
    private final Map<String, Principal> principalMap = new HashMap<>();

    public InMemoryPrincipalRepository(List<Principal> principalList) {
        principalMap.putAll(principalList.stream().collect(Collectors.toMap(Principal::getNameId, Function.identity())));
    }

    @Override
    public Optional<Principal> findByNameId(String nameId) {
        return Optional.ofNullable(principalMap.get(nameId));
    }

    @Override
    public List<Principal> findAll() {
        return principalMap.values().stream().collect(Collectors.toList());
    }
}
