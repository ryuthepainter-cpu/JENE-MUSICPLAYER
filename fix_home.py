import re

with open("app/src/main/java/com/jene/music/ui/screens/HomeScreen.kt", "r") as f:
    content = f.read()

content = content.replace("""        IconButton(onClick = { /* More */ }) {
            Icon(
                imageVector = Icons.Filled.MoreVert,
                contentDescription = "More",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }""", "")

with open("app/src/main/java/com/jene/music/ui/screens/HomeScreen.kt", "w") as f:
    f.write(content)
