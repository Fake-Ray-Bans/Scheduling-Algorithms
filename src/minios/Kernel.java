package minios;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Kernel {
    private final SchedulingAlgo algo;
    private final List<Process> readyQueue = new ArrayList<>();
    private final List<Process> waitQueue = new ArrayList<>();
    private Process runningProcess = null;

    public Kernel(SchedulingAlgo algo) {
        this.algo = algo;
    }

    public void admitProcess(Process p, int currentTime) {
        p.setReady(currentTime);
        algo.addProcess(readyQueue, p);
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
            if (runningProcess == null) {
                if (dispatchNextProcess(currentTime) == null){
                    // No more process to schedule; simulation finishes.
                    break;
                }
            }

            Instruction inst = runningProcess.getCurrentInstruction();

            // Current process has finished.
            if (inst == null) {
                System.out.println("[Tick " + currentTime + "] Process " + runningProcess.pid + " Terminates.");
                terminateProcess(runningProcess, currentTime);
                // In this case, no instruction is executed, so no CPU cycle
                // is used. Can't advance the simulation clock. Loop
                // back and grab the next process to execute.
                continue;
            }

            // I/O instruction: process enters I/O and no longer uses CPU
            // (Assume this transition itself doesn't consume CPU cycles.)
            else if (inst.type != Instruction.OpType.CPU) {
                System.out.println("[Tick " + currentTime + "] Process " + runningProcess.pid + " enters I/O wait.");
                runningProcess.setBlocked(currentTime);
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
                    System.out.println("[Tick " + currentTime + "] Process " + p.pid + " I/O completes.");
                    p.programCounter++;
                    p.setReady(currentTime);
                    // Move the process to Ready Queue
                    algo.addProcess(readyQueue, p);
                    it.remove();
                }
            }
        }
    }

    private void terminateProcess(Process p, int currentTime) {
        p.terminate(currentTime);
        runningProcess = null;
    }

    private Process dispatchNextProcess(int currentTime) {
        runningProcess = algo.selectNextProcess(readyQueue);
        if (runningProcess != null) {
            System.out.println("[Tick " + currentTime + "] Process " + runningProcess.pid + " executes.");
            runningProcess.setRunning(currentTime);
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