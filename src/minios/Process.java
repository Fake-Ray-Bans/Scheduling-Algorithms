package minios;

import java.util.ArrayList;
import java.util.List;

public class Process {
    public enum State { NEW, READY, RUNNING, BLOCKED, TERMINATED, SWITCH}

    public final int pid;
    public final int arrivalTime;

    public State state = State.NEW;
    public final List<Instruction> code;
    public int programCounter = 0;

    public Process(int pid, int arrivalTime, List<Instruction> code) {
        this.pid = pid;
        this.arrivalTime = arrivalTime;
        this.code = new ArrayList<>(code);
    }

    public Instruction getCurrentInstruction() {
        if (programCounter < code.size()){
            return code.get(programCounter);
        }else{
            return null;
        }
    }


}