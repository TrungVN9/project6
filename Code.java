import java.util.HashMap;
import java.util.Map;

// Generates binary code
public class Code {
    private static final Map<String, String> DEST_TABLE = new HashMap<>();
    private static final Map<String, String> JUMP_TABLE = new HashMap<>();
    private static final Map<String, String> COMP_TABLE = new HashMap<>();
    // Table
    static {
        // --- dest (3 bits: d1 d2 d3) ---
        DEST_TABLE.put("", "000"); // null
        DEST_TABLE.put("M", "001"); 
        DEST_TABLE.put("D", "010"); 
        DEST_TABLE.put("MD", "011"); 
        DEST_TABLE.put("DM", "011"); 
        DEST_TABLE.put("A", "100"); 
        DEST_TABLE.put("AM", "101"); 
        DEST_TABLE.put("MA", "101");
        DEST_TABLE.put("AD", "110");
        DEST_TABLE.put("DA", "110");
        DEST_TABLE.put("AMD", "111");
        DEST_TABLE.put("ADM", "111");
        DEST_TABLE.put("DAM", "111");
        DEST_TABLE.put("DMA", "111");
        DEST_TABLE.put("MAD", "111");
        DEST_TABLE.put("MDA", "111");
 
        // --- jump (3 bits: j1 j2 j3) ---
        JUMP_TABLE.put("", "000");
        JUMP_TABLE.put("JGT", "001");
        JUMP_TABLE.put("JEQ", "010");
        JUMP_TABLE.put("JGE", "011");
        JUMP_TABLE.put("JLT", "100");
        JUMP_TABLE.put("JNE", "101");
        JUMP_TABLE.put("JLE", "110");
        JUMP_TABLE.put("JMP", "111");
 
        // --- comp (7 bits: a c1 c2 c3 c4 c5 c6) ---
        // a = 0  →  operate on A register
        COMP_TABLE.put("0", "0101010");
        COMP_TABLE.put("1", "0111111");
        COMP_TABLE.put("-1", "0111010");
        COMP_TABLE.put("D", "0001100");
        COMP_TABLE.put("A", "0110000");
        COMP_TABLE.put("!D", "0001101");
        COMP_TABLE.put("!A", "0110001");
        COMP_TABLE.put("-D", "0001111");
        COMP_TABLE.put("-A", "0110011");
        COMP_TABLE.put("D+1", "0011111");
        COMP_TABLE.put("A+1", "0110111");
        COMP_TABLE.put("D-1", "0001110");
        COMP_TABLE.put("A-1", "0110010");
        COMP_TABLE.put("D+A", "0000010");
        COMP_TABLE.put("D-A", "0010011");
        COMP_TABLE.put("A-D", "0000111");
        COMP_TABLE.put("D&A", "0000000");
        COMP_TABLE.put("D|A", "0010101");
        // a = 1  →  operate on M = RAM[A]
        COMP_TABLE.put("M",   "1110000");
        COMP_TABLE.put("!M",  "1110001");
        COMP_TABLE.put("-M",  "1110011");
        COMP_TABLE.put("M+1", "1110111");
        COMP_TABLE.put("M-1", "1110010");
        COMP_TABLE.put("D+M", "1000010");
        COMP_TABLE.put("D-M", "1010011");
        COMP_TABLE.put("M-D", "1000111");
        COMP_TABLE.put("D&M", "1000000");
        COMP_TABLE.put("D|M", "1010101");
    }

    public Code(){}

    // dest(string): Returns the binary representation of the parsed dest field (string)
    public static String dest(String ch) {
        String bits = DEST_TABLE.get(ch == null ? "" : ch);
        if (bits == null) {
            throw new IllegalArgumentException("Unknown dest ch: '" + ch + "'");
        }
        return bits;
    }

    public static String jump(String ch) {
        String bits = JUMP_TABLE.get(ch == null ? "" : ch);
        if (bits == null) {
            throw new IllegalArgumentException("Unknown jump ch: '" + ch + "'");
        }
        return bits;
    }
    
    public static String comp(String ch) {
        String bits = COMP_TABLE.get(ch);
        if (bits == null) {
            throw new IllegalArgumentException("Unknown comp ch: '" + ch + "'");
        }
        return bits;
    }
    
    public static String toBinary15(int value) {
        if (value < 0 || value > 32767) {
            throw new IllegalArgumentException(
                "A-instruction value " + value + " is out of range [0, 32767].");
        }
        return String.format("%15s", Integer.toBinaryString(value)).replace(' ', '0');
    }
 
}
