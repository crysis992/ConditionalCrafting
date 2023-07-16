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

package net.crytec.recipes.util;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import net.crytec.libs.commons.utils.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;

public class RecipeConfirmer {

  public RecipeConfirmer() {
    this.recipes = Sets.newHashSet();
    this.recipeMap = Maps.newHashMap();
    final Iterator<Recipe> iter = Bukkit.recipeIterator();
    while (iter.hasNext()) {
      final Recipe recipe = iter.next();
      if (recipe instanceof ShapedRecipe) {
        final ItemMatrix matrix = new ItemMatrix(this.fromRecipe((ShapedRecipe) recipe));

        this.recipes.add(matrix);
        this.recipeMap.put(matrix, recipe);
      }
    }
  }

  private final Set<ItemMatrix> recipes;
  private final Map<ItemMatrix, Recipe> recipeMap;

  public boolean isVanilla(final ItemStack[] matrix) {
    return this.recipes.contains(new ItemMatrix(matrix));
  }

  public Recipe getRecipeFromMatrix(final ItemStack[] matrix) {
    return this.recipeMap.get(new ItemMatrix(matrix));
  }

  public void remove(final ItemStack[] matrix) {
    this.recipes.remove(new ItemMatrix(matrix));
  }

  private ItemStack[] fromRecipe(final ShapedRecipe rec) {

    final ItemStack[] matrix = new ItemStack[9];
    String[] shape = rec.getShape();

    while (shape.length < 3) {
      shape = ArrayUtils.add(shape, "   ");
    }

    System.out.println(shape[0]);
    System.out.println(shape[1]);
    System.out.println(shape[2]);

    final StringBuilder shapeFull = new StringBuilder(shape[0] + shape[1] + shape[2]);

    while (shapeFull.length() < 9) {
      shapeFull.append(" ");
    }

    for (int index = 0; index < 9; index++) {
      final char c = shapeFull.charAt(index);
      if (c == ' ' || c == '0') {
        matrix[index] = null;
      } else {
        matrix[index] = rec.getIngredientMap().get(c);
      }
    }

    return matrix;
  }

}
