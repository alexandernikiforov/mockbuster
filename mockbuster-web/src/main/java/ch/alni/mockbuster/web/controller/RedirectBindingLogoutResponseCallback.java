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

package ch.alni.mockbuster.web.controller;

import ch.alni.mockbuster.service.LogoutResponseCallback;

class RedirectBindingLogoutResponseCallback implements LogoutResponseCallback {
    private final String relayState;

    RedirectBindingLogoutResponseCallback(String relayState) {
        this.relayState = relayState;
    }

    @Override
    public void sendResponse(String destination, String samlResponse) {

    }

    @Override
    public void sendInvalidRequest() {

    }
}