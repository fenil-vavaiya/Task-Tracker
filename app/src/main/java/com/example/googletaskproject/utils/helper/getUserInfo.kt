package com.example.googletaskproject.utils.helper

import com.example.googletaskproject.core.SessionManager
import com.example.googletaskproject.data.model.UserModel
import com.example.googletaskproject.utils.Const

fun getUserInfo(): UserModel {
    SessionManager.getObject(Const.USER_INFO, UserModel::class.java)?.let {
        return it
    }
    return UserModel()
}