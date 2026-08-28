import sys

def read_file(filepath):
    with open(filepath, 'r') as f:
        print(f.read())

read_file("app/src/main/java/com/jene/music/ui/screens/NowPlayingScreen.kt")
