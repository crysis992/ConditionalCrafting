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

package net.crytec.recipes.data;

import com.google.common.collect.Sets;
import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import lombok.Getter;
import net.crytec.libs.commons.utils.PluginConfig;
import net.crytec.recipes.CustomRecipes;
import net.crytec.recipes.conditions.ConditionBase;
import net.crytec.recipes.editor.RecipeEditSession;
import net.crytec.recipes.manager.RecipeManager;
import net.crytec.recipes.util.UtilFiles;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("deprecation")
public class CustomShapelessRecipe extends ShapelessRecipe implements IRecipe {

  private static final CustomRecipes plugin = (CustomRecipes) JavaPlugin.getProvidingPlugin(CustomRecipes.class);

  public static List<Integer> position = Arrays.asList(11, 12, 13, 20, 21, 22, 29, 30, 31);

  @Getter
  public ItemStack result;

  @Getter
  public HashSet<ConditionBase> conditions = Sets.newHashSet();

  public CustomShapelessRecipe(final String id, final ItemStack result) {
    super(new NamespacedKey(plugin, id), result);
    this.result = result;
  }

  public static CustomShapelessRecipe registerNewRecipe(final RecipeEditSession session) {
    final ItemStack result = session.getInventory().getItem(24);
    if (result == null) {
      session.getPlayer().sendMessage("Result cannot be empty");
      return null;
    }

    final CustomShapelessRecipe recipe = new CustomShapelessRecipe(session.getId(), result);

    if (session.isEditMode()) {
      plugin.getRecipeManager().removeRecipe(recipe);
    }

    for (int i = 0; i < 9; i++) {
      final ItemStack item = session.getInventory().getItem(position.get(i));
      if (item == null || item.getType() == Material.AIR) {
        continue;
      }

      if (item.getAmount() > 1) {
        int x = 0;
        while (x < item.getAmount()) {
          recipe.addIngredient(getChoice(item));
          x++;
        }
      } else {
        recipe.addIngredient(getChoice(item));
      }
    }

    if (session.getGroup() != null && !session.getGroup().isEmpty()) {
      recipe.setGroup(session.getGroup());
    }

    plugin.getRecipeManager().registerRecipe(recipe);

    recipe.saveRecipe();
    return recipe;
  }

  private static RecipeChoice getChoice(final ItemStack item) {
    if (!item.hasItemMeta()) {
      return new RecipeChoice.MaterialChoice(item.getType());
    } else {
      return new RecipeChoice.ExactChoice(item);
    }
  }

  @Override
  public void saveRecipe() {
    final File file = new File(RecipeManager.shapelessFolder, this.getKey().getKey() + ".yml");
    UtilFiles.createNewFile(file);

    final PluginConfig config = new PluginConfig(plugin, RecipeManager.shapelessFolder, file.getName());

    config.set("type", "shapeless");

    config.set("key", this.getKey().getKey());

    config.set("result", this.getResult());

    if (this.getGroup() != null && !this.getGroup().isEmpty()) {
      config.set("group", this.getGroup());
    }

    config.setItemStackArray("ingredients", this.getIngredientList().toArray(new ItemStack[this.getIngredientList().size()]));

    for (final ConditionBase con : this.conditions) {
      con.save(config);
    }
    config.saveConfig();
  }


  public static void load(final File file) {
    final PluginConfig config = new PluginConfig(plugin, RecipeManager.shapelessFolder, file.getName());

    CustomShapelessRecipe recipe = null;

    try {
      recipe = new CustomShapelessRecipe(config.getString("key"), config.getItemStack("result"));
    } catch (final Exception ex) {
      plugin.getLogger().severe("Failed to load recipe from file: " + file.getName());
    }

    if (recipe == null) {
      return;
    }

    if (config.isSet("group")) {
      recipe.setGroup(config.getString("group"));
    }

    final ItemStack[] items = config.getItemStackArray("ingredients");

    for (final ItemStack i : items) {
      recipe.addIngredient(getChoice(i));
    }

    if (config.isSet("conditions")) {
      final ConfigurationSection cons = config.getConfigurationSection("conditions");

      for (final String key : cons.getKeys(false)) {
        final Class<? extends ConditionBase> clazz = plugin.getConditionRegistrar().getClassByID(key);
        if (clazz == null) {
          plugin.getLogger().severe("Failed to load condition " + key + " on recipe: " + recipe.getKey().getKey() + " - Missing class.");
          cons.set(key, null);
          continue;
        }

        final ConditionBase base = plugin.getConditionRegistrar().getNewInstance(clazz);
        base.loadConditions(config);
        recipe.getConditions().add(base);
      }


    }

    plugin.getRecipeManager().registerRecipe(recipe);
  }

  @Override
  public void openEditor(final Player player) {
    final RecipeEditSession session = new RecipeEditSession(player, this.getType(), this.getKey().getKey());
    session.setEditMode(true);
    session.setGroup(this.getGroup());
    session.setRecipeType(this.getType());

    int pos = 0;
    for (final RecipeChoice choice : this.getChoiceList()) {
      final ItemStack item = choice.getItemStack();
      if (item != null) {
        session.getInventory().setItem(position.get(pos), item);
      }
      pos++;
    }
    session.getInventory().setItem(24, this.result);

    plugin.getEditManager().addEditSession(player, session);
    session.openInterface();
  }

  @Override
  public RecipeType getType() {
    return RecipeType.SHAPELESS;
  }

  @Override
  public Recipe getRecipe() {
    return this;
  }

  @Override
  public boolean deleteRecipeFile() {
    final File file = new File(RecipeManager.shapelessFolder, this.getKey().getKey() + ".yml");
    return file.delete();
  }

  @Override
  public boolean isNBTShape() {
    return this.getIngredientList().stream().filter(item -> item != null).anyMatch(item -> item.hasItemMeta());
  }
}
