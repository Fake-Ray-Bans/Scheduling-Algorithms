package minios;

import java.util.ArrayList;
import java.util.List;

public class Process {
    public enum State { NEW, READY, RUNNING, BLOCKED, TERMINATED }

    public final int pid;
    public final int arrivalTime;

    private int burstStartTime = 0;

    // Start off with a low default value
    // Gather data about how fast other processes will be
    // To make the next best prediction
    private int nextPredictedBurst = 2;

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
    }

    // Ready to run instructions
    public void setReady(int currentTime) {
        this.state = State.READY;
    }

    // CPU is running instructions
    public void setRunning(int currentTime) {
        this.state = State.RUNNING;
        this.burstStartTime = currentTime;
    }

    // Waiting on IO
    public void setBlocked(int currentTime) {
        this.state = State.BLOCKED;
        // Simple predict as previous time TODO: change later to better prediction model
        this.nextPredictedBurst = currentTime - this.burstStartTime;
        System.out.println("Process " + this.pid + " predicted burst: " + this.nextPredictedBurst);
    }

    public int getNextPredictedBurst() {
        return this.nextPredictedBurst;
    }

    public Instruction getCurrentInstruction() {
        if (programCounter < code.size()){
            return code.get(programCounter);
        }else{
            return null;
        }
    }


}