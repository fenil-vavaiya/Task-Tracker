package com.example.googletaskproject.data.model

data class GroupItem(
    var id: String = "",
    var members: List<GroupMember> = emptyList(),
    val createdBy: String = "",
    val createdAt: Long = 0L
)
