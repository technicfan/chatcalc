package ca.rttv.chatcalc.mixin;

import ca.rttv.chatcalc.ChatCalc;
import ca.rttv.chatcalc.duck.ChatInputSuggesterDuck;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.brigadier.suggestion.Suggestions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;
import net.minecraft.client.gui.components.CommandSuggestions;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.input.KeyEvent;

@Mixin(ChatScreen.class)
abstract class ChatScreenMixin {
    @Shadow
    protected EditBox input;

    @Shadow
    CommandSuggestions commandSuggestions;

    @Inject(at = @At("HEAD"), method = "keyPressed(Lnet/minecraft/client/input/KeyEvent;)Z", cancellable = true)
    private void keyPressed(KeyEvent input, CallbackInfoReturnable<Boolean> cir) {
        CompletableFuture<Suggestions> suggestions = ((ChatInputSuggesterDuck) this.commandSuggestions).chatcalc$pendingSuggestions();
        if ((suggestions != null && suggestions.isDone() && !suggestions.isCompletedExceptionally() && suggestions.getNow(null).isEmpty())) {
            if (input.key() == InputConstants.KEY_TAB && ChatCalc.tryParse(this.input)) {
                cir.setReturnValue(true);
            }
        }
    }
}
