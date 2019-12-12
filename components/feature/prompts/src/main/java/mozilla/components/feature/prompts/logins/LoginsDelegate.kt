/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package mozilla.components.feature.prompts.logins

import mozilla.components.concept.engine.Login

/**
 * Delegate to connect logins storage to the prompt
 */
interface LoginsDelegate {
    fun loginExists(login: Login): Boolean
}

/**
 * Default [LoginsDelegate] implementation that returns false
 */
class DefaultLoginsDelegate : LoginsDelegate {
    override fun loginExists(login: Login): Boolean {
        return false
    }
}
