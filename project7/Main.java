import java.io.File;

public class Main {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: VMTranslator path-to .vm file or directory");
            System.exit(1);
        }

        String path = args[0];
        File inputFile = new File(path);
        // if input is a file and ends-with .vm -> process it
        if (inputFile.isFile() && inputFile.getName().endsWith(".vm")) {
            processFile(path);
        } else if (inputFile.isDirectory()) {
            // if input is a directory -> process all .vm files in the directory
            processFile(inputFile);
        } else {
            // not a file and not a directory - invalid input
            System.err.println("Invalid input: " + args[0]);
            System.exit(1);
        }
    }

    private static void processFile(String vmPath){
        /* call for a single vm file */
        String asmPath = vmPath.substring(0, vmPath.lastIndexOf(".")) + ".asm";
        Parser parse = new Parser(vmPath);
        CodeWriter writer = new CodeWriter(asmPath);
        getOutput(parse, writer);
        writer.close();
    }
    private static void processFile(File vmDir) {
        /*
          overloaded function - call for a directory
          appends the output asm files to one vmDir.asm file
         */
        String asmPath = new File(vmDir.getAbsolutePath(), vmDir.getName() + ".asm").getPath();
        CodeWriter writer = new CodeWriter(asmPath);
        File[] files = vmDir.listFiles((dir, name) -> name.endsWith(".vm"));
        if (files != null) {
            for (File file : files) {
                Parser parse = new Parser(file.getAbsolutePath());
                getOutput(parse, writer);
            }
        }
        writer.close();
    }

    private static void getOutput (Parser parse, CodeWriter writer) {
        //Initialize
        String commandType;

        //Iterate File
        parse.advance();
        while ((parse.currCommand) != null) { // while there is an instruction
            commandType = parse.commandType();
            if(commandType.equals(Parser.C_ARITHMETIC)) {
                writer.writeArithmetic(parse.arg1());
            }
            else if(commandType.equals(Parser.C_POP )|| commandType.equals(Parser.C_PUSH)) {
                writer.writePushPop(commandType, parse.arg1(), parse.arg2());
            }
            parse.advance();
        }
        parse.close();
    }

}

