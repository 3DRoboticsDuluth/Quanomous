package org.firstinspires.ftc.teamcode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.threedrd.quanomous.CommandFactory;
import com.threedrd.quanomous.Quanomous;
import com.threedrd.quanomous.QuanomousCompiler;

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
