package mozilla.components.browser.engine.gecko.autofill

/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

import mozilla.appservices.logins.LoginsStorage
import mozilla.components.lib.dataprotect.SecureAbove22Preferences
import org.mozilla.geckoview.GeckoResult

// Temporary Data Class before lands in GV
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

// Temporary Interface before lands in GV
internal interface LoginDelegate {
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
 * [LoginDelegate] implementation.
 * App will have to instantiate this and set it on the runtime and pass in the [LoginsStorage] and [SecureAbove22Preferences] with a key that conforms to "passwords"
 */
class LoginStorageDelegate(
    private val loginStorage: LoginsStorage,
    private val keyStore: SecureAbove22Preferences
) : LoginDelegate {
    override fun onLoginUsed(login: Login) {
        val passwordsKey = keyStore.getString(PASSWORDS_KEY) ?: return
        loginStorage.ensureUnlocked(passwordsKey).also {
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
        loginStorage.ensureUnlocked(passwordsKey).also {
            val result = GeckoResult.fromValue(loginStorage.getByHostname(domain).map {
                Login(it.id, it.hostname, it.formSubmitURL, it.httpRealm, it.username, it.password)
            }.toTypedArray())
            loginStorage.lock()
            return result
        }
    }

    override fun onLoginSave(login: Login, hint: Int) {
        val passwordsKey = keyStore.getString(PASSWORDS_KEY) ?: return
        loginStorage.ensureUnlocked(passwordsKey).also {
            // TODO use hints and ensureValid to help determine whether to show update/save prompt
            // TODO should we use prompt feature / notify Observers to show bottom dialog prompt for logins?
        }.also {
            loginStorage.lock()
        }
    }

    companion object {
        const val PASSWORDS_KEY = "passwords"
    }
}
