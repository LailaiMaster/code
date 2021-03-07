import os
import re
import shutil
import subprocess
import sys

if len(sys.argv) < 2:
    print("Input app name.")
    exit(1)

lines = subprocess.check_output(["Listdlls.exe", sys.argv[1]]).decode(encoding="utf-8").split("\r\n")

output_path = None
collecting = False
mingw_dlls = []
for line in lines:
    if collecting and "mingw" in line:
        mingw_dlls.append(re.split(r"\s+", line)[2])

    if line.startswith("Command line: "):
        output_path = line[14:].strip('"')
        output_path = os.path.dirname(output_path)

    if line.startswith("Base                Size      Path"):
        collecting = True

print(output_path)
print(mingw_dlls)

for dll_file in mingw_dlls:
    dll_filename = os.path.basename(dll_file)
    dest_file = f"{output_path}{os.sep}{dll_filename}"
    if os.path.exists(dest_file):
        print(f"Skip: {dest_file}")
    else:
        print(shutil.copy(dll_file, dest_file))
