package net.lostpatrol.tradetweaks.client.gui;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.ModList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@OnlyIn(Dist.CLIENT)
public final class TradeSearchMatcher {
    private static final Pattern FILTER_SPLIT_PATTERN = Pattern.compile("(-?\".*?(?:\"|$)|\\S+)");
    private static final Pattern QUOTE_PATTERN = Pattern.compile("\"");
    private static final Pattern MOD_NAME_SEPARATOR_PATTERN = Pattern.compile("(?=[A-Z_-])|\\s+");
    private static final Pattern SPACE_PATTERN = Pattern.compile("\\s");
    private static final List<NamedColor> COLORS = List.of(
            new NamedColor("white", 0xEEEEEE),
            new NamedColor("lightblue", 0x7492CC),
            new NamedColor("cyan", 0x00EEEE),
            new NamedColor("blue", 0x2222DD),
            new NamedColor("lapisblue", 0x25418B),
            new NamedColor("teal", 0x008080),
            new NamedColor("yellow", 0xCACB58),
            new NamedColor("goldenyellow", 0xEED700),
            new NamedColor("orange", 0xD97634),
            new NamedColor("pink", 0xD1899D),
            new NamedColor("hotpink", 0xFC0FC0),
            new NamedColor("magenta", 0xB24BBB),
            new NamedColor("purple", 0x813EB9),
            new NamedColor("evilpurple", 0x2E1649),
            new NamedColor("lavender", 0xB57EDC),
            new NamedColor("indigo", 0x480082),
            new NamedColor("sand", 0xDBD3A0),
            new NamedColor("tan", 0xBB9B63),
            new NamedColor("lightbrown", 0xA0522D),
            new NamedColor("brown", 0x634B33),
            new NamedColor("darkbrown", 0x3A2D13),
            new NamedColor("limegreen", 0x43B239),
            new NamedColor("slimegreen", 0x83CB73),
            new NamedColor("green", 0x008000),
            new NamedColor("darkgreen", 0x224D22),
            new NamedColor("grassgreen", 0x548049),
            new NamedColor("red", 0x963430),
            new NamedColor("brickred", 0xB0604B),
            new NamedColor("netherbrick", 0x2A1516),
            new NamedColor("redstone", 0xCE3E36),
            new NamedColor("black", 0x181515),
            new NamedColor("charcoalgray", 0x464646),
            new NamedColor("irongray", 0x646464),
            new NamedColor("gray", 0x808080),
            new NamedColor("silver", 0xC0C0C0)
    );

    private final Map<MerchantOffer, OfferSearchData> offerCache = new IdentityHashMap<>();

    public int[] findMatches(MerchantOffers offers, String filterText) {
        List<SearchGroup> groups = parseSearchGroups(filterText);
        if (groups.isEmpty()) {
            return null;
        }

        List<Integer> matches = new ArrayList<>();
        for (int index = 0; index < offers.size(); index++) {
            MerchantOffer offer = offers.get(index);
            OfferSearchData searchData = this.offerCache.computeIfAbsent(offer, OfferSearchData::new);
            if (searchData.matches(groups)) {
                matches.add(index);
            }
        }
        return matches.stream().mapToInt(Integer::intValue).toArray();
    }

    /**
     * Stable text-matching hook for optional search integrations such as Just Enough Characters.
     */
    public static boolean contains(String searchableText, String query) {
        return searchableText.contains(query);
    }

    private static List<SearchGroup> parseSearchGroups(String filterText) {
        List<SearchGroup> groups = new ArrayList<>();
        for (String filter : normalize(filterText).split("\\|")) {
            SearchGroup group = parseSearchGroup(filter);
            if (!group.isEmpty()) {
                groups.add(group);
            }
        }
        return groups;
    }

    private static SearchGroup parseSearchGroup(String filterText) {
        List<SearchToken> included = new ArrayList<>();
        List<SearchToken> excluded = new ArrayList<>();
        Matcher matcher = FILTER_SPLIT_PATTERN.matcher(filterText);
        while (matcher.find()) {
            String tokenText = matcher.group(1);
            boolean remove = tokenText.startsWith("-");
            if (remove) {
                tokenText = tokenText.substring(1);
            }
            tokenText = QUOTE_PATTERN.matcher(tokenText).replaceAll("");
            SearchToken token = parseSearchToken(tokenText);
            if (token == null) {
                continue;
            }
            (remove ? excluded : included).add(token);
        }
        return new SearchGroup(included, excluded);
    }

    private static SearchToken parseSearchToken(String token) {
        if (token.isEmpty()) {
            return null;
        }
        SearchType type = SearchType.fromPrefix(token.charAt(0));
        if (type == SearchType.DEFAULT) {
            return new SearchToken(type, token);
        }
        return token.length() == 1 ? null : new SearchToken(type, token.substring(1));
    }

    private static String normalize(String value) {
        String stripped = ChatFormatting.stripFormatting(value);
        return (stripped == null ? "" : stripped).toLowerCase(Locale.ROOT);
    }

    private static boolean containsAny(Collection<String> strings, String query) {
        for (String string : strings) {
            if (contains(string, query)) {
                return true;
            }
        }
        return false;
    }

    private static final class OfferSearchData {
        private final List<ItemSearchData> items;

        private OfferSearchData(MerchantOffer offer) {
            this.items = new ArrayList<>(3);
            add(offer.getBaseCostA());
            add(offer.getCostB());
            add(offer.getResult());
        }

        private void add(ItemStack stack) {
            if (!stack.isEmpty()) {
                this.items.add(new ItemSearchData(stack));
            }
        }

        private boolean matches(List<SearchGroup> groups) {
            for (SearchGroup group : groups) {
                for (ItemSearchData item : this.items) {
                    if (group.matches(item)) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    private record SearchGroup(List<SearchToken> included, List<SearchToken> excluded) {
        private boolean isEmpty() {
            return this.included.isEmpty() && this.excluded.isEmpty();
        }

        private boolean matches(ItemSearchData item) {
            for (SearchToken token : this.included) {
                if (!token.matches(item)) {
                    return false;
                }
            }
            for (SearchToken token : this.excluded) {
                if (token.matches(item)) {
                    return false;
                }
            }
            return true;
        }
    }

    private record SearchToken(SearchType type, String text) {
        private boolean matches(ItemSearchData item) {
            return switch (this.type) {
                case DEFAULT -> containsAny(item.names(), this.text) || containsAny(item.tooltips(), this.text);
                case MOD_NAME -> containsAny(item.modNames(), this.text);
                case TOOLTIP -> containsAny(item.tooltips(), this.text);
                case TAG -> containsAny(item.tags(), this.text);
                case CREATIVE_TAB -> containsAny(item.creativeTabs(), this.text);
                case COLOR -> containsAny(item.colors(), this.text);
                case RESOURCE_LOCATION -> containsAny(item.resourceLocations(), this.text);
            };
        }
    }

    private enum SearchType {
        DEFAULT('\0'),
        MOD_NAME('@'),
        TOOLTIP('$'),
        TAG('#'),
        CREATIVE_TAB('%'),
        COLOR('^'),
        RESOURCE_LOCATION('&');

        private final char prefix;

        SearchType(char prefix) {
            this.prefix = prefix;
        }

        private static SearchType fromPrefix(char prefix) {
            for (SearchType type : values()) {
                if (type.prefix == prefix) {
                    return type;
                }
            }
            return DEFAULT;
        }
    }

    private static final class ItemSearchData {
        private final ItemStack stack;
        private List<String> names;
        private List<String> tooltips;
        private List<String> modNames;
        private List<String> tags;
        private List<String> creativeTabs;
        private List<String> colors;
        private List<String> resourceLocations;

        private ItemSearchData(ItemStack stack) {
            this.stack = stack;
        }

        private List<String> names() {
            if (this.names == null) {
                this.names = List.of(normalize(this.stack.getHoverName().getString()));
            }
            return this.names;
        }

        private List<String> tooltips() {
            if (this.tooltips == null) {
                List<String> values = new ArrayList<>();
                try {
                    Minecraft minecraft = Minecraft.getInstance();
                    List<net.minecraft.network.chat.Component> lines = this.stack.getTooltipLines(
                            Item.TooltipContext.of(minecraft.level),
                            minecraft.player,
                            TooltipFlag.NORMAL.asCreative()
                    );
                    for (int index = 1; index < lines.size(); index++) {
                        values.add(normalize(lines.get(index).getString()));
                    }
                } catch (RuntimeException | LinkageError ignored) {
                }
                this.tooltips = List.copyOf(values);
            }
            return this.tooltips;
        }

        private List<String> modNames() {
            if (this.modNames == null) {
                Set<String> values = new LinkedHashSet<>();
                ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(this.stack.getItem());
                addModNames(values, itemId.getNamespace());
                String creatorModId = this.stack.getItem().getCreatorModId(this.stack);
                if (creatorModId != null) {
                    addModNames(values, creatorModId);
                }
                this.modNames = List.copyOf(values);
            }
            return this.modNames;
        }

        private List<String> tags() {
            if (this.tags == null) {
                this.tags = this.stack.getTags()
                        .map(TagKey::location)
                        .map(ResourceLocation::getPath)
                        .map(TradeSearchMatcher::normalize)
                        .toList();
            }
            return this.tags;
        }

        private List<String> creativeTabs() {
            if (this.creativeTabs == null) {
                Set<String> values = new LinkedHashSet<>();
                for (CreativeModeTab tab : CreativeModeTabs.allTabs()) {
                    if (!tab.shouldDisplay() || tab.getType() != CreativeModeTab.Type.CATEGORY || !tab.contains(this.stack)) {
                        continue;
                    }
                    String[] words = normalize(tab.getDisplayName().getString()).split(" ");
                    for (String word : words) {
                        if (!word.isEmpty()) {
                            values.add(word);
                        }
                    }
                }
                this.creativeTabs = List.copyOf(values);
            }
            return this.creativeTabs;
        }

        private List<String> colors() {
            if (this.colors == null) {
                this.colors = findColorNames(this.stack);
            }
            return this.colors;
        }

        private List<String> resourceLocations() {
            if (this.resourceLocations == null) {
                this.resourceLocations = List.of(normalize(BuiltInRegistries.ITEM.getKey(this.stack.getItem()).toString()));
            }
            return this.resourceLocations;
        }
    }

    private static void addModNames(Set<String> values, String modId) {
        values.add(normalize(modId));
        ModList.get().getModContainerById(modId).ifPresent(container -> {
            String modName = container.getModInfo().getDisplayName();
            values.add(SPACE_PATTERN.matcher(normalize(modName)).replaceAll(""));
            String[] words = MOD_NAME_SEPARATOR_PATTERN.split(modName);
            if (words.length > 1) {
                values.add(combineFirstLetters(words, 1));
                values.add(combineFirstLetters(words, 2));
            }
        });
    }

    private static String combineFirstLetters(String[] words, int count) {
        StringBuilder result = new StringBuilder();
        for (String word : words) {
            int end = Math.min(count, word.length());
            result.append(word, 0, end);
        }
        return normalize(result.toString());
    }

    private static List<String> findColorNames(ItemStack stack) {
        Set<String> names = new LinkedHashSet<>();
        try {
            Minecraft minecraft = Minecraft.getInstance();
            BakedModel model = minecraft.getItemRenderer().getModel(stack, minecraft.level, null, 0);
            TextureAtlasSprite sprite = model.getParticleIcon();
            NativeImage image = sprite.contents().getOriginalImage();
            int tint = minecraft.getItemColors().getColor(stack, 0);
            addDominantColorNames(names, image, sprite.contents().width(), sprite.contents().height(), tint);
        } catch (RuntimeException | LinkageError ignored) {
        }

        String fallback = normalize(stack.getHoverName().getString()) + " "
                + normalize(BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath());
        for (NamedColor color : COLORS) {
            if (contains(fallback.replace(" ", ""), color.name())) {
                names.add(color.name());
            }
        }
        return List.copyOf(names);
    }

    private static void addDominantColorNames(
            Set<String> names,
            NativeImage image,
            int width,
            int height,
            int tint
    ) {
        Map<Integer, ColorBucket> buckets = new HashMap<>();
        int pixelCount = width * height;
        int step = Math.max(1, (int) Math.ceil(Math.sqrt(pixelCount / 4096.0D)));
        int tintRed = tint >> 16 & 255;
        int tintGreen = tint >> 8 & 255;
        int tintBlue = tint & 255;
        for (int y = 0; y < height; y += step) {
            for (int x = 0; x < width; x += step) {
                int rgba = image.getPixelRGBA(x, y);
                int alpha = rgba >> 24 & 255;
                if (alpha < 125) {
                    continue;
                }
                int red = (rgba & 255) * tintRed / 255;
                int green = (rgba >> 8 & 255) * tintGreen / 255;
                int blue = (rgba >> 16 & 255) * tintBlue / 255;
                int key = (red >> 4) << 8 | (green >> 4) << 4 | blue >> 4;
                buckets.computeIfAbsent(key, ignored -> new ColorBucket()).add(red, green, blue);
            }
        }

        buckets.values().stream()
                .sorted(Comparator.comparingInt(ColorBucket::count).reversed())
                .limit(2)
                .map(ColorBucket::color)
                .map(TradeSearchMatcher::closestColorName)
                .forEach(names::add);
    }

    private static String closestColorName(int color) {
        return COLORS.stream()
                .min(Comparator.comparingDouble(namedColor -> colorDistance(namedColor.color(), color)))
                .map(NamedColor::name)
                .orElse("");
    }

    private static double colorDistance(int first, int second) {
        int red1 = first >> 16 & 255;
        int green1 = first >> 8 & 255;
        int blue1 = first & 255;
        int red2 = second >> 16 & 255;
        int green2 = second >> 8 & 255;
        int blue2 = second & 255;
        int redMean = red1 + red2 >> 1;
        int red = red1 - red2;
        int green = green1 - green2;
        int blue = blue1 - blue2;
        double distance = ((512 + redMean) * red * red >> 8)
                + 4.0D * green * green
                + ((767 - redMean) * blue * blue >> 8);
        double gray1 = (red1 + green1 + blue1) / 3.0D;
        double gray2 = (red2 + green2 + blue2) / 3.0D;
        double grayDistance = Math.abs(gray1 - red1) + Math.abs(gray1 - green1) + Math.abs(gray1 - blue1)
                - Math.abs(gray2 - red2) - Math.abs(gray2 - green2) - Math.abs(gray2 - blue2);
        return distance + grayDistance * grayDistance / 10.0D;
    }

    private record NamedColor(String name, int color) {
    }

    private static final class ColorBucket {
        private int count;
        private int red;
        private int green;
        private int blue;

        private void add(int red, int green, int blue) {
            this.count++;
            this.red += red;
            this.green += green;
            this.blue += blue;
        }

        private int count() {
            return this.count;
        }

        private int color() {
            return (this.red / this.count) << 16 | (this.green / this.count) << 8 | this.blue / this.count;
        }
    }
}
