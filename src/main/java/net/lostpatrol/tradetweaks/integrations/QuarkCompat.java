package net.lostpatrol.tradetweaks.integrations;

import net.lostpatrol.tradetweaks.TradeTweaks;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraftforge.fml.ModList;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuarkCompat {

    private static boolean isQuarkLoaded = false;
    private static Method getEnchantedItemStackMethod;
    private static Method getTomeEnchantmentMethod;
    private static Item ancientTomeItem;
    private static List<Enchantment> validEnchants;
    public static boolean isQuarkMethodsLoaded;

    static {
        if (isQuarkLoaded()) {
            tryLoadQuarkMethods();
        }
    }

    public static void tryLoadQuarkMethods(){
        try {
            Class<?> ancientTomeItemClass = Class.forName("org.violetmoon.quark.content.tools.item.AncientTomeItem");
            getEnchantedItemStackMethod = ancientTomeItemClass.getMethod("getEnchantedItemStack", Enchantment.class);

            Class<?> ancientTomesModuleClass = Class.forName("org.violetmoon.quark.content.tools.module.AncientTomesModule");
            getTomeEnchantmentMethod = ancientTomesModuleClass.getMethod("getTomeEnchantment", ItemStack.class);
            validEnchants = (List<Enchantment>) ancientTomesModuleClass.getField("validEnchants").get(null);
            ancientTomeItem = (Item) ancientTomesModuleClass.getField("ancient_tome").get(null);
        } catch (Exception e) {
            TradeTweaks.LOGGER.error("Failed to load Quark methods", e);
        }
    }

    public static boolean isQuarkLoaded() {
        if (isQuarkLoaded)
            return true;

        if (ModList.get().isLoaded("quark")){
            isQuarkLoaded = true;
            if (!isQuarkMethodsLoaded){
                tryLoadQuarkMethods();
            }
            return getEnchantedItemStackMethod != null;
        }
        return false;
    }

    public static List<MerchantOffer> getAncientTomeOffers() throws InvocationTargetException, IllegalAccessException {
        if (validEnchants==null || ancientTomeItem == null || getEnchantedItemStackMethod == null) {
            TradeTweaks.LOGGER.info("Some reflects went wrong. getAncientTomeOffers() returns empty list");
            return Collections.emptyList();
        }
        List <MerchantOffer> offers = new ArrayList<>();
        for (Enchantment enchantment : validEnchants) {
            ItemStack anyTome = new ItemStack(ancientTomeItem);
            ItemStack enchantedBook = EnchantedBookItem.createForEnchantment(new EnchantmentInstance(enchantment, enchantment.getMaxLevel()));
            ItemStack outputTome = (ItemStack) getEnchantedItemStackMethod.invoke(null, enchantment);
            offers.add(new MerchantOffer(anyTome, enchantedBook, outputTome, 3, 3, 0.2F));
        }
        return offers;
    }
}