awk '
/fun SongCard/ { in_song_card = 1 }
/^}/ && in_song_card { in_song_card = 0; print "}\n"; next }
in_song_card { next }
{ print }
' app/src/main/java/com/jene/music/ui/screens/HomeScreen.kt > temp.kt && mv temp.kt app/src/main/java/com/jene/music/ui/screens/HomeScreen.kt
