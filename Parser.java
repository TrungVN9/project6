// Reads and parses an instruction

import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileReader;
import java.util.List;
import java.util.ArrayList;

public class Parser {
    // Routines
    // Initialize
    private final List<String> instructions = new ArrayList<>();
    // point to current instruction
    private int index = -1; 
    // current raw instruction string
    private String current = "";
    
    // 3 instructions Types
    public enum InstructionType{
        // @value: value is a decimal literal or a symbol
        A_INSTRUCTION,
        // dest=comp; jump or any fields 
        C_INSTRUCTION,
        // SYMBOL label 
        L_INSTRUCTION
    }
    // Constructor/ initializer: Creates a Parser and opens the source file
    public Parser(String sourceFile) throws IOException{
        try (BufferedReader bufferRead = new BufferedReader(
                                            new FileReader(sourceFile)
                                            )){
            String line;
            while ((line = bufferRead.readLine()) != null){
                int commentIndex = line.indexOf("//");
                if (commentIndex >= 0){
                    line = line.substring(0, commentIndex);
                }
                line = line.trim();
                if (!line.isEmpty()){
                    instructions.add(line);
                }
            }
        }
    }

    // Getting the current instruction:
    /* Returns true if there are more lines to process */
    public boolean hasMoreLines(){
        return index < instructions.size() - 1;
    }

    // Gets the next instruction. Execute when hasMorelines is true
    public void advance(){
        current = instructions.get(++index);
    }

    // Rewinds parser to the beginning for second pas
    public void reset(){
        index = -1;
        current = "";
    }
    // Get symbol
    public String symbol() {
        switch (instructionType()) {
            case A_INSTRUCTION: return current.substring(1);            // strip '@'
            case L_INSTRUCTION: return current.substring(1, current.length() - 1); // strip '(' and ')'
            default: throw new IllegalStateException(
                "symbol() called on a C-instruction: '" + current + "'");
        }
    }
    // Returns type of current instruction
    public InstructionType instructionType(){
        if (current.startsWith("@"))
            return InstructionType.A_INSTRUCTION;
        
        if (current.startsWith("("))
            return InstructionType.L_INSTRUCTION;
        
        return InstructionType.C_INSTRUCTION;
    }
    //Returns the instruction’s dest field (string)
    public String dest(){
        assertCInstructionType("dest");
        int matchIndex= current.indexOf("=");
        return (matchIndex >= 0) ? current.substring(0, matchIndex).trim() : "";
    }

    //Returns the instruction’s comp field (string)
    public String comp(){
        assertCInstructionType("comp");
        String s = current;
        int matchIndex = s.indexOf("=");
        if (matchIndex >= 0)
            s = s.substring(matchIndex + 1);
        
        // Remove ;jump suffix 
        int sourceIndex = s.indexOf(';');
        if (sourceIndex >=0){
            s = s.substring(0, sourceIndex);
        }
        return s.trim();
    }

    //Returns the instruction’s jump field (string)
    public String jump(){
        assertCInstructionType("jump");
        int sourceIndex = current.indexOf(";");
        return (sourceIndex >= 0) ? current.substring(sourceIndex + 1).trim(): "";
    }
    // Helper
    public void assertCInstructionType(String field){
        if (instructionType() != InstructionType.C_INSTRUCTION){
            throw new IllegalStateException(
                field + " non C-instruction at " + current
            );
        }
    }
}
