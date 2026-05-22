package net.lostpatrol.tradetweaks.common.dummy;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import net.minecraft.core.registries.BuiltInRegistries;

public class DummyVillager {
    private final EntityType<Villager> entityType;
    private final VillagerType villagerType;
    private final VillagerProfession profession;
    private final int professionLevel;
    private final MerchantOffers offers;
    private final int villagerId;
    private final Level level;
    private final Villager dummyVillager;
    private final boolean librarianEnchantedBookSelectionEnabled;

    public DummyVillager(VillagerType villagerType, VillagerProfession profession, int professionLevel, MerchantOffers offers, int villagerId, Level level, EntityType<Villager> entityType) {
        this.entityType = entityType;
        this.villagerType = villagerType;
        this.profession = profession;
        this.professionLevel = professionLevel;
        this.offers = offers;
        this.villagerId = villagerId;
        this.level = level;
        this.librarianEnchantedBookSelectionEnabled = true;
        this.dummyVillager = new Villager(
                this.entityType,
                this.level,
                this.villagerType
        );
    }

    public DummyVillager(String typeName, String professionName, int professionLevel, MerchantOffers offers, int villagerId, Level level,
                         boolean librarianEnchantedBookSelectionEnabled){
        this.villagerType = new VillagerType(typeName);
        ResourceLocation professionNameSrc = ResourceLocation.parse(professionName);
        this.profession = BuiltInRegistries.VILLAGER_PROFESSION.get(professionNameSrc);
        this.professionLevel = professionLevel;
        this.offers = offers;
        this.entityType = EntityType.VILLAGER;
        this.villagerId = villagerId;
        this.level = level;
        this.librarianEnchantedBookSelectionEnabled = librarianEnchantedBookSelectionEnabled;
        this.dummyVillager = new Villager(
                this.entityType,
                this.level,
                this.villagerType
        );
    }

    public EntityType<Villager> getEntityType() {
        return entityType;
    }

    public VillagerType getVillagerType() {
        return villagerType;
    }

    public VillagerProfession getProfession() {
        return profession;
    }

    public int getProfessionLevel() {
        return professionLevel;
    }

    public MerchantOffers getOffers() {
        return offers;
    }

    public int getVillagerId() {
        return villagerId;
    }

    public Villager getDummyVillager() {
        return dummyVillager;
    }

    public Level getLevel() {
        return level;
    }

    public boolean isLibrarianEnchantedBookSelectionEnabled() {
        return librarianEnchantedBookSelectionEnabled;
    }
}

