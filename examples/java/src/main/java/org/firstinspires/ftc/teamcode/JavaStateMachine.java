package org.firstinspires.ftc.teamcode;

import java.util.List;

/** Minimal loop-driven sequence; no command framework or threads. Single use. */
public final class JavaStateMachine {
    public interface Action {
        void start();
        /** Called once per OpMode loop; return true only when finished. Never block. */
        boolean update();
        /** Release outputs on completion as well as on OpMode stop. */
        void stop();
    }

    private final List<Action> actions;
    private int index;
    private boolean started;
    private boolean stopped;

    public JavaStateMachine(List<Action> actions) { this.actions = actions; }

    /** Call only after Auto starts. Starts at most one action per loop. */
    public void loop() {
        if (isFinished()) return;
        Action action = actions.get(index);
        if (!started) { started = true; action.start(); }
        if (action.update()) {
            action.stop();
            started = false;
            index++;
        }
    }

    public boolean isFinished() { return stopped || index == actions.size(); }

    public void stop() {
        if (started && !stopped) actions.get(index).stop();
        stopped = true;
    }

    /** Real nonblocking wait, measured from execution, not JSON compilation. */
    public static Action delay(double seconds) {
        return new Action() {
            private long start;
            public void start() { start = System.nanoTime(); }
            public boolean update() { return (System.nanoTime() - start) / 1e9 >= seconds; }
            public void stop() {}
        };
    }

    /** Reporting/demo actions only; long-running hardware needs its own Action. */
    public static Action once(Runnable work) {
        return new Action() {
            public void start() { work.run(); }
            public boolean update() { return true; }
            public void stop() {}
        };
    }
}
