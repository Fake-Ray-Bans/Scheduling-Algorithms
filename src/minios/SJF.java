package minios;

import java.util.ArrayList;
import java.util.List;

public class SJF implements SchedulingAlgo {
    @Override
    public void addProcess(List<Process> readyQueue, Process p) {
        // FCFS means appending to end of the queue
        readyQueue.add(p);
    }

    @Override
    public Process selectNextProcess(List<Process> readyQueue) {
        if (readyQueue.isEmpty()) {
            return null;
        }

        // Find the process with the shortest burst time
        int shortestIndex = 0;

        System.out.println();
        System.out.println("Choosing Next process, Burst Lengths:");
        System.out.println("Process " + readyQueue.getFirst().pid + ": " + readyQueue.getFirst().getNextPredictedBurst());

        for (int i = 1; i < readyQueue.size(); i++) {
            System.out.println("Process " + readyQueue.get(i).pid + ": " + readyQueue.get(i).getNextPredictedBurst());
            if (readyQueue.get(i).getNextPredictedBurst() < readyQueue.get(shortestIndex).getNextPredictedBurst()) {
                shortestIndex = i;
            }
        }

        System.out.println();

        // Return shortest job
        return readyQueue.remove(shortestIndex);
    }
}