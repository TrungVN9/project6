// Handles symbols
import java.util.HashMap;
import java.util.Map;
 
public class SymbolTable {

    private final Map<String, Integer> table = new HashMap<>();
    private int nextVariableAddress = 16;   // new variables start at RAM[16]
    
    public SymbolTable() {
        // Virtual registers
        table.put("SP", 0);
        table.put("LCL", 1);
        table.put("ARG", 2);
        table.put("THIS", 3);
        table.put("THAT", 4);
 
        // General-purpose registers R0–R15  (map to RAM[0..15])
        for (int i = 0; i <= 15; i++) {
            table.put("R" + i, i);
        }
 
        // I/O pointers
        table.put("SCREEN", 16384);  // 0x4000
        table.put("KBD",    24576);  // 0x6000
    }

    /** Adds the pair (symbol, address) to the table. */
    public void addEntry(String symbol, int address) {
        table.put(symbol, address);
    }
 
    /** Returns true if the symbol table contains the given symbol. */
    public boolean contains(String symbol) {
        return table.containsKey(symbol);
    }
 
    /**
     * Returns the address associated with the symbol.
     * @throws IllegalArgumentException if the symbol is not found.
     */
    public int getAddress(String symbol) {
        if (!contains(symbol)) {
            throw new IllegalArgumentException(
                "Symbol not found in table: '" + symbol + "'");
        }
        return table.get(symbol);
    }

    public int allocateVariable(String symbol) {
        int address = nextVariableAddress++;
    
        addEntry(symbol, address);
    
        return address;
    }
 
}
