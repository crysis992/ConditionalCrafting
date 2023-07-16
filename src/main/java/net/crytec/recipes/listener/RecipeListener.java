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

package net.crytec.recipes.listener;

import me.clip.placeholderapi.PlaceholderAPI;
import net.crytec.recipes.CustomRecipes;
import net.crytec.recipes.data.IRecipe;
import net.crytec.recipes.manager.RecipeManager;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;

public class RecipeListener implements Listener {

  private final RecipeManager manager;
  private final boolean placeHolderSupport;

  public RecipeListener(final CustomRecipes plugin, final RecipeManager manager, final boolean placeHolderSupport) {
    this.manager = manager;
    this.placeHolderSupport = placeHolderSupport;
  }

  @EventHandler
  public void checkRecipeConditions(final PrepareItemCraftEvent event) {
    if (event.getRecipe() == null) {
      return;
    }

    NamespacedKey key = null;

    if (event.getRecipe() instanceof ShapedRecipe) {
      key = ((ShapedRecipe) event.getRecipe()).getKey();
    } else if (event.getRecipe() instanceof ShapelessRecipe) {
      key = ((ShapelessRecipe) event.getRecipe()).getKey();
    }
    if (!this.manager.isCustomRecipe(key)) {
      return;
    }

    final IRecipe recipe = this.manager.getRecipeByKey(key);

    final Player player = (Player) event.getView().getPlayer();

    recipe.getConditions().stream().filter(condition -> !condition.test(player)).forEach(condition -> {
      event.getInventory().setResult(new ItemStack(Material.AIR));
      condition.onFail(player);
    });

    final ItemStack item = event.getInventory().getResult();
    if (item == null || item.getType() == Material.AIR) {
      return;
    }

    if (!item.hasItemMeta()) {
      return;
    }
    if (this.placeHolderSupport) {
      final ItemMeta meta = item.getItemMeta();
      if (meta.hasDisplayName()) {
        meta.setDisplayName(PlaceholderAPI.setPlaceholders(player, meta.getDisplayName()));
      }
      if (meta.hasLore()) {
        meta.setLore(PlaceholderAPI.setPlaceholders(player, meta.getLore()));
      }

      item.setItemMeta(meta);
    }
    event.getInventory().setResult(item);
  }
}