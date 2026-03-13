import os
import re
import xml.etree.ElementTree as ET

res_dir = r"c:\Users\david\.gemini\antigravity\scratch\iptv-player\app\src\main\res"
ref_file = os.path.join(res_dir, "values", "strings.xml")

def parse_strings(file_path):
    try:
        tree = ET.parse(file_path)
        root = tree.getroot()
        strings = {}
        for string in root.findall('string'):
            name = string.get('name')
            text = "".join(string.itertext())
            strings[name] = text
        
        # Also check plurals and arrays which might contain text
        others = []
        for p in root.findall('plurals'):
            others.append(p.get('name'))
        for a in root.findall('string-array'):
            others.append(a.get('name'))
            
        return strings, others
    except Exception as e:
        print(f"Error parsing {file_path}: {e}")
        return None, None

def analyze():
    ref_strings, ref_others = parse_strings(ref_file)
    if not ref_strings:
        return

    print(f"Reference file contains {len(ref_strings)} strings and {len(ref_others)} plurals/arrays.")

    # Skip list for strings that are often the same
    skip_untranslated = {"app_name", "welcome_version", "welcome_version_code", "settings_developer_name", "settings_github_url", "settings_github"}

    report_path = r"c:\Users\david\.gemini\antigravity\scratch\iptv-player\tmp\analysis_report.txt"
    with open(report_path, 'w', encoding='utf-8') as report:
        report.write(f"Reference (values/strings.xml): {len(ref_strings)} strings, {len(ref_others)} others\n")
        report.write("=" * 40 + "\n")
        
        for folder in sorted(os.listdir(res_dir)):
            if folder.startswith("values-"):
                lang_file = os.path.join(res_dir, folder, "strings.xml")
                if not os.path.exists(lang_file):
                    continue
                
                lang_strings, lang_others = parse_strings(lang_file)
                if not lang_strings:
                    continue
                
                report.write(f"Analyzing {folder} ({len(lang_strings)} strings)...\n")
                
                # 1. Missing strings
                missing = [k for k in ref_strings if k not in lang_strings]
                if missing:
                    report.write(f"  Missing ({len(missing)}): {', '.join(missing[:10])}{'...' if len(missing) > 10 else ''}\n")
                
                # 2. Untranslated strings (likely English sentences)
                untranslated_sentences = []
                for k, v in lang_strings.items():
                    if k in ref_strings and k not in skip_untranslated:
                        if v == ref_strings[k] and any(c.isalpha() for c in v):
                            # Heuristic: if it has spaces and looks like a sentence
                            if ' ' in v and len(v) > 10:
                                untranslated_sentences.append(k)
                
                if untranslated_sentences:
                    report.write(f"  Untranslated Sentences ({len(untranslated_sentences)}): {', '.join(untranslated_sentences[:10])}{'...' if len(untranslated_sentences) > 10 else ''}\n")
                    # For sentences, show the content to confirm
                    for k in untranslated_sentences[:3]:
                        report.write(f"    - {k}: \"{lang_strings[k][:50]}...\"\n")
                
                # 3. Formatting errors (unescaped apostrophes)
                with open(lang_file, 'r', encoding='utf-8') as f:
                    content = f.read()
                    
                raw_matches = re.findall(r'<string name="([^"]+)">(.*?)<\/string>', content)
                escaped_errors = []
                for name, text in raw_matches:
                    for i, char in enumerate(text):
                        if char == "'":
                            if i == 0 or text[i-1] != "\\":
                                escaped_errors.append(name)
                                break
                                
                if escaped_errors:
                    report.write(f"  Unescaped apostrophes ({len(escaped_errors)}): {', '.join(escaped_errors[:10])}{'...' if len(escaped_errors) > 10 else ''}\n")
                
                report.write("-" * 20 + "\n")

if __name__ == "__main__":
    analyze()
