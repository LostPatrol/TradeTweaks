package net.lostpatrol.tradetweaks.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.StringRepresentable;

import java.util.Optional;

public final class AdvancementEventTrigger
        extends SimpleCriterionTrigger<AdvancementEventTrigger.TriggerInstance> {

    @Override
    public Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player, Event event) {
        trigger(player, instance -> instance.event() == event);
    }

    public enum Event implements StringRepresentable {
        RESET("reset"),
        TRACK_BLOCK("track_block"),
        TRACK_VILLAGER("track_villager"),
        REFRESH("refresh"),
        UPGRADE("upgrade"),
        OPEN_SELECTION("open_selection"),
        REPLACE_TRADE("replace_trade"),
        SELECT_ENCHANTED_BOOK("select_enchanted_book"),
        ENLIGHTEN_NITWIT("enlighten_nitwit"),
        REVERT_VILLAGER("revert_villager"),
        RESTORE_VILLAGER("restore_villager"),
        RESTOCK("restock"),
        USE_HERO_TOTEM("use_hero_totem"),
        RECEIVE_TRADE_REPORT("receive_trade_report"),
        FIND_MENDING("find_mending");

        public static final Codec<Event> CODEC = StringRepresentable.fromEnum(Event::values);

        private final String name;

        Event(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }

    public record TriggerInstance(Optional<ContextAwarePredicate> player, Event event)
            implements SimpleCriterionTrigger.SimpleInstance {
        public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player),
                Event.CODEC.fieldOf("event").forGetter(TriggerInstance::event)
        ).apply(instance, TriggerInstance::new));
    }
}
