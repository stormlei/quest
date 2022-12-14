package com.qpsoft.quest.entity

data class Result<T>(
    val code: Int,
    val data: T?,
    val message: String,
)