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

import com.google.common.collect.Maps;
import java.util.HashMap;
import net.crytec.inventoryapi.SmartInventory;
import net.crytec.libs.commons.utils.UtilPlayer;
import net.crytec.recipes.CustomRecipes;
import net.crytec.recipes.data.CustomFurnaceRecipe;
import net.crytec.recipes.data.CustomShapedRecipe;
import net.crytec.recipes.data.CustomShapelessRecipe;
import net.crytec.recipes.data.RecipeType;
import net.crytec.recipes.gui.RecipeListGUI;
import net.crytec.recipes.io.Language;
import net.crytec.recipes.manager.RecipeManager;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class RecipeEditManager implements Listener {

  private final HashMap<Player, RecipeEditSession> sessions;
  private final RecipeManager manager;
  private final CustomRecipes plugin;

  public RecipeEditManager(final CustomRecipes plugin, final RecipeManager manager) {
    Bukkit.getPluginManager().registerEvents(this, plugin);
    this.plugin = plugin;
    this.manager = manager;
    this.sessions = Maps.newHashMap();
  }

  public void addEditSession(final Player player, final RecipeEditSession session) {
    this.sessions.put(player, session);
  }

  public void removeEditSession(final Player player) {
    this.sessions.remove(player);
  }

  public boolean hasEditSession(final Player player) {
    return this.sessions.containsKey(player);
  }

  public RecipeEditSession getSession(final Player player) {
    return this.sessions.get(player);
  }

  private void saveRecipe(final RecipeEditSession session) {
    final Player player = session.getPlayer();
    if (session.getRecipeType() == RecipeType.SHAPELESS) {
      final CustomShapelessRecipe recipe = CustomShapelessRecipe.registerNewRecipe(session);
      if (recipe == null) {
        UtilPlayer.playSound(player, Sound.ENTITY_LEASH_KNOT_BREAK, 1.2F, 0.4F);
        return;
      }
      recipe.saveRecipe();
      UtilPlayer.playSound(player, Sound.ENTITY_PLAYER_LEVELUP);
      player.sendMessage(Language.GENERAL_MESSAGE_RECIPE_ADDED.toChatString().replace("%id%", session.getId()));
    } else if (session.getRecipeType() == RecipeType.SHAPED) {
      final CustomShapedRecipe recipe = CustomShapedRecipe.registerNewRecipe(session);
      if (recipe == null) {
        UtilPlayer.playSound(player, Sound.ENTITY_LEASH_KNOT_BREAK, 1.2F, 0.4F);
        return;
      }
      recipe.saveRecipe();
      UtilPlayer.playSound(player, Sound.ENTITY_PLAYER_LEVELUP);
      player.sendMessage(Language.GENERAL_MESSAGE_RECIPE_ADDED.toChatString().replace("%id%", session.getId()));
    } else {
      final CustomFurnaceRecipe recipe = CustomFurnaceRecipe.registerNewRecipe(session);
      if (recipe == null) {
        UtilPlayer.playSound(player, Sound.ENTITY_LEASH_KNOT_BREAK, 1.2F, 0.4F);
        return;
      }
      recipe.saveRecipe();
      UtilPlayer.playSound(player, Sound.ENTITY_PLAYER_LEVELUP);
      player.sendMessage(Language.GENERAL_MESSAGE_RECIPE_ADDED.toChatString().replace("%id%", session.getId()));
    }
    Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
      SmartInventory.builder().provider(new RecipeListGUI()).size(5).title(Language.INTERFACE_TITLE_RECIPELIST.toString()).build().open(player);
    }, 3L);
  }


  @EventHandler
  public void onInventoryClick(final InventoryClickEvent event) {
    final Player player = (Player) event.getWhoClicked();
    final Inventory inventory = event.getClickedInventory();

    if (inventory == null || !inventory.equals(event.getView().getTopInventory()) || !this.hasEditSession(player)) {
      return;
    }

    final RecipeEditSession session = this.getSession(player);

    if (session.getRecipeType() == RecipeType.FURNACE) {
      if (event.getSlot() != 22 && event.getSlot() != 24) {
        event.setCancelled(true);
      }
    }

    if (!this.manager.isReservedSlot(event.getSlot())) {
      event.setCancelled(true);

      switch (event.getSlot()) {
        case 18:
          session.setRecipeType(session.getRecipeType().getNext());
          UtilPlayer.playSound(player, Sound.UI_BUTTON_CLICK);
          break;
        case 40:
          session.handleCookingTimeButton();
          UtilPlayer.playSound(player, Sound.UI_BUTTON_CLICK);
          break;
        case 38:
          session.handleExperienceButton();
          UtilPlayer.playSound(player, Sound.UI_BUTTON_CLICK);
          break;
        case 53:
          this.saveRecipe(session);
          UtilPlayer.playSound(player, Sound.UI_BUTTON_CLICK);
          player.closeInventory();
          break;
        default:
          break;
      }
    }
  }

  @EventHandler
  public void saveOnClose(final InventoryCloseEvent event) {
    final Player p = (Player) event.getPlayer();

    if (!this.hasEditSession(p)) {
      return;
    }

    final Inventory inv = event.getInventory();

    final RecipeEditSession session = this.getSession(p);
    this.removeEditSession(p);

    if (session.getRecipeType() == RecipeType.FURNACE) {
      final ItemStack item = inv.getItem(22);
      final ItemStack item2 = inv.getItem(24);

      if (item != null) {
        p.getInventory().addItem(item);
      }

      if (item2 != null) {
        p.getInventory().addItem(item2);
      }


    } else {
      this.manager.getReservedSlots().forEach(s -> {
        if (s != 22 && s != 24) {
          if (inv.getItem(s) != null && !inv.getItem(s).isSimilar(RecipeEditSession.separator)) {
            p.getInventory().addItem(inv.getItem(s));
          }
          inv.setItem(s, null);
        }
      });
    }
  }
}