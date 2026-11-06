
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
    public void onProcessTick(List<Process> readyQueue, Process p, int currentTime) {

        // Increment process durations by 1
        processDurations.put(p, processDurations.getOrDefault(p, 0) + 1);

        System.out.println("[Tick " + currentTime + "] Process "
                + p.pid + " tick #" + processDurations.get(p));

        // Check whether the current instruction has completed
        Instruction inst = p.getCurrentInstruction();

        if (inst == null || inst.remainingTicks <= 0) {
            processDurations.put(p, 0);
            return;
        }

        // Check whether the time quantum has expired
        if (processDurations.get(p) >= TIME_QUANT) {

            processDurations.put(p, 0);

            // Tell the kernel to remove the process from the CPU
            p.setProcessToSwitch();

            // Add process to the back of the ready queue
            readyQueue.add(p);

            System.out.println("[Tick " + currentTime + "] Process "
                    + p.pid + " set to switch out.");
        }
    }

    @Override
    public Process selectNextProcess(List<Process> readyQueue) {

        if (readyQueue.isEmpty()) {
            return null;
        }

        return readyQueue.remove(0);
    }
}
