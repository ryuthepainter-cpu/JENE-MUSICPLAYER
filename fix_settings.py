import re

with open("app/src/main/java/com/jene/music/ui/screens/SettingsScreen.kt", "r") as f:
    content = f.read()

content = re.sub(r'item \{\s+SettingsSectionTitle\("Appearance"\)[\s\S]*?\}', '', content)
content = content.replace("Icons.Filled.ArrowBack", "androidx.compose.material.icons.automirrored.filled.ArrowBack")

with open("app/src/main/java/com/jene/music/ui/screens/SettingsScreen.kt", "w") as f:
    f.write(content)
