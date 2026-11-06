
package minios;

import java.util.List;

public class SJF implements SchedulingAlgo {

    @Override
    public void addProcess(List<Process> readyQueue, Process p) {
        readyQueue.add(p);
    }

    @Override
    public void onProcessTick(List<Process> readyQueue, Process p, int currentTime) {
        // SJF does not need additional tick processing.
    }

    @Override
    public Process selectNextProcess(List<Process> readyQueue) {

        if (readyQueue.isEmpty()) {
            return null;
        }

        int shortestIndex = 0;

        for (int i = 1; i < readyQueue.size(); i++) {

            int currentBurst =
                    readyQueue.get(i).currentCPUBurstLength();

            int shortestBurst =
                    readyQueue.get(shortestIndex).currentCPUBurstLength();

            if (currentBurst < shortestBurst) {
                shortestIndex = i;
            }
        }

        return readyQueue.remove(shortestIndex);
    }
}
