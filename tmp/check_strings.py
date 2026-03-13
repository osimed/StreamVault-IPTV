import os
import re

res_dir = r"c:\Users\david\.gemini\antigravity\scratch\iptv-player\app\src\main\res"
string_pattern = re.compile(r'<string name="[^"]+">(.*?)<\/string>')

def find_unescaped_apostrophes():
    for root, dirs, files in os.walk(res_dir):
        if "strings.xml" in files:
            file_path = os.path.join(root, "strings.xml")
            with open(file_path, 'r', encoding='utf-8') as f:
                content = f.read()
                
            matches = string_pattern.finditer(content)
            for match in matches:
                text = match.group(1)
                # Check for unescaped apostrophes
                # An apostrophe is unescaped if it's not preceded by a backslash
                # and the string isn't wrapped in quotes.
                # Simplified: find any ' not preceded by \
                # AND also find any \\\' which I saw in the French file
                
                # Rule 1: Find ' not preceded by \
                unescaped = []
                for i, char in enumerate(text):
                    if char == "'":
                        if i == 0 or text[i-1] != "\\":
                            unescaped.append(i)
                
                # Rule 2: Find any double backslash escape \\' which is redundant or wrong
                redundant_escapes = [m.start() for m in re.finditer(r"\\{2,}'", text)]

                if unescaped or redundant_escapes:
                    print(f"File: {file_path}")
                    print(f"  String: {match.group(0)}")
                    if unescaped:
                        print(f"  Unescaped ' at positions: {unescaped}")
                    if redundant_escapes:
                        print(f"  Wrong escape (\\\\') at positions: {redundant_escapes}")
                    print("-" * 40)

if __name__ == "__main__":
    find_unescaped_apostrophes()
