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

package net.crytec.recipes;

import co.aikar.commands.BukkitCommandManager;
import com.google.common.collect.Maps;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import lombok.Getter;
import net.crytec.inventoryapi.InventoryAPI;
import net.crytec.libs.commons.utils.CommonsAPI;
import net.crytec.recipes.commands.CommandSetup;
import net.crytec.recipes.commands.ItemEditorCommand;
import net.crytec.recipes.commands.RecipeCommand;
import net.crytec.recipes.conditions.AttributeCondition;
import net.crytec.recipes.conditions.BiomeCondition;
import net.crytec.recipes.conditions.ConditionRegistrar;
import net.crytec.recipes.conditions.EntityAmountNearby;
import net.crytec.recipes.conditions.LevelCondition;
import net.crytec.recipes.conditions.PermissionCondition;
import net.crytec.recipes.conditions.RegionContainsNameCondition;
import net.crytec.recipes.conditions.TimeCondition;
import net.crytec.recipes.conditions.VaultCondition;
import net.crytec.recipes.conditions.WorldCondition;
import net.crytec.recipes.conditions.WorldGuardFlagCondition;
import net.crytec.recipes.data.RecipeType;
import net.crytec.recipes.editor.RecipeEditManager;
import net.crytec.recipes.io.Language;
import net.crytec.recipes.listener.RecipeListener;
import net.crytec.recipes.manager.HookManager;
import net.crytec.recipes.manager.RecipeManager;
import net.crytec.recipes.metrics.ConditionBarChart;
import net.crytec.recipes.metrics.Metrics;
import net.crytec.recipes.metrics.Metrics.AdvancedPie;
import net.crytec.recipes.util.CommandFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

public class CustomRecipes extends JavaPlugin {

  @Getter
  private RecipeListener recipeListener;

  @Getter
  private static CustomRecipes instance;

  @Getter
  private ConditionRegistrar conditionRegistrar;

  @Getter
  private RecipeManager recipeManager;

  @Getter
  RecipeEditManager editManager;

  @Getter
  private HookManager hookManager;

  private boolean placeHolderSupport = true;

  @Override
  public void onLoad() {
    instance = this;
  }

  @Override
  public void onEnable() {
    final File file = this.getDataFolder();
    if (!file.exists()) {
      file.mkdir();
    }

    new InventoryAPI(this);
    new CommonsAPI(this);

    this.hookManager = new HookManager();

    try {
      Language.initialize(this);
    } catch (final IOException ignored) {
    }

    if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
      this.getLogger().info("PlaceholderAPI was not found. No Placeholder support enabled.");
      this.placeHolderSupport = false;
    }

    this.conditionRegistrar = new ConditionRegistrar(this);
    this.setupConditions();
    this.recipeManager = new RecipeManager(this);
    this.getRecipeManager().loadRecipes();
    this.recipeListener = new RecipeListener(this, this.recipeManager, this.placeHolderSupport);

    this.editManager = new RecipeEditManager(this, this.recipeManager);

    Bukkit.getPluginManager().registerEvents(this.recipeListener, this);

    final BukkitCommandManager manager = new BukkitCommandManager(this);
    new CommandSetup(this, manager);
    manager.registerCommand(new RecipeCommand(this));
    manager.registerCommand(new ItemEditorCommand());

    final Logger l = (Logger) LogManager.getRootLogger();
    l.addFilter(new CommandFilter());

    // Metrics
    final Metrics metrics = new Metrics(this);
    final AdvancedPie pie = new AdvancedPie("recipe_count", () -> {
      final Map<String, Integer> data = Maps.newHashMap();

      for (final RecipeType type : RecipeType.values()) {
        data.put(type.toString(), (int) this.getRecipeManager().getRecipes().stream().filter(recipe -> recipe.getType().equals(type)).count());
      }
      return data;
    });

    metrics.addCustomChart(new ConditionBarChart(this.recipeManager));
    metrics.addCustomChart(pie);
  }

  @Override
  public void onDisable() {
    if (this.recipeManager != null) {
      this.recipeManager.saveDisabledRecipes();
      this.recipeManager.saveRecipes();
    }
  }

  private void setupConditions() {
    this.conditionRegistrar.registerCondition(TimeCondition.class, "Time", Material.CLOCK);
    this.conditionRegistrar.registerCondition(PermissionCondition.class, "Permission", Material.BOOK);
    this.conditionRegistrar.registerCondition(BiomeCondition.class, "Biome", Material.STRUCTURE_BLOCK);
    this.conditionRegistrar.registerCondition(WorldCondition.class, "World", Material.COMPASS);
    this.conditionRegistrar.registerCondition(LevelCondition.class, "Level", Material.EXPERIENCE_BOTTLE);
    this.conditionRegistrar.registerCondition(VaultCondition.class, "Vault", Material.GOLD_NUGGET);
    this.conditionRegistrar.registerCondition(WorldGuardFlagCondition.class, "WorldGuardFlag", Material.BLACK_BANNER);
    this.conditionRegistrar.registerCondition(EntityAmountNearby.class, "NearbyEntities", Material.ZOMBIE_HEAD);
    this.conditionRegistrar.registerCondition(AttributeCondition.class, "Attribute", Material.ARMOR_STAND);
    this.conditionRegistrar.registerCondition(RegionContainsNameCondition.class, "RegionName", Material.NAME_TAG);
    this.getLogger().info("Sucessfully registered " + this.getConditionRegistrar().getConditions().size() + " Conditions.");
  }
}