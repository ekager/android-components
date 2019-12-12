/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package mozilla.components.concept.engine

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

// Temporary Data Class before lands in GV
@Parcelize
data class Login(
    var guid: String? = null,
    // @Fenix: currently called `hostname` in AsyncLoginsStorage.
    var origin: String? = null,
    // @Fenix: currently called `formSubmitURL` in AsyncLoginsStorage
    var formActionOrigin: String? = null,
    var httpRealm: String? = null,
    var username: String? = null,
    var password: String? = null
) : Parcelable

@Parcelize
class Hint: Parcelable {
    // @Fenix: Automatically save the login and indicate this
// to the user.
    var GENERATED = 0
    // @Fenix: Don’t prompt to save but allow the user to open
// UI to save if they really want.
    var PRIVATE_MODE = 0
    // The data looks like it may be some other data (e.g. CC)
// entered in a password field.
// @Fenix: Don’t prompt to save but allow the user to open
// UI to save if they want (e.g. in case the CC number is
// actually the username for a credit card account)
    var LOW_CONFIDENCE = 0 // TBD
}
