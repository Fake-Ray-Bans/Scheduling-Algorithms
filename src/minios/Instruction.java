
package minios;

public class Instruction {
    public enum OpType {
        COMPUTE, LOAD, STORE, IO
    }

    public final OpType type;
    public int duration;
    public int remainingTicks;
    public int address;
    public int value;

    // For COMPUTE and IO
    public Instruction(OpType type, int duration) {
        if (type != OpType.COMPUTE && type != OpType.IO) {
            throw new IllegalArgumentException(
                    "Invalid instruction type for duration constructor: " + type);
        }

        if (duration <= 0) {
            throw new IllegalArgumentException(
                    "Duration must be > 0. Got: " + duration);
        }

        this.type = type;
        this.duration = duration;
        this.remainingTicks = duration;
    }

    // For LOAD and STORE
    public Instruction(OpType type, int address, int value) {
        if (type != OpType.LOAD && type != OpType.STORE) {
            throw new IllegalArgumentException(
                    "Invalid instruction type for memory constructor: " + type);
        }

        this.type = type;
        this.duration = 1;
        this.remainingTicks = 1;
        this.address = address;
        this.value = value;
    }
}
