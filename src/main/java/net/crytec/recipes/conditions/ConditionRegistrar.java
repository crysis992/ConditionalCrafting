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

import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;
import net.crytec.recipes.CustomRecipes;
import net.crytec.recipes.conditions.annotations.ConditionDefaults;
import net.crytec.recipes.conditions.annotations.ConditionHook;
import net.crytec.recipes.io.Language;
import net.crytec.recipes.manager.HookManager;
import net.crytec.recipes.manager.hooks.Hook;
import org.bukkit.Bukkit;
import org.bukkit.Material;

public class ConditionRegistrar {

  private final HashBiMap<Class<? extends ConditionBase>, String> conditions = HashBiMap.create();
  private final HashMap<String, Material> conditionIcons = Maps.newHashMap();

  private final CustomRecipes plugin;

  public ConditionRegistrar(final CustomRecipes plugin) {
    this.plugin = plugin;
  }

  public void registerCondition(final Class<? extends ConditionBase> clazz, final String id, final Material icon) {
    final ConditionDefaults def = clazz.getDeclaredAnnotation(ConditionDefaults.class);
    final HookManager hookManager = this.plugin.getHookManager();

    if (def == null) {
      this.plugin.getLogger().severe("Failed to register condition for class " + clazz.getName() + " because it is missing ConditionDefaults!");
      this.plugin.getLogger().severe("Please inform the plugin author to update.");
      return;
    }

    final ConditionHook hookannotation = clazz.getDeclaredAnnotation(ConditionHook.class);
    if (hookannotation != null) {
      final Hook hook = hookManager.getHook(hookannotation.type());

      if (hook == null) {
        return;
      }

      if (Bukkit.getPluginManager().getPlugin(hook.getPluginName()) != null) {
        if (!hook.canLoad()) {
          return;
        }
        hook.initialize();
      } else {
        return;
      }
    }

    if (!Language.getFile().isSet("condition." + id + ".name") || !Language.getFile().isSet("condition." + id + ".desc")) {

      Language.getFile().set("condition." + id + ".name", def.name());
      Language.getFile().set("condition." + id + ".desc", Arrays.stream(def.description()).collect(Collectors.toList()));
      Language.saveFile();
    }

    this.conditions.put(clazz, id);
    this.conditionIcons.put(id, icon);
  }

  public Material getIcon(final String id) {
    return this.conditionIcons.get(id);
  }

  public Class<? extends ConditionBase> getClassByID(final String id) {
    return this.conditions.inverse().get(id);
  }

  public String getID(final Class<? extends ConditionBase> clazz) {
    return this.conditions.get(clazz);
  }

  public Set<Class<? extends ConditionBase>> getConditions() {
    return this.conditions.keySet();
  }

  public ConditionBase getNewInstance(final Class<? extends ConditionBase> clazz) {
    try {
      return clazz.getDeclaredConstructor().newInstance();
    } catch (final InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
      this.plugin.getLogger().severe("Failed to initialize class instance for " + clazz.getName() + ": " + e.getMessage());
      return null;
    }
  }
}