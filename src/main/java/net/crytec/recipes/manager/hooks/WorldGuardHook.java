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

package net.crytec.recipes.manager.hooks;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.internal.platform.WorldGuardPlatform;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.managers.RegionManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class WorldGuardHook implements Hook {

  @Getter
  private WorldGuardPlatform worldguard;
  private WorldGuardPlugin wg;

  @Override
  public void initialize() {
    if (Bukkit.getPluginManager().getPlugin("WorldGuard") != null) {
      this.worldguard = WorldGuard.getInstance().getPlatform();
      this.wg = WorldGuardPlugin.inst();
    }
  }

  public boolean regionContains(Location location, String text) {

    RegionManager rgManager = this.worldguard.getRegionContainer().get(BukkitAdapter.adapt(location.getWorld()));
    ApplicableRegionSet rgSet = rgManager.getApplicableRegions(BlockVector3.at(location.getX(), location.getY(), location.getZ()));

    return rgSet.getRegions().stream().anyMatch(rg -> rg.getId().contains(text));
  }

  public boolean checkStateOfCurrentRegion(Player player, StateFlag[] flags) {
    RegionManager m = this.worldguard.getRegionContainer().get(BukkitAdapter.adapt(player.getWorld()));
    LocalPlayer lp = this.wg.wrapPlayer(player);
    ApplicableRegionSet set = m.getApplicableRegions(BukkitAdapter.adapt(player.getLocation()).toVector().toBlockPoint());
		if (set.size() == 0)
			return false;
    State state = set.queryState(lp, flags);
    return (state == State.ALLOW);

  }

  @Override
  public boolean canLoad() {
    return true;
  }

  @Override
  public String getPluginName() {
    return "WorldGuard";
  }

}
