import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Main {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: Assembler path-to .asm file or directory");
            System.exit(1);
        }

        String path = args[0];
        File inputFile = new File(path);
        // if input is a file and ends-with .asm -> process it
        if (inputFile.isFile() && inputFile.getName().endsWith(".asm")) {
            processFile(path);
        } else if (inputFile.isDirectory()) {
            // if input is a directory -> process all .asm files in the directory
            File[] files = inputFile.listFiles((dir, name) -> name.endsWith(".asm"));
            if (files != null) {
                for (File file : files) {
                    processFile(file.getAbsolutePath());
                }
            }
        } else {
            // not a file and not a directory - invalid input
            System.err.println("Invalid input: " + args[0]);
            System.exit(1);
        }
    }
    static void processFile(String asmFilePath) {
        //Initialize
        Parser parse = new Parser(asmFilePath);
        SymbolTable symbolTable = new SymbolTable();
        int n = 16;

        //First pass
        parse.advance();
        List<String> symbols = new ArrayList<>();
        while ((parse.currInstruction) != null) { // while there is an instruction
            //check for (label) declarations until the next C/A instruction
            if (Objects.equals(parse.instructionType(), Parser.L_INSTRUCTION)) {
                symbols.add(parse.symbol());
            } else {
                // add all symbols up to the next C/A instruction to symbol table
                // handles >1 (labels) row after row
                for (String symbol : symbols) {
                    // if symbol is not in the symbol table set its value
                    if (!symbolTable.contains(symbol)) {
                        symbolTable.addEntry(symbol, parse.numInstruction);
                    }
                }
                symbols.clear();
            }
            parse.advance();
        }
        parse.close();

        //Second pass
        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(asmFilePath.substring(0, asmFilePath.lastIndexOf(".")) + ".hack"))) {
            parse = new Parser(asmFilePath);
            Code code = new Code();
            String C, symbol;
            int A;

            parse.advance();
            while ((parse.currInstruction) != null) { // while there is an instruction
                symbol = parse.symbol();

                // if it's an a instruction
                if (Objects.equals(parse.instructionType(), Parser.A_INSTRUCTION)) {
                    try {
                        //address that is not a symbol or variable
                        A = Integer.parseInt(symbol);
                    } catch (NumberFormatException e) {
                        // symbol or variable
                        if (!symbolTable.contains(symbol)) { symbolTable.addEntry(symbol, n++); }
                        A = symbolTable.getAddress(symbol);
                    }
                    writer.write(createAAddress(A) + "\n");
                }
                if (Objects.equals(parse.instructionType(), Parser.C_INSTRUCTION)) {
                    C = "111";
                    C += code.comp(parse.comp());
                    C += code.dest(parse.dest());
                    C += code.jump(parse.jump());
                    writer.write(C + "\n");
                }
                parse.advance();
            }
            parse.close();
        } catch (IOException e) {
            System.err.println("Failed to process file : " + e.getMessage()); }
    }

    public static String createAAddress(int number) {
        String binaryString = Integer.toBinaryString(number);
        // Calculate the number of zeros needed for padding
        int paddingLength = 16 - binaryString.length();
        StringBuilder padding = new StringBuilder();
        padding.append("0".repeat(Math.max(0, paddingLength)));
        // Concatenate the padding and binary string
        return padding + binaryString;
    }

}
