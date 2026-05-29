package com.molox.infcedim;

import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

public class InfCeDimConfig {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.IntValue SCENARIO_CORE_COOLDOWN_SECONDS;
    public static final ModConfigSpec.DoubleValue MARK_RADIUS;
    public static final ModConfigSpec.BooleanValue ENTITY_LIST_IS_WHITELIST;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> ENTITY_LIST;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.comment("Scenario Core Settings");
        SCENARIO_CORE_COOLDOWN_SECONDS = builder
                .comment("Cooldown time for the Scenario Core item in seconds. Default: 60")
                .defineInRange("scenario_core_cooldown_seconds", 60, 1, 3600);
        MARK_RADIUS = builder
                .comment("Maximum radius in blocks for keeping entity marks. Default: 10.0")
                .defineInRange("mark_radius", 10.0, 1.0, 128.0);
        ENTITY_LIST_IS_WHITELIST = builder
                .comment("If true, entity_list is a whitelist (only listed entities can be marked).",
                        "If false, entity_list is a blacklist (listed entities cannot be marked).")
                .define("entity_list_is_whitelist", false);
        ENTITY_LIST = builder
                .comment("List of entity IDs affected by the whitelist/blacklist.",
                        "Supports exact IDs (e.g. minecraft:ender_dragon) and wildcard namespace (e.g. minecraft:*).")
                .defineListAllowEmpty("entity_list", List.of(), e -> e instanceof String s && isValidEntry(s));

        SPEC = builder.build();
    }

    private static boolean isValidEntry(String s) {
        if (s.contains(":*")) {
            String[] parts = s.split(":\\*", 2);
            return parts.length == 2 && parts[1].isEmpty() && !parts[0].isEmpty();
        }
        return s.contains(":") && s.split(":", 2)[0].length() > 0 && s.split(":", 2)[1].length() > 0;
    }

    public static boolean isEntityAllowed(String entityId) {
        List<? extends String> list = ENTITY_LIST.get();
        boolean isWhitelist = ENTITY_LIST_IS_WHITELIST.get();
        String namespace = entityId.contains(":") ? entityId.split(":", 2)[0] : "";

        boolean matched = false;
        for (String entry : list) {
            if (entry.equals(entityId)) {
                matched = true;
                break;
            }
            if (entry.endsWith(":*") && entry.substring(0, entry.length() - 2).equals(namespace)) {
                matched = true;
                break;
            }
        }

        return isWhitelist ? matched : !matched;
    }
}