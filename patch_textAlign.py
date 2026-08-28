import re
with open("app/src/main/java/com/jene/music/ui/screens/HomeScreen.kt", "r") as f:
    content = f.read()

content = content.replace("textAlign = androidx.compose.ui.text.style.TextAlign.Center", "textAlign = androidx.compose.ui.text.style.TextAlign.Center")
# Actually, since I fully qualified `androidx.compose.ui.text.style.TextAlign.Center`, it should compile fine without import!
