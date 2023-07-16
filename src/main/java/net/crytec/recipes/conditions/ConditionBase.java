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

import java.util.LinkedHashSet;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import lombok.Getter;
import net.crytec.inventoryapi.api.ClickableItem;
import net.crytec.inventoryapi.api.InventoryContent;
import net.crytec.inventoryapi.api.InventoryProvider;
import net.crytec.recipes.CustomRecipes;
import net.crytec.recipes.data.IRecipe;
import net.crytec.recipes.io.Language;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class ConditionBase implements Predicate<Player> {

  protected static final CustomRecipes plugin = JavaPlugin.getPlugin(CustomRecipes.class);

  @Getter
  private final String id;

  public ConditionBase(final String name) {
    this.id = name;
  }

  public abstract void onFail(Player player);

  public final String getName() {
    return ChatColor.translateAlternateColorCodes('&', Language.getFile().getString("condition." + this.getId() + ".name"));
  }

  public final List<String> getDescription() {
    return Language.getFile().getStringList("condition." + this.getId() + ".desc").stream()
        .map(x -> ChatColor.translateAlternateColorCodes('&', x)).collect(Collectors.toList());
  }

  public abstract LinkedHashSet<ClickableItem> getGUIRepresenter(IRecipe recipe, Player player, InventoryContent contents, Supplier<InventoryProvider> click);

  public abstract void save(YamlConfiguration config);

  public abstract void loadConditions(YamlConfiguration config);

  public final String getConfigPath(final String entry) {
    return "conditions." + this.getId() + ".entry";
  }
}
