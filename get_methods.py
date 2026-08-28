import urllib.request
import re

url = "https://raw.githubusercontent.com/ijabz/jaudiotagger/master/src/org/jaudiotagger/tag/id3/framebody/FrameBodySYLT.java"
try:
    req = urllib.request.Request(url)
    with urllib.request.urlopen(req) as response:
        content = response.read().decode('utf-8')
        for line in content.split('\n'):
            if 'public ' in line and '(' in line and '{' in line:
                print(line.strip())
except Exception as e:
    print(e)
