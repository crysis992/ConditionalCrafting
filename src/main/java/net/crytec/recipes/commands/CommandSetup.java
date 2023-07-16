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

package net.crytec.recipes.commands;

import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.BukkitCommandManager;
import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.InvalidCommandArgument;
import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;
import net.crytec.recipes.CustomRecipes;
import net.crytec.recipes.data.IRecipe;
import net.crytec.recipes.io.Language;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;

public class CommandSetup {

  @SuppressWarnings("deprecation")
  public CommandSetup(CustomRecipes plugin, BukkitCommandManager manager) {

    ImmutableList<String> matCompletion = ImmutableList
        .copyOf(Arrays.stream(Material.values()).filter(mat -> !mat.toString().startsWith("LEGACY")).map(Material::toString).collect(Collectors.toList()));
    ImmutableList<String> enchantmentCompletion = ImmutableList.copyOf(Arrays.stream(Enchantment.values()).map(e -> e.getKey().getKey()).collect(Collectors.toList()));

    manager.getCommandCompletions().registerCompletion("material", c -> matCompletion);
    manager.getCommandCompletions().registerCompletion("enchantment", c -> enchantmentCompletion);

    manager.getCommandContexts().registerContext(IRecipe.class, c -> {
      String tag = c.popFirstArg();

      Optional<NamespacedKey> key = plugin.getRecipeManager().getRecipeKeys().stream().filter(k -> k.getKey().equals(tag)).findFirst();
      if (key.isPresent())
				return plugin.getRecipeManager().getRecipeByKey(key.get());
			else
				throw new InvalidCommandArgument(Language.ERROR_INVALID_RECIPE.toString());
    });

    manager.getCommandContexts().registerContext(Enchantment.class, c -> {
      String tag = c.popFirstArg();
      Optional<Enchantment> ench = Arrays.stream(Enchantment.values()).filter(e -> e.getKey().getKey().equals(tag)).findFirst();

      if (ench.isPresent())
				return ench.get();
			else
				throw new InvalidCommandArgument(Language.ERROR_INVALID_ENCHANTMENT.toString());
    });

    manager.getCommandConditions().addCondition(String.class, "validKey", (context, executionContext, input) -> {
      Optional<NamespacedKey> key = plugin.getRecipeManager().getRecipeKeys().stream().filter(k -> k.getKey().equals(input)).findFirst();
      if (key.isPresent())
				throw new ConditionFailedException(Language.ERROR_INVALID_RECIPE_PRESENT.toString());
    });

    manager.getCommandCompletions().registerCompletion("recipes", c -> {
      return ImmutableList.copyOf(plugin.getRecipeManager().getRecipeKeys().stream().map(NamespacedKey::getKey).collect(Collectors.toList()));
    });

    manager.getCommandConditions().addCondition("itemInHand", (context -> {
      BukkitCommandIssuer issuer = context.getIssuer();
      if (issuer.isPlayer()) {
        issuer.getPlayer().getInventory().getItemInMainHand();
        if (issuer.getPlayer().getInventory().getItemInMainHand().getType() == Material.AIR)
					throw new ConditionFailedException(Language.ERROR_NOITEM.toString());
      } else
				throw new ConditionFailedException("This Command cannot be executed as console.");
    }));
    manager.enableUnstableAPI("help");
  }

}
