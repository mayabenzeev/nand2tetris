import os
import sys
from CompilationEngine import *


def main():
    """
    Main entry point of the Jack compiler.

    Accepts a command-line argument indicating the path to a Jack source file or directory.
    If a single Jack source file is provided, processes that file.
    If a directory is provided, processes all Jack source files in that directory.

    Usage:
    $ python script_name.py path-to-file-or-directory
    """
    ...
    if len(sys.argv) != 2:
        print("Usage: Assembler path-to .asm file or directory", file=sys.stderr)
        sys.exit(1)

    path = sys.argv[1]

    if os.path.isfile(path) and path.endswith(".jack"):
        process_file(path)
    elif os.path.isdir(path):
        files = [f for f in os.listdir(path) if f.endswith(".jack")]
        for file in files:
            process_file(os.path.join(path, file))
    else:
        print(f"Invalid input: {sys.argv[1]}", file=sys.stderr)
        sys.exit(1)


def process_file(in_file):
    """
    Processes a Jack source file and generates the corresponding XML output.

    Parameters:
    - in_file (str): Path to the Jack source file.

    Outputs an XML file with the same base name as the input file and a '.xml' extension.
    """
    outfile = in_file.split(".jack")[0] + ".xml"
    compilationEngine = CompilationEngine(in_file, outfile)
    compilationEngine.compileClass()


if __name__ == "__main__":
    main()
