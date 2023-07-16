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
import net.crytec.inventoryapi.api.ClickableItem;
import net.crytec.inventoryapi.api.InventoryContent;
import net.crytec.inventoryapi.api.InventoryProvider;
import net.crytec.libs.commons.utils.UtilPlayer;
import net.crytec.libs.commons.utils.chatinput.ChatInput;
import net.crytec.libs.commons.utils.item.ItemBuilder;
import net.crytec.recipes.CustomRecipes;
import net.crytec.recipes.conditions.annotations.ConditionDefaults;
import net.crytec.recipes.conditions.annotations.ConditionHook;
import net.crytec.recipes.data.IRecipe;
import net.crytec.recipes.io.Language;
import net.crytec.recipes.manager.HookManager;
import net.crytec.recipes.manager.HookManager.HookType;
import net.crytec.recipes.manager.hooks.WorldGuardHook;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

@ConditionHook(type = HookType.WORLDGUARD)
@ConditionDefaults(name = "RegionName", description = {"&7The item can only be crafted within", "&7a region which has a name", "�7containing this text."})
public class RegionContainsNameCondition extends ConditionBase {

  public RegionContainsNameCondition() {
    super("RegionName");

  }

  @Getter
  @Setter
  private String text = ">?<";
  private final HookManager hookManager = CustomRecipes.getInstance().getHookManager();

  @Override
  public boolean test(final Player player) {
    if (!this.hookManager.canLoad(HookType.WORLDGUARD)) {
      return false;
    }
    return ((WorldGuardHook) this.hookManager.getHook(HookType.WORLDGUARD)).regionContains(player.getLocation(), this.text);
  }

  @Override
  public void onFail(final Player player) {
    player.sendMessage(Language.CONDITION_WORLDGUARD_NAME.toChatString().replace("%text%", this.text));
  }

  @Override
  public LinkedHashSet<ClickableItem> getGUIRepresenter(final IRecipe recipe, final Player player, final InventoryContent contents, final Supplier<InventoryProvider> click) {
    final LinkedHashSet<ClickableItem> items = Sets.newLinkedHashSet();

    items.add(ClickableItem.of(new ItemBuilder(Material.PAPER).name(Language.CONDITION_WORLDGUARD_NAME_BUTTON.toString())
        .lore("")
        .lore("-> " + this.text)
        .build(), event -> {
      player.closeInventory();
      new ChatInput(player, "Please enter the Region Name: ", pinput -> {
        this.text = pinput;
        click.get().reopen(player, contents);
        UtilPlayer.playSound(player, Sound.BLOCK_COMPARATOR_CLICK, 0.85F, 0.75F);
      });

    }));
    return items;
  }

  @Override
  public void save(final YamlConfiguration config) {
    config.set(this.getConfigPath("text"), this.text);
  }

  @Override
  public void loadConditions(final YamlConfiguration config) {
    this.text = config.getString(this.getConfigPath("text"));
  }

}
