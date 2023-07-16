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
import net.crytec.libs.commons.utils.item.ItemBuilder;
import net.crytec.libs.commons.utils.lang.EnumUtils;
import net.crytec.recipes.CustomRecipes;
import net.crytec.recipes.conditions.annotations.ConditionDefaults;
import net.crytec.recipes.data.IRecipe;
import net.crytec.recipes.io.Language;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

@ConditionDefaults(name = "Entities nearby", description = {"&7The item can only be crafted when at", "&7least <amount> entities of type <type> are in", "&7range of <radius> blocks."})
public class EntityAmountNearby extends ConditionBase {

  public EntityAmountNearby() {
    super("NearbyEntities");
  }

  @Getter
  @Setter
  private int radius = 8;
  @Getter
  @Setter
  private int amount = 1;
  @Getter
  @Setter
  private EntityType type = EntityType.PLAYER;

  @Override
  public boolean test(final Player player) {
    return player.getNearbyEntities(this.radius, this.radius, this.radius).stream().filter(ent -> ent.getType().equals(this.type)).count() >= this.amount;
  }

  @Override
  public void onFail(final Player player) {
    player.sendMessage(Language.CONDITION_ENTITYRADIUS_ERROR.toChatString().replace("%amount%", "" + this.amount).replace("%radius%", "" + this.radius).replace("%type%", this.type.toString()));
  }

  @Override
  public LinkedHashSet<ClickableItem> getGUIRepresenter(final IRecipe recipe, final Player player, final InventoryContent contents, final Supplier<InventoryProvider> click) {
    final LinkedHashSet<ClickableItem> set = Sets.newLinkedHashSet();

    set.add(ClickableItem.of(new ItemBuilder(Material.RAIL).name(Language.CONDITION_ENTITYRADIUS_RADIUS.toString() + ": " + this.radius).build(), event -> {

      new AnvilGUI(player, "" + this.radius, (p1, reply) -> {

        if (UtilMath.isInt(reply)) {
          this.radius = Integer.parseInt(reply);
        }

        Bukkit.getScheduler().runTaskLater(CustomRecipes.getInstance(), () -> {
          click.get().reopen(player, contents);
        }, 1);
        return null;
      });

    }));

    set.add(ClickableItem.of(new ItemBuilder(Material.CHEST).name(Language.CONDITION_ENTITYRADIUS_AMOUNT.toString() + ": " + this.amount).build(), event -> {

      new AnvilGUI(player, "" + this.amount, (p1, reply) -> {

        if (UtilMath.isInt(reply)) {
          this.amount = Integer.parseInt(reply);
        }

        Bukkit.getScheduler().runTaskLater(CustomRecipes.getInstance(), () -> {
          click.get().reopen(player, contents);
        }, 1);
        return null;
      });

    }));

    set.add(ClickableItem.of(new ItemBuilder(Material.ZOMBIE_HEAD).name(Language.CONDITION_ENTITYRADIUS_TYPE.toString() + ": " + this.type.toString()).build(), event -> {

      new AnvilGUI(player, "" + this.type.toString(), (p1, reply) -> {

        if (EnumUtils.isValidEnum(EntityType.class, reply)) {
          this.type = EntityType.valueOf(reply.toUpperCase());
        }

        Bukkit.getScheduler().runTaskLater(CustomRecipes.getInstance(), () -> {
          click.get().reopen(player, contents);
        }, 1);
        return null;
      });

    }));

    return set;
  }

  @Override
  public void save(final YamlConfiguration config) {

    config.set(this.getConfigPath("radius"), this.radius);
    config.set(this.getConfigPath("amount"), this.amount);
    config.set(this.getConfigPath("type"), this.type.toString());

  }

  @Override
  public void loadConditions(final YamlConfiguration config) {

    this.radius = config.getInt(this.getConfigPath("radius"));
    this.amount = config.getInt(this.getConfigPath("amount"));
    this.type = EntityType.valueOf(config.getString(this.getConfigPath("type")));

  }

}
