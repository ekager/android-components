/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

@file:Suppress("PLUGIN_WARNING")

package mozilla.components.concept.engine

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

// Temporary Data Class before lands in GV
/**
 *
 */
@Parcelize
data class Login(
    var guid: String? = null,
    var origin: String? = null,
    var formActionOrigin: String? = null,
    var httpRealm: String? = null,
    var username: String? = null,
    var password: String? = null
) : Parcelable

/**
 *
 */
@Parcelize
class Hint : Parcelable {
    var GENERATED = 0
    var PRIVATE_MODE = 0
    var LOW_CONFIDENCE = 0 // TBD
}
