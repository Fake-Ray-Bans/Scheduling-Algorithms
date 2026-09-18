package minios;

import java.util.*;

public class RR implements SchedulingAlgo {

    private static final int TIME_QUANT = 2;
    private final HashMap<Process, Integer> processDurations = new HashMap<>();

    @Override
    public void addProcess(List<Process> readyQueue, Process p) {
        readyQueue.add(p);
    }

    @Override
    public void onProcessTick(List<Process> readyQueue, Process p) {
        // Increment process durations by 1
        processDurations.put(p, processDurations.getOrDefault(p, 0) + 1);
        System.out.println("Process " + p.pid + " tick #" + processDurations.get(p));

        // Fix edge case where IO wait is not entered until next time
        // Process is running
        Instruction inst = p.getCurrentInstruction();
        if (inst == null || inst.remainingTicks <= 0) return;

        // If enough cpu cycles have done to reach time quant, set the process to switch out and add to the back of the queue
        if (processDurations.get(p) >= TIME_QUANT) {
            processDurations.put(p, 0);

            // Tell the kernel to remove the process from the CPU
            p.state = Process.State.SWITCH;

            System.out.println("Process " + p.pid + " swapped out");

            // Add process to last in queue
            readyQueue.addLast(p);
        }
    }

    @Override
    public Process selectNextProcess(List<Process> readyQueue) {
        if (readyQueue.isEmpty()) {
            return null;
        }

        // Queue should be ordered how we want it
        return readyQueue.removeFirst();
    }
}