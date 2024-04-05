import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.RecursiveTask;

public class Parser {
    // Define constants
    public static final String A_INSTRUCTION = "A_INSTRUCTION";
    public static final String C_INSTRUCTION = "C_INSTRUCTION";
    public static final String L_INSTRUCTION = "L_INSTRUCTION";

    private BufferedReader srcFile;
    public String currInstruction;
    public int numInstruction=-1;

    public Parser(String path){
        try{
            this.srcFile = new BufferedReader(new FileReader(path));
        } catch (FileNotFoundException e) {System.out.println("File not found");}
    }

    private boolean hasMoreLines() {
        try {
            return srcFile.ready();
        }
        catch (IOException e) {System.err.println(e);}
        return false;
    }

    public void advance() {
        String line = null;
        try {
            while (hasMoreLines()) {
                line = stripWhiteSpaces(srcFile.readLine());
                if (!line.isEmpty()) break; // Break the loop if line is not empty
            }
        } catch (IOException e) {System.err.println(e);}
        if (line != null && !line.startsWith("(")) numInstruction++;
        currInstruction = line;
    }

    public String instructionType() {
        if (currInstruction.contains("@")){ return A_INSTRUCTION; }
        else if (currInstruction.contains("=") || currInstruction.contains(";")){ return C_INSTRUCTION; }
        else if (currInstruction.contains("(")){ return L_INSTRUCTION; }
        else return null;
    }

    public String symbol(){
        return switch (instructionType()) {
            case A_INSTRUCTION -> currInstruction.replace("@", "");
            case L_INSTRUCTION -> currInstruction.replaceAll("\\((.*?)\\)", "$1");
            default -> null; // not an a instruction
        };
    }
    public String dest() {
        if (instructionType()== C_INSTRUCTION) {
            if (currInstruction.contains("=")) {return currInstruction.split("=")[0];}
        }
        return null;// not a c instruction
    }
    public String comp() {
        if (instructionType()== C_INSTRUCTION) {
                if (currInstruction.contains("=")) {return currInstruction.split("=")[1].split(";")[0];}
                else {return currInstruction.split(";")[0];} // looks like D;JMP
        }
        return null; //not a c instruction
    }
    public String jump() {
        if (instructionType()== C_INSTRUCTION) {
                if (currInstruction.contains(";")) {return currInstruction.split(";")[1];}
                else {return null;} // looks like D=1
        }
        return null; //not a c instruction
    }

    private String stripWhiteSpaces(String line) {
        if (line == null) return "";
        return line.split("//")[0].trim();
    }
    public void close(){
        try {
            srcFile.close();
        } catch (IOException e) {throw new RuntimeException(e);}
    }
}
