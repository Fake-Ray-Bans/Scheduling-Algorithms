package minios;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SJF implements SchedulingAlgo {

    private Process runningProcess = null;
    private final HashMap<Process, Integer> processDurations = new HashMap<>();
    private final HashMap<Process, Integer> predictedBursts = new HashMap<>();

    @Override
    public void addProcess(List<Process> readyQueue, Process p) {
        readyQueue.add(p);

        // Start off with reasonable value
        predictedBursts.putIfAbsent(p, 2);
    }

    @Override
    public void onProcessTick(List<Process> readyQueue, Process p) {
        processDurations.put(p, processDurations.getOrDefault(p, 0) + 1);
    }

    @Override
    public Process selectNextProcess(List<Process> readyQueue) {
        if (readyQueue.isEmpty()) {
            return null;
        }

        // Set predicted next burst to the previous burst and reset duration back to 0
        if (this.runningProcess != null) {
            this.predictedBursts.put(this.runningProcess, this.processDurations.get(this.runningProcess));
            this.processDurations.put(this.runningProcess, 0);
            System.out.println("Process " + this.runningProcess.pid + " next burst prediction: " + this.predictedBursts.get(this.runningProcess));
        }


        // Find the process with the shortest burst time
        int shortestIndex = 0;

        for (int i = 1; i < readyQueue.size(); i++) {
            if (this.predictedBursts.get(readyQueue.get(i)) < this.predictedBursts.get(readyQueue.get(shortestIndex))) {
                shortestIndex = i;
            }
        }

        // Return shortest job
        this.runningProcess = readyQueue.remove(shortestIndex);
        return this.runningProcess;
    }
}