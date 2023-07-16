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
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import net.crytec.inventoryapi.api.ClickableItem;
import net.crytec.inventoryapi.api.InventoryContent;
import net.crytec.inventoryapi.api.InventoryProvider;
import net.crytec.libs.commons.utils.UtilPlayer;
import net.crytec.libs.commons.utils.item.ItemBuilder;
import net.crytec.recipes.conditions.annotations.ConditionDefaults;
import net.crytec.recipes.data.IRecipe;
import net.crytec.recipes.io.Language;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

@ConditionDefaults(name = "World", description = {"&7This requires the user to be in one", "&7of the given worlds to craft this item."})
public class WorldCondition extends ConditionBase {

  @Setter
  @Getter
  private HashSet<String> worlds = Sets.newHashSet("world");

  public WorldCondition() {
    super("World");
  }

  @Override
  public boolean test(final Player player) {
    return this.worlds.contains(player.getWorld().getName());
  }

  @Override
  public void onFail(final Player player) {
    player.sendMessage(Language.CONDITION_WORLD_ERROR.toChatString());
  }

  @Override
  public LinkedHashSet<ClickableItem> getGUIRepresenter(final IRecipe recipe, final Player player, final InventoryContent contents, final Supplier<InventoryProvider> click) {

    final LinkedHashSet<ClickableItem> items = Sets.newLinkedHashSet();

    Bukkit.getWorlds().stream().sorted(Comparator.comparing(World::getName)).forEach(world -> {

      if (this.worlds.contains(world.getName())) {
        items.add(new ClickableItem(new ItemBuilder(Material.LIME_DYE)
            .name("�e" + world.getName())
            .enchantment(Enchantment.ARROW_INFINITE)
            .setItemFlag(ItemFlag.HIDE_ENCHANTS)
            .lore(Language.GENERAL_LEFT_CHANGE.toString()).build(), e -> {
          this.worlds.remove(world.getName());
          UtilPlayer.playSound(player, Sound.BLOCK_NOTE_BLOCK_BELL, 0.5F, 0.6F);
          click.get().reopen(player, contents);
        }));
      } else {
        items.add(new ClickableItem(new ItemBuilder(Material.GRAY_DYE)
            .name("�e" + world.getName())
            .lore(Language.GENERAL_LEFT_CHANGE.toString()).build(), e -> {
          this.worlds.add(world.getName());
          UtilPlayer.playSound(player, Sound.BLOCK_NOTE_BLOCK_BELL, 0.5F, 1.2F);
          click.get().reopen(player, contents);
        }));
      }
    });
    return items;
  }

  @Override
  public void save(final YamlConfiguration config) {
    config.set(this.getConfigPath("worlds"), this.worlds);
  }

  @Override
  public void loadConditions(final YamlConfiguration config) {
    this.worlds = config.getStringList(this.getConfigPath("worlds")).stream().collect(Collectors.toCollection(HashSet::new));
  }

}