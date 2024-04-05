
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class CodeWriter {

    private final BufferedWriter destFile; // destination out file
    private String fileName = "";
    private int labelCount = 0;
    /**
     * Constructs a CodeWriter instance with the specified file path.
     *
     * @param path The file path where the CodeWriter will create and write to a file.
     * @throws RuntimeException If an IOException occurs during file handling.
     */
    public CodeWriter(String path) {
        try {
            this.destFile = new BufferedWriter(new FileWriter(path));
            writeInit();
        } catch (IOException e) {throw new RuntimeException(e);}
    }

    /**
     * This method extracts the file name without the extension
     * @param file_name The full file name, including the file extension.
     */
    public void setFileName (String file_name) {
        this.fileName = file_name.substring(0, file_name.lastIndexOf('.'));
    }

    /**
     * This method gets an arithmetic-logical command and writes this command to the output file, in an assembly code.
     * @param command The arithmetic-logical command to write
     * @throws RuntimeException If failed to write to the output file.
     * @throws IllegalArgumentException If the given command is invalid arithmetic-logical command
     */
    public void writeArithmetic(String command){
        String cmd;
        String arithmeticTemplate =  "\n@SP\nAM=M-1\nD=M\nA=A-1";
        String logicTemplate = "\n@SP\nAM=M-1\nD=M\nA=A-1\nD=M-D\n@FALSE_%s" +
                "\nD;%s\n@SP\nA=M-1\nM=-1\n@CONTINUE_%s\n0;JMP\n(FALSE_%s)\n@SP\nA=M-1\nM=0\n(CONTINUE_%s)";
        switch (command) {
            // add command
            case "add":
                cmd = arithmeticTemplate + "\nM=M+D";
                break;
            // sub command
            case "sub":
                cmd = arithmeticTemplate + "\nM=M-D";
                break;
            // neg command
            case "neg":
                cmd = "\nD=0\n@SP\nA=M-1\nM=D-M";
                break;
            // eq command
            case "eq":
                cmd = String.format(logicTemplate, labelCount, "JNE", labelCount, labelCount, labelCount);
                labelCount++;
                break;
            // gt command
            case "gt":
                cmd = String.format(logicTemplate, labelCount, "JLE", labelCount, labelCount, labelCount); //we search for a false condition
                labelCount++;
                break;
            // lt command
            case "lt":
                cmd = String.format(logicTemplate, labelCount, "JGE", labelCount, labelCount, labelCount); //we search for a false condition
                labelCount++;
                break;
            // and command
            case "and":
                cmd = arithmeticTemplate + "\nM=M&D";
                break;
            // and command
            case "or":
                cmd = arithmeticTemplate + "\nM=M|D";
                break;
            // not command
            case "not":
                cmd = "\n@SP\nA=M-1\nM=!M";
                break;
            default:
                throw new IllegalArgumentException("Not a valid Arithmetic command");
        }
        try {
            this.destFile.write(cmd);
        } catch (IOException e) {throw new RuntimeException(e);}
    }

    /**
     * This method gets push or pop command and writes this command to the output file, in an assembly code.
     * @param command The push or pop command to write
     * @param segment The segment type
     * @param index The command index
     * @throws RuntimeException If failed to write to the output file.
     * @throws IllegalArgumentException if the given segment is invalid
     */
    public void writePushPop(String command, String segment, int index) {
        String cmd = null;
        String LATTPushTemplate = "\n@%s\nD=M\n@%s\nA=D+A\nD=M";
        String LATTPopTemplate = "\n@%s\nD=M\n@%s\nD=D+A";
        String pushTemplate = "\n@SP\nA=M\nM=D\n@SP\nM=M+1";
        String popTemplate = "\n@R13\nM=D\n@SP\nAM=M-1\nD=M\n@R13\nA=M\nM=D";
        String fileNameSymbol = this.fileName + "." + index;
        // push command
        if (command.equals(Parser.C_PUSH)) {
            // check which segment is the 'segment' argument
            switch (segment) {
                case "constant":
                    cmd = "\n@" + index + "\nD=A" + pushTemplate;
                    break;
                case "local":
                    cmd = String.format(LATTPushTemplate, "LCL", index) + pushTemplate;
                    break;
                case "argument":
                    cmd = String.format(LATTPushTemplate, "ARG", index) + pushTemplate;
                    break;
                case "this":
                    cmd = String.format(LATTPushTemplate, "THIS", index) + pushTemplate;
                    break;
                case "that":
                    cmd = String.format(LATTPushTemplate, "THAT", index) + pushTemplate;
                    break;
                // f"@{names[segment]}\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n"
                case "static":
                    cmd = "\n@" + fileNameSymbol + "\nD=M" + pushTemplate;
                    break;
                case "temp":
                    cmd = "\n@R" + (index + 5) + "\nD=M" + pushTemplate;
                    break;
                case "pointer":
                    if (index == 0) cmd = "\n@THIS\nD=M" + pushTemplate;
                    else if (index == 1) cmd = "\n@THAT\nD=M" + pushTemplate;
                    break;
            }
            // pop command
        } else if (command.equals(Parser.C_POP)) {
            // check which segment is the 'segment' argument
            switch (segment) {
                case "local":
                    cmd = String.format(LATTPopTemplate, "LCL", index) + popTemplate;
                    break;
                case "argument":
                    cmd = String.format(LATTPopTemplate, "ARG", index) + popTemplate;
                    break;
                case "this":
                    cmd = String.format(LATTPopTemplate, "THIS", index) + popTemplate;
                    break;
                case "that":
                    cmd = String.format(LATTPopTemplate, "THAT", index) + popTemplate;
                    break;
                case "static":
                    cmd = "\n@SP\nAM=M-1\nD=M\n@" + fileNameSymbol + "\nM=D";
                    break;
                case "temp":
                    cmd = "\n@SP\nAM=M-1\nD=M\n@R" + (index + 5) +"\nM=D";
                    break;
                case "pointer":
                    if (index == 0) cmd = "\n@THIS\nD=A" + popTemplate;
                    else if (index == 1) cmd = "\n@THAT\nD=A" + popTemplate;
                    break;
                default:
                    throw new IllegalArgumentException("Not a valid command");
            }
        }
        try {
            if (cmd != null) {
                this.destFile.write(cmd);
            }
        } catch (IOException e) {throw new RuntimeException(e);}
    }

    /**
     * Writes the initialization assembly code to the output file.
     * @throws RuntimeException If failed to write to the output file.
     */
    public void writeInit(){
        try {
            this.destFile.write("\n@256\nD=A\n@SP\nM=D");
        } catch (IOException e) {throw new RuntimeException(e);}
        writeCall("Sys.init", 0);
    }

    /**
     * Writes assembly code to the output file, that affects the label command
     * @param label The label to write
     * @throws RuntimeException If failed to write to the output file.
     */
    public void writeLabel (String label) {
        try {
            this.destFile.write("\n(" + label + ")");
        } catch (IOException e) {throw new RuntimeException(e);}
    }

    /**
     * Writes assembly code to the output file, that affects the goto command
     * @param label The label to goto
     * @throws RuntimeException If failed to write to the output file.
     */
    public void writeGoto (String label) {
        try {
            this.destFile.write("\n@" + label + "\n0;JMP");
        } catch (IOException e) {throw new RuntimeException(e);}
    }

    /**
     * Writes assembly code to the output file, that affects the function command
     * @param label the label to goto if the condition is true
     * @throws RuntimeException If failed to write to the output file.
     */
    public void writeIf (String label) {
        try {
            this.destFile.write("\n@SP\nAM=M-1\nD=M"); // let cond = pop
            this.destFile.write("\n@" + label + "\nD;JNE"); // if D!=0 goto (meaning true)
        } catch (IOException e) {throw new RuntimeException(e);}
    }

    /**
     * Writes assembly code to the output file, that affects the function command
     * @param functionName the function name
     * @param nVars the number of variables that the function functionName gets as input
     * @throws RuntimeException If failed to write to the output file.
     */
    public void writeFunction (String functionName, int nVars) {
        writeLabel(functionName);
        for (int i=0; i < nVars; i++){
            try {
                this.destFile.write("\n@LCL\nD=M\n@" + i + "\nA=D+A\nM=0\n@SP\nM=M+1");
            } catch (IOException e) {throw new RuntimeException(e);}
        }
    }

    /**
     * Writes assembly code to the output file, that affects the call command
     * @param functionName the function name
     * @param nVars the number of variables that the function functionName gets as input
     * @throws RuntimeException If failed to write to the output file.
     */
    public void writeCall (String functionName, int nVars) {
        try {
            // save the return address
            this.destFile.write("\n@" + functionName + "$ret." + labelCount + "\nD=A\n@SP\nA=M\nM=D\n@SP\nM=M+1");
            // save the caller's segment pointers
            String framesTemplate = "\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1";
            this.destFile.write("\n@LCL" + framesTemplate);
            this.destFile.write("\n@ARG" + framesTemplate);
            this.destFile.write("\n@THIS" + framesTemplate);
            this.destFile.write("\n@THAT" + framesTemplate);
            // reposition ARG
            this.destFile.write("\n@SP\nD=M\n@5\nD=D-A\n@" + nVars + "\nD=D-A\n@ARG\nM=D");
            // reposition LCL
            this.destFile.write("\n@SP\nD=M\n@LCL\nM=D");
        } catch (IOException e) {throw new RuntimeException(e);}
        writeGoto(functionName);
        writeLabel(functionName + "$ret." + labelCount);
        labelCount++;
    }

    /**
     * Writes assembly code to the output file, that affects the return command
     * @throws RuntimeException If failed to write to the output file.
     */
    public void writeReturn () {
        // endFrame = R13
        //retAddr = R14
        String framesTemplate = "\n@R15\nAM=M-1\nD=M\n@%s\nM=D";
        try {
            // endFrame = LCL
            this.destFile.write("\n@LCL\nD=M\n@R15\nM=D");
            // retAddr = *(endFrame-5)
            this.destFile.write("\n@R15\nD=M\n@5\nA=D-A\nD=M\n@R14\nM=D");
            // *ARG = pop()
            this.destFile.write("\n@SP\nAM=M-1\nD=M\n@ARG\nA=M\nM=D");
            // SP = ARG + 1
            this.destFile.write("\n@ARG\nD=M+1\n@SP\nM=D");
            // recycle the memory used by the callee
            this.destFile.write(String.format(framesTemplate,"THAT"));
            this.destFile.write(String.format(framesTemplate,"THIS"));
            this.destFile.write(String.format(framesTemplate,"ARG"));
            this.destFile.write(String.format(framesTemplate,"LCL"));
            // goto retAddr
            this.destFile.write("\n@R14\nA=M\n0;JMP");
        } catch (IOException e) {throw new RuntimeException(e);}
    }

    /**
     * Closes the file
     * @throws RuntimeException If failed to close the file.
     */
    public void close(){
        try {
            destFile.close();
        } catch (IOException e) {throw new RuntimeException(e);}
    }
}

