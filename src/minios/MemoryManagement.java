
package minios;

public interface MemoryManagement {
    boolean allocate(Process p, int currentTime);
    void release(Process p, int currentTime);
    int translate(Process p, int addr);
    int readPhysical(int physicalAddress);
    void writePhysical(int physicalAddress, int value);
}
