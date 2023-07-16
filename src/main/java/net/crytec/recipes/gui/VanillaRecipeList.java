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

import com.google.common.collect.Lists;
import java.util.List;
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
import net.crytec.recipes.CustomRecipes;
import net.crytec.recipes.io.Language;
import net.crytec.recipes.manager.RecipeManager;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

public class VanillaRecipeList implements InventoryProvider {

  private final RecipeManager manager;

  public VanillaRecipeList() {
    this.manager = CustomRecipes.getInstance().getRecipeManager();
  }

  @Override
  public void init(Player player, InventoryContent contents) {

    Pagination pagination = contents.pagination();
    List<ClickableItem> items = Lists.newArrayList();

    this.manager.getVanillaRecipes().forEach((id, recipe) -> {
      ItemBuilder icon = new ItemBuilder(recipe.getIcon().clone()).lore(Language.GENERAL_LEFT_CHANGE.toString()).setItemFlag(ItemFlag.HIDE_ATTRIBUTES);
      if (recipe.isDisabled()) {
        icon.enchantment(Enchantment.ARROW_DAMAGE);
        icon.setItemFlag(ItemFlag.HIDE_ENCHANTS);
        icon.lore(Language.INTERFACE_VANILLA_STATUS.toString().replace("%status%", Language.INTERFACE_VANILLA_STATUS_DISABLED.toString()));
      } else
        icon.lore(Language.INTERFACE_VANILLA_STATUS.toString().replace("%status%", Language.INTERFACE_VANILLA_STATUS_ENABLED.toString()));

      items.add(ClickableItem.of(icon.build(), e -> {
        if (recipe.isDisabled()) {
          recipe.activate();
          UtilPlayer.playSound(player, Sound.BLOCK_NOTE_BLOCK_BELL, 0.5F, 1.2F);
          this.reopen(player, contents);
        } else {
          recipe.deactivate();
          UtilPlayer.playSound(player, Sound.BLOCK_NOTE_BLOCK_BELL, 0.5F, 0.6F);
          this.reopen(player, contents);
        }
      }));
    });

    pagination.setItems(items.toArray(new ClickableItem[items.size()]));
    pagination.setItemsPerPage(45);

    SlotIterator slotIterator = contents.newIterator(Type.HORIZONTAL, SlotPos.of(0, 0));
    slotIterator = slotIterator.allowOverride(false);
    pagination.addToIterator(slotIterator);

//		contents.set(SlotPos.of(4, 6), ClickableItem.of(new ItemBuilder(Material.ACACIA_BOAT).name("Test").build(), e -> {
//			CustomRecipes.getInstance().getResultBlockManager().openEdit(player);
//		}));

    // Next Page Button
    if (items.size() > 0 && !pagination.isLast())
      contents.set(5, 6, ClickableItem.of(new ItemBuilder(Material.MAP).name(Language.INTERFACE_BUTTON_NEXT_PAGE.toString()).build(), e -> {
        contents.getHost().open(player, pagination.next().getPage());
        UtilPlayer.playSound(player, Sound.UI_BUTTON_CLICK);
      }));

    // Previous Page Button
    if (!pagination.isFirst())
      contents.set(5, 2, ClickableItem.of(new ItemBuilder(Material.MAP).name(Language.INTERFACE_BUTTON_PREVIOUS_PAGE.toString()).build(), e -> {
        contents.getHost().open(player, pagination.previous().getPage());
        UtilPlayer.playSound(player, Sound.UI_BUTTON_CLICK);
      }));

    //Back Button
    contents.set(SlotPos.of(5, 8), ClickableItem.of(new ItemBuilder(Material.BARRIER).name(Language.INTERFACE_BUTTON_BACK.toString()).build(), e -> {
      SmartInventory.builder().provider(new RecipeListGUI()).size(5).title(Language.INTERFACE_TITLE_RECIPELIST.toString()).build().open(player);
      UtilPlayer.playSound(player, Sound.BLOCK_LEVER_CLICK, 1, 1.2F);
    }));

  }
}