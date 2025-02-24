package com.example.googletaskproject.core.state

sealed class TypeState(  val name: String) {
    data object SettingState : TypeState("Setting Mode")
    data object UsageState : TypeState("Usage Mode")
    data object SleepState : TypeState("Sleep Mode")

    override fun toString(): String = name // ✅ Ensures proper display in Spinner
}