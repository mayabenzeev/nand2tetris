public class Code {
    public String dest(String dstTxt) {
        if (dstTxt == null) return "000";
        String A = dstTxt.contains("A") ? "1" : "0";
        String D = dstTxt.contains("D") ? "1" : "0";
        String M = dstTxt.contains("M") ? "1" : "0";
        return A + D + M;
    }

    public String comp(String compTxt) {
        String a = compTxt.contains("M") ? "1" : "0";
        switch (compTxt) {
            case "0":
                return a + "101010";
            case "1":
                return a + "111111";
            case "-1":
                return a + "111010";
            case "D":
                return a + "001100";
            case "A":
            case "M":
                return a + "110000";
            case "!D":
                return a + "001101";
            case "!A":
            case "!M":
                return a + "110001";
            case "-D":
                return a + "001111";
            case "-A":
            case "-M":
                return a + "110011";
            case "D+1":
                return a + "011111";
            case "A+1":
            case "M+1":
                return a + "110111";
            case "D-1":
                return a + "001110";
            case "A-1":
            case "M-1":
                return a + "110010";
            case "D+A":
            case "D+M":
                return a + "000010";
            case "D-A":
            case "D-M":
                return a + "010011";
            case "A-D":
            case "M-D":
                return a + "000111";
            case "D&A":
            case "D&M":
                return a + "000000";
            case "D|A":
            case "D|M":
                return a + "010101";
            default:
                throw new IllegalArgumentException("Invalid instruction: " + compTxt);
        }
    }

    public String jump(String jumpTxt) {
        if (jumpTxt == null) return "000";
        switch (jumpTxt) {
            case "JGT":
                return "001";
            case "JEQ":
                return "010";
            case "JGE":
                return "011";
            case "JLT":
                return "100";
            case "JNE":
                return "101";
            case "JLE":
                return "110";
            case "JMP":
                return "111";
            default:
                throw new IllegalArgumentException("Invalid instruction: " + jumpTxt);
        }
    }
}
