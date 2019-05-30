package com.codemetrictech.seed_go.utils

import android.content.Context
import android.preference.PreferenceManager


class Preferences {

    companion object PrefController{

        //LOGIN ID
        private const val LOGIN_COUNT_ID = "com.codemetrictech.seed_go.login_count"

        fun setLoginCount(context: Context, value: Int){
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putInt(LOGIN_COUNT_ID, value)
            editor.apply()
        }

        fun getLoginCount(context: Context): Int {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getInt(LOGIN_COUNT_ID, 0)
        }

        //REMEMBER ME
        private const val REMEMBER_ME_ID = "com.codemetrictech.seed_go.remember_me"

        fun setRememberMe(context: Context, value: Boolean){
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putBoolean(REMEMBER_ME_ID, value)
            editor.apply()
        }

        fun getRememberMe(context: Context): Boolean {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getBoolean(REMEMBER_ME_ID,false)
        }

        //USERNAME
        private const val USER_NAME_ID = "com.codemetrictech.seed_go.user_name"

        fun setUserName(context: Context, value: String){
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putString(USER_NAME_ID, value)
            editor.apply()
        }

        fun getUserName(context: Context): String {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getString(USER_NAME_ID, "")
        }

        //UserPassword
        private const val USER_PASSWORD_ID = "com.codemetrictech.seed_go.user_password"

        fun setUserPassword(context: Context, value: String){
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putString(USER_PASSWORD_ID, value)
            editor.apply()
        }

        fun getUserPassword(context: Context): String {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getString(USER_PASSWORD_ID, "")
        }


    }
}

