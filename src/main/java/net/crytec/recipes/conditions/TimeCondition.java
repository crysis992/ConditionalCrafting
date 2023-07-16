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
import net.crytec.libs.commons.utils.item.ItemBuilder;
import net.crytec.libs.commons.utils.lang.StringUtils;
import net.crytec.recipes.conditions.annotations.ConditionDefaults;
import net.crytec.recipes.data.IRecipe;
import net.crytec.recipes.io.Language;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

@ConditionDefaults(name = "Time", description = {"&7The item can only be crafted during", "&7the selected daytime."})
public class TimeCondition extends ConditionBase {

  @Setter
  @Getter
  private DayTime time = DayTime.DAY;

  public TimeCondition() {
    super("Time");
  }

  @Override
  public boolean test(final Player player) {
    final DayTime current = (player.getWorld().getTime() < 12000) ? DayTime.DAY : DayTime.NIGHT;
    return this.time.equals(current);
  }

  private enum DayTime {
    DAY, NIGHT
  }

  @Override
  public void onFail(final Player player) {
    player.sendMessage(Language.CONDITION_TIME_ERROR.toChatString().replace("%daytime%", StringUtils.capitalize(this.time.toString().toLowerCase())));
  }

  @Override
  public LinkedHashSet<ClickableItem> getGUIRepresenter(final IRecipe recipe, final Player player, final InventoryContent contents, final Supplier<InventoryProvider> click) {

    final LinkedHashSet<ClickableItem> items = Sets.newLinkedHashSet();

    items.add(new ClickableItem(new ItemBuilder(Material.CLOCK)
        .name(Language.CONDITION_TIME_BUTTON.toString().replace("%daytime%", this.time.toString()))
        .lore(Language.GENERAL_LEFT_CHANGE.toString())
        .build(), e -> {
      if (this.time == DayTime.DAY) {
        this.time = DayTime.NIGHT;
        click.get().reopen(player, contents);
      } else {
        this.time = DayTime.DAY;
        click.get().reopen(player, contents);
      }
    }));

    return items;
  }

  @Override
  public void save(final YamlConfiguration config) {
    config.set(this.getConfigPath("time"), this.time.toString());
  }

  @Override
  public void loadConditions(final YamlConfiguration config) {
    this.time = DayTime.valueOf(config.getString(this.getConfigPath("time")));
  }
}