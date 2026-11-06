
package minios;

import java.util.List;

public interface SchedulingAlgo {

    // Add a process to the Ready Queue
    void addProcess(List<Process> readyQueue, Process p);

    // Called when a process uses a CPU tick
    void onProcessTick(List<Process> readyQueue, Process p, int currentTime);

    // Select the next process to execute
    Process selectNextProcess(List<Process> readyQueue);
}
