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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.File;
import java.nio.CharBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import net.crytec.libs.commons.utils.PluginConfig;
import net.crytec.recipes.CustomRecipes;
import net.crytec.recipes.conditions.ConditionBase;
import net.crytec.recipes.editor.RecipeEditSession;
import net.crytec.recipes.manager.RecipeManager;
import net.crytec.recipes.util.UtilFiles;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("deprecation")
public class CustomShapedRecipe extends ShapedRecipe implements IRecipe {

  private static final char[] SLOTS = new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i'};

  public static List<Integer> position = Arrays.asList(11, 12, 13, 20, 21, 22, 29, 30, 31);

  private static final CustomRecipes plugin = (CustomRecipes) JavaPlugin.getProvidingPlugin(CustomRecipes.class);

  @Getter
  public ItemStack result;

  @Getter
  public HashSet<ConditionBase> conditions = Sets.newHashSet();

  @Getter
  @Setter
  private String shapeString;

  @Getter
  @Setter
  private String permission;

  public CustomShapedRecipe(final String id, final ItemStack result) {
    super(new NamespacedKey(plugin, id), result);
    this.result = result;
  }

  private static CustomShapedRecipe registerNewRecipe(final Player player, final String id, final ItemStack result, final ItemStack[] matrix, final String group, final boolean isEdit) {
    if (result == null) {
      player.sendMessage("Result cannot be empty");
      return null;
    }

    final CustomShapedRecipe recipe = new CustomShapedRecipe(id, result);

    Bukkit.getRecipesFor(new ItemStack(result.getType()));

    if (isEdit) {
      plugin.getRecipeManager().removeRecipe(recipe);
    }

    if (group != null && !group.isEmpty()) {
      recipe.setGroup(group);
    }

    final HashMap<Integer, ItemStack> temporary = Maps.newHashMap();
    final HashMap<Character, ItemStack> shapedItems = Maps.newHashMap();

    for (int i = 0; i < 9; i++) {
      temporary.put(i, matrix[i]);
    }

    final StringBuilder shape = new StringBuilder();

    for (int i = 0; i < 9; i++) {
      if (temporary.get(i) == null || temporary.get(i).getType() == Material.AIR) {
        shape.append('0');
      } else {
        shape.append(SLOTS[i]);
        shapedItems.put(SLOTS[i], temporary.get(i));
      }
    }

    final String finalShape = shape.toString();

    if (finalShape.equals("000000000")) {
      player.sendMessage("Shape cannot be empty");
      return null;
    }

    final String first = finalShape.substring(0, 3);
    final String second = finalShape.substring(3, 6);
    final String third = finalShape.substring(6, 9);

    final String[] shapeArray = new String[]{first, second, third};

    recipe.setShapeString(finalShape);

    recipe.shape(recipe.modifyShape(shapeArray));

    shapedItems.forEach((c, i) -> {
      recipe.setIngredient(c, getChoice(i));
    });

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

  public static CustomShapedRecipe registerNewRecipe(final RecipeEditSession session) {
    final Inventory inventory = session.getInventory();
    return registerNewRecipe(session.getPlayer(), session.getId(), session.getInventory().getItem(24), new ItemStack[]{
        inventory.getItem(position.get(0))
        , inventory.getItem(position.get(1))
        , inventory.getItem(position.get(2))
        , inventory.getItem(position.get(3))
        , inventory.getItem(position.get(4))
        , inventory.getItem(position.get(5))
        , inventory.getItem(position.get(6))
        , inventory.getItem(position.get(7))
        , inventory.getItem(position.get(8))}, session.getGroup(), session.isEditMode());
  }

  @Override
  public void saveRecipe() {
    final File file = new File(RecipeManager.shapedFolder, this.getKey().getKey() + ".yml");
    UtilFiles.createNewFile(file);

    final PluginConfig config = new PluginConfig(plugin, RecipeManager.shapedFolder, file.getName());

    config.set("type", "shaped");
    config.set("key", this.getKey().getKey());
    config.set("result", this.getResult());
    config.set("shape", this.getShapeString());

    if (this.getGroup() != null && !this.getGroup().isEmpty()) {
      config.set("group", this.getGroup());
    }

    this.getChoiceMap().forEach((c, r) -> {
      if (!c.equals('0')) {
        config.set("recipe." + c, r.getItemStack());
      }
    });

    for (final ConditionBase con : this.conditions) {
      con.save(config);
    }

    config.saveConfig();
  }


  public static void load(final File file) {
    final PluginConfig config = new PluginConfig(plugin, RecipeManager.shapedFolder, file.getName());
    final CustomShapedRecipe recipe = new CustomShapedRecipe(config.getString("key"), config.getItemStack("result"));

    final String finalShape = config.getString("shape");

    final String first = finalShape.substring(0, 3);
    final String second = finalShape.substring(3, 6);
    final String third = finalShape.substring(6, 9);

    final String[] shapeArray = new String[]{first, second, third};

    recipe.setShapeString(finalShape);

    recipe.shape(recipe.modifyShape(shapeArray));
    recipe.setShapeString(finalShape);

    CharBuffer.wrap(finalShape.toCharArray()).chars().mapToObj(ch -> (char) ch).collect(Collectors.toList())
        .forEach(c -> {
          if (!c.equals('0')) {
            recipe.setIngredient(c, getChoice(config.getItemStack("recipe." + c)));
          }
        });

    if (config.isSet("group") && !config.getString("group").isEmpty()) {
      recipe.setGroup(config.getString("group"));
    } else {
      recipe.setGroup("");
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

  public String[] modifyShape(final String[] shape) {

    // Corner
    if (shape[0].charAt(0) != '0' && shape[2].charAt(2) != '0') {
      return this.crop(shape);
    }
    if (shape[0].charAt(2) != '0' && shape[2].charAt(0) != '0') {
      return this.crop(shape);
    }

    //Top and Bottom
    if (shape[0].charAt(1) != '0' && shape[2].charAt(1) != '0') {
      if (this.columnEmpty(shape, 0)) {
        this.removeColumn(shape, 0);
      }
      if (this.columnEmpty(shape, 2)) {
        this.removeColumn(shape, 2);
      }
      return this.crop(shape);
    }
    if (shape[0].charAt(0) != '0' && shape[2].charAt(0) != '0') {
      if (this.columnEmpty(shape, 1)) {
        this.removeColumn(shape, 1);
      }
      if (this.columnEmpty(shape, 2)) {
        this.removeColumn(shape, 2);
      }
      return this.crop(shape);
    }
    if (shape[0].charAt(2) != '0' && shape[2].charAt(2) != '0') {
      if (this.columnEmpty(shape, 0)) {
        this.removeColumn(shape, 0);
      }
      if (this.columnEmpty(shape, 1)) {
        this.removeColumn(shape, 1);
      }
      return this.crop(shape);
    }

    //Left and Right
    if (shape[0].charAt(0) != '0' && shape[0].charAt(2) != '0') {
      if (this.rowEmpty(shape, 1)) {
        this.removeRow(shape, 1);
      }
      if (this.rowEmpty(shape, 2)) {
        this.removeRow(shape, 2);
      }
      return this.crop(shape);
    }
    if (shape[1].charAt(0) != '0' && shape[1].charAt(2) != '0') {
      if (this.rowEmpty(shape, 0)) {
        this.removeRow(shape, 0);
      }
      if (this.rowEmpty(shape, 2)) {
        this.removeRow(shape, 2);
      }
      return this.crop(shape);
    }
    if (shape[2].charAt(0) != '0' && shape[2].charAt(2) != '0') {
      if (this.rowEmpty(shape, 1)) {
        this.removeRow(shape, 1);
      }
      if (this.rowEmpty(shape, 0)) {
        this.removeRow(shape, 0);
      }
      return this.crop(shape);
    }

    //Crop
    for (int row = 0; row < 3; row++) {
      if (this.rowEmpty(shape, row)) {
        this.removeRow(shape, row);
      }
    }
    for (int column = 0; column < 3; column++) {
      if (this.columnEmpty(shape, column)) {
        this.removeColumn(shape, column);
      }
    }

    return this.crop(shape);
  }

  private String[] crop(String[] shape) {

    for (int i = 0; i < 3; i++) {
      shape[i] = shape[i].replace("#", "");
    }

    shape = Arrays.stream(shape).filter(entry -> !entry.isEmpty()).toArray(String[]::new);

    return shape;
  }

  private void removeRow(final String[] shape, final int rowIndex) {
    shape[rowIndex] = "###";
  }

  private void removeColumn(final String[] shape, final int columnIndex) {
    for (int i = 0; i < 3; i++) {
      final char[] ca = shape[i].toCharArray();
      ca[columnIndex] = '#';
      shape[i] = String.valueOf(ca);
    }
  }

  private boolean rowEmpty(final String[] shape, final int rowIndex) {
    if (shape[rowIndex].equals("000")) {
      return true;
    }
    if (shape[rowIndex].equals("0  ")) {
      return true;
    }
    if (shape[rowIndex].equals(" 0 ")) {
      return true;
    }
    if (shape[rowIndex].equals("  0")) {
      return true;
    }
    if (shape[rowIndex].equals("0 0")) {
      return true;
    }
    if (shape[rowIndex].equals("00 ")) {
      return true;
    }
    if (shape[rowIndex].equals(" 00")) {
      return true;
    }
    if (shape[rowIndex].equals("   ")) {
      return true;
    }
    return false;
  }

  private boolean columnEmpty(final String[] shape, final int columnIndex) {
    final List<Character> chars = Lists.newArrayList('0', ' ', '#');

    for (int i = 0; i < 3; i++) {
      if (!chars.contains(shape[i].charAt(columnIndex))) {
        return false;
      }
    }

    return true;
  }

  @Override
  public void openEditor(final Player player) {
    final RecipeEditSession session = new RecipeEditSession(player, this.getType(), this.getKey().getKey());
    session.setEditMode(true);
    session.setGroup(this.getGroup());
    session.setRecipeType(this.getType());

    final List<Character> tmp = CharBuffer.wrap(this.shapeString.toCharArray()).chars().mapToObj(ch -> (char) ch).collect(Collectors.toList());

    int pos = 0;
    for (final Character c : tmp) {
      if (!c.equals('0')) {
        session.getInventory().setItem(position.get(pos), this.getChoiceMap().get(c).getItemStack());
      } else {
        session.getInventory().setItem(position.get(pos), new ItemStack(Material.AIR));
      }
      pos++;
    }
    session.getInventory().setItem(24, this.result);

    plugin.getEditManager().addEditSession(player, session);
    session.openInterface();
  }

  @Override
  public RecipeType getType() {
    return RecipeType.SHAPED;
  }

  @Override
  public Recipe getRecipe() {
    return this;
  }

  @Override
  public boolean deleteRecipeFile() {
    final File file = new File(RecipeManager.shapedFolder, this.getKey().getKey() + ".yml");
    return file.delete();
  }

  @Override
  public boolean isNBTShape() {
    return this.getIngredientMap().values().stream().filter(item -> item != null).anyMatch(item -> item.hasItemMeta());
  }
}
