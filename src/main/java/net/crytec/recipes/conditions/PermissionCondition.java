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

package net.crytec.recipes.conditions;

import com.google.common.collect.Sets;
import java.util.LinkedHashSet;
import java.util.function.Supplier;
import lombok.Getter;
import lombok.Setter;
import net.crytec.inventoryapi.anvil.AnvilGUI;
import net.crytec.inventoryapi.api.ClickableItem;
import net.crytec.inventoryapi.api.InventoryContent;
import net.crytec.inventoryapi.api.InventoryProvider;
import net.crytec.libs.commons.utils.item.ItemBuilder;
import net.crytec.recipes.CustomRecipes;
import net.crytec.recipes.conditions.annotations.ConditionDefaults;
import net.crytec.recipes.data.IRecipe;
import net.crytec.recipes.io.Language;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

@ConditionDefaults(name = "Permission", description = {"&7The item can only be crafted with", "&7the selected permission node."})
public class PermissionCondition extends ConditionBase {

  @Setter
  @Getter
  private String permission = "default.permission";

  public PermissionCondition() {
    super("Permission");
  }

  @Override
  public boolean test(final Player player) {
    return player.hasPermission(this.permission);
  }

  @Override
  public void onFail(final Player player) {
    player.sendMessage(Language.CONDITION_PERMISSION_ERROR.toChatString());
  }

  @Override
  public LinkedHashSet<ClickableItem> getGUIRepresenter(final IRecipe recipe, final Player player, final InventoryContent contents, final Supplier<InventoryProvider> click) {

    final LinkedHashSet<ClickableItem> items = Sets.newLinkedHashSet();

    items.add(new ClickableItem(new ItemBuilder(Material.BOOK).name(Language.CONDITION_PERMISSION_BUTTON.toString())
        .lore(Language.CONDITION_PERMISSION_DESC.toString())
        .lore("�f" + this.permission)
        .build(), e -> {

      player.closeInventory();

      new AnvilGUI(player, "Permission", (p, perm) -> {
        this.permission = perm;
        Bukkit.getScheduler().runTaskLater(CustomRecipes.getInstance(), () -> click.get().reopen(player, contents), 1L);
        return null;
      });
    }));

    return items;
  }

  @Override
  public void save(final YamlConfiguration config) {
    config.set(this.getConfigPath("permission"), this.permission);
  }

  @Override
  public void loadConditions(final YamlConfiguration config) {
    this.permission = config.getString(this.getConfigPath("permission"));
  }
}