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

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;
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
import net.crytec.recipes.conditions.ConditionBase;
import net.crytec.recipes.conditions.ConditionRegistrar;
import net.crytec.recipes.data.IRecipe;
import net.crytec.recipes.data.RecipeType;
import net.crytec.recipes.io.Language;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.plugin.java.JavaPlugin;

public class ConditionListGUI implements InventoryProvider {

  private static final CustomRecipes plugin = JavaPlugin.getPlugin(CustomRecipes.class);

  private final IRecipe recipe;

  public ConditionListGUI(IRecipe recipe) {
    this.recipe = recipe;
  }

  @Override
  public void init(Player player, InventoryContent contents) {

    ConditionRegistrar registrar = plugin.getConditionRegistrar();

    Pagination pagination = contents.pagination();
    ArrayList<ClickableItem> items = new ArrayList<ClickableItem>();

    for (Class<? extends ConditionBase> clazz : registrar.getConditions()) {
      String id = registrar.getID(clazz);
      Material icon = registrar.getIcon(id);

      Optional<ConditionBase> condition = this.recipe.getConditions().stream().filter(cb -> cb.getId().equals(id)).findFirst();
      boolean present = condition.isPresent();

      String name = Language.getFile().getString("condition." + id + ".name");

      ItemBuilder builder = new ItemBuilder(icon).name("�f" + ChatColor.translateAlternateColorCodes('&', name));

      builder.lore("");
      builder.lore(Language.getFile().getStringList("condition." + id + ".desc").stream().map(l -> ChatColor.translateAlternateColorCodes('&', l)).collect(Collectors.toList()));
      builder.lore("");

      if (present)
        builder.enchantment(Enchantment.ARROW_INFINITE)
            .setItemFlag(ItemFlag.HIDE_ENCHANTS)
            .lore(Language.INTERFACE_LEFT_CLICK_CONFIG.toString())
            .lore(Language.INTERFACE_RIGHT_CLICK_DELETE.toString());
      else
        builder.lore(Language.INTERFACE_LEFT_CLICK_ADD_CONDITION.toString());

      items.add(ClickableItem.of(builder.build(), e -> {

        if (present) {

          if (e.isRightClick()) {
            this.recipe.getConditions().remove(condition.get());
            this.reopen(player, contents);
            UtilPlayer.playSound(player, Sound.ENTITY_EGG_THROW, 1, 1.5F);
            return;
          }

          SmartInventory.builder().provider(new ConditionEditor(condition.get(), this.recipe))
              .size(6)
              .title(Language.INTERFACE_TITLE_CONDITIONEDIT.toString())
              .build().open(player);

          UtilPlayer.playSound(player, Sound.UI_BUTTON_CLICK);

        } else {
          ConditionBase conditionInstance = registrar.getNewInstance(clazz);
          this.recipe.getConditions().add(conditionInstance);
          contents.getHost().open(player);
          UtilPlayer.playSound(player, Sound.ENTITY_PLAYER_LEVELUP);
        }

      }));

    }

    ClickableItem[] c = new ClickableItem[items.size()];
    c = items.toArray(c);

    pagination.setItems(c);
    pagination.setItemsPerPage(27);

    contents.set(SlotPos.of(4, 5), ClickableItem.of(new ItemBuilder(Material.TNT).name(Language.INTERFACE_MAIN_DELETE_RECIPE.toString()).build(), e -> {
      plugin.getRecipeManager().removeRecipe(this.recipe);
      UtilPlayer.playSound(player, Sound.ENTITY_EGG_THROW, 1, 1.5F);
      player.sendMessage(Language.INTERFACE_RECIPE_DELETED.toChatString());
      SmartInventory.builder().provider(new RecipeListGUI()).size(5).title(Language.INTERFACE_TITLE_RECIPELIST.toString()).build().open(player);
    }));

    contents.set(SlotPos.of(4, 3), ClickableItem.of(new ItemBuilder(Material.CRAFTING_TABLE).name(Language.INTERFACE_MAIN_EDITSHAPE.toString()).build(), e -> {
      if (this.recipe.getType() == RecipeType.FURNACE) {
        player.closeInventory();
        UtilPlayer.playSound(player, Sound.UI_BUTTON_CLICK);
        this.recipe.openEditor(player);
      } else {
        player.closeInventory();
        UtilPlayer.playSound(player, Sound.UI_BUTTON_CLICK);
        this.recipe.openEditor(player);
      }
    }));

    if (items.size() > 0 && !pagination.isLast())
      contents.set(4, 7, ClickableItem.of(new ItemBuilder(Material.MAP).name(Language.INTERFACE_BUTTON_NEXT_PAGE.toString()).build(), e -> {
        contents.getHost().open(player, pagination.next().getPage());
        UtilPlayer.playSound(player, Sound.UI_BUTTON_CLICK);
      }));

    if (!pagination.isFirst())
      contents.set(4, 1, ClickableItem.of(new ItemBuilder(Material.MAP).name(Language.INTERFACE_BUTTON_PREVIOUS_PAGE.toString()).build(), e -> {
        contents.getHost().open(player, pagination.previous().getPage());
        UtilPlayer.playSound(player, Sound.UI_BUTTON_CLICK);
      }));

    SlotIterator slotIterator = contents.newIterator(Type.HORIZONTAL, SlotPos.of(0, 0));
    slotIterator = slotIterator.allowOverride(false);
    pagination.addToIterator(slotIterator);

    contents.set(SlotPos.of(4, 8), ClickableItem.of(new ItemBuilder(Material.BARRIER).name(Language.INTERFACE_BUTTON_BACK.toString()).build(), e -> {
      SmartInventory.builder().provider(new RecipeListGUI()).size(5).title(Language.INTERFACE_TITLE_RECIPELIST.toString()).build().open(player);
      UtilPlayer.playSound(player, Sound.BLOCK_LEVER_CLICK, 1, 1.2F);
    }));

  }
}