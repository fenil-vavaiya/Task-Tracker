package com.example.googletaskproject.data.model

data class UserModel(
    val name: String,
    val email: String,
    val photoUrl: String,
    var userId: String = "",
    var location: String = "",
    var groupId: String = "",
)
