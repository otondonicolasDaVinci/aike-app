package com.tesis.aike.util // O donde tengas tu TokenManager

import android.content.Context
import android.content.SharedPreferences

object TokenManager {
    private const val PREFS_NAME = "aike_prefs"
    private const val KEY_AUTH_TOKEN = "auth_token"

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveToken(context: Context, token: String) {
        val editor = getPreferences(context).edit()
        editor.putString(KEY_AUTH_TOKEN, token)
        editor.apply()
    }

    // ---- CORRECCIÓN AQUÍ ----
    fun getToken(context: Context): String? { // Especifica el tipo de retorno: String?
        return getPreferences(context).getString(KEY_AUTH_TOKEN, null)
    }
    // ---- FIN DE LA CORRECCIÓN ----

    fun clearToken(context: Context) {
        val editor = getPreferences(context).edit()
        editor.remove(KEY_AUTH_TOKEN)
        editor.apply()
    }
}