/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package mozilla.components.feature.prompts.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import mozilla.components.feature.prompts.R
import mozilla.components.support.ktx.android.content.appName
import com.google.android.material.R as MaterialR

private const val KEY_USERNAME_EDIT_TEXT = "KEY_USERNAME_EDIT_TEXT"
private const val KEY_PASSWORD_EDIT_TEXT = "KEY_PASSWORD_EDIT_TEXT"
private const val KEY_HOSTNAME_TEXT = "KEY_HOSTNAME_TEXT"
private const val KEY_LOGIN_HINT = "KEY_LOGIN_HINT"
private const val KEY_LOGIN = "KEY_LOGIN"

/**
 * [android.support.v4.app.DialogFragment] implementation to display a
 * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Authentication">authentication</a>
 * dialog with native dialogs.
 */
internal class LoginDialogFragment : PromptDialogFragment() {

    internal var username: String
        get() = safeArguments.getString(KEY_USERNAME_EDIT_TEXT, "")
        set(value) {
            safeArguments.putString(KEY_USERNAME_EDIT_TEXT, value)
        }

    internal var password: String
        get() = safeArguments.getString(KEY_PASSWORD_EDIT_TEXT, "")
        set(value) {
            safeArguments.putString(KEY_PASSWORD_EDIT_TEXT, value)
        }

    internal var hostName: String
        get() = safeArguments.getString(KEY_HOSTNAME_TEXT, "")
        set(value) {
            safeArguments.putString(KEY_HOSTNAME_TEXT, value)
        }

    internal var hint: Hint
        get() = safeArguments.getParcelable<Hint>(KEY_LOGIN_HINT)
        set(value) {
            safeArguments.putParcelable(KEY_LOGIN_HINT, value)
        }

    internal var login: Login
        get() = safeArguments.getParcelable<Login>(KEY_LOGIN)
        set(value) {
            safeArguments.putParcelable(KEY_LOGIN, value)
        }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // TODO how do we theme this dialog correctly?

        return BottomSheetDialog(requireContext(), this.theme).apply {
            setOnShowListener {
                val bottomSheet =
                    findViewById<View>(MaterialR.id.design_bottom_sheet) as FrameLayout
                val behavior = BottomSheetBehavior.from(bottomSheet)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflateRootView(container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val hostView = view.findViewById<TextView>(R.id.host_name)
        hostView.setText(hostName)

        val saveMessage = view.findViewById<TextView>(R.id.save_message)

        // TODO how do I get the app name correctly here?
        // TODO this should be save or update depending on the hints / if login already exists?
        saveMessage.text =
            getString(R.string.mozac_feature_prompt_logins_save_message, activity?.appName)

        val saveConfirm = view.findViewById<MaterialButton>(R.id.save_confirm)
        val cancelButton = view.findViewById<Button>(R.id.save_cancel)

        saveConfirm.setOnClickListener {
            onPositiveClickAction()
        }

        cancelButton.setOnClickListener {
            // TODO the negative action also needs to add an exception...
            //  What's the best place to do this? Maybe we also have to ask GV to route the negative action to onLoginSaved
            feature?.onCancel(sessionId)
        }
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        feature?.onCancel(sessionId)
    }

    private fun onPositiveClickAction() {
        feature?.onConfirm(sessionId, login.copy(username = username, password = password))
    }

    private fun inflateRootView(container: ViewGroup? = null): View {
        val rootView = LayoutInflater.from(requireContext()).inflate(
            R.layout.mozac_feature_prompt_login_prompt,
            container,
            false
        )
        bindUsername(rootView)
        bindPassword(rootView)
        return rootView
    }

    private fun bindUsername(view: View) {
        val usernameEditText = view.findViewById<TextInputEditText>(R.id.username_field)

        usernameEditText.setText(username)
        usernameEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable) {
                username = editable.toString()
            }

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) = Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) =
                Unit
        })
    }

    private fun bindPassword(view: View) {
        val passwordEditText = view.findViewById<TextInputEditText>(R.id.password_field)

        passwordEditText.setText(password)
        passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable) {
                password = editable.toString()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) =
                Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
        })
    }

    companion object {
        /**
         * A builder method for creating a [LoginDialogFragment]
         * @param sessionId the id of the session for which this dialog will be created.
         * @param username the default value of the username text field.
         * @param password the default value of the password text field.
         * @param hostName the host of the site of the login.
         */
        fun newInstance(
            sessionId: String,
            hint: Hint,
            login: Login,
            username: String,
            password: String,
            hostName: String
        ): LoginDialogFragment {

            val fragment = LoginDialogFragment()
            val arguments = fragment.arguments ?: Bundle()

            with(arguments) {
                putString(KEY_SESSION_ID, sessionId)
                putParcelable(KEY_LOGIN_HINT, hint)
                putParcelable(KEY_LOGIN, login)
                putString(KEY_USERNAME_EDIT_TEXT, username)
                putString(KEY_PASSWORD_EDIT_TEXT, password)
                putString(KEY_HOSTNAME_TEXT, hostName)
            }

            fragment.arguments = arguments
            return fragment
        }
    }
}
