package ca.rttv.chatcalc.mixin;

import ca.rttv.chatcalc.ChatCalc;
import ca.rttv.chatcalc.ChatHelper;
import ca.rttv.chatcalc.Config;
import ca.rttv.chatcalc.FunctionParameter;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.datafixers.util.Pair;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.OptionalDouble;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;

@Mixin(EditBox.class)
abstract class TextFieldWidgetMixin extends AbstractWidget {
    public TextFieldWidgetMixin(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
    }

    @Shadow @Final private Font font;

    @Shadow public native int getCursorPosition();

    @Shadow public native String getValue();

    @Shadow
    private int textY;

    @Unique
    @Nullable
    private Pair<String, OptionalDouble> evaluationCache;

    @Inject(method = "renderWidget", at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Ljava/lang/String;isEmpty()Z", ordinal = 1))
    private void renderWidget1202(CallbackInfo ci, @Local GuiGraphics context, @Local(ordinal = 4) int k) {
        displayAbove(context, k, textY);
    }

//    @Inject(method = "renderButton", at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Ljava/lang/String;isEmpty()Z", ordinal = 1), locals = LocalCapture.CAPTURE_FAILSOFT)
//    private void renderWidget1201(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci, int i, int j, int k, String string, boolean bl, boolean bl2, int l, int m, int n, boolean bl3, int o) {
//        displayAbove(context, o, m);
//    }

    @Unique
    private void displayAbove(GuiGraphics context, int x, int y) {
        if (!(getMessage().getContents() instanceof TranslatableContents translatable && translatable.getKey().equals("chat.editBox"))) {
            return;
        }

        if (!Config.displayAbove()) {
            evaluationCache = null;
            return;
        }

        String word = ChatHelper.getSection(getValue(), getCursorPosition());

        if (ChatCalc.NUMBER.matcher(word).matches()) {
            evaluationCache = null;
            return;
        }

        try {
            double result;
            if (evaluationCache != null && evaluationCache.getFirst().equals(word)) {
                if (evaluationCache.getSecond().isEmpty()) {
                    return;
                }

                result = evaluationCache.getSecond().getAsDouble();
            } else {
                ChatCalc.CONSTANT_TABLE.clear();
                ChatCalc.FUNCTION_TABLE.clear();
                result = Config.makeEngine().eval(word, new FunctionParameter[0]);
                evaluationCache = new Pair<>(word, OptionalDouble.of(result));
            }
            Component text = Component.literal("=" + Config.getDecimalFormat().format(result));
            context.setTooltipForNextFrame(font, text, x - 8, y - 4);
        } catch (Throwable ignored) {
            evaluationCache = new Pair<>(word, OptionalDouble.empty());
        }
    }
}
