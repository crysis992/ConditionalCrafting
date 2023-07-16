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
import net.crytec.libs.commons.utils.UtilMath;
import net.crytec.libs.commons.utils.UtilPlayer;
import net.crytec.libs.commons.utils.item.ItemBuilder;
import net.crytec.recipes.CustomRecipes;
import net.crytec.recipes.conditions.annotations.ConditionDefaults;
import net.crytec.recipes.conditions.annotations.ConditionHook;
import net.crytec.recipes.data.IRecipe;
import net.crytec.recipes.io.Language;
import net.crytec.recipes.manager.HookManager.HookType;
import net.crytec.recipes.manager.hooks.VaultHook;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

@ConditionHook(type = HookType.VAULT)
@ConditionDefaults(name = "Vault", description = {"&7The item can only be crafted when the player", "&7has a minimum of <money>"})
public class VaultCondition extends ConditionBase {

  private static final VaultHook vault;

  static {
    vault = CustomRecipes.getInstance().getHookManager().getVault();
  }

  @Setter
  @Getter
  private double amount = 1;

  public VaultCondition() {
    super("Vault");
  }

  @Override
  public boolean test(final Player player) {
    return vault.getEcon().has(player, this.amount);
  }

  @Override
  public void onFail(final Player player) {
    player.sendMessage(Language.CONDITION_VAULT_ERROR.toString().replace("%money%", String.valueOf(this.amount)));
  }

  @Override
  public LinkedHashSet<ClickableItem> getGUIRepresenter(final IRecipe recipe, final Player player, final InventoryContent contents, final Supplier<InventoryProvider> click) {

    final LinkedHashSet<ClickableItem> items = Sets.newLinkedHashSet();

    items.add(new ClickableItem(new ItemBuilder(Material.GOLD_NUGGET).name(this.getName()).lore(Language.GENERAL_LEFT_CHANGE.toString())
        .lore(Language.CONDITION_VAULT_DESC.toString()).lore("�6" + String.valueOf(this.amount)).build(), e -> {

      player.closeInventory();
      new AnvilGUI(player, String.valueOf(this.amount), (p, i) -> {

        if (UtilMath.isDouble(i)) {
          this.amount = Double.parseDouble(i);
        }
        Bukkit.getScheduler().runTaskLater(CustomRecipes.getInstance(), () -> click.get().reopen(player, contents), 1L);
        UtilPlayer.playSound(player, Sound.ENTITY_VILLAGER_YES, 0.7F, 1.15F);
        return null;
      });

    }));

    return items;
  }

  @Override
  public void save(final YamlConfiguration config) {
    config.set(this.getConfigPath("amount"), this.amount);
  }

  @Override
  public void loadConditions(final YamlConfiguration config) {
    this.amount = config.getDouble(this.getConfigPath("amount"));
  }
}