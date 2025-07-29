package net.lostpatrol.tradetweaks.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;

import java.util.UUID;

public class Util {
    public static String toRoman(int level) {
        return switch (level) {
            case 1 -> "I";
            case 2 -> "II";
            case 3 -> "III";
            case 4 -> "IV";
            case 5 -> "V";
            case 6 -> "VI";
            case 7 -> "VII";
            case 8 -> "VIII";
            case 9 -> "IX";
            case 10 -> "X";
            case 11 -> "XI";
            case 12 -> "XII";
            case 13 -> "XIII";
            case 14 -> "XIV";
            case 15 -> "XV";
            case 16 -> "XVI";
            case 17 -> "XVII";
            case 18 -> "XVIII";
            case 19 -> "XIX";
            case 20 -> "XX";
            default -> String.valueOf(level);
        };
    }

    //profession name and level.
    public static Component getProfessionComponent(VillagerProfession profession)
    {
        String professionKey = "entity.minecraft.villager." + profession.toString().toLowerCase();
        return Component.translatable(professionKey).withStyle(ChatFormatting.GRAY);
    }

    public static Component getLevelComponent(int level) {
        String levelKey = "merchant.level." + level;
        ChatFormatting color = switch (level) {
            case 2 -> ChatFormatting.GREEN;
            case 3 -> ChatFormatting.AQUA;
            case 4 -> ChatFormatting.DARK_PURPLE;
            case 5 -> ChatFormatting.GOLD;
            default -> ChatFormatting.WHITE;
        };
        return Component.translatable(levelKey).withStyle(color);
    }

    public static void highlightVillagerWithEntity(Villager villager, int duration) {
        villager.addEffect(new MobEffectInstance(MobEffects.GLOWING, duration, 0));
    }

    public static boolean isNitwit(Villager villager) {
        return villager.getVillagerData().getProfession() == VillagerProfession.NITWIT;
    }

    public static boolean isUnemployed(Villager villager){
        return villager.getVillagerData().getProfession() == VillagerProfession.NONE;
    }

    public static boolean isLibrarian(Villager villager){
        return villager.getVillagerData().getProfession() == VillagerProfession.LIBRARIAN;
    }

    @Deprecated
    public  static void highlightVillagerWithUUID(UUID uuid, int duration){
//        Villager villager = level.getServer().getLevel(level.dimension()).getEntity(uuid);
    }
}
