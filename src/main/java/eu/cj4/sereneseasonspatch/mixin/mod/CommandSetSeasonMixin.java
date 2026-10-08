package eu.cj4.sereneseasonspatch.mixin.mod;

import com.google.gson.JsonPrimitive;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.serialization.JsonOps;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import sereneseasons.api.season.Season;
import sereneseasons.command.CommandSetSeason;

@Mixin(CommandSetSeason.class)
public class CommandSetSeasonMixin {
    @Unique
    private static final DynamicCommandExceptionType vita$ERROR_INVALID_VALUE = new DynamicCommandExceptionType((value) -> Component.translatableEscape("argument.enum.invalid", value));

    @WrapOperation(method = "register", at = @At(value = "INVOKE", target = "Lnet/minecraft/commands/Commands;argument(Ljava/lang/String;Lcom/mojang/brigadier/arguments/ArgumentType;)Lcom/mojang/brigadier/builder/RequiredArgumentBuilder;"))
    private static <T> RequiredArgumentBuilder<CommandSourceStack, T> suggestResource(String name, ArgumentType<T> type, Operation<RequiredArgumentBuilder<CommandSourceStack, T>> original) {
        return original.call(name, StringArgumentType.word()).suggests(type::listSuggestions);
    }

    @WrapOperation(method = "lambda$register$0", at = @At(value = "INVOKE", target = "Lsereneseasons/command/SeasonArgument;getSeason(Lcom/mojang/brigadier/context/CommandContext;Ljava/lang/String;)Lsereneseasons/api/season/Season$SubSeason;"))
    private static Season.SubSeason modifyExecutes(CommandContext<CommandSourceStack> context, String s, Operation<Season.SubSeason> original) throws CommandSyntaxException {
        String value = StringArgumentType.getString(context, s);
        return Season.SubSeason.CODEC.parse(JsonOps.INSTANCE, new JsonPrimitive(value)).result().orElseThrow(() -> vita$ERROR_INVALID_VALUE.create(value));
    }
}
