package org.firstinspires.ftc.teamcode;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

/** Hardware-free BioBuzz demo. Press/release A to complete each simulated robot action. */
public final class BioBuzzStateMachineDemo implements JavaExample.Auto<JavaStateMachine.Action> {
    private final Consumer<String> report;
    private final BooleanSupplier advance;

    public BioBuzzStateMachineDemo(Consumer<String> report, BooleanSupplier advance) {
        this.report = report;
        this.advance = advance;
    }

    public JavaStateMachine.Action delay(double seconds) { return JavaStateMachine.delay(seconds); }
    public JavaStateMachine.Action chase(int cycles) { return simulated("Chase " + cycles + " cycles"); }
    public JavaStateMachine.Action intake(JavaExample.Place place, JavaExample.Side side) {
        return simulated("Intake " + place + " on " + side);
    }
    public JavaStateMachine.Action deposit(JavaExample.Side side, double ao, double lo) {
        return simulated("Deposit " + side + ": offsets in tiles ao=" + ao + " lo=" + lo);
    }
    public JavaStateMachine.Action park(JavaExample.Side side) { return simulated("Park " + side); }
    public JavaStateMachine.Action drive(double tx, double ty, double h, String axial, String lateral, double ao, double lo) {
        return simulated("Drive target in tiles x=" + tx + " y=" + ty + ", heading=" + h
            + " degrees, alignment=" + axial + "/" + lateral + ", offsets=" + ao + "/" + lo);
    }

    private JavaStateMachine.Action simulated(String description) {
        return new JavaStateMachine.Action() {
            private boolean previous;
            public void start() {
                previous = advance.getAsBoolean();
                report.accept(description + " [SIMULATED: release then press A to finish]");
                // Real action: initialize the target / outputs here.
            }
            public boolean update() {
                boolean pressed = advance.getAsBoolean();
                boolean complete = pressed && !previous;
                previous = pressed;
                return complete;
                // Real action: update control logic and return sensor/controller completion.
            }
            public void stop() {
                // Real action: stop this action's outputs on completion or cancellation.
            }
        };
    }
}
