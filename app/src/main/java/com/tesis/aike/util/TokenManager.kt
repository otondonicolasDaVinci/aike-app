package com.tesis.aike.util // O el paquete donde lo tengas

import android.content.Context
import android.content.SharedPreferences

object TokenManager {
    private const val PREFS_NAME = "aike_prefs"
    private const val KEY_AUTH_TOKEN = "auth_token"
    private const val KEY_USER_ID = "user_id"

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    // 3. ASEGÚRATE DE QUE ESTA FUNCIÓN EXISTA Y TENGA ESTA FIRMA:
    fun saveToken(context: Context, token: String) {
        val editor = getPreferences(context).edit()
        editor.putString(KEY_AUTH_TOKEN, token)
        editor.apply()
    }

    // Esta es la función que habías modificado para guardar ambos:
    // Si estás usando esta, entonces en ChatViewModel deberías llamar a saveAuthData
    fun saveAuthData(context: Context, token: String, userId: String) {
        val editor = getPreferences(context).edit()
        editor.putString(KEY_AUTH_TOKEN, token)
        editor.putString(KEY_USER_ID, userId)
        editor.apply()
    }

    fun getToken(context: Context): String? {
        return getPreferences(context).getString(KEY_AUTH_TOKEN, null)
    }

    fun getUserId(context: Context): String? {
        return getPreferences(context).getString(KEY_USER_ID, null)
    }

    fun clearAuthData(context: Context) {
        val editor = getPreferences(context).edit()
        editor.remove(KEY_AUTH_TOKEN)
        editor.remove(KEY_USER_ID)
        editor.apply()
    }
}