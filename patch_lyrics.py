import re
with open("app/src/main/java/com/jene/music/ui/screens/NowPlayingScreen.kt", "r") as f:
    content = f.read()

content = content.replace("currentPosition >= it.timeMs", "currentPosition >= it.startTimeMs")

with open("app/src/main/java/com/jene/music/ui/screens/NowPlayingScreen.kt", "w") as f:
    f.write(content)
