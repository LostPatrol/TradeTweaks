package net.lostpatrol.tradetweaks.common.predict;

import net.lostpatrol.tradetweaks.util.DeepCopy;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.trading.MerchantOffer;


@Deprecated
public class VillagerTradePredictor {

    public static void predictAndCompareTrades(Villager originalVillager, Player player) {
        Villager clonedVillager = cloneVillager(originalVillager);

        while(clonedVillager.getVillagerData().getLevel() < 5)
            clonedVillager.increaseMerchantCareer();

        player.sendSystemMessage(Component.literal("Predict:"));
        for (MerchantOffer offer : clonedVillager.getOffers()) {
            player.sendSystemMessage(formatOffer(offer));
        }
    }

    private static Villager cloneVillager(Villager original) {
        Villager clone = new Villager(
                EntityType.VILLAGER,
                original.level(),
                original.getVillagerData().getType()
        );

        clone.setVillagerData(original.getVillagerData());
        clone.setUUID(original.getUUID());
        CompoundTag nbt = new CompoundTag();
        original.saveWithoutId(nbt);
        clone.load(nbt);

        // This won't work. villager.random is effected by many actions.
        // Also, the mod Visible Trader by Ramixin can do this prediction already.
        clone.random = original.getRandom().fork();
//        clone.random = original.random;
//        clone.random = deepcopy(original.getRandom().fork())
        return clone;
    }

    private static Component formatOffer(MerchantOffer offer) {
        return Component.literal(
                String.format("- %s x%d → %s x%d",
                        offer.getBaseCostA().getItem().getName(offer.getBaseCostA()).getString(),
                        offer.getBaseCostA().getCount(),
                        offer.getResult().getItem().getName(offer.getResult()).getString(),
                        offer.getResult().getCount()
                )
        );
    }
}