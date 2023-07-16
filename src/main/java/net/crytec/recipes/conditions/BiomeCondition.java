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
import java.util.Arrays;
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
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Biome;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;

@ConditionDefaults(name = "Biome", description = {"&7This requires the user to be in one", "&7of the given biomes to craft this item."})
public class BiomeCondition extends ConditionBase {

  @Setter
  @Getter
  private HashSet<Biome> biomes = Sets.newHashSet(Biome.BADLANDS);

  public BiomeCondition() {
    super("Biome");
  }

  @Override
  public boolean test(final Player player) {
    return this.biomes.contains(player.getLocation().getBlock().getBiome());
  }

  @Override
  public void onFail(final Player player) {
    player.sendMessage(Language.CONDITION_BIOME_ERROR.toChatString());
  }

  @Override
  public LinkedHashSet<ClickableItem> getGUIRepresenter(final IRecipe recipe, final Player player, final InventoryContent contents, final Supplier<InventoryProvider> click) {

    final LinkedHashSet<ClickableItem> items = Sets.newLinkedHashSet();

    Arrays.stream(Biome.values()).sorted(Comparator.comparing(Biome::name)).forEach(biome -> {

      if (this.biomes.contains(biome)) {
        items.add(new ClickableItem(new ItemBuilder(Material.LIME_DYE)
            .name(ChatColor.YELLOW + biome.toString())
            .enchantment(Enchantment.ARROW_INFINITE)
            .setItemFlag(ItemFlag.HIDE_ENCHANTS)
            .lore(Language.GENERAL_LEFT_CHANGE.toString()).build(), e -> {
          this.biomes.remove(biome);
          UtilPlayer.playSound(player, Sound.BLOCK_NOTE_BLOCK_BELL, 0.5F, 0.6F);
          click.get().reopen(player, contents);
        }));
      } else {
        items.add(new ClickableItem(new ItemBuilder(Material.GRAY_DYE)
            .name(ChatColor.YELLOW + biome.toString())
            .lore(Language.GENERAL_LEFT_CHANGE.toString()).build(), e -> {
          this.biomes.add(biome);
          UtilPlayer.playSound(player, Sound.BLOCK_NOTE_BLOCK_BELL, 0.5F, 1.2F);
          click.get().reopen(player, contents);
        }));
      }
    });
    return items;
  }

  @Override
  public void save(final YamlConfiguration config) {
    config.set(this.getConfigPath("biome"), this.biomes.stream().map(Biome::toString).collect(Collectors.toList()));
  }

  @Override
  public void loadConditions(final YamlConfiguration config) {
    this.biomes = config.getStringList(this.getConfigPath("biome")).stream().map(Biome::valueOf).collect(Collectors.toCollection(HashSet::new));
  }
}