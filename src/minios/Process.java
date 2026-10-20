package minios;

import java.util.ArrayList;
import java.util.List;

public class Process {
    public enum State { NEW, READY, RUNNING, BLOCKED, TERMINATED, SWITCH}

    public final int pid;
    public final int arrivalTime;

    private int readyTime = 0;
    private int waitingTime = 0;

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

    public void setState(State state, int currentTime) {
        // If new state is the same as old, do nothing
        if (this.state == state) return;

        switch (state) {
            case State.READY:
                this.readyTime = currentTime;
                if (this.state == State.BLOCKED) {
                    System.out.println("[Tick " + currentTime + "] Process " + pid + " I/O completes. Waiting to execute.");
                    break;
                }
                System.out.println("[Tick " + currentTime + "] Process " + pid + " arrives. Waiting to execute.");
                break;
            case State.RUNNING:
                this.waitingTime += currentTime - this.readyTime;
                System.out.println("[Tick " + currentTime + "] Process " + pid + " executes. Waited for " + this.waitingTime + " Tick(s).");
                break;
            case State.TERMINATED:
                System.out.println("[Tick " + currentTime + "] Process " + pid + " Terminates. Total wait: " + this.waitingTime + " Tick(s).");
                break;
            case State.BLOCKED:
                System.out.println("[Tick " + currentTime + "] Process " + pid + " enters I/O wait.");
                break;
            case State.SWITCH:
                System.out.println("[Tick " + currentTime + "] Process " + pid + " set to switch out.");
                break;
        }

        this.state = state;
    }

    public Instruction getCurrentInstruction() {
        if (programCounter < code.size()){
            return code.get(programCounter);
        }else{
            return null;
        }
    }


}