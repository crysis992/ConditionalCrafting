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
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import net.crytec.inventoryapi.SmartInventory;
import net.crytec.inventoryapi.anvil.AnvilGUI;
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
import net.crytec.recipes.data.IRecipe;
import net.crytec.recipes.data.RecipeType;
import net.crytec.recipes.editor.RecipeEditSession;
import net.crytec.recipes.io.Language;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class RecipeListGUI implements InventoryProvider {

  private static final CustomRecipes plugin = JavaPlugin.getPlugin(CustomRecipes.class);
  private static final Pattern VALID_KEY = Pattern.compile("[a-z0-9/._-]+");

  @Override
  public void init(final Player player, final InventoryContent contents) {

    final Pagination pagination = contents.pagination();
    final ArrayList<ClickableItem> items = new ArrayList<>();

    for (final IRecipe recipe : plugin.getRecipeManager().getRecipes()) {

      final String type = recipe.getType().getDisplayname();
      final List<String> conditions = recipe.getConditions().stream().map(con -> ("�f- " + con.getId())).collect(Collectors.toList());

      final ItemBuilder icon = new ItemBuilder(recipe.getResult().clone())
          .name(ChatColor.WHITE + recipe.getKey().getKey())
          .lore(ChatColor.YELLOW + "------------------------")
          .lore(ChatColor.WHITE + type)
          .lore(conditions)
          .lore("")
          .lore(Language.INTERFACE_LEFT_CLICK_CONFIG.toString());

      if (recipe.getType() == RecipeType.FURNACE) {
        icon.lore("");
        icon.lore(Language.ERROR_APPLY_FURNACE.toString());
      }

      items.add(ClickableItem.of(icon.build(), e -> {
        UtilPlayer.playSound(player, Sound.UI_BUTTON_CLICK);

        SmartInventory.builder().provider(new ConditionListGUI(recipe))
            .title(Language.INTERFACE_TITLE_CONDITIONEDIT.toString())
            .size(6)
            .build()
            .open(player);
      }));

    }

    contents.set(SlotPos.of(4, 3), ClickableItem.of(new ItemBuilder(Material.NETHER_STAR).name(Language.INTERFACE_MAIN_ADDRECIPE.toString()).build(), e -> {
      UtilPlayer.playSound(player, Sound.UI_BUTTON_CLICK);
      new AnvilGUI(player, "ID..", (p, id) -> {

        final String recipeID = id.toLowerCase().replace(" ", "");

        final Optional<NamespacedKey> key = plugin.getRecipeManager().getRecipeKeys().stream().filter(k -> k.getKey().equals(recipeID)).findFirst();
        if (key.isPresent()) {
          p.sendMessage(Language.ERROR_INVALID_RECIPE_PRESENT.toChatString());
          UtilPlayer.playSound(player, Sound.ENTITY_LEASH_KNOT_BREAK, 1.2F, 0.4F);
          return Language.ERROR_INVALID_RECIPE_PRESENT.toString();
        }

        if (!VALID_KEY.matcher(recipeID).matches()) {
          UtilPlayer.playSound(player, Sound.ENTITY_LEASH_KNOT_BREAK, 1.2F, 0.4F);
          p.sendMessage("Invalid key. Must be [a-z0-9/._-]: " + recipeID);
          return "Invalid key. Must be [a-z0-9/._-]: " + recipeID;
        }
        player.sendMessage("Your input was " + id);
        final RecipeEditSession session = new RecipeEditSession(player, RecipeType.SHAPELESS, recipeID);
        Bukkit.getScheduler().runTaskLater(plugin, session::openInterface, 1L);
        UtilPlayer.playSound(player, Sound.UI_BUTTON_CLICK);
        return null;
      });


    }));

    contents.set(SlotPos.of(4, 5), ClickableItem.of(new ItemBuilder(Material.CRAFTING_TABLE).name(Language.INTERFACE_MAIN_DISABLEVANILLA.toString()).build(), e -> {
      SmartInventory.builder().provider(new VanillaRecipeList()).title(Language.INTERFACE_VANILLA_RECIPE_TITLE.toString()).size(6).build().open(player);
      UtilPlayer.playSound(player, Sound.UI_BUTTON_CLICK);
    }));

    ClickableItem[] c = new ClickableItem[items.size()];
    c = items.toArray(c);
    pagination.setItems(c);
    pagination.setItemsPerPage(27);

    if (items.size() > 0 && !pagination.isLast()) {
      contents.set(4, 7, ClickableItem.of(new ItemBuilder(Material.MAP).name(Language.INTERFACE_BUTTON_NEXT_PAGE.toString()).build(), e -> {
        contents.getHost().open(player, pagination.next().getPage());
        UtilPlayer.playSound(player, Sound.UI_BUTTON_CLICK);
      }));
    }

    if (!pagination.isFirst()) {
      contents.set(4, 1, ClickableItem.of(new ItemBuilder(Material.MAP).name(Language.INTERFACE_BUTTON_PREVIOUS_PAGE.toString()).build(), e -> {
        contents.getHost().open(player, pagination.previous().getPage());
        UtilPlayer.playSound(player, Sound.UI_BUTTON_CLICK);
      }));
    }

    SlotIterator slotIterator = contents.newIterator(Type.HORIZONTAL, SlotPos.of(0, 0));
    slotIterator = slotIterator.allowOverride(false);
    pagination.addToIterator(slotIterator);

  }
}