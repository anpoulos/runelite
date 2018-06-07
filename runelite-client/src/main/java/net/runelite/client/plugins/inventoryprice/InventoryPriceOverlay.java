/*
 * Copyright (c) 2018, Angelo Poulos
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.inventoryprice;

import net.runelite.api.*;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.runepouch.Runes;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;
import net.runelite.http.api.item.ItemPrice;

import javax.inject.Inject;
import java.awt.*;
import java.text.NumberFormat;

class InventoryPriceOverlay extends Overlay
{

    private static final Varbits[] AMOUNT_VARBITS =
            {
                    Varbits.RUNE_POUCH_AMOUNT1, Varbits.RUNE_POUCH_AMOUNT2, Varbits.RUNE_POUCH_AMOUNT3
            };
    private static final Varbits[] RUNE_VARBITS =
            {
                    Varbits.RUNE_POUCH_RUNE1, Varbits.RUNE_POUCH_RUNE2, Varbits.RUNE_POUCH_RUNE3
            };

    private final Client client;
    private final PanelComponent panelComponent = new PanelComponent();
    private final InventoryPriceConfig config;

    @Inject
    private ItemManager itemManager;

    @Inject
    InventoryPriceOverlay(Client client, InventoryPriceConfig config)
    {
        setPosition(OverlayPosition.BOTTOM_RIGHT);
        this.client = client;
        this.config = config;
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        ItemContainer container = client.getItemContainer(InventoryID.INVENTORY);

        if(container == null)
        {
            return null;
        }

        final Item[] items = container.getItems();
        int geTotal = 0;

        for(Item item : items)
        {
            if(item == null){
                continue;
            }

            int id = item.getId();
            int price = 0;

            switch(id)
            {
                case -1:
                    continue;

                    //courtesy of the RunePouch plugin for handling the hard parts
                case ItemID.RUNE_POUCH:
                    if(!config.includePouch())
                    {
                        continue;
                    }
                    assert AMOUNT_VARBITS.length == RUNE_VARBITS.length;
                    for (int i = 0; i < AMOUNT_VARBITS.length; i++)
                    {
                        Varbits amountVarbit = AMOUNT_VARBITS[i];

                        int amount = client.getVar(amountVarbit);
                        if (amount <= 0)
                        {
                            continue;
                        }

                        Varbits runeVarbit = RUNE_VARBITS[i];
                        int runeId = client.getVar(runeVarbit);
                        Runes rune = Runes.getRune(runeId);
                        if (rune == null)
                        {
                            continue;
                        }

                        price += GetTotalPrice(amount, rune.getItemId());
                    }
                    break;

                default:
                    price = GetTotalPrice(item.getQuantity(), id);
                    break;
            }

            geTotal += price;
        }

        final String geTotalString = "GE Total: "+NumberFormat.getInstance().format(geTotal);

        panelComponent.getChildren().clear();

        panelComponent.getChildren().add(TitleComponent.builder()
                .text(geTotalString)
                .color(Color.WHITE)
                .build());

        panelComponent.setPreferredSize(new Dimension(
                graphics.getFontMetrics().stringWidth(geTotalString) + 10,
                0));

        return panelComponent.render(graphics);
    }

    private int GetTotalPrice(int quantity, int itemId)
    {
        if(itemId == ItemID.COINS_995)
        {
            return quantity;
        }

        final ItemPrice itemPrice = itemManager.getItemPriceAsync(itemId);
        if (itemPrice != null)
        {
            return itemPrice.getPrice() * quantity;
        }
        return 0;
    }
}
