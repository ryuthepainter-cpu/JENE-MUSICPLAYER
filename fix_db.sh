sed -i 's/version = 2/version = 3/' app/src/main/java/com/jene/music/data/AppDatabase.kt
sed -i 's/PlaylistSongCrossRef::class\]/PlaylistSongCrossRef::class, LyricAssociation::class\]/' app/src/main/java/com/jene/music/data/AppDatabase.kt
sed -i '/abstract fun playlistDao(): PlaylistDao/a \    abstract fun lyricAssociationDao(): LyricAssociationDao' app/src/main/java/com/jene/music/data/AppDatabase.kt

sed -i '/companion object {/a \
        val MIGRATION_2_3 = object : Migration(2, 3) {\
            override fun migrate(db: SupportSQLiteDatabase) {\
                db.execSQL("ALTER TABLE playlists ADD COLUMN description TEXT")\
                db.execSQL("ALTER TABLE playlists ADD COLUMN artworkUri TEXT")\
                db.execSQL("CREATE TABLE IF NOT EXISTS `lyric_associations` (`songId` TEXT NOT NULL, `lyricUri` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`songId`))")\
            }\
        }' app/src/main/java/com/jene/music/data/AppDatabase.kt

sed -i 's/.addMigrations(MIGRATION_1_2)/.addMigrations(MIGRATION_1_2, MIGRATION_2_3)/' app/src/main/java/com/jene/music/data/AppDatabase.kt
