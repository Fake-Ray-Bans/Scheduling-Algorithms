package minios;

import java.util.HashMap;
import java.util.List;

public class SJF implements SchedulingAlgo {

    @Override
    public void addProcess(List<Process> readyQueue, Process p) {
        readyQueue.add(p);
    }

    @Override
    public void onProcessTick(List<Process> readyQueue, Process p) {}

    @Override
    public Process selectNextProcess(List<Process> readyQueue) {
        if (readyQueue.isEmpty()) {
            return null;
        }

        // Find the process with the shortest cpu burst time
        int shortestIndex = 0;

        for (int i = 1; i < readyQueue.size(); i++) {
            // Prevent from counting io bursts
            if (readyQueue.get(i).getCurrentInstruction().type != Instruction.OpType.CPU)
                continue;
            if (readyQueue.get(i).getCurrentInstruction().duration < readyQueue.get(shortestIndex).getCurrentInstruction().duration) {
                shortestIndex = i;
            }
        }

        // Return shortest job
        return readyQueue.remove(shortestIndex);
    }
}