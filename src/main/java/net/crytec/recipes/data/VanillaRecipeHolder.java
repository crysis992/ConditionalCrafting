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

import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_15_R1.MinecraftKey;
import net.minecraft.server.v1_15_R1.MinecraftServer;
import net.minecraft.server.v1_15_R1.Recipes;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_15_R1.CraftServer;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftFurnaceRecipe;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftShapedRecipe;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftShapelessRecipe;
import org.bukkit.craftbukkit.v1_15_R1.util.CraftNamespacedKey;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

public class VanillaRecipeHolder {

  @Getter
  private final Recipe recipe;

  @Getter
  private final String id;

  @Getter
  private final MinecraftKey NMSKey;

  @Getter
  @Setter
  private ItemStack icon;

  @Getter
  @Setter
  private boolean disabled;

  public VanillaRecipeHolder(Recipe recipe) {
    this.recipe = recipe;
    this.icon = this.getResult();

    this.NMSKey = CraftNamespacedKey.toMinecraft(this.getRecipeKey());
    this.id = this.NMSKey.getKey();

  }


  @SuppressWarnings("deprecation")
  public void deactivate() {
    Recipes<?> NMS_something = MinecraftServer.getServer().getCraftingManager().a(this.NMSKey).get().g();

    ((CraftServer) Bukkit.getServer()).getHandle().getServer().getCraftingManager().recipes.get(NMS_something).remove(this.NMSKey);

    MinecraftServer.getServer().getCraftingManager().recipes.get(NMS_something).remove(this.NMSKey);
    this.disabled = true;
  }

  public void activate() {
		if (this.recipe instanceof ShapedRecipe)
			((CraftShapedRecipe) this.recipe).addToCraftingManager();
		else if (this.recipe instanceof ShapelessRecipe)
			((CraftShapelessRecipe) this.recipe).addToCraftingManager();
		else
			((CraftFurnaceRecipe) this.recipe).addToCraftingManager();

    this.disabled = false;
  }


  private ItemStack getResult() {
		if (this.recipe instanceof ShapedRecipe)
			return ((ShapedRecipe) this.recipe).getResult();
		else if (this.recipe instanceof ShapelessRecipe)
			return ((ShapelessRecipe) this.recipe).getResult();
		else
			return ((FurnaceRecipe) this.recipe).getResult();
  }

  private NamespacedKey getRecipeKey() {
		if (this.recipe instanceof ShapedRecipe)
			return ((ShapedRecipe) this.recipe).getKey();
		else if (this.recipe instanceof ShapelessRecipe)
			return ((ShapelessRecipe) this.recipe).getKey();
		else
			return ((FurnaceRecipe) this.recipe).getKey();
  }

}
