/*
 *
 *  * This file is part of LuckPerms, licensed under the MIT License.
 *  *
 *  *  Copyright (c) crysis992 <crysis992@gmail.com>
 *  *  Copyright (c) contributors
 *  *
 *  *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  *  of this software and associated documentation files (the "Software"), to deal
 *  *  in the Software without restriction, including without limitation the rights
 *  *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  *  copies of the Software, and to permit persons to whom the Software is
 *  *  furnished to do so, subject to the following conditions:
 *  *
 *  *  The above copyright notice and this permission notice shall be included in all
 *  *  copies or substantial portions of the Software.
 *  *
 *  *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  *  SOFTWARE.
 *
 */

package net.crytec.recipes.gui;

import net.crytec.inventoryapi.SmartInventory;
import net.crytec.inventoryapi.api.ClickableItem;
import net.crytec.inventoryapi.api.InventoryContent;
import net.crytec.inventoryapi.api.InventoryProvider;
import net.crytec.inventoryapi.api.Pagination;
import net.crytec.inventoryapi.api.SlotIterator;
import net.crytec.inventoryapi.api.SlotIterator.Type;
import net.crytec.inventoryapi.api.SlotPos;
import net.crytec.libs.commons.utils.UtilPlayer;
import net.crytec.libs.commons.utils.item.ItemBuilder;
import net.crytec.recipes.conditions.ConditionBase;
import net.crytec.recipes.data.IRecipe;
import net.crytec.recipes.io.Language;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class ConditionEditor implements InventoryProvider {


  private final ConditionBase condition;
  private final IRecipe recipe;

  public ConditionEditor(ConditionBase condition, IRecipe recipe) {
    this.condition = condition;
    this.recipe = recipe;
  }


  @Override
  public void init(Player player, InventoryContent contents) {

    Pagination pagination = contents.pagination();

    ClickableItem[] array = this.condition.getGUIRepresenter(this.recipe, player, contents, () -> this).stream().toArray(ClickableItem[]::new);

    pagination.setItems(array);
    pagination.setItemsPerPage(45);

    SlotIterator slotIterator = contents.newIterator(Type.HORIZONTAL, SlotPos.of(0, 0));
    slotIterator = slotIterator.allowOverride(false);
    pagination.addToIterator(slotIterator);

    // Next Page Button
    if (array.length > 0 && !pagination.isLast())
      contents.set(5, 7, ClickableItem.of(new ItemBuilder(Material.MAP).name(Language.INTERFACE_BUTTON_NEXT_PAGE.toString()).build(), e -> {
        contents.getHost().open(player, pagination.next().getPage());
        UtilPlayer.playSound(player, Sound.UI_BUTTON_CLICK);
      }));

    // Previous Page Button
    if (!pagination.isFirst())
      contents.set(5, 1, ClickableItem.of(new ItemBuilder(Material.MAP).name(Language.INTERFACE_BUTTON_PREVIOUS_PAGE.toString()).build(), e -> {
        contents.getHost().open(player, pagination.previous().getPage());
        UtilPlayer.playSound(player, Sound.UI_BUTTON_CLICK);
      }));

    //Back Button
    contents.set(SlotPos.of(5, 8), ClickableItem.of(new ItemBuilder(Material.BARRIER).name(Language.INTERFACE_BUTTON_BACK.toString()).build(), e -> {

      SmartInventory.builder().provider(new ConditionListGUI(this.recipe))
          .title(Language.INTERFACE_TITLE_CONDITIONLIST.toString())
          .size(6)
          .build().open(player);

      UtilPlayer.playSound(player, Sound.BLOCK_LEVER_CLICK, 1, 1.2F);
    }));

  }
}