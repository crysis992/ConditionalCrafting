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

package net.crytec.recipes.editor;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import lombok.Setter;
import net.crytec.libs.commons.utils.UtilMath;
import net.crytec.libs.commons.utils.chatinput.ChatInput;
import net.crytec.libs.commons.utils.item.ItemBuilder;
import net.crytec.recipes.CustomRecipes;
import net.crytec.recipes.data.RecipeType;
import net.crytec.recipes.io.Language;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class RecipeEditSession {

  private static final ImmutableList<Integer> reservedSlots = ImmutableList.of(11, 12, 13, 20, 21, 22, 29, 30, 31, 24);
  protected static final ItemStack separator = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).name(" ").build();

  @Getter
  private RecipeType recipeType;
  @Getter
  private final String id;
  @Getter
  @Setter
  private String group = "";

  @Getter
  @Setter
  private int cookingTime = 200;
  @Getter
  @Setter
  private float experience = 20;
  @Getter
  private final Player player;
  @Getter
  private final Inventory inventory;

  @Getter
  @Setter
  private boolean isEditMode = false;

  public RecipeEditSession(final Player player, final RecipeType type, final String id) {
    this.player = player;
    this.recipeType = type;
    this.id = id;

    this.inventory = Bukkit.createInventory(player, 54, Language.INTERFACE_TITLE_RECIPEEDIT.toString());
    final ItemStack separator = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).name(" ").build();

    for (int i = 0; i < 54; i++) {
      if (reservedSlots.contains(i)) {
        continue;
      }
      this.inventory.setItem(i, separator);
    }

    this.inventory.setItem(18, new ItemBuilder(RecipeType.SHAPED.getIcon()).name(RecipeType.SHAPED.getDisplayname()).build());
    this.inventory.setItem(53, new ItemBuilder(Material.EMERALD).name(Language.INTERFACE_BUTTON_SAVE.toString()).build());
  }

  public void openInterface() {
    this.player.openInventory(this.inventory);
  }

  public void handleCookingTimeButton() {
    new ChatInput(this.player, "Please enter the cook time (in Ticks):", true, input -> {
      if (!UtilMath.isInt(input)) {
        Bukkit.getScheduler().runTaskLater(CustomRecipes.getInstance(), () -> this.player.sendMessage("Input must be a number."), 1L);
        this.player.openInventory(this.inventory);
        return;
      }
      this.cookingTime = Integer.parseInt(input);
      this.player.openInventory(this.inventory);
      new ItemBuilder(this.inventory.getItem(40)).lore(Language.INTERFACE_BUTTON_COOKING_DESC.toString().replace("%time%", "" + this.cookingTime)).build();
    });
  }

  public void handleExperienceButton() {
    new ChatInput(this.player, "Please enter the experience:", true, input -> {
      if (!UtilMath.isInt(input)) {
        Bukkit.getScheduler().runTaskLater(CustomRecipes.getInstance(), () -> this.player.sendMessage("Input must be a number."), 1L);
        this.player.openInventory(this.inventory);
        return;
      }
      this.experience = Integer.parseInt(input);
      this.player.openInventory(this.inventory);
      new ItemBuilder(this.inventory.getItem(40)).lore(Language.INTERFACE_BUTTON_EXP_DESC.toString().replace("%exp%", "" + this.experience)).build();
    });
  }

  public void setRecipeType(final RecipeType type) {
    this.recipeType = type;
    this.setInventoryType(type);
  }

  private RecipeType setInventoryType(final RecipeType type) {
    this.inventory.setItem(18, new ItemBuilder(type.getIcon()).name(type.getDisplayname()).build());

    if (type == RecipeType.FURNACE) {
      reservedSlots.forEach(s -> {
        if (s != 22 && s != 24) {
          if (this.inventory.getItem(s) != null && !this.inventory.getItem(s).isSimilar(separator)) {
            this.player.getInventory().addItem(this.inventory.getItem(s));
          }
          this.inventory.setItem(s, separator);
        }
      });

      this.inventory.setItem(38, new ItemBuilder(Material.EXPERIENCE_BOTTLE).name(Language.INTERFACE_BUTTON_EXP.toString()).lore(Language.INTERFACE_BUTTON_EXP_DESCDEF.toString()).build());
      this.inventory.setItem(40, new ItemBuilder(Material.CLOCK).name(Language.INTERFACE_BUTTON_COOKING.toString()).lore(Language.INTERFACE_BUTTON_COOKING_DESCDEF.toString()).build());

    } else {
      reservedSlots.forEach(s -> {
        if (s != 22 && s != 24) {
          if (this.inventory.getItem(s) != null && !this.inventory.getItem(s).isSimilar(separator)) {
            this.player.getInventory().addItem(this.inventory.getItem(s));
          }
          this.inventory.setItem(s, null);
        }
      });
      this.inventory.setItem(38, separator);
      this.inventory.setItem(40, separator);
    }
    return type;
  }

}