package minios;

import java.util.List;

public class FCFS implements SchedulingAlgo {
    @Override
    public void addProcess(List<Process> readyQueue, Process p) {
        // FCFS means appending to end of the queue
        readyQueue.add(p);
    }

    @Override
    public Process selectNextProcess(List<Process> readyQueue) {
        if (readyQueue.isEmpty()) {
            return null;
        } else {
            // FCFS means always selecting the process at the front
            // of the queue
            return readyQueue.remove(0);
        }
    }
}