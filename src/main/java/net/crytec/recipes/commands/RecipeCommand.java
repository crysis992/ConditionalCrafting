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

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import java.util.Optional;
import java.util.regex.Pattern;
import net.crytec.inventoryapi.SmartInventory;
import net.crytec.libs.commons.utils.UtilPlayer;
import net.crytec.recipes.CustomRecipes;
import net.crytec.recipes.data.RecipeType;
import net.crytec.recipes.editor.RecipeEditSession;
import net.crytec.recipes.gui.RecipeListGUI;
import net.crytec.recipes.io.Language;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

@CommandAlias("customrecipe|crecipe|recipes")
@CommandPermission("cc.admin")
public class RecipeCommand extends BaseCommand {

  private final CustomRecipes plugin;

  public RecipeCommand(final CustomRecipes plugin) {
    this.plugin = plugin;
  }

  private static final Pattern VALID_KEY = Pattern.compile("[a-z0-9/._-]+");

  @Default
  public void addCommand(final Player player) {
    SmartInventory.builder().provider(new RecipeListGUI()).size(5).title(Language.INTERFACE_TITLE_RECIPELIST.toString()).build().open(player);
  }

  @Subcommand("add")
  @Syntax("This will add a new recipe")
  public void addRecipe(final Player sender, final String recipe) {
    final String recipeID = recipe.toLowerCase().replace(" ", "");

    final Optional<NamespacedKey> key = this.plugin.getRecipeManager().getRecipeKeys().stream().filter(k -> k.getKey().equals(recipeID)).findFirst();
    if (key.isPresent()) {
      sender.sendMessage(Language.ERROR_INVALID_RECIPE_PRESENT.toChatString());
      UtilPlayer.playSound(sender, Sound.ENTITY_LEASH_KNOT_BREAK, 1.2F, 0.4F);
      return;
    }

    if (!VALID_KEY.matcher(recipeID).matches()) {
      UtilPlayer.playSound(sender, Sound.ENTITY_LEASH_KNOT_BREAK, 1.2F, 0.4F);
      sender.sendMessage("Invalid key [" + recipeID + "]. Must be [a-z0-9/._-]");
      return;
    }
    final RecipeEditSession session = new RecipeEditSession(sender, RecipeType.SHAPELESS, recipeID);
    CustomRecipes.getInstance().getEditManager().addEditSession(sender, session);
    session.openInterface();
  }
}