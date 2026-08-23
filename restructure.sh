mkdir -p app/src/main/java/com/jene/music/data/model
mkdir -p app/src/main/java/com/jene/music/data/local
mkdir -p app/src/main/java/com/jene/music/data/repository
mkdir -p app/src/main/java/com/jene/music/data/mediastore

mv app/src/main/java/com/jene/music/data/Song.kt app/src/main/java/com/jene/music/data/model/
mv app/src/main/java/com/jene/music/data/Playlist.kt app/src/main/java/com/jene/music/data/model/
mv app/src/main/java/com/jene/music/data/Album.kt app/src/main/java/com/jene/music/data/model/
mv app/src/main/java/com/jene/music/data/LyricAssociation.kt app/src/main/java/com/jene/music/data/model/

mv app/src/main/java/com/jene/music/data/SongDao.kt app/src/main/java/com/jene/music/data/local/
mv app/src/main/java/com/jene/music/data/PlaylistDao.kt app/src/main/java/com/jene/music/data/local/
mv app/src/main/java/com/jene/music/data/LyricAssociationDao.kt app/src/main/java/com/jene/music/data/local/
mv app/src/main/java/com/jene/music/data/AppDatabase.kt app/src/main/java/com/jene/music/data/local/

mv app/src/main/java/com/jene/music/data/MediaRepository.kt app/src/main/java/com/jene/music/data/repository/
mv app/src/main/java/com/jene/music/data/SettingsRepository.kt app/src/main/java/com/jene/music/data/repository/

mv app/src/main/java/com/jene/music/data/MediaScanner.kt app/src/main/java/com/jene/music/data/mediastore/

# Fix package declarations
find app/src/main/java/com/jene/music/data/model -name "*.kt" -exec sed -i 's/package com.jene.music.data/package com.jene.music.data.model/' {} +
find app/src/main/java/com/jene/music/data/local -name "*.kt" -exec sed -i 's/package com.jene.music.data/package com.jene.music.data.local/' {} +
find app/src/main/java/com/jene/music/data/repository -name "*.kt" -exec sed -i 's/package com.jene.music.data/package com.jene.music.data.repository/' {} +
find app/src/main/java/com/jene/music/data/mediastore -name "*.kt" -exec sed -i 's/package com.jene.music.data/package com.jene.music.data.mediastore/' {} +

# Add necessary imports back to the moved files
find app/src/main/java/com/jene/music/data/local -name "*.kt" -exec sed -i '/import androidx.room/i import com.jene.music.data.model.*' {} +
find app/src/main/java/com/jene/music/data/repository -name "*.kt" -exec sed -i '2i import com.jene.music.data.model.*\nimport com.jene.music.data.local.*\nimport com.jene.music.data.mediastore.*' {} +
find app/src/main/java/com/jene/music/data/mediastore -name "*.kt" -exec sed -i '2i import com.jene.music.data.model.*\nimport com.jene.music.data.local.*' {} +

# Update MainViewModel
sed -i 's/import com.jene.music.data.*/import com.jene.music.data.model.*\nimport com.jene.music.data.local.*\nimport com.jene.music.data.repository.*\nimport com.jene.music.data.mediastore.*\nimport com.jene.music.data.LyricsParser/' app/src/main/java/com/jene/music/ui/MainViewModel.kt
sed -i 's/com.jene.music.data.SettingsRepository/SettingsRepository/' app/src/main/java/com/jene/music/ui/MainViewModel.kt

# Update UI and Player components
find app/src/main/java/com/jene/music/ui/ -name "*.kt" -exec sed -i 's/import com.jene.music.data.Song/import com.jene.music.data.model.Song/' {} +
find app/src/main/java/com/jene/music/ui/ -name "*.kt" -exec sed -i 's/import com.jene.music.data.Playlist/import com.jene.music.data.model.Playlist/' {} +
find app/src/main/java/com/jene/music/ui/ -name "*.kt" -exec sed -i 's/import com.jene.music.data.Album/import com.jene.music.data.model.Album/' {} +
find app/src/main/java/com/jene/music/ui/ -name "*.kt" -exec sed -i 's/import com.jene.music.data.LyricLine/import com.jene.music.data.model.LyricLine/' {} +

find app/src/main/java/com/jene/music/core/player -name "*.kt" -exec sed -i 's/import com.jene.music.data.Song/import com.jene.music.data.model.Song/' {} +
find app/src/main/java/com/jene/music/core/player -name "*.kt" -exec sed -i 's/import com.jene.music.data.AppDatabase/import com.jene.music.data.local.AppDatabase/' {} +
