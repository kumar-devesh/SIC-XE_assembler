This repository contains the implementation of SIC/XE assembler according to the book ```Software Systems: An Introduction to Systems Programming``` by ```Leland L. Beck```

## Features implemented

- [x] SIC/XE Instructions, Format 4 instruction, Modification records, code blocks
- [x] LTORG, EQU, ORG assembler directives
- [x] PROGRAM BLOCKS
- [x] Error Messages

## Usage

- compile the code using:

	``` javac -d build *.java ```

- cd into build directory where the ```.class``` files are 

	```java assembler ./path_to_the_tc_file```

- to run these test cases use the command:

	```java assembler ../tc_name.txt```