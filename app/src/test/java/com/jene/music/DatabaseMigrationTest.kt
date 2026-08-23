package com.jene.music

import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.jene.music.data.local.AppDatabase
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DatabaseMigrationTest {
    @Test
    fun testMigration() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = AppDatabase.getDatabase(context)
        val dao = db.playlistDao()
        println("DB opened successfully")
    }
}
