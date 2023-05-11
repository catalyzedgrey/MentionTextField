package com.example.android.mentiontextfield.data

data class Mention(
    val id: String,
    val name: String,
    val pictureUrl: String,
    val type: String?,

    ) {
    fun getFullName(): String {
        return name
    }
}