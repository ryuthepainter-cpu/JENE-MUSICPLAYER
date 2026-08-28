import sys

def read_file(filepath):
    try:
        with open(filepath, 'r') as f:
            print(f.read())
    except:
        pass

read_file("app/src/main/java/com/jene/music/ui/components/MiniPlayer.kt")
