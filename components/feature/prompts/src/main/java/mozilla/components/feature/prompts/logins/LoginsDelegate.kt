/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package mozilla.components.feature.prompts.logins

data class Login(
    private val password: String,
    private val username: String)

/**
 * Delegate to display a share prompt.
 */
interface LoginsDelegate {
    fun loginExists(login: Login) : Boolean
}

/**
 * Default [ShareDelegate] implementation that displays the native share sheet.
 */
class DefaultLoginsDelegate : LoginsDelegate {
    override fun loginExists(login: Login): Boolean {
        return false
    }
}
