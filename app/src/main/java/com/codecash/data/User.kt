package com.codecash.data

data class User(
    val id: Int,
    val fullName: String,
    val email: String,
    val password: String,
    val createdAt: Long = System.currentTimeMillis()
)