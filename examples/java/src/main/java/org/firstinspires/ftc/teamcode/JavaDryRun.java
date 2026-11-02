package org.firstinspires.ftc.teamcode;

import java.util.function.Consumer;

/** A complete first-use adapter: report the BioBuzz steps without moving hardware. */
public final class JavaDryRun {
    private JavaDryRun() {}

    public static JavaExample<Runnable> create(Consumer<String> report) {
        return new JavaExample<>(new JavaExample.Auto<Runnable>() {
            private Runnable message(String text) { return () -> report.accept(text); }
            public Runnable delay(double seconds) { return message("Delay " + seconds + " seconds"); }
            public Runnable chase(int cycles) { return message("Chase " + cycles + " cycles"); }
            public Runnable intake(JavaExample.Place place, JavaExample.Side side) {
                return message("Intake " + place + " on " + side);
            }
            public Runnable deposit(JavaExample.Side side, double ao, double lo) {
                return message("Deposit " + side + ": offsets in tiles ao=" + ao + " lo=" + lo);
            }
            public Runnable park(JavaExample.Side side) { return message("Park " + side); }
            public Runnable drive(double tx, double ty, double h, String axial, String lateral, double ao, double lo) {
                return message("Drive target in tiles x=" + tx + " y=" + ty + ", heading=" + h
                    + " degrees, alignment=" + axial + "/" + lateral + ", offsets=" + ao + "/" + lo);
            }
        });
    }
}
