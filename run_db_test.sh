mkdir -p app/src/test/java/com/jene/music
mv test_db.kt app/src/test/java/com/jene/music/DatabaseMigrationTest.kt
gradle :app:testDebugUnitTest --tests "com.jene.music.DatabaseMigrationTest"
