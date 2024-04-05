import java.io.File;

public class Main {
    /**
     * Main method to start the VMTranslator.
     * @param args Command-line arguments. Expects a single argument: path to .vm file or directory.
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: VMTranslator path-to .vm file or directory");
            System.exit(1);
        }
        String path = args[0];
        File inputFile = new File(path);
        // if input is a file and ends-with .vm -> process it
        if (inputFile.isFile() && inputFile.getName().endsWith(".vm")) {
            processFile(inputFile);
        } else if (inputFile.isDirectory()) {
            // if input is a directory -> process all .vm files in the directory
            processDir(inputFile);
        } else {
            // not a file and not a directory - invalid input
            System.err.println("Invalid input: " + args[0]);
            System.exit(1);
        }
    }

    /**
     * Process a single .vm file
     * @param vmFile The .vm file to process
     */
    private static void processFile(File vmFile){
        String vmPath = vmFile.getAbsolutePath();
        String asmPath = vmPath.substring(0, vmPath.lastIndexOf(".")) + ".asm";
        Parser parse = new Parser(vmPath);
        CodeWriter writer = new CodeWriter(asmPath);

        getOutput(parse, writer);
        writer.close();
    }

    /**
     * Process a directory containing .vm files.
     * Appends the output asm files to one vmDir.asm file.
     *
     * @param vmDir The directory containing .vm files.
     */
    private static void processDir(File vmDir) {
        String asmPath = vmDir.getAbsolutePath() + File.separator + vmDir.getName() + ".asm";
        CodeWriter writer = new CodeWriter(asmPath);

        File[] files = vmDir.listFiles((dir, name) -> name.endsWith(".vm"));
        if (files != null) {
            for (File file : files) {
                writer.setFileName(file.getName()); //set file name
                Parser parse = new Parser(file.getAbsolutePath());
                getOutput(parse, writer);
            }
        }
        writer.close();
    }

    /**
     * Generates the output assembly code for a given VM file.
     *
     * @param parse  The Parser object
     * @param writer The CodeWriter object
     */
    private static void getOutput (Parser parse, CodeWriter writer) {
        //Initialize
        String commandType;

        //Iterate File
        parse.advance();
        while ((parse.currCommand) != null) { // while there is an instruction
            commandType = parse.commandType();
            switch (commandType) {
                case Parser.C_ARITHMETIC:
                    writer.writeArithmetic(parse.arg1());
                    break;
                case Parser.C_POP, Parser.C_PUSH:
                    writer.writePushPop(commandType, parse.arg1(), parse.arg2());
                    break;
                case Parser.C_LABEL:
                    writer.writeLabel(parse.arg1());
                    break;
                case Parser.C_GOTO:
                    writer.writeGoto(parse.arg1());
                    break;
                case Parser.C_IF:
                    writer.writeIf(parse.arg1());
                    break;
                case Parser.C_FUNCTION:
                    writer.writeFunction(parse.arg1(), parse.arg2());
                    break;
                case Parser.C_RETURN:
                    writer.writeReturn();
                    break;
                case Parser.C_CALL:
                    writer.writeCall(parse.arg1(), parse.arg2());
                    break;
            }
            parse.advance();
        }
        parse.close();
    }

}

