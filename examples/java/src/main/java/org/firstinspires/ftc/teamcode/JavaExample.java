package org.firstinspires.ftc.teamcode;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import com.google.gson.JsonObject;
import com.threedrd.quanomous.CommandFactory;
import com.threedrd.quanomous.Quanomous;
import com.threedrd.quanomous.QuanomousCompiler;

/** Devil Dogs' BioBuzz JSON contract. T is your framework's command/action type. */
public final class JavaExample<T> {
    public enum Side { LEFT, RIGHT }
    public enum Place { GARDEN, FLOWER, LOADING_ZONE }

    /** Implement these factories with your team's Auto/Nav methods; do not schedule here. */
    public interface Auto<T> {
        T delay(double seconds);
        T chase(int cycles);
        T intake(Place place, Side side);
        T deposit(Side side, double ao, double lo);
        T park(Side side);
        T drive(double tx, double ty, double h, String axial, String lateral, double ao, double lo);
    }

    private final QuanomousCompiler<T> compiler;

    public JavaExample(Auto<T> auto) {
        Map<String, CommandFactory<T>> handlers = new HashMap<>();
        handlers.put("delay", step -> auto.delay(step.get("seconds").getAsDouble()));
        handlers.put("chase", step -> auto.chase(step.get("cycles").getAsInt()));
        handlers.put("intake", step -> auto.intake(
            Place.valueOf(step.get("place").getAsString().toUpperCase(Locale.ROOT)), side(step)));
        handlers.put("deposit", step -> auto.deposit(side(step), offset(step, "ao", "txo"), offset(step, "lo", "tyo")));
        handlers.put("park", step -> auto.park(side(step)));
        handlers.put("drive", step -> auto.drive(
            step.get("tx").getAsDouble(), step.get("ty").getAsDouble(), step.get("h").getAsDouble(),
            reference(step, "axial", "front", "back"), reference(step, "lateral", "left", "right"),
            number(step, "ao"), number(step, "lo")));
        compiler = QuanomousCompiler.fromHandlers(handlers);
    }

    public List<T> compile(String json) { return compiler.compile(Quanomous.INSTANCE.parse(json)); }
    public List<T> decode(String barcodeData) { return compile(Quanomous.INSTANCE.decode(barcodeData)); }
    public List<T> load(String filename) { return compiler.load(filename); }

    private static Side side(JsonObject step) {
        return Side.valueOf(step.get("side").getAsString().toUpperCase(Locale.ROOT));
    }
    private static double number(JsonObject step, String name) {
        return step.has(name) ? step.get(name).getAsDouble() : 0.0;
    }
    private static double offset(JsonObject step, String name, String legacy) {
        return step.has(name) ? step.get(name).getAsDouble() : number(step, legacy);
    }
    private static String reference(JsonObject step, String name, String first, String second) {
        String value = step.has(name) ? step.get(name).getAsString().toLowerCase(Locale.ROOT) : "center";
        return value.equals(first) || value.equals(second) ? value : "center";
    }
}
