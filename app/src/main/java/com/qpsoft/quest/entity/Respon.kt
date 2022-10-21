package com.qpsoft.quest.entity

data class Respon(
    val code: Int,
    val data: AppVersion?,
    val message: String,
)