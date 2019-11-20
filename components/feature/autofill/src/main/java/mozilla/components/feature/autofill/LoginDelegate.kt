package mozilla.components.feature.autofill

/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

import mozilla.appservices.logins.LoginsStorage
import mozilla.components.lib.dataprotect.SecureAbove22Preferences
import org.mozilla.geckoview.GeckoResult

data class Login(
    var guid: String? = null,
    // @Fenix: currently called `hostname` in AsyncLoginsStorage.
    var origin: String? = null,
    // @Fenix: currently called `formSubmitURL` in AsyncLoginsStorage
    var formActionOrigin: String? = null,
    var httpRealm: String? = null,
    var username: String? = null,
    var password: String? = null
)

interface Delegate {
    // Notify that the given login has been used for login.
    // @Fenix: call AsyncLoginsStorage.touch(login.guid).
    fun onLoginUsed(login: Login)

    // Request logins for the given domain.
    // @Fenix: return AsyncLoginsStorage.getByHostname(domain).
    fun onLoginRequest(domain: String): GeckoResult<Array<Login>>

    // Request to save or update the given login.
    // The hint should help determining the appropriate user prompting
    // behavior.
    // @Fenix: Use the API from application-services/issues/1983 to
    // determine whether to show a Save or Update button on the
    // doorhanger, taking into account un/pw edits in the doorhanger.
    // When the user confirms the save/update,
    fun onLoginSave(login: Login, hint: Int)
}

/**
 * [Delegate] implementation
 */
internal class LoginDelegate(
    private val loginStorage: LoginsStorage,
    private val keyStore: SecureAbove22Preferences
) : Delegate {
    override fun onLoginUsed(login: Login) {
        val passwordsKey = keyStore.getString(PASSWORDS_KEY) ?: return
        loginStorage.unlock(passwordsKey).also {
            login.guid?.let {
                loginStorage.touch(it)
            }
        }.also {
            loginStorage.lock()
        }
    }

    override fun onLoginRequest(domain: String): GeckoResult<Array<Login>> {
        val passwordsKey =
            keyStore.getString(PASSWORDS_KEY) ?: return GeckoResult.fromValue(arrayOf())
        loginStorage.unlock(passwordsKey).also {
            val result = GeckoResult.fromValue(loginStorage.getByHostname(domain).map {
                Login(it.id, it.hostname, it.formSubmitURL, it.httpRealm, it.username, it.password)
            }.toTypedArray())
            loginStorage.lock()
            return result
        }
    }

    override fun onLoginSave(login: Login, hint: Int) {
        val passwordsKey = keyStore.getString(PASSWORDS_KEY) ?: return
        loginStorage.unlock(passwordsKey).also {
            // TODO use hints and ensureValid to help determine whether to show update/save prompt
            // Use prompt feature to show bottom dialog prompt for logins?
            // loginStorage.ensureValid()
        }.also {
            loginStorage.lock()
        }
    }

    companion object {
        const val PASSWORDS_KEY = "passwords"
    }
}
