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

package net.crytec.recipes.manager;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import net.crytec.libs.commons.utils.item.ItemBuilder;
import net.crytec.recipes.CustomRecipes;
import net.crytec.recipes.data.CustomFurnaceRecipe;
import net.crytec.recipes.data.CustomShapedRecipe;
import net.crytec.recipes.data.CustomShapelessRecipe;
import net.crytec.recipes.data.IRecipe;
import net.crytec.recipes.data.VanillaRecipeHolder;
import net.crytec.recipes.util.UtilFiles;
import net.minecraft.server.v1_15_R1.CraftingManager;
import net.minecraft.server.v1_15_R1.DedicatedServer;
import net.minecraft.server.v1_15_R1.MinecraftServer;
import net.minecraft.server.v1_15_R1.RecipeArmorDye;
import net.minecraft.server.v1_15_R1.RecipeFireworks;
import net.minecraft.server.v1_15_R1.Recipes;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.FileUtils;
import org.bukkit.craftbukkit.v1_15_R1.CraftServer;
import org.bukkit.craftbukkit.v1_15_R1.util.CraftNamespacedKey;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

public class RecipeManager {

  @Getter
  private final ImmutableList<Integer> reservedSlots = ImmutableList.of(11, 12, 13, 20, 21, 22, 29, 30, 31, 24);
  private final HashMap<NamespacedKey, IRecipe> recipes = Maps.newHashMap();

  public static File shapedFolder;
  public static File shapelessFolder;
  public static File furnaceFolder;

  @Getter
  private final HashMap<String, VanillaRecipeHolder> vanillaRecipes = Maps.newHashMap();

  private final YamlConfiguration disabledRecipes;

  public RecipeManager(final CustomRecipes plugin) {
    shapelessFolder = new File(plugin.getDataFolder(), "shapeless");
    shapedFolder = new File(plugin.getDataFolder(), "shaped");
    furnaceFolder = new File(plugin.getDataFolder(), "furnace");
    UtilFiles.createFolder(shapelessFolder, shapedFolder, furnaceFolder);

//    this.disabledRecipes = new PluginConfig(plugin, plugin.getDataFolder(), "disabledVanillaRecipes");
    this.disabledRecipes = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "disabledRecipes.yml"));

    final MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
    final CraftingManager manager = server.getCraftingManager();

    Bukkit.recipeIterator().forEachRemaining(recipe -> {

      if ((recipe instanceof ShapedRecipe) || (recipe instanceof FurnaceRecipe) || (recipe instanceof ShapelessRecipe)) {
        final VanillaRecipeHolder holder = new VanillaRecipeHolder(recipe);

        if (holder.getIcon() != null && holder.getIcon().getType() != Material.AIR && holder.getIcon().getType() != Material.BARRIER) {
          this.vanillaRecipes.put(holder.getId(), holder);
        } else {

          final Recipes<?> NMS_something = manager.a(holder.getNMSKey()).get().g();
          final net.minecraft.server.v1_15_R1.IRecipe<?> rec = manager.recipes.get(NMS_something).get(holder.getNMSKey());
          if (rec instanceof RecipeFireworks) {
            holder.setIcon(new ItemBuilder(Material.FIREWORK_ROCKET).lore(ChatColor.RED + "Can not be re-activated without a restart!").build());
            this.vanillaRecipes.put(holder.getId(), holder);
          }
          if (rec instanceof RecipeArmorDye) {
            final ItemStack temp = new ItemBuilder(Material.LEATHER_CHESTPLATE).name(ChatColor.GRAY + "Colored armor").lore(ChatColor.RED + "Can not be re-activated without a restart!").build();
            holder.setIcon(temp);
            this.vanillaRecipes.put(holder.getId(), holder);
          }

        }
      }
    });

    if (this.disabledRecipes.isSet("disabled")) {
      for (final String recipeID : this.disabledRecipes.getStringList("disabled")) {
        if (this.vanillaRecipes.containsKey(recipeID)) {
          this.vanillaRecipes.get(recipeID).deactivate();
        }
      }
      plugin.getLogger().info("Disabled " + this.disabledRecipes.getStringList("disabled").size() + " vanilla recipes!");
    }
  }

  public void saveRecipes() {
    this.recipes.values().forEach(IRecipe::saveRecipe);
  }

  public void loadRecipes() {
    final Iterator<File> shapedIterator = FileUtils.iterateFiles(shapedFolder, new String[]{"yml"}, false);

    while (shapedIterator.hasNext()) {
      final File cur = shapedIterator.next();
      CustomShapedRecipe.load(cur);
    }

    final Iterator<File> shapelessIterator = FileUtils.iterateFiles(shapelessFolder, new String[]{"yml"}, false);

    while (shapelessIterator.hasNext()) {
      final File cur = shapelessIterator.next();
      CustomShapelessRecipe.load(cur);
    }

    final Iterator<File> furnaceIterator = FileUtils.iterateFiles(furnaceFolder, new String[]{"yml"}, false);

    while (furnaceIterator.hasNext()) {
      final File cur = furnaceIterator.next();
      CustomFurnaceRecipe.load(cur);
    }
  }

  public void saveDisabledRecipes() {
    this.disabledRecipes.set("disabled", this.vanillaRecipes.values().stream().filter(VanillaRecipeHolder::isDisabled).map(VanillaRecipeHolder::getId).collect(Collectors.toList()));
//    this.disabledRecipes.saveConfig();
  }

  public boolean isReservedSlot(final int slot) {
    return this.reservedSlots.contains(slot);
  }

  public boolean isCustomRecipe(final NamespacedKey key) {
    if (key == null) {
      return false;
    }
    return this.recipes.containsKey(key);
  }

  public IRecipe getRecipeByKey(final NamespacedKey key) {
    return this.recipes.get(key);
  }


  public void registerRecipe(final IRecipe recipe) {
    this.recipes.put(recipe.getKey(), recipe);
    Bukkit.addRecipe(recipe.getRecipe());
    if (!recipe.isNBTShape()) {

      final DedicatedServer server = ((CraftServer) Bukkit.getServer()).getHandle().getServer();
      final CraftingManager craftingManager = server.getCraftingManager();

      final Recipes<?> NMS_something = craftingManager.a(CraftNamespacedKey.toMinecraft(recipe.getKey())).get().g();
      craftingManager.recipes.get(NMS_something).getAndMoveToLast(craftingManager.recipes.get(NMS_something).firstKey());
    }
  }

  public Collection<IRecipe> getRecipes() {
    return this.recipes.values();
  }

  public Set<NamespacedKey> getRecipeKeys() {
    return this.recipes.keySet();
  }

  public void removeRecipe(final IRecipe recipe) {
    final Iterator<Recipe> iter = Bukkit.recipeIterator();
    final MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
    final CraftingManager manager = server.getCraftingManager();

    while (iter.hasNext()) {
      final Recipe current = iter.next();

      final Recipes<?> recipeChoices = manager.a(CraftNamespacedKey.toMinecraft(recipe.getKey())).get().g();

      if (current instanceof ShapelessRecipe) {
        final ShapelessRecipe r = (ShapelessRecipe) current;
        if (r.getKey().equals(recipe.getKey())) {
          iter.remove();
          manager.recipes.get(recipeChoices).remove(CraftNamespacedKey.toMinecraft(recipe.getKey()));
          this.recipes.remove(recipe.getKey());
          recipe.deleteRecipeFile();
          return;
        }
      } else if (current instanceof ShapedRecipe) {
        final ShapedRecipe r = (ShapedRecipe) current;
        if (r.getKey().equals(recipe.getKey())) {
          iter.remove();
          manager.recipes.get(recipeChoices).remove(CraftNamespacedKey.toMinecraft(recipe.getKey()));
          this.recipes.remove(recipe.getKey());
          recipe.deleteRecipeFile();
          return;
        }

      } else if (current instanceof FurnaceRecipe) {
        final FurnaceRecipe r = (FurnaceRecipe) current;
        if (r.getKey().equals(recipe.getKey())) {
          iter.remove();
          manager.recipes.get(recipeChoices).remove(CraftNamespacedKey.toMinecraft(recipe.getKey()));
          this.recipes.remove(recipe.getKey());
          recipe.deleteRecipeFile();
          return;
        }
      }
    }
  }

}
