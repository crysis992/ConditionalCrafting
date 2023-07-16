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

import com.google.common.collect.Maps;
import java.util.HashMap;
import lombok.Getter;
import net.crytec.recipes.manager.hooks.Hook;
import net.crytec.recipes.manager.hooks.JobsHook;
import net.crytec.recipes.manager.hooks.VaultHook;
import net.crytec.recipes.manager.hooks.WorldGuardHook;
import org.bukkit.Bukkit;

public class HookManager {

  private final HashMap<HookType, Hook> enabled = Maps.newHashMap();

  @Getter
  private VaultHook vault;
  @Getter
  private WorldGuardHook worldguard;
  @Getter
  private JobsHook jobs;

  public boolean canLoad(final HookType type) {
    return this.enabled.containsKey(type) && this.enabled.get(type).canLoad();
  }

  public Hook getHook(final HookType type) {
    return this.enabled.get(type);
  }

  public HookManager() {
    if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
      this.vault = new VaultHook();
      this.enabled.put(HookType.VAULT, this.vault);
    }
    if (Bukkit.getPluginManager().getPlugin("WorldGuard") != null) {
      this.worldguard = new WorldGuardHook();
      this.enabled.put(HookType.WORLDGUARD, this.worldguard);
    }
    if (Bukkit.getPluginManager().getPlugin("Jobs") != null) {
      this.jobs = new JobsHook();
      this.enabled.put(HookType.JOBS, this.jobs);
    }
  }

  public static enum HookType {
    VAULT, WORLDGUARD, JOBS
  }

}
