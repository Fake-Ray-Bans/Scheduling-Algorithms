package minios;

import java.util.ArrayList;
import java.util.Collections;

public class ContiguousAllocation implements MemoryManagement {

    private final int maxMemory;
    private final ArrayList<Region> freeMemory = new ArrayList<>();

    public ContiguousAllocation(int maxMemory) {
        this.maxMemory = maxMemory;
        // Start with all memory being free
        this.freeMemory.add(new Region(0, this.maxMemory));
    }

    @Override
    public boolean allocate(Process p, int currentTime) {
        // Invalid process size
        if (p.size <= 0 || p.size > this.maxMemory)
            throw new RuntimeException("Invalid Process size!");

        for (int i = 0; i < this.freeMemory.size(); ++i) {
            Region free = this.freeMemory.get(i);

            if (free.size >= p.size) {
                p.baseAddress = free.baseAddress;

                free.baseAddress += p.size;
                free.size -= p.size;

                if (free.size == 0) {
                    this.freeMemory.remove(i);
                }

                System.out.print("[Tick " + currentTime + "] Process " + p.pid + " allocated at address " + p.baseAddress + " with size " + p.size + " and was ");

                if (p.arrivalTime == currentTime) {
                    System.out.println("immediately allocated!");
                } else {
                    p.memoryQueueWaitingTime = currentTime - p.arrivalTime;
                    System.out.println("allocated after " + p.memoryQueueWaitingTime + " Tick(s)!");
                }

                return true;
            }
        }

        System.out.println("[Tick " + currentTime + "] Process " + p.pid + " could not be allocated (Not enough free memory, needs " + p.size + "!" );

        System.out.println("--");
        for (int i = 0; i < this.freeMemory.size(); ++i) {
            Region region = this.freeMemory.get(i);
            System.out.println("    [Tick " + currentTime + "] Region " + i + ": Starts at " + region.baseAddress + " and spans " + region.size);
        }
        System.out.println("--");

        return false;
    }

    @Override
    public void release(Process p, int currentTime) {
        System.out.println("[Tick " + currentTime + "] Process " + p.pid + " released." );

        int i = 0;
        while (i < this.freeMemory.size() && this.freeMemory.get(i).baseAddress < p.baseAddress) {
            ++i;
        }

        Region freeRegion = new Region(p.baseAddress, p.size);
        this.freeMemory.add(freeRegion);

        coalesce(currentTime);
    }

    // Merges regions that are next to each other
    private void coalesce(int currentTime) {

        // Coalesce needs a sorted list.  This is the lazy way to do it.
        Collections.sort(this.freeMemory);

        StringBuilder message = new StringBuilder();

        message.append("--").append("\n");
        message.append("    -- Before coalesce --").append("\n");

        for (int i = 0; i < this.freeMemory.size(); ++i) {
            Region region = this.freeMemory.get(i);
            message.append("    [Tick ").append(currentTime).append("] Region ").append(i).append(": Starts at ").append(region.baseAddress).append(" and spans ").append(region.size).append("\n");
        }

        boolean didCoalesce = false;
        {
            int i = 0;
            while (i < this.freeMemory.size() - 1) {
                Region current = this.freeMemory.get(i);
                Region next = this.freeMemory.get(i + 1);

                if (current.baseAddress + current.size == next.baseAddress) {
                    current.size += next.size;
                    this.freeMemory.remove(i + 1);
                    didCoalesce = true;
                } else {
                    i++;
                }
            }
        }

        message.append("    -- After coalesce --").append("\n");

        for (int i = 0; i < this.freeMemory.size(); ++i) {
            Region region = this.freeMemory.get(i);
            message.append("    [Tick ").append(currentTime).append("] Region ").append(i).append(": Starts at ").append(region.baseAddress).append(" and spans ").append(region.size).append("\n");
        }
        message.append("--").append("\n");

        if (didCoalesce) {
            System.out.println(message);
        }
    }

    static class Region implements Comparable<Region> {
        public int baseAddress;
        public int size;

        public Region(int baseAddress, int size) {
            this.baseAddress = baseAddress;
            this.size = size;
        }

        int end() {
            return this.baseAddress + this.size - 1;
        }

        @Override
        public int compareTo(Region o) {
            return Integer.compare(this.baseAddress, o.baseAddress);
        }
    }
}
