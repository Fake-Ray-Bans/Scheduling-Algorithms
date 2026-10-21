package minios;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;

public class Kernel {
    private final SchedulingAlgo algo;
    private final MemoryManagement memory;
    private final List<Process> readyQueue = new ArrayList<>();
    private final List<Process> waitQueue = new ArrayList<>();
    private final List<Process> memoryQueue = new ArrayList<>();
    private Process runningProcess = null;

    public int totalProcesses = 0;
    public int totalWaitingTime = 0;
    public int totalMemoryQueueWaitingTime = 0;

    public Kernel(SchedulingAlgo algo, MemoryManagement memory) {
        this.algo = algo;
        this.memory = memory;
    }

    public void admitProcess(Process p, int currentTime) {
        System.out.println("[Tick " + currentTime + "] Process " + p.pid + " arrives with size " + p.size + ".");

        // Attempt to allocate process, if it can't be allocated add it to the memory queue
        if (this.memory.allocate(p, currentTime)) {
            p.setProcessReady(currentTime);
            algo.addProcess(readyQueue, p);
        } else {
            this.memoryQueue.add(p);
        }
    }

    // Called on every clock tick
    public void onClockTick(int currentTime) {
        // check all processes currently waiting in I/O wait queue
        serviceWaitQueue(currentTime);

        boolean cpuCycleUsed = false;
        // Execute one CPU cycle
        // (One clock tick is one CPU cycle)
        while (!cpuCycleUsed) {
            // No process currently running
            if (runningProcess == null || runningProcess.getState() == Process.State.SWITCH) {
                if (dispatchNextProcess(currentTime) == null){
                    // No more process to schedule; simulation finishes.
                    break;
                }
            }

            Instruction inst = runningProcess.getCurrentInstruction();

            // Current process has finished.
            if (inst == null) {
                terminateProcess(currentTime);
                // In this case, no instruction is executed, so no CPU cycle
                // is used. Can't advance the simulation clock. Loop
                // back and grab the next process to execute.
                continue;
            }

            // I/O instruction: process enters I/O and no longer uses CPU
            // (Assume this transition itself doesn't consume CPU cycles.)
            else if (inst.type != Instruction.OpType.CPU) {
                runningProcess.setProcessBlocked(currentTime);
                waitQueue.add(runningProcess);
                runningProcess = null;
                // Can't advance the simulation clock. Loop back
                // and grab the next process to execute.
                continue;
            }

            // CPU instruction: the instruction has not finished;
            // Utilize one CPU cycle
            else if(inst.remainingTicks > 0){
                inst.remainingTicks--;
                cpuCycleUsed = true;
                this.algo.onProcessTick(readyQueue, runningProcess);
                if (inst.remainingTicks == 0) {
                    // The instruction now finishes; load the next
                    // instruction.
                    runningProcess.programCounter++;
                }
            }

            // CPU instruction: the instruction has finished
            // (should never come here as this is already handled above,
            // but just in case...)
            else{
                runningProcess.programCounter++;
                // Can't advance the simulation clock. Loop back
                // and move to the next instruction.
                continue;
            }
        }
    }

    private void serviceWaitQueue(int currentTime) {
        Iterator<Process> it = waitQueue.iterator();
        while (it.hasNext()) {
            Process p = it.next();
            Instruction inst = p.getCurrentInstruction();
            if (inst != null) {
                inst.remainingTicks--;
                if (inst.remainingTicks <= 0) {
                    // The I/O wait has completed;
                    // load the next instruction.
                    p.programCounter++;
                    p.setProcessIOComplete(currentTime);
                    // Move the process to Ready Queue
                    algo.addProcess(readyQueue, p);
                    it.remove();
                }
            }
        }
    }

    private void terminateProcess(int currentTime) {
        // Release current process and search for other processes that can be added
        this.memory.release(this.runningProcess, currentTime);
        this.memoryQueue.removeIf(p -> {
            if (this.memory.allocate(p, currentTime)) {
                p.setProcessReady(currentTime);
                this.algo.addProcess(this.readyQueue, p);
                return true;
            }
            return false;
        });

        this.runningProcess.setProcessTerminated(currentTime);

        this.totalProcesses++;
        this.totalWaitingTime += this.runningProcess.getWaitingTime();
        this.totalMemoryQueueWaitingTime += this.runningProcess.memoryQueueWaitingTime;

        this.runningProcess = null;
    }

    private Process dispatchNextProcess(int currentTime) {
        runningProcess = algo.selectNextProcess(readyQueue);
        if (runningProcess != null) {
            runningProcess.setProcessRunning(currentTime);
            return runningProcess;
        }else{
            return null;
        }

    }

    public boolean isIdle() {
        return runningProcess == null &&
                readyQueue.isEmpty() &&
                waitQueue.isEmpty();
    }
}