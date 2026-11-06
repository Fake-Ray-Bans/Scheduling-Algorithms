
package minios;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TraceParser {

    public static List<Process> parseWorkload(String filePath)
            throws IOException {

        List<Process> processes = new ArrayList<>();

        try (BufferedReader br =
                     new BufferedReader(new FileReader(filePath))) {

            String line;
            int currentPid = -1;
            int currentArrival = -1;
            int currentSize = -1;

            List<Instruction> currentInstructions = new ArrayList<>();

            while ((line = br.readLine()) != null) {

                line = line.trim();

                if (line.isEmpty()) {
                    if (currentPid != -1) {
                        processes.add(new Process(
                                currentPid,
                                currentArrival,
                                currentSize,
                                currentInstructions
                        ));

                        currentPid = -1;
                        currentArrival = -1;
                        currentSize = -1;
                        currentInstructions = new ArrayList<>();
                    }
                    continue;
                }

                String[] parts = line.split("\\s+");

                if (parts[0].equalsIgnoreCase("PROCESS")) {

                    if (currentPid != -1) {
                        processes.add(new Process(
                                currentPid,
                                currentArrival,
                                currentSize,
                                currentInstructions
                        ));
                    }

                    currentPid = Integer.parseInt(parts[1]);
                    currentArrival = Integer.parseInt(parts[2]);
                    currentSize = Integer.parseInt(parts[3]);

                    currentInstructions = new ArrayList<>();

                } else {

                    Instruction.OpType type =
                            Instruction.OpType.valueOf(
                                    parts[0].toUpperCase()
                            );

                    switch (type) {

                        case COMPUTE:
                        case IO:
                            currentInstructions.add(
                                    new Instruction(
                                            type,
                                            Integer.parseInt(parts[1])
                                    )
                            );
                            break;

                        case LOAD:
                            currentInstructions.add(
                                    new Instruction(
                                            type,
                                            Integer.parseInt(parts[1]),
                                            0
                                    )
                            );
                            break;

                        case STORE:
                            currentInstructions.add(
                                    new Instruction(
                                            type,
                                            Integer.parseInt(parts[1]),
                                            Integer.parseInt(parts[2])
                                    )
                            );
                            break;
                    }
                }
            }

            // Add final process if file doesn't end with blank line.
            if (currentPid != -1) {
                processes.add(new Process(
                        currentPid,
                        currentArrival,
                        currentSize,
                        currentInstructions
                ));
            }
        }

        return processes;
    }
}
