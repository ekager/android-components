/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package mozilla.components.browser.engine.gecko.textinput

import android.annotation.TargetApi
import android.graphics.Matrix
import android.os.Build
import android.os.Bundle
import android.os.LocaleList
import android.text.InputType
import android.util.SparseArray
import android.view.View
import android.view.ViewStructure
import android.view.autofill.AutofillId
import android.view.autofill.AutofillValue
import android.widget.EditText
import androidx.annotation.RequiresApi
import mozilla.components.browser.engine.gecko.GeckoEngineSession
import org.mozilla.geckoview.GeckoSession
import org.mozilla.geckoview.GeckoSession.TextInputDelegate

internal class GeckoSessionTextInputDelegate(private val geckoEngineSession: GeckoEngineSession) :
        TextInputDelegate {
    private var autoFillValues = SparseArray<CharSequence>(5)

    override fun notifyAutoFill(geckoSession: GeckoSession, notification: Int, virtualId: Int) {
        // We need to call the default stuff here as well to make sure that other services work currently this breaks keyboard etc
        geckoSession.getTextInput().getView() ?: return
        // I guess this is still 23+ :/
        if (Build.VERSION.SDK_INT < 23) return
        if (notification == TextInputDelegate.AUTO_FILL_NOTIFY_STARTED) {
            val rootStructure = ViewStructureExample()
            geckoSession.textInput.onProvideAutofillVirtualStructure(rootStructure, 0)
            checkAutoFillChild(rootStructure)
            geckoSession.textInput.autofill(autoFillValues)
        }
    }

    // Perform auto-fill and return number of auto-fills performed.
    @RequiresApi(Build.VERSION_CODES.M)
    fun checkAutoFillChild(child: ViewStructureExample) {
        // Seal the node info instance so we can perform actions on it.
        if (child.childCount > 0) {
            for (c in child.children) {
                checkAutoFillChild(c!!)
            }
        }

        if (child.id == View.NO_ID) {
            return
        }

        if (EditText::class.java.name == child.className) {
            // TODO remove this. Figure out why fillAutofillStructure doesn't let us tag under 26
            if (Build.VERSION.SDK_INT < 26) {
                autoFillValues.append(child.id, "bar")
                return
            }

            // Dumb Autofill Values
            // TODO actually provide real things here
            autoFillValues.append(child.id, when (child.inputType) {
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD -> "baz"
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS -> "a@b.c"
                InputType.TYPE_CLASS_NUMBER -> "24"
                InputType.TYPE_CLASS_PHONE -> "42"
                else -> "bar"
            })
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    // TODO actually do things with these values or decide which ones we actually need/don't need
    class ViewStructureExample : ViewStructure() {
        private var mClassName: String? = null
        private var mEnabled = false
        private var mVisibility = -1
        private var mPackageName: String? = null
        private var mTypeName: String? = null
        private var mEntryName: String? = null
        private var mAutofillType = -1
        private var mAutofillHints: Array<String>? = null
        private var mInputType = -1
        private var mHtmlInfo: HtmlInfo? = null
        private var mWebDomain: String? = null
        private var mFocused = false
        private var mFocusable = false

        var children = ArrayList<ViewStructureExample?>()
        var id = View.NO_ID
        var height = 0
        var width = 0

        val className get() = mClassName
        val htmlInfo get() = mHtmlInfo
        val autofillHints get() = mAutofillHints
        val autofillType get() = mAutofillType
        val webDomain get() = mWebDomain
        val isEnabled get() = mEnabled
        val isFocused get() = mFocused
        val isFocusable get() = mFocusable
        val visibility get() = mVisibility
        val inputType get() = mInputType

        override fun setId(id: Int, packageName: String?, typeName: String?, entryName: String?) {
            this.id = id
            mPackageName = packageName
            mTypeName = typeName
            mEntryName = entryName
        }

        override fun setHint(hint: CharSequence?) {
        }

        override fun setElevation(elevation: Float) {
        }

        override fun getText(): CharSequence {
            return "hello"
        }

        override fun setText(text: CharSequence?) {
        }

        override fun setText(text: CharSequence?, selectionStart: Int, selectionEnd: Int) {
        }

        override fun asyncCommit() {
        }

        override fun getChildCount(): Int = children.size

        override fun setEnabled(state: Boolean) {
            mEnabled = state
        }

        override fun setLocaleList(localeList: LocaleList?) {

        }

        override fun setDimens(left: Int, top: Int, scrollX: Int, scrollY: Int, width: Int, height: Int) {
            this.width = width
            this.height = height
        }

        override fun setChecked(state: Boolean) {
        }

        override fun setContextClickable(state: Boolean) {
        }

        override fun setAccessibilityFocused(state: Boolean) {

        }

        override fun setAlpha(alpha: Float) {
        }

        override fun setTransformation(matrix: Matrix?) {
        }

        override fun setClassName(className: String?) {
            mClassName = className
        }

        override fun setLongClickable(state: Boolean) {
        }

        override fun newChild(index: Int): ViewStructure {
            val child = ViewStructureExample()
            children[index] = child
            return child
        }

        override fun getHint(): CharSequence {
            return "hint"
        }

        override fun setInputType(inputType: Int) {
            mInputType = inputType
        }

        override fun setWebDomain(domain: String?) {
            mWebDomain = domain
        }

        override fun setAutofillOptions(options: Array<out CharSequence>?) {

        }

        override fun setTextStyle(size: Float, fgColor: Int, bgColor: Int, style: Int) {
        }

        override fun setVisibility(visibility: Int) {
            mVisibility = visibility
        }

        override fun getAutofillId(): AutofillId? {
            return null
        }

        override fun setHtmlInfo(htmlInfo: HtmlInfo) {
            mHtmlInfo = htmlInfo
        }

        override fun setTextLines(charOffsets: IntArray?, baselines: IntArray?) {

        }

        override fun getExtras(): Bundle {
            TODO("not implemented")
        }

        override fun setClickable(state: Boolean) {
            TODO("not implemented")
        }

        @TargetApi(Build.VERSION_CODES.O)
        override fun newHtmlInfoBuilder(tagName: String): HtmlInfo.Builder {
            return MockHtmlInfoBuilder(tagName)
        }

        override fun getTextSelectionEnd(): Int {
            TODO("not implemented")
        }

        override fun setAutofillId(id: AutofillId) {
        }

        override fun setAutofillId(parentId: AutofillId, virtualId: Int) {
        }

        override fun hasExtras(): Boolean {
            TODO("not implemented")
        }

        override fun addChildCount(num: Int): Int {
            TODO("not implemented")
        }

        override fun setAutofillType(type: Int) {
            mAutofillType = type
        }

        override fun setActivated(state: Boolean) {
            TODO("not implemented")
        }

        override fun setFocused(state: Boolean) {
            mFocused = state
        }

        override fun getTextSelectionStart(): Int {
            TODO("not implemented")
        }

        override fun setChildCount(num: Int) {
            children = ArrayList()
            for (i in 0 until num) {
                children.add(null)
            }
        }

        override fun setAutofillValue(value: AutofillValue?) {
            TODO("not implemented")
        }

        override fun setAutofillHints(hint: Array<String>?) {
            mAutofillHints = hint
        }

        override fun setContentDescription(contentDescription: CharSequence?) {
            TODO("not implemented")
        }

        override fun setFocusable(state: Boolean) {
            mFocusable = state
        }

        override fun setCheckable(state: Boolean) {
            TODO("not implemented")
        }

        override fun asyncNewChild(index: Int): ViewStructure {
            TODO("not implemented")
        }

        override fun setSelected(state: Boolean) {
            TODO("not implemented")
        }

        override fun setDataIsSensitive(sensitive: Boolean) {
            TODO("not implemented")
        }

        override fun setOpaque(opaque: Boolean) {
            TODO("not implemented")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    class MockHtmlInfoBuilder(tagName: String) : ViewStructure.HtmlInfo.Builder() {
        val mTagName = tagName
        val mAttributes: MutableList<android.util.Pair<String, String>> = mutableListOf()

        override fun addAttribute(name: String, value: String): ViewStructure.HtmlInfo.Builder {
            mAttributes.add(android.util.Pair(name, value))
            return this
        }

        override fun build(): ViewStructure.HtmlInfo {
            return MockHtmlInfo(mTagName, mAttributes)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    class MockHtmlInfo(tagName: String, attributes: MutableList<android.util.Pair<String, String>>)
        : ViewStructure.HtmlInfo() {
        private val mTagName = tagName
        private val mAttributes = attributes

        override fun getTag() = mTagName
        override fun getAttributes(): MutableList<android.util.Pair<String, String>>? = mAttributes
    }
}
