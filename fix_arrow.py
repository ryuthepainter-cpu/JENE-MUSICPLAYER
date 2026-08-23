import re

with open("app/src/main/java/com/jene/music/ui/screens/PlaylistDetailScreen.kt", "r") as f:
    content = f.read()

content = content.replace("Icons.AutoMirrored.Filled.ArrowBack", "Icons.Filled.ArrowBack")

with open("app/src/main/java/com/jene/music/ui/screens/PlaylistDetailScreen.kt", "w") as f:
    f.write(content)
