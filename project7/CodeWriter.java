
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CodeWriter {

    private final BufferedWriter destFile;
    private final Path outPath;
    private int labelCount = 0;
    public CodeWriter(String path) {
        this.outPath = Paths.get(path);
        try {
            this.destFile = new BufferedWriter(new FileWriter(path));
        } catch (IOException e) {throw new RuntimeException(e);}
    }
    public void writeArithmetic(String command){
        String cmd;
        String arithmeticTemplate =  "\n@SP\nAM=M-1\nD=M\nA=A-1";
        String logicTemplate = "\n@SP\nAM=M-1\nD=M\nA=A-1\nD=M-D\n@FALSE_%s" +
                "\nD;%s\n@SP\nA=M-1\nM=-1\n@CONTINUE_%s\n0;JMP\n(FALSE_%s)\n@SP\nA=M-1\nM=0\n(CONTINUE_%s)";
        switch (command) {
            case "add":
                cmd = arithmeticTemplate + "\nM=M+D";
                break;
            case "sub":
                cmd = arithmeticTemplate + "\nM=M-D";
                break;
            case "neg":
                cmd = "\nD=0\n@SP\nA=M-1\nM=D-M";
                break;
            case "eq":
                cmd = String.format(logicTemplate, labelCount, "JNE", labelCount, labelCount, labelCount);
                labelCount++;
                break;
            case "gt":
                cmd = String.format(logicTemplate, labelCount, "JLE", labelCount, labelCount, labelCount); //we search for a false condition
                labelCount++;
                break;
            case "lt":
                cmd = String.format(logicTemplate, labelCount, "JGE", labelCount, labelCount, labelCount); //we search for a false condition
                labelCount++;
                break;
            case "and":
                cmd = arithmeticTemplate + "\nM=M&D";
                break;
            case "or":
                cmd = arithmeticTemplate + "\nM=M|D";
                break;
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

    public void writePushPop(String command, String segment, int index) {
        String cmd = null;
        String LATTPushTemplate = "\n@%s\nD=M\n@%s\nA=D+A\nD=M";
        String LATTPopTemplate = "\n@%s\nD=M\n@%s\nD=D+A";
        String pushTemplate = "\n@SP\nA=M\nM=D\n@SP\nM=M+1";
        String popTemplate = "\n@R13\nM=D\n@SP\nAM=M-1\nD=M\n@R13\nA=M\nM=D";
        String fileNameSymbol = this.outPath.getFileName().toString() + "." + index;
        if (command.equals(Parser.C_PUSH)) {
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
                case "static":
                    cmd = "\n@" + fileNameSymbol + "\nD=M" + pushTemplate;
                    break;
                case "temp":
                    cmd = String.format(LATTPushTemplate, "R5", index + 5) + pushTemplate;
                    break;
                case "pointer":
                    if (index == 0) cmd = "\n@THIS\nD=M" + pushTemplate;
                    else if (index == 1) cmd = "\n@THAT\nD=M" + pushTemplate;
                    break;
            }
        } else if (command.equals(Parser.C_POP)) {
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
                    cmd = String.format(LATTPopTemplate, "R5", index + 5) + popTemplate;
                    break;
                case "pointer":
                    if (index == 0) cmd = "\n@THIS\nD=A" + popTemplate;
                    else if (index == 1) cmd = "\n@THAT\nD=A" + popTemplate;
                    break;
                default:
                    throw new IllegalArgumentException("Not a valid Arithmetic command");
            }
        }
        try {
            if (cmd != null) {
                this.destFile.write(cmd);
            }
        } catch (IOException e) {throw new RuntimeException(e);}
    }

    public void close(){
        try {
            destFile.close();
        } catch (IOException e) {throw new RuntimeException(e);}
    }
}

