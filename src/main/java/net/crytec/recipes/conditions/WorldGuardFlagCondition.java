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
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.StateFlag;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import net.crytec.inventoryapi.api.ClickableItem;
import net.crytec.inventoryapi.api.InventoryContent;
import net.crytec.inventoryapi.api.InventoryProvider;
import net.crytec.libs.commons.utils.UtilPlayer;
import net.crytec.libs.commons.utils.item.ItemBuilder;
import net.crytec.recipes.CustomRecipes;
import net.crytec.recipes.conditions.annotations.ConditionDefaults;
import net.crytec.recipes.conditions.annotations.ConditionHook;
import net.crytec.recipes.data.IRecipe;
import net.crytec.recipes.io.Language;
import net.crytec.recipes.manager.HookManager.HookType;
import net.crytec.recipes.manager.hooks.WorldGuardHook;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

@ConditionHook(type = HookType.WORLDGUARD)
@ConditionDefaults(name = "WorldGuardFlag", description = {"&7This requires the user to be in a region",
    "&7with the given flag.",
    "&7The player must be able to complete the flag check."
})
public class WorldGuardFlagCondition extends ConditionBase {

  private static final WorldGuardHook wg;
  private static final List<StateFlag> registeredFlags;

  static {
    wg = CustomRecipes.getInstance().getHookManager().getWorldguard();

    registeredFlags = WorldGuard.getInstance().getFlagRegistry().getAll().stream()
        .filter(f -> f instanceof StateFlag)
        .map(flag -> (StateFlag) flag)
        .collect(Collectors.toList());

  }

  @Setter
  @Getter
  private HashSet<StateFlag> flags = Sets.newHashSet();

  public WorldGuardFlagCondition() {
    super("WorldGuardFlag");
  }

  @Override
  public boolean test(final Player player) {
    final StateFlag[] flags = this.flags.toArray(new StateFlag[0]);
//    return wg.checkStateOfCurrentRegion(player, this.flags.toArray(new StateFlag[this.flags.size()]));
    return wg.checkStateOfCurrentRegion(player, flags);
  }

  @Override
  public void onFail(final Player player) {
    player.sendMessage(Language.CONDITION_WORLDGUARD_FLAGS.toChatString().replace("%flags%", this.flags.stream().map(flag -> flag.getName()).collect(Collectors.joining(","))));
  }

  @Override
  public LinkedHashSet<ClickableItem> getGUIRepresenter(final IRecipe recipe, final Player player, final InventoryContent contents, final Supplier<InventoryProvider> click) {

    final LinkedHashSet<ClickableItem> items = Sets.newLinkedHashSet();

    registeredFlags.forEach(flag -> {

      if (this.flags.contains(flag)) {
        items.add(new ClickableItem(new ItemBuilder(Material.LIME_DYE)
            .name("�e" + flag.getName())
            .enchantment(Enchantment.ARROW_INFINITE)
            .setItemFlag(ItemFlag.HIDE_ENCHANTS)
            .lore(Language.GENERAL_LEFT_CHANGE.toString()).build(), e -> {
          this.flags.remove(flag);
          UtilPlayer.playSound(player, Sound.BLOCK_NOTE_BLOCK_BELL, 0.5F, 0.6F);
          click.get().reopen(player, contents);
        }));
      } else {
        items.add(new ClickableItem(new ItemBuilder(Material.GRAY_DYE)
            .name("�e" + flag.getName())
            .lore(Language.GENERAL_LEFT_CHANGE.toString()).build(), e -> {
          this.flags.add(flag);
          UtilPlayer.playSound(player, Sound.BLOCK_NOTE_BLOCK_BELL, 0.5F, 1.2F);
          click.get().reopen(player, contents);
        }));
      }
    });
    return items;
  }

  @Override
  public void save(final YamlConfiguration config) {
    config.set(this.getConfigPath("wgflag"), this.flags.stream().map(flag -> flag.getName()).collect(Collectors.toList()));
  }

  @Override
  public void loadConditions(final YamlConfiguration config) {
    this.flags = config.getStringList(this.getConfigPath("wgflag")).stream()
        .map(name -> (StateFlag) WorldGuard.getInstance().getFlagRegistry().get(name))
        .collect(Collectors.toCollection(HashSet::new));
  }
}