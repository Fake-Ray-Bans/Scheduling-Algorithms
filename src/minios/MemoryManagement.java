package minios;

public interface MemoryManagement {
    void allocate(Process p);
    void release(Process p);
}
