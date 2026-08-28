import re

with open("app/src/main/java/com/jene/music/ui/MainViewModel.kt", "r") as f:
    content = f.read()

content = content.replace(
    "playerController.playerState.map { it.currentSong?.id }.distinctUntilChanged().collect { songId ->",
    "playerController.playerState.map { it.currentSong?.id }.distinctUntilChanged().collectLatest { songId ->"
)

if "import kotlinx.coroutines.flow.collectLatest" not in content:
    content = content.replace(
        "import kotlinx.coroutines.flow.collect",
        "import kotlinx.coroutines.flow.collect\nimport kotlinx.coroutines.flow.collectLatest"
    )

with open("app/src/main/java/com/jene/music/ui/MainViewModel.kt", "w") as f:
    f.write(content)

