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

import org.bukkit.inventory.ItemStack;

public class ItemMatrix {

  public ItemMatrix(ItemStack[] matrix) {
    this.matrix = matrix;
  }

  private final ItemStack[] matrix;

  private int slotedID() {

    int currentIndex = 0;
    int currentHash = 0;
    int slotedHash = 0;
    boolean found = false;

    for (int index = 0; index < 8; index++) {
      ItemStack item = this.matrix[index];
      if (item != null) {
        currentHash = item.getType().toString().hashCode();

        if (currentIndex != 0 || found) {
          int dif = index - currentIndex;
          System.out.println(dif);
          currentHash += (int) Math.pow(dif, 10);
        }

        slotedHash += currentHash;
        currentIndex = index;
        found = true;
      }

    }

    return slotedHash;
  }

  @Override
  public int hashCode() {
    return this.slotedID();
  }

  @Override
  public boolean equals(Object other) {
		if (!(other instanceof ItemMatrix))
			return false;
    ItemMatrix otherMatrix = (ItemMatrix) other;
    return otherMatrix.hashCode() == this.hashCode();
  }

}