
package minios;

import java.util.List;

public class FCFS implements SchedulingAlgo {

    @Override
    public void addProcess(List<Process> readyQueue, Process p) {
        readyQueue.add(p);
    }

    @Override
    public void onProcessTick(List<Process> readyQueue, Process p, int currentTime) {
        // FCFS does not need additional tick processing.
    }

    @Override
    public Process selectNextProcess(List<Process> readyQueue) {

        if (readyQueue.isEmpty()) {
            return null;
        }

        return readyQueue.remove(0);
    }
}
