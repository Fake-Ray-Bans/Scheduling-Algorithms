package minios;

public interface MemoryManagement {
    // Returns true if immediately allocated
    boolean allocate(Process p, int currentTime);

    void release(Process p, int currentTime);
}
