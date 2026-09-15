package minios;

import java.util.ArrayList;
import java.util.List;

public class Process {
    public enum State { NEW, READY, RUNNING, BLOCKED, TERMINATED }

    public final int pid;
    public final int arrivalTime;

    public int burstTime;
    public int predictedBurseTime;

    private State state = State.NEW;
    public final List<Instruction> code;
    public int programCounter = 0;

    public Process(int pid, int arrivalTime, List<Instruction> code) {
        this.pid = pid;
        this.arrivalTime = arrivalTime;
        this.code = new ArrayList<>(code);
    }

    public State getState() {
        return this.state;
    }

    public void terminate(int endTime) {
        this.state = State.TERMINATED;
        this.burstTime = endTime - arrivalTime;
        System.out.println("Process " + this.pid + " Ended at " + this.burstTime + " (" + this.programCounter + ")");
    }

    // Ready to run instructions
    public void setReady(int currentTime) {
        this.state = State.READY;
    }

    // CPU is running instructions
    public void setRunning(int currentTime) {
        this.state = State.RUNNING;
    }

    // Waiting on IO
    public void setBlocked(int currentTime) {
        this.state = State.BLOCKED;
    }

    public Instruction getCurrentInstruction() {
        if (programCounter < code.size()){
            return code.get(programCounter);
        }else{
            return null;
        }
    }


}