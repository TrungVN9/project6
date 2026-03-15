// Drives the process
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class HackerAssembly {
    private final String sourceFile;
    private final String destFile;
    private final Parser parser;
    private final SymbolTable symbolTable;
    private final List<String> binaryLines = new ArrayList<>();
 
    public HackerAssembly(String sourceFile) throws IOException {
        this.sourceFile = sourceFile;
        this.destFile = toHackPath(sourceFile);
        this.parser = new Parser(sourceFile);
        this.symbolTable = new SymbolTable();
    }

    public String assemble() throws IOException {
        firstPass();
        secondPass();
        writeOutput();
        return destFile;
    }
    
    private void firstPass() {
        int romAddress = 0;
 
        while (parser.hasMoreLines()) {
            parser.advance();
 
            switch (parser.instructionType()) {
                case L_INSTRUCTION -> {
                    String symbol = parser.symbol();
                    if (!symbolTable.contains(symbol)) {
                        symbolTable.addEntry(symbol, romAddress);
                    }
                }
                // A and C instructions advance the ROM counter
                default -> romAddress++;
            }
        }
    }

    private void secondPass() {
        parser.reset();
 
        while (parser.hasMoreLines()) {
            parser.advance();
 
            switch (parser.instructionType()) {
                case L_INSTRUCTION -> { /* labels produce no output */ }
                case A_INSTRUCTION -> binaryLines.add(translateA());
                case C_INSTRUCTION -> binaryLines.add(translateC());
            }
        }
    }
    
    private String translateA() {
        String raw = parser.symbol();
        int value;
 
        if (raw.matches("\\d+")) {
            // Numeric literal
            value = Integer.parseInt(raw);
        } else {
            // Symbol: look up or allocate as a new variable
            if (symbolTable.contains(raw)) {
                value = symbolTable.getAddress(raw);
            } else {
                value = symbolTable.allocateVariable(raw);
            }
        }
 
        return "0" + Code.toBinary15(value);
    }
    
    private String translateC() {
        String compBits = Code.comp(parser.comp());   // 7 bits (a + cccccc)
        String destBits = Code.dest(parser.dest());   // 3 bits
        String jumpBits = Code.jump(parser.jump());   // 3 bits
 
        return "111" + compBits + destBits + jumpBits;
    }
    
    private void writeOutput() throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(destFile))) {
            for (String line : binaryLines) {
                bw.write(line);
                bw.newLine();
            }
        }
        System.out.printf("[OK] Assembled %d instructions → %s%n",
                          binaryLines.size(), destFile);
    }
    
    private static String toHackPath(String asmPath) {
        Path p = Paths.get(asmPath);
        String filename = p.getFileName().toString();
        int dot = filename.lastIndexOf('.');
        String stem = (dot >= 0) ? filename.substring(0, dot) : filename;
        Path parent = p.getParent();
        return (parent != null ? parent.resolve(stem + ".hack")
                               : Paths.get(stem + ".hack")).toString();
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java HackerAssembly <source.asm>");
            System.exit(1);
        }
 
        try {
            HackerAssembly assembler = new HackerAssembly(args[0]);
            assembler.assemble();
        } catch (IOException e) {
            System.err.println("[ERROR] I/O error: " + e.getMessage());
            System.exit(1);
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.err.println("[ERROR] Assembly failed: " + e.getMessage());
            System.exit(1);
        }
    }
}
