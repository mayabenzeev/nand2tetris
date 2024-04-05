import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Parser {
    // Define constants
    public static final String C_ARITHMETIC = "C_ARITHMETIC";
    public static final String C_PUSH = "C_PUSH";
    public static final String C_POP = "C_POP";
    public static final String C_LABEL = "C_LABEL";
    public static final String C_GOTO = "C_GOTO";
    public static final String C_IF = "C_IF";
    public static final String C_FUNCTION = "C_FUNCTION";
    public static final String C_RETURN = "C_RETURN";
    public static final String C_CALL = "C_CALL";

    private BufferedReader srcFile;
    public String currCommand;

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
        currCommand = line;
    }

    public String commandType() {
        List<String> keywords = Arrays.asList("add", "sub", "neg", "eq", "gt", "lt", "and", "or", "not");
        if (keywords.stream().anyMatch(keyword -> currCommand.contains(keyword))){ return C_ARITHMETIC; }
        else if (currCommand.contains("push")){ return C_PUSH; }
        else if (currCommand.contains("pop")){ return C_POP; }
        else if (currCommand.contains("return")){ return C_RETURN; }
        else if (currCommand.contains("label")){ return C_LABEL; }
        else if (currCommand.contains("if")){ return C_IF; }
        else if (currCommand.contains("goto")){ return C_GOTO; }
        else if (currCommand.contains("function")){ return C_FUNCTION; }
        else if (currCommand.contains("call")){ return C_CALL; }

        else return null;
    }

    public String arg1(){
        //returns null in case of C_RETURN.arg1()
        switch (commandType()) {
            case C_ARITHMETIC:
                return currCommand;
            case C_POP, C_PUSH, C_LABEL, C_IF, C_GOTO, C_FUNCTION, C_CALL:
                return currCommand.split(" ")[1];
            default:
                return null; // C_RETURN
        }
    }
    public int arg2() {
        // returns -1 in case that it's not C_PUSH, C_POP, C_FUNCTION, C_CALL
        String currentCommandType = commandType();
        if (currentCommandType.equals(C_POP) || currentCommandType.equals(C_PUSH)
                || currentCommandType.equals(C_FUNCTION) || currentCommandType.equals(C_CALL)) {
            return Integer.parseInt(currCommand.split(" ")[2]);
        }
        return -1; // not a valid command
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

