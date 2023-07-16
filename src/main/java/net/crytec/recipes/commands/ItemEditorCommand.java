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

package net.crytec.recipes.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Conditions;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Split;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Values;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import net.crytec.libs.commons.utils.UtilPlayer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@CommandAlias("edititem")
@CommandPermission("cc.edititem")
public class ItemEditorCommand extends BaseCommand {

  @Default
  @Conditions("itemInHand")
  public void addCommand(final Player player, final CommandHelp help) {
    //Send Help
  }

  @Subcommand("help")
  public void addCommand(final CommandIssuer issuer, final CommandHelp help) {
    help.showHelp(issuer);
    return;
  }

  @Subcommand("give")
  @CommandCompletion("@material @range:0-64 @players")
  public void giveCommand(final Player player, @Values("@material") final Material mat, @Optional final int amount) {
    final int targetamount = amount != 0 ? amount : 1;

    player.getInventory().addItem(new ItemStack(mat, targetamount));
    UtilPlayer.playSound(player, Sound.ENTITY_ITEM_PICKUP);
  }

  @Subcommand("enchant")
  @CommandCompletion("@enchantment @range:1-10")
  @Conditions("itemInHand")
  public void giveEnchantmentCommand(final Player player, @Values("@enchantment") final Enchantment ench, @Optional final int level) {
    final int targetamount = level != 0 ? level : 1;

    final ItemStack item = player.getInventory().getItemInMainHand();
    final ItemMeta meta = item.getItemMeta();
    meta.addEnchant(ench, targetamount, true);
    item.setItemMeta(meta);

    UtilPlayer.playSound(player, Sound.BLOCK_ENCHANTMENT_TABLE_USE);
  }

  @Subcommand("name")
  @Conditions("itemInHand")
  public void setDisplayname(final Player player, final String[] args) {

    final StringBuilder builder = new StringBuilder();
    Arrays.stream(args).forEachOrdered(a -> builder.append(a + " "));

    final ItemStack item = player.getInventory().getItemInMainHand();
    final ItemMeta meta = item.getItemMeta();
    meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', builder.toString()));
    item.setItemMeta(meta);

    UtilPlayer.playSound(player, Sound.ITEM_AXE_STRIP);
  }

  @Subcommand("setlore")
  @Conditions("itemInHand")
  @CommandCompletion("@range:1-20")
  public void setLore(final Player player, final int line, final String[] args) {

    final StringBuilder builder = new StringBuilder();
    Arrays.stream(args).forEachOrdered(a -> builder.append(a + " "));

    final ItemStack item = player.getInventory().getItemInMainHand();
    final ItemMeta meta = item.getItemMeta();

    final List<String> lore = meta.hasLore() ? meta.getLore() : Lists.newArrayList();
    if (line > lore.size()) {
      while (lore.size() <= line) {
        lore.add("");
      }
    }

    lore.set(line, ChatColor.translateAlternateColorCodes('&', builder.toString()));
    meta.setLore(lore);
    item.setItemMeta(meta);

    UtilPlayer.playSound(player, Sound.ITEM_AXE_STRIP);
  }

  @Subcommand("addlore")
  @Conditions("itemInHand")
  public void addLore(final Player player, @Split(";") final String[] args) {

    final List<String> lore = Arrays.stream(args).map(line -> ChatColor.translateAlternateColorCodes('&', line)).collect(Collectors.toCollection(ArrayList::new));

    final ItemStack item = player.getInventory().getItemInMainHand();
    final ItemMeta meta = item.getItemMeta();
    meta.setLore(lore);
    item.setItemMeta(meta);

    UtilPlayer.playSound(player, Sound.ITEM_AXE_STRIP);
  }
}