sed -i 's/^import kotlinx.coroutines.Dispatchers$//' app/src/main/java/com/jene/music/ui/MainViewModel.kt
sed -i 's/^package com.jene.music.ui$/package com.jene.music.ui\n\nimport kotlinx.coroutines.Dispatchers/' app/src/main/java/com/jene/music/ui/MainViewModel.kt
