package minios;

import java.util.ArrayList;
import java.util.List;

public class Process {
    public enum State { NEW, READY, RUNNING, BLOCKED, TERMINATED, SWITCH}

    public final int pid;
    public final int arrivalTime;

    private int readyTime = 0;
    private int waitingTime = 0;

    public int size;
    // For contiguous allocation
    public int baseAddress;
    // For paged allocation
    public int[] pageTable;

    private State state = State.NEW;
    public final List<Instruction> code;
    public int programCounter = 0;

    public Process(int pid, int arrivalTime, List<Instruction> code) {
        this.pid = pid;
        this.arrivalTime = arrivalTime;
        this.code = new ArrayList<>(code);
    }

    public int getWaitingTime() { return this.waitingTime; }

    public State getState() { return this.state; }

    public void setProcessArrived(int currentTime) {
        if (this.state != State.NEW)
            throw new RuntimeException("Process has already arrived!");

        this.readyTime = currentTime;
        this.state = State.READY;

        System.out.println("[Tick " + currentTime + "] Process " + pid + " arrives. Waiting to execute.");
    }

    public void setProcessIOComplete(int currentTime) {
        if (this.state != State.BLOCKED)
            throw new RuntimeException("Process is not in IO!");

        this.readyTime = currentTime;
        this.state = State.READY;

        System.out.println("[Tick " + currentTime + "] Process " + pid + " I/O completes. Waiting to execute.");
    }

    public void setProcessRunning(int currentTime) {
        if (this.state == State.RUNNING)
            throw new RuntimeException("Process is already running!");
        if (this.state == State.TERMINATED)
            throw new RuntimeException("Process is terminated!");

        this.waitingTime += currentTime - this.readyTime;
        this.state = State.RUNNING;

        System.out.println("[Tick " + currentTime + "] Process " + pid + " executes. Waited for " + this.waitingTime + " Tick(s).");
    }

    public void setProcessTerminated(int currentTime) {
        if (this.state == State.TERMINATED)
            throw new RuntimeException("Process is already terminated!");

        this.state = State.TERMINATED;

        System.out.println("[Tick " + currentTime + "] Process " + pid + " Terminates. Total wait: " + this.waitingTime + " Tick(s).");
    }

    public void setProcessBlocked(int currentTime) {
        if (this.state == State.BLOCKED)
            throw new RuntimeException("Process is already blocked!");
        if (this.state == State.TERMINATED)
            throw new RuntimeException("Process is terminated!");

        this.state = State.BLOCKED;

        System.out.println("[Tick " + currentTime + "] Process " + pid + " enters I/O wait.");
    }

    public void setProcessToSwitch() {
        if (this.state == State.SWITCH)
            throw new RuntimeException("Process is already ready to switch out!");
        if (this.state == State.TERMINATED)
            throw new RuntimeException("Process is terminated!");

        this.state = State.SWITCH;

        System.out.println("Process " + pid + " set to switch out.");
    }

    public Instruction getCurrentInstruction() {
        if (programCounter < code.size()){
            return code.get(programCounter);
        }else{
            return null;
        }
    }


}