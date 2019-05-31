package com.codemetrictech.seed_go.utils

class InputValidator {
    companion object {
        fun validatePassword(password : CharSequence): Boolean {
            if (!password.contains("@G!C", false)) {
                return false
            }
            return true
        }

        fun validateUsername(username : CharSequence): Boolean {
            if (!username.contains("uwi", true)) {
                return false
            }
            return true
        }
    }
}