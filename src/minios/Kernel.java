
package minios;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

        System.out.println("[Tick " + currentTime +
                "] Process " + p.pid +
                " arrives with size " + p.size + ".");

        if (memory.allocate(p, currentTime)) {
            p.setProcessReady(currentTime);
            algo.addProcess(readyQueue, p);
        } else {
            memoryQueue.add(p);
        }
    }

    public void onClockTick(int currentTime) {

        serviceWaitQueue(currentTime);

        boolean cpuCycleUsed = false;

        while (!cpuCycleUsed) {

            if (runningProcess == null ||
                    runningProcess.getState() == Process.State.SWITCH) {

                if (dispatchNextProcess(currentTime) == null) {
                    break;
                }
            }

            Instruction inst =
                    runningProcess.getCurrentInstruction();

            if (inst == null) {
                terminateProcess(currentTime);
                continue;
            }

            if (inst.type == Instruction.OpType.IO) {

                System.out.println("[Tick " + currentTime +
                        "] Process " + runningProcess.pid +
                        " executes IO for " + inst.duration +
                        " tick(s).");

                runningProcess.setProcessBlocked(currentTime);
                waitQueue.add(runningProcess);
                runningProcess = null;

                continue;
            }

            if (inst.type == Instruction.OpType.COMPUTE) {

                if (inst.remainingTicks > 0) {

                    System.out.println("[Tick " + currentTime +
                            "] Process " + runningProcess.pid +
                            " executes COMPUTE. Remaining ticks: " +
                            (inst.remainingTicks - 1));

                    inst.remainingTicks--;

                    cpuCycleUsed = true;

                    algo.onProcessTick(readyQueue, runningProcess, currentTime);

                    if (inst.remainingTicks == 0) {
                        runningProcess.programCounter++;
                    }
                } else {
                    runningProcess.programCounter++;
                }

                continue;
            }

            if (inst.type == Instruction.OpType.STORE) {

                int pid = runningProcess.pid;
                int virtualAddress = inst.address;
                int value = inst.value;

                int physicalAddress =
                        memory.translate(runningProcess, virtualAddress);

                inst.remainingTicks = 0;
                cpuCycleUsed = true;

                if (physicalAddress == -1) {

                    System.out.println("[Tick " + currentTime +
                            "] WARNING: Process " + pid +
                            " attempted STORE addr " +
                            virtualAddress + " value " + value +
                            " - invalid virtual address. Process terminated.");

                    terminateProcess(currentTime);
                    continue;
                }

                System.out.println("[Tick " + currentTime +
                        "] Process " + pid +
                        ": Virtual address " + virtualAddress +
                        " -> Physical address " + physicalAddress);

                memory.writePhysical(physicalAddress, value);

                System.out.println("[Tick " + currentTime +
                        "] Process " + pid +
                        " STORE value " + value +
                        " at physical address " + physicalAddress);

                runningProcess.programCounter++;

                algo.onProcessTick(readyQueue, runningProcess, currentTime);

                continue;
            }

            if (inst.type == Instruction.OpType.LOAD) {

                int pid = runningProcess.pid;
                int virtualAddress = inst.address;

                int physicalAddress =
                        memory.translate(runningProcess, virtualAddress);

                inst.remainingTicks = 0;
                cpuCycleUsed = true;

                if (physicalAddress == -1) {

                    System.out.println("[Tick " + currentTime +
                            "] WARNING: Process " + pid +
                            " attempted LOAD addr " +
                            virtualAddress +
                            " - invalid virtual address. Process terminated.");

                    terminateProcess(currentTime);
                    continue;
                }

                System.out.println("[Tick " + currentTime +
                        "] Process " + pid +
                        ": Virtual address " + virtualAddress +
                        " -> Physical address " + physicalAddress);

                int loadedValue =
                        memory.readPhysical(physicalAddress);

                System.out.println("[Tick " + currentTime +
                        "] Process " + pid +
                        " LOAD value " + loadedValue +
                        " from virtual address " + virtualAddress);

                runningProcess.programCounter++;

                algo.onProcessTick(readyQueue, runningProcess, currentTime);
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

                    p.programCounter++;

                    p.setProcessIOComplete(currentTime);

                    algo.addProcess(readyQueue, p);

                    it.remove();
                }
            }
        }
    }

    private void terminateProcess(int currentTime) {

        Process terminated = runningProcess;

        if (terminated == null) {
            return;
        }

        memory.release(terminated, currentTime);

        terminated.setProcessTerminated(currentTime);

        totalProcesses++;
        totalWaitingTime += terminated.getWaitingTime();
        totalMemoryQueueWaitingTime +=
                terminated.memoryQueueWaitingTime;

        runningProcess = null;

        memoryQueue.removeIf(p -> {

            if (memory.allocate(p, currentTime)) {

                p.setProcessReady(currentTime);
                algo.addProcess(readyQueue, p);

                return true;
            }

            return false;
        });
    }

    private Process dispatchNextProcess(int currentTime) {

        runningProcess = algo.selectNextProcess(readyQueue);

        if (runningProcess != null) {

            runningProcess.setProcessRunning(currentTime);

            return runningProcess;
        }

        return null;
    }

    public boolean isIdle() {

        return runningProcess == null &&
                readyQueue.isEmpty() &&
                waitQueue.isEmpty() &&
                memoryQueue.isEmpty();
    }
}
