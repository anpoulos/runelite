package net.runelite.client.plugins.inventoryprice;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(
    keyName = "inventoryprice",
    name = "Inventory Price",
    description = "Configuration for the Inventory Price"
)

public interface InventoryPriceConfig extends Config {

    @ConfigItem(
            keyName = "includePouch",
            name = "Include Rune Pouch",
            description = "Determines whether to include runes inside pouch for calculation"
    )
    default boolean includePouch()
    {
        return false;
    }
}
