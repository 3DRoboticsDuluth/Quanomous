package org.threedrd.examples;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.firstinspires.ftc.threedrd.quanomous.CommandFactory;
import org.firstinspires.ftc.threedrd.quanomous.Quanomous;
import org.firstinspires.ftc.threedrd.quanomous.QuanomousCompiler;

/** Replace Runnable with your command or state-machine action type. */
public final class JavaExample {
    public static List<Runnable> compile(String json, List<String> events) {
        Map<String, CommandFactory<Runnable>> handlers = new HashMap<>();
        handlers.put("signal", step -> {
            String label = step.get("label").getAsString();
            return () -> events.add(label);
        });
        return QuanomousCompiler.fromHandlers(handlers).compile(Quanomous.INSTANCE.parse(json));
    }
}
