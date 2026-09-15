package minios;

public class Instruction {
    public enum OpType { CPU, IO }

    public final OpType type;
    public int duration;
    public int remainingTicks;

    public Instruction(OpType type, int duration) {
        if (duration <= 0) {
            throw new IllegalArgumentException("Duration must be > 0. Got: " + duration);
        }
        this.type = type;
        this.duration = duration;
        this.remainingTicks = duration;
    }
}