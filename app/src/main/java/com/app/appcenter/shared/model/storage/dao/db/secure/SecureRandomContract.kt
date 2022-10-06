package com.app.appcenter.shared.model.storage.dao.db.secure

interface SecureRandomContract {
    fun generateRandom(byteArray: ByteArray): String
}