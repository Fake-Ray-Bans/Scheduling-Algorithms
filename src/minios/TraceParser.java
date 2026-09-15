package minios;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TraceParser {
    public static List<Process> parseWorkload(String filePath) throws IOException {
        List<Process> processes = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            int currentPid = -1;
            int currentArrival = -1;
            List<Instruction> currentInstructions = new ArrayList<>();

            while ((line = br.readLine()) != null) {
                line = line.trim();

                // Blank line signifies the end of a process block
                if (line.isEmpty()) {
                    if (currentPid != -1) {
                        processes.add(new Process(currentPid, currentArrival, currentInstructions));
                        currentPid = -1;
                        currentInstructions = new ArrayList<>(); // Reset for next process
                    }
                    continue;
                }

                if (line.startsWith("PROCESS")) {
                    String[] parts = line.split("\\s+");
                    currentPid = Integer.parseInt(parts[1]);
                    currentArrival = Integer.parseInt(parts[2]);
                } else {
                    // initialize the instruction and add to the process's code block
                    String[] parts = line.split("\\s+");
                    Instruction.OpType type = Instruction.OpType.valueOf(parts[0].toUpperCase());
                    int duration = Integer.parseInt(parts[1]);
                    currentInstructions.add(new Instruction(type, duration));
                }
            }

            // Add the final process (in case the trace doesn't end with a blank line)
            if (currentPid != -1) {
                processes.add(new Process(currentPid, currentArrival, currentInstructions));
            }
        }
        return processes;
    }
}