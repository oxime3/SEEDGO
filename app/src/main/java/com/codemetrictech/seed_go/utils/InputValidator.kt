package com.codemetrictech.seed_go.utils

class InputValidator {
    companion object {
        fun validateEmail(email: CharSequence): Boolean{
            if (email == null){
                return false
            }
            return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }

        fun validatePassword(password : CharSequence): Boolean{
            if(password == null){
                return false
            }

            if (!password.contains("@g!c", true)){
                return false
            }
            return true
        }

        fun validateUsername(username : CharSequence): Boolean{
            if(username == null){
                return false
            }
            if(!username.contains("uwi", true)){
                return false
            }
            return true
        }
    }
}