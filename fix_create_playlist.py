import re

with open("app/src/main/java/com/jene/music/data/repository/PlaylistRepository.kt", "r") as f:
    repo = f.read()

repo = repo.replace("suspend fun createPlaylist(name: String) {", "suspend fun createPlaylist(name: String, description: String? = null) {")
repo = repo.replace("playlistDao.insertPlaylist(Playlist(name = name))", "playlistDao.insertPlaylist(Playlist(name = name, description = description))")

with open("app/src/main/java/com/jene/music/data/repository/PlaylistRepository.kt", "w") as f:
    f.write(repo)


with open("app/src/main/java/com/jene/music/ui/MainViewModel.kt", "r") as f:
    vm = f.read()

vm = vm.replace("fun createPlaylist(name: String) {", "fun createPlaylist(name: String, description: String? = null) {")
vm = vm.replace("playlistRepository.createPlaylist(name)", "playlistRepository.createPlaylist(name, description)")

with open("app/src/main/java/com/jene/music/ui/MainViewModel.kt", "w") as f:
    f.write(vm)


with open("app/src/main/java/com/jene/music/ui/screens/PlaylistsScreen.kt", "r") as f:
    ps = f.read()

ps = ps.replace("onCreate: (String) -> Unit", "onCreate: (String, String) -> Unit")
ps = ps.replace("var name by remember { mutableStateOf(\"\") }", 'var name by remember { mutableStateOf("") }\n    var description by remember { mutableStateOf("") }')
ps = ps.replace("""                OutlinedTextField(
                    value = name,""", """                OutlinedTextField(
                    value = name,""")

# We need to insert description text field right after the name text field
match = re.search(r'(OutlinedTextField\([\s\S]*?label = \{ Text\("Playlist name"\) \},[\s\S]*?singleLine = true\s*\))', ps)
if match:
    replacement = match.group(1) + """
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (optional)") },
                    maxLines = 3
                )"""
    ps = ps.replace(match.group(1), replacement)

ps = ps.replace("onCreate(trimmed)", "onCreate(trimmed, description.trim().takeIf { it.isNotEmpty() } ?: \"\")")

# Now handle the calls in PlaylistsScreen and AddToPlaylistDialog
ps = ps.replace("onCreate = { name ->", "onCreate = { name, desc ->")
ps = ps.replace("viewModel.createPlaylist(name)", "viewModel.createPlaylist(name, desc.takeIf { it.isNotEmpty() })")

with open("app/src/main/java/com/jene/music/ui/screens/PlaylistsScreen.kt", "w") as f:
    f.write(ps)


with open("app/src/main/java/com/jene/music/ui/components/AddToPlaylistDialog.kt", "r") as f:
    ap = f.read()

ap = ap.replace("onCreate = { name ->", "onCreate = { name, desc ->")
ap = ap.replace("viewModel.createPlaylist(name)", "viewModel.createPlaylist(name, desc.takeIf { it.isNotEmpty() })")

with open("app/src/main/java/com/jene/music/ui/components/AddToPlaylistDialog.kt", "w") as f:
    f.write(ap)
