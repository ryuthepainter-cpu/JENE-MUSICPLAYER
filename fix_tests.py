import os

files = [
    "app/src/test/java/com/jene/music/DatabaseMigrationTest.kt",
    "app/src/test/java/com/jene/music/PlaylistTest.kt"
]

for file_path in files:
    with open(file_path, "r") as f:
        content = f.read()

    content = content.replace("import com.jene.music.data.AppDatabase", "import com.jene.music.data.local.AppDatabase")
    content = content.replace("import com.jene.music.data.Playlist", "import com.jene.music.data.model.Playlist")
    content = content.replace("import com.jene.music.data.PlaylistSongCrossRef", "import com.jene.music.data.model.PlaylistSongCrossRef")
    content = content.replace("import com.jene.music.data.Song", "import com.jene.music.data.model.Song")
    
    with open(file_path, "w") as f:
        f.write(content)

