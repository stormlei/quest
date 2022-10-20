package com.qpsoft.quest.entity

data class AppVersion (
    var version: String,
    var versionCode: Int,
    var updateContent: String,
    var forceUpdate: Boolean,
    val downloadUrl: String, //下载的url
    var remark: String,
)




