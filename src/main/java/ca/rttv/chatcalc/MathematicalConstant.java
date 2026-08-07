package ca.rttv.chatcalc;

import java.util.LinkedHashSet;
import java.util.function.DoubleSupplier;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;

public class MathematicalConstant {
    public static final LinkedHashSet<MathematicalConstant> CONSTANTS = new LinkedHashSet<>();

    static {
        CONSTANTS.add(new MathematicalConstant("random", Math::random));
        CONSTANTS.add(new MathematicalConstant("rand", Math::random));
        CONSTANTS.add(new MathematicalConstant("rad", () -> Config.radians() ? 1.0 : 57.29577951308232));
        CONSTANTS.add(new MathematicalConstant("deg", () -> Config.radians() ? 0.017453292519943295 : 1.0));
        CONSTANTS.add(new MathematicalConstant("yaw", () -> Config.convertFromDegrees(Mth.wrapDegrees(Minecraft.getInstance().player.getYRot()))));
        CONSTANTS.add(new MathematicalConstant("pitch", () -> Config.convertFromDegrees(Mth.wrapDegrees(Minecraft.getInstance().player.getXRot()))));
        CONSTANTS.add(new MathematicalConstant("pi", () -> Math.PI));
        CONSTANTS.add(new MathematicalConstant("tau", () -> 2.0d * Math.PI));
        CONSTANTS.add(new MathematicalConstant("e", () -> Math.E));
        CONSTANTS.add(new MathematicalConstant("phi", () -> 1.6180339887498948482));
        CONSTANTS.add(new MathematicalConstant("x", () -> Minecraft.getInstance().player.getX()));
        CONSTANTS.add(new MathematicalConstant("y", () -> Minecraft.getInstance().player.getY()));
        CONSTANTS.add(new MathematicalConstant("z", () -> Minecraft.getInstance().player.getZ()));
    }

    private final String name;
    private final DoubleSupplier value;

    public MathematicalConstant(String name, DoubleSupplier value) {
        this.name = name;
        this.value = value;
    }

    public String name() {
        return name;
    }

    public double value() {
        return value.getAsDouble();
    }
}
