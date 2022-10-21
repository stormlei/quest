package com.qpsoft.quest.entity

import org.json.JSONArray

data class Quest(
    val id: String,
    val title: String,
    val time: String,
    val tabletNo: String,
    val data: List<Any>,
)