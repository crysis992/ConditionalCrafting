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
import java.util.HashSet;
import lombok.Getter;
import lombok.Setter;
import net.crytec.libs.commons.utils.PluginConfig;
import net.crytec.recipes.CustomRecipes;
import net.crytec.recipes.conditions.ConditionBase;
import net.crytec.recipes.editor.RecipeEditSession;
import net.crytec.recipes.manager.RecipeManager;
import net.crytec.recipes.util.UtilFiles;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("deprecation")
public class CustomFurnaceRecipe extends FurnaceRecipe implements IRecipe {

  private static final CustomRecipes plugin = (CustomRecipes) JavaPlugin.getProvidingPlugin(CustomRecipes.class);

  public CustomFurnaceRecipe(final String id, final ItemStack result, final ItemStack ingredient, final float exp, final int cookingTime) {
    super(new NamespacedKey(plugin, id), result, getChoice(ingredient), exp, cookingTime);
    this.result = result;
  }

  private static RecipeChoice getChoice(final ItemStack item) {
    if (!item.hasItemMeta()) {
      return new RecipeChoice.MaterialChoice(item.getType());
    } else {
      return new RecipeChoice.ExactChoice(item);
    }
  }

  @Getter
  public ItemStack result;

  @Getter
  @Setter
  private String shapeString;

  public static CustomFurnaceRecipe registerNewRecipe(final RecipeEditSession session) {
    final ItemStack result = session.getInventory().getItem(24);
    final ItemStack ingredient = session.getInventory().getItem(22);

    if (ingredient == null) {
      session.getPlayer().sendMessage("Ingredient cannot be empty!");
      return null;
    }

    if (result == null) {
      session.getPlayer().sendMessage("Result cannot be empty!");
      return null;
    }

    final CustomFurnaceRecipe recipe = new CustomFurnaceRecipe(session.getId(), result, ingredient, session.getExperience(), session.getCookingTime());

    if (session.isEditMode()) {
      plugin.getRecipeManager().removeRecipe(recipe);
    }

    if (session.getGroup() != null && !session.getGroup().isEmpty()) {
      recipe.setGroup(session.getGroup());
    }

    plugin.getRecipeManager().registerRecipe(recipe);

    recipe.saveRecipe();
    return recipe;
  }


  public static void load(final File file) {
    final YamlConfiguration config = new PluginConfig(plugin, RecipeManager.furnaceFolder, file.getName());

    CustomFurnaceRecipe recipe = null;

    try {
      final ItemStack result = config.getItemStack("result");
      final ItemStack ingredient = config.getItemStack("ingredient");

      recipe = new CustomFurnaceRecipe(config.getString("key"), result, ingredient, (float) config.getDouble("exp"), config.getInt("cookingtime"));
    } catch (final Exception ex) {
      plugin.getLogger().severe("Failed to load recipe from file: " + file.getName());
    }

    if (config.isSet("group")) {
      recipe.setGroup(config.getString("group"));
    }

    if (recipe == null) {
      return;
    }

    plugin.getRecipeManager().registerRecipe(recipe);
  }

  @Override
  public void saveRecipe() {
    final File file = new File(RecipeManager.furnaceFolder, this.getKey().getKey() + ".yml");
    UtilFiles.createNewFile(file);

    final PluginConfig config = new PluginConfig(plugin, RecipeManager.furnaceFolder, file.getName());

    config.set("type", "furnace");
    config.set("key", this.getKey().getKey());
    config.set("result", this.getResult());

    if (this.getGroup() != null && !this.getGroup().isEmpty()) {
      config.set("group", this.getGroup());
    }

    config.set("ingredient", this.getInputChoice().getItemStack());
    config.set("cookingtime", this.getCookingTime());
    config.set("exp", this.getExperience());

    config.saveConfig();
  }

  @Override
  public void openEditor(final Player player) {

    final RecipeEditSession session = new RecipeEditSession(player, this.getType(), this.getKey().getKey());
    session.setGroup(this.getGroup());
    session.setEditMode(true);
    session.setRecipeType(this.getType());
    session.setExperience(this.getExperience());
    session.setCookingTime(this.getCookingTime());

    session.getInventory().setItem(22, this.getInput());
    session.getInventory().setItem(24, this.result);

    plugin.getEditManager().addEditSession(player, session);
    session.openInterface();
  }


  @Override
  public RecipeType getType() {
    return RecipeType.FURNACE;
  }

  @Override
  public HashSet<ConditionBase> getConditions() {
    return Sets.newHashSet();
  }

  @Override
  public Recipe getRecipe() {
    return this;
  }

  @Override
  public boolean deleteRecipeFile() {
    final File file = new File(RecipeManager.furnaceFolder, this.getKey().getKey() + ".yml");
    return file.delete();
  }

  @Override
  public boolean isNBTShape() {
    return this.getInput().hasItemMeta();
  }
}
