package ca.rttv.chatcalc.mixin;

import ca.rttv.chatcalc.duck.ChatInputSuggesterDuck;
import com.mojang.brigadier.suggestion.Suggestions;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.concurrent.CompletableFuture;
import net.minecraft.client.gui.components.CommandSuggestions;

@Mixin(CommandSuggestions.class)
abstract class ChatInputSuggesterMixin implements ChatInputSuggesterDuck {
    @Shadow @Nullable private CompletableFuture<Suggestions> pendingSuggestions;

    @Override
    public CompletableFuture<Suggestions> chatcalc$pendingSuggestions() {
        return this.pendingSuggestions;
    }
}
