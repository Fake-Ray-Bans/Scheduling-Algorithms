package minios;

import java.util.ArrayList;

public class PagedAllocation implements MemoryManagement {

    private final int maxMemory;
    private final int pageSize;

    private final ArrayList<Integer> freeFrames = new ArrayList<>();

    public PagedAllocation(int maxMemory, int pageSize) {
        this.maxMemory = maxMemory;
        this.pageSize = pageSize;
        int numFrames = this.maxMemory / this.pageSize;
        for (int i = 0; i < numFrames; ++i)
            this.freeFrames.add(i);
    }
    @Override
    public boolean allocate(Process p, int currentTime) {
        // Invalid process size
        if (p.size <= 0 || p.size > this.maxMemory)
            throw new RuntimeException("Invalid Process size!");

        int pagesNeeded = (p.size + this.pageSize - 1) / this.pageSize;

        if (this.freeFrames.size() < pagesNeeded) {
            System.out.println("[Tick " + currentTime + "] Process " + p.pid + " could not be allocated (need " + pagesNeeded + " pages, only " + this.freeFrames.size() + " free.)");
            return false;
        }

        p.pageTable.addAll(this.freeFrames.subList(0, pagesNeeded));
        this.freeFrames.subList(0, pagesNeeded).clear();

        System.out.print("[Tick " + currentTime + "] Process " + p.pid + " allocated with page(s) ");
        for (int page : p.pageTable) {
            System.out.print(page + " ");
        }
        System.out.print("and was ");

        if (p.arrivalTime == currentTime) {
            System.out.println("immediately allocated!");
        } else {
            p.memoryQueueWaitingTime = currentTime - p.arrivalTime;
            System.out.println("allocated after " + p.memoryQueueWaitingTime + " Tick(s)!");
        }

        return true;
    }

    @Override
    public void release(Process p, int currentTime) {
        System.out.println("[Tick " + currentTime + "] Process " + p.pid + " released." );

        this.freeFrames.addAll(p.pageTable);
        p.pageTable.clear();
    }
}
