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

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.crytec.inventoryapi.anvil.AnvilGUI;
import net.crytec.inventoryapi.api.ClickableItem;
import net.crytec.inventoryapi.api.InventoryContent;
import net.crytec.inventoryapi.api.InventoryProvider;
import net.crytec.libs.commons.utils.UtilMath;
import net.crytec.libs.commons.utils.UtilPlayer;
import net.crytec.libs.commons.utils.item.ItemBuilder;
import net.crytec.libs.commons.utils.lang.EnumUtils;
import net.crytec.recipes.CustomRecipes;
import net.crytec.recipes.conditions.annotations.ConditionDefaults;
import net.crytec.recipes.data.IRecipe;
import net.crytec.recipes.io.Language;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;

@ConditionDefaults(name = "Attribute", description = {"&7The player can only craft this item if", "&7he has all of the required attributes.", "&7eg Health > 20"})
public class AttributeCondition extends ConditionBase {

  public AttributeCondition() {
    super("Attribute");
    this.values = Maps.newHashMap();
    this.values.put(Attribute.GENERIC_MAX_HEALTH, 20D);
  }

  private final Map<Attribute, Double> values;

  @Override
  public boolean test(Player player) {
    return !this.values.entrySet().stream().anyMatch((entry) -> {
      return player.getAttribute(entry.getKey()).getValue() < entry.getValue();
    });
  }

  @Override
  public void onFail(final Player player) {
    player.sendMessage(Language.CONDITION_ATTRIBUTE_ERROR.toChatString());
  }

  @Override
  public LinkedHashSet<ClickableItem> getGUIRepresenter(final IRecipe recipe, final Player player, final InventoryContent contents,
      final Supplier<InventoryProvider> click) {

    final LinkedHashSet<ClickableItem> set = Sets.newLinkedHashSet();

    for (final Attribute att : Attribute.values()) {
      final ItemBuilder builder = new ItemBuilder(Material.ARMOR_STAND).name(att.toString());

      if (this.values.containsKey(att)) {
        builder.enchantment(Enchantment.ARROW_DAMAGE);
        builder.setItemFlag(ItemFlag.HIDE_ENCHANTS);
        builder.lore(Language.INTERFACE_RIGHT_CLICK_DELETE.toString());
        builder.lore(Language.CONDITION_ATTRIBUTE_BUTTON.toString());
        builder.lore("�6" + this.values.get(att));
      } else {
        builder.lore(Language.GENERAL_LEFT_CHANGE.toString());
      }

      final ClickableItem item = ClickableItem.of(builder.build(), e -> {

        if (e.getClick() == ClickType.RIGHT) {
          this.values.remove(att);
          click.get().reopen(player, contents);
        } else {
          new AnvilGUI(player, "1.0", (p, input) -> {

            if (!UtilMath.isDouble(input)) {
              player.sendMessage(Language.ERROR_INVALID_DOUBLE_INPUT.toChatString());
              UtilPlayer.playSound(player, Sound.ENTITY_LEASH_KNOT_BREAK, 1.2F, 0.4F);
              return "invalid";
            }

            this.values.put(att, Double.parseDouble(input));
            Bukkit.getScheduler().runTaskLater(CustomRecipes.getInstance(), () -> click.get().reopen(player, contents), 1L);
            return null;
          });
        }
      });

      set.add(item);
    }
    return set;
  }

  @Override
  public void save(final YamlConfiguration config) {
    config.set(this.getConfigPath("attributes"), this.values.entrySet().stream().map(entry -> entry.getKey().toString() + "#" + entry.getValue()).collect(Collectors.toList()));
  }

  @Override
  public void loadConditions(final YamlConfiguration config) {

    final List<String> attrStrings = config.getStringList(this.getConfigPath("attributes"));

    for (final String str : attrStrings) {

      final String[] split = str.split("#");
      if (EnumUtils.isValidEnum(Attribute.class, split[0]) && UtilMath.isDouble(split[1])) {
        this.values.put(Attribute.valueOf(split[0]), Double.parseDouble(split[1]));
      }

    }

  }

}
