package minios;

import java.util.Iterator;
import java.util.List;

public class Simulator {
    private final Kernel kernel;
    private final List<Process> incomingProcesses;
    private int clock = 0;

    public Simulator(Kernel kernel, List<Process> processes) {
        this.kernel = kernel;
        this.incomingProcesses = processes;
    }

    public void run() {
        // Continue the simulation as long as:
        // 1. there are more processes to come; or
        // 2. one or more existing processes have not completed yet
        while (!incomingProcesses.isEmpty() || !kernel.isIdle()) {
            // Step 1: Check if any processes should arrive now
            Iterator<Process> it = incomingProcesses.iterator();
            while (it.hasNext()) {
                Process p = it.next();
                if (p.arrivalTime == clock) {
                    kernel.admitProcess(p, clock);
                    it.remove();
                }
            }

            // Step 2: perform tasks that need to happen during this clock tick
            kernel.onClockTick(clock);

            // Step 3: Advance the simulation clock
            clock++;
        }

        // Kernel is now finished, so we can use the total processes and total waiting time to calculate the average waiting time
        System.out.println("Total Waiting Time: " + this.kernel.totalWaitingTime);
        int avgWaitingTime = this.kernel.totalWaitingTime / this.kernel.totalProcesses;
        System.out.println("Average Waiting Time per Process: " + avgWaitingTime);
    }

    private static class RunFCFS {
        static void main(String[] args) throws Exception{
            List<Process> workload = TraceParser.parseWorkload("workload.txt");

            SchedulingAlgo algo = new FCFS();
            Kernel kernel = new Kernel(algo);
            Simulator sim = new Simulator(kernel, workload);

            sim.run();
        }
    }

    private static class RunSJF {
        static void main(String[] args) throws Exception{
            List<Process> workload = TraceParser.parseWorkload("workload.txt");

            SchedulingAlgo algo = new SJF();
            Kernel kernel = new Kernel(algo);
            Simulator sim = new Simulator(kernel, workload);

            sim.run();
        }
    }

    private static class RunRR {
        static void main(String[] args) throws Exception{
            List<Process> workload = TraceParser.parseWorkload("workload.txt");

            SchedulingAlgo algo = new RR();
            Kernel kernel = new Kernel(algo);
            Simulator sim = new Simulator(kernel, workload);

            sim.run();
        }
    }

}