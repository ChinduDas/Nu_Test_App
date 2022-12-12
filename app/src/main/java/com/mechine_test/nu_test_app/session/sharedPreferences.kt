package com.mechine_test.nu_test_app.session

import android.content.Context

/***
 * Purpose : For managing the session values (by using get/set methods)
 */
object sharedPreferences {

    private const val _SESSION_NAME = "NU_PREF_NAME"
    private const val _PREF_USER_TOKEN = "USER_TOKEN"
    private const val _PREF_USER_EMAIL = "USER_EMAIL"
    private const val _PREF_USER_PASSWORD = "USER_PASSWORD"
    const val __NULL = ""

    fun setTokenPreferences(context: Context, value: String?) {
        val editor = context.getSharedPreferences(_SESSION_NAME, Context.MODE_PRIVATE).edit()
        editor.putString(_PREF_USER_TOKEN, value)
        editor.apply()
    }

    fun getTokenPreferences(context: Context?): String? {
        return if (context != null) {
            val prefs =
                context.getSharedPreferences(_SESSION_NAME, Context.MODE_PRIVATE)
            prefs.getString(_PREF_USER_TOKEN, "")
        } else ""
    }

}