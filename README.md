This repository contains the implementation of SIC/XE assembler according to the book ```Software Systems: An Introduction to Systems Programming``` by ```Leland L. Beck```

## Features implemented

- [x] SIC/XE Instructions, Format 4 instruction, Modification records, code blocks
- [x] LTORG, EQU, ORG assembler directives
- [x] PROGRAM BLOCKS
- [x] Error Messages

## Error Handling

The error messages are listed in the error.txt file and in the intermediate file with error codes

errors listed in intermediate file:

| locctr/programblock		| error_code	| instruction 	| comments	|
| ------------------------- | ------------- | ------------- | --------- |
| 6/0						| 0		    | LDA LENGTH     | . TEST FOR EOF |

	error codes:
    * 0 => no error
    * 1 => duplicate symbol
    * 2 => invalid opcode
    * 3 => invalid instruction format
    * 4 => invalid expression


## Usage

- compile the code using:

	``` javac -d build *.java ```

- cd into build directory where the ```.class``` files are 

	```java assembler ./path_to_the_tc_file```

- to run these test cases use the command:

	```java assembler ../tc_name.txt```