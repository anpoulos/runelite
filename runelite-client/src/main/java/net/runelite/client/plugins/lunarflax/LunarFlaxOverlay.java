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
package net.runelite.client.plugins.lunarflax;

import net.runelite.api.*;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;

class LunarFlaxOverlay extends Overlay
{
	private final Client client;
	private final PanelComponent panelComponent = new PanelComponent();

	@Inject
	LunarFlaxOverlay(Client client)
	{
		setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
		this.client = client;
	}

	@Override
	public Dimension render(Graphics2D graphics) {

		ItemContainer container = client.getItemContainer(InventoryID.INVENTORY);

		if(container == null)
		{
			return null;
		}

		final Item[] items = container.getItems();
		int flaxTotal = 0;

		for(Item item : items)
		{
			if(item.getId() == 1779)
			{
				flaxTotal += 1;
			}
		}

		final String flaxString = "Flax: "+flaxTotal;

		panelComponent.getChildren().clear();

		panelComponent.getChildren().add(TitleComponent.builder()
				.text(flaxString)
				.color(flaxTotal == 0 ? Color.RED : Color.GREEN)
				.build());

		panelComponent.setPreferredSize(new Dimension(
				graphics.getFontMetrics().stringWidth(flaxString) + 10,
				0));

		return panelComponent.render(graphics);
	}
}
