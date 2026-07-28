package net.lostpatrol.tradetweaks.advancement;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;

import java.util.Arrays;

public final class AdvancementEventTrigger
        extends SimpleCriterionTrigger<AdvancementEventTrigger.TriggerInstance> {
    private final ResourceLocation id;

    public AdvancementEventTrigger(ResourceLocation id) {
        this.id = id;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    protected TriggerInstance createInstance(
            JsonObject json, ContextAwarePredicate player, DeserializationContext context) {
        return new TriggerInstance(
                id,
                player,
                Event.fromName(GsonHelper.getAsString(json, "event"))
        );
    }

    public void trigger(ServerPlayer player, Event event) {
        trigger(player, instance -> instance.matches(event));
    }

    public enum Event {
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

        private final String name;

        Event(String name) {
            this.name = name;
        }

        private static Event fromName(String name) {
            return Arrays.stream(values())
                    .filter(event -> event.name.equals(name))
                    .findFirst()
                    .orElseThrow(() -> new JsonSyntaxException(
                            "Unknown Trade Tweaks advancement event: " + name));
        }
    }

    public static final class TriggerInstance extends AbstractCriterionTriggerInstance {
        private final Event event;

        private TriggerInstance(ResourceLocation id, ContextAwarePredicate player, Event event) {
            super(id, player);
            this.event = event;
        }

        private boolean matches(Event event) {
            return this.event == event;
        }

        @Override
        public JsonObject serializeToJson(SerializationContext context) {
            JsonObject json = super.serializeToJson(context);
            json.addProperty("event", event.name);
            return json;
        }
    }
}
