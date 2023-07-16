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

package net.crytec.recipes.io;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import net.crytec.recipes.CustomRecipes;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * An enum for requesting strings from the language file.
 */

public enum Language {
  VERSION("version", "1.0"),

  ERROR_INVALID_RECIPE("error.invalidRecipe", "&cThere is no recipe with the given name."),
  ERROR_INVALID_RECIPE_PRESENT("error.recipeAlreadyPresent", "&cThere is already a recipe with the given ID"),
  ERROR_INVALID_ENCHANTMENT("error.invalidEnchantment", "&cThere is no enchantment with the given name."),
  ERROR_NOITEM("error.noItem", "&cYou need to hold an item in your hand to do this."),
  ERROR_APPLY_FURNACE("error.applyFurnace", "&cConditions cannot be applied to furnace recipes."),
  ERROR_INVALID_DOUBLE_INPUT("error.invalidDoubleInput", "Please enter a double value (example: 2.5) "),

  CONDITION_BIOME_ERROR("condition.Biome.error", "&cYou are in the wrong biome to craft this item."),

  CONDITION_PERMISSION_ERROR("condition.Permission.error", "&cYou lack the proper permission to craft this item."),
  CONDITION_PERMISSION_BUTTON("condition.Permission.button", "&2Click to set a permission node."),
  CONDITION_PERMISSION_DESC("condition.Permission.desc", "&7Current permission node:"),

  CONDITION_ATTRIBUTE_ERROR("condition.Attribute.error", "&cYou dont have the needed attributes for this recipe."),
  CONDITION_ATTRIBUTE_BUTTON("condition.Attribute.button", "&fCurrent value:"),

  CONDITION_TIME_ERROR("condition.Time.error", "&cThis item can only be crafting during %daytime% time"),
  CONDITION_TIME_BUTTON("condition.Time.button", "&fMode &6%daytime%"),

  CONDITION_LEVEL_BUTTON("condition.Level.desc", "&7Current level required:"),
  CONDITION_LEVEL_ERROR("condition.Level.error", "&cTo craft this item you need at least level %level%"),

  CONDITION_VAULT_ERROR("condition.Vault.Error", "&cYou need at least %money% to craft this item."),
  CONDITION_VAULT_DESC("condition.Vault.current", "&7Current value:"),

  CONDITION_WORLDGUARD_FLAGS("condition.WorldGuardFlag.error", "Cannot craft item because you failed one of the following region flag checks: %flags%"),

  CONDITION_WORLDGUARD_NAME("condition.WorldGuardName.error", "Cannot craft item because you are not in a region containing %text%."),
  CONDITION_WORLDGUARD_NAME_BUTTON("condition.WorldGuardName.button", "&fSet Region"),

  CONDITION_WORLD_ERROR("condition.World.error", "&cYou are in the wrong world to craft this item."),

  CONDITION_ENTITYRADIUS_RADIUS("condition.NearbyEntities.radius", "&6Change Radius"),
  CONDITION_ENTITYRADIUS_AMOUNT("condition.NearbyEntities.amount", "&6Change Amount"),
  CONDITION_ENTITYRADIUS_TYPE("condition.NearbyEntities.type", "&6Change Type"),
  CONDITION_ENTITYRADIUS_ERROR("condition.NearbyEntities.Error", "&6There must be at least %amount% %type%s in a radius of %radius% to craft this item."),

  RESULT_BLOCKER_RIGHTCLICK("resultblocker.rightclick", "&6Right Click -> &fremoce blocked item"),

  GENERAL_LEFTCLICK_INCREASE("interface.general.leftClickIncrease", "&a<Left click to increase value>"),
  GENERAL_RIGHTCLICK_DECREASE("interface.general.rightClickDecrease", "&6<Right click to decrease value>"),
  GENERAL_LEFT_CHANGE("interface.general.leftClickEdit", "&2<Left click to change value>"),
  GENERAL_MESSAGE_RECIPE_ADDED("message.recipeAdded", "&7Recipe with ID &6%id%&7 is now registered."),

  CONDITION_CHATINPUT("condition.chatinput", "&e&lThis conditon configuration requires chat input (&d%condition%&e&l): "),

  RECIPETYPE_FURNACE("recipetype.furnace", "&e&lFurnace"),
  RECIPETYPE_SHAPED("recipetype.shaped", "&e&lShaped"),
  RECIPETYPE_SHAPELESS("recipetype.shapeless", "&e&lShapeless"),

  INTERFACE_RECIPE_DELETED("interface.recipeDeleted", "&cRecipe deleted."),
  INTERFACE_RECIPE_OPEN("interface.recipeEditorOpen", "&7You are now adding a new recipe with ID %id%"),

  INTERFACE_RIGHT_CLICK_DELETE("interface.rightClickRemove", "&4Right click to delete"),
  INTERFACE_LEFT_CLICK_CONFIG("interface.leftClickConfigure", "&2Left click to configure"),
  INTERFACE_LEFT_CLICK_ADD_CONDITION("interface.leftClickAddCondition", "&2Left click to add selected condition"),

  INTERFACE_TITLE_RECIPEEDIT("interface.title.recipeEditor", "&8&lRecipe Editor"),
  INTERFACE_TITLE_CONDITIONEDIT("interface.title.conditionEditor", "&8&lCondition Editor"),
  INTERFACE_TITLE_CONDITIONLIST("interface.title.conditionList", "&8&lCondition List"),
  INTERFACE_TITLE_RECIPELIST("interface.title.recipeList", "&8&lRecipe List"),

  INTERFACE_VANILLA_STATUS("interface.vanilla.status", "&fStatus: %status%"),
  INTERFACE_VANILLA_STATUS_ENABLED("interface.vanilla.statusEnabled", "&2Enabled"),
  INTERFACE_VANILLA_STATUS_DISABLED("interface.vanilla.statusDisabled", "&cDisabled"),
  INTERFACE_VANILLA_RECIPE_TITLE("interface.vanilla.title", "&8&lVanilla Recipes"),

  INTERFACE_MAIN_ADDRECIPE("interface.main.addRecipe", "&2Add a new recipe"),
  INTERFACE_MAIN_DISABLEVANILLA("interface.main.disableVanilla", "&4Disable vanilla recipes"),
  INTERFACE_MAIN_DELETE_RECIPE("interface.main.deleteRecipe", "&4Delete recipe"),
  INTERFACE_MAIN_EDITSHAPE("interface.main.editshape", "&fEdit shape"),

  INTERFACE_BUTTON_GROUP("interface.buttonGroup", "&fGroup"),
  INTERFACE_BUTTON_GROUP_CHATINPUT("interface.buttonGroupChatInput", "&7Please enter a group name:"),
  INTERFACE_BUTTON_GROUP_DESC("interface.buttonDescGroup", "&7Group: %group%"),
  INTERFACE_BUTTON_GROUP_DESCDEF("interface.buttonDescDefGroup", "&7Default: Not grouped"),

  INTERFACE_BUTTON_EXP("interface.buttonExp", "&fExperience"),
  INTERFACE_BUTTON_EXP_DESC("interface.buttonDescExp", "&7Current: %exp% experience"),
  INTERFACE_BUTTON_EXP_DESCDEF("interface.buttonDescDefExp", "&7Default: 0 experience"),

  INTERFACE_BUTTON_COOKING("interface.buttonTime", "&fCooking Time"),
  INTERFACE_BUTTON_COOKING_DESC("interface.buttonDescTime", "&7Current: %time% ticks"),
  INTERFACE_BUTTON_COOKING_DESCDEF("interface.buttonDescDefTime", "&7Default: 200 ticks"),

  INTERFACE_BUTTON_BACK("interface.button.back", "&f&lBack"),
  INTERFACE_BUTTON_NEXT_PAGE("interface.button.nextPage", "&f&lNext Page"),
  INTERFACE_BUTTON_PREVIOUS_PAGE("interface.button.previousPage", "&f&lPrevious Page"),

  INTERFACE_BUTTON_SAVE("interface.buttonSave", "&2Save recipe"),

  EDITOR_NO_PERMISSION("editor.nopermission", "&cYou dont have the permission to use this editor."),

  EDITOR_MAIN_TITLE("editor.title", "&8&lItem Editor"),
  EDITOR_MAIN_FLAGICON("editor.flagicon", "&6Edit Item-Flags"),
  EDITOR_MAIN_SPECIALICON("editor.specialicon", "&6Special Options"),
  EDITOR_MAIN_ENCHANTICON("editor.enchanticon", "&6Edit Enchantments"),
  EDITOR_MAIN_NBTICON("editor.tagicon", "&6Edit NBT-Data"),
  EDITOR_MAIN_TEXTICON("editor.texticon", "&6Edit Item-Text"),

  EDITOR_ENCHANT_SAFEMODE_MESSAGE("editor.enchantments.unsafemode.message", "&fYou need a permission to enchant beyond the max Level!"),
  EDITOR_ENCHANT_SAFEMODE("editor.enchantments.unsafemode.name", "UNSAFE MODE"),

  EDITOR_SPECIAL_UNBREAKABLE("editor.special.unbreakable", "Unbreakable"),

  EDITOR_JSON_TITLE("editor.namelore.title", "&fName and Lore editor"),
  EDITOR_JSON_PAGE("editor.namelore.page", "&fPage"),
  EDITOR_JSON_DISPLAYNAME("editor.namelore.displayname", "&fDisplay name: "),
  EDITOR_JSON_EXIT_TEXT("editor.namelore.exittext", "&c[&fExit Editor&c]&f"),
  EDITOR_JSON_EXIT_HOVER("editor.namelore.hoverinfo", "&fYou recieve the finished item."),
  EDITOR_JSON_APPEND_LINE("editor.appendline.text", "&e[&fAppend&e]&f"),
  EDITOR_JSON_APPEND_HOVER("editor.appendline.hoverinfo", "&fAdds empty line"),
  EDITOR_JSON_ENTER_LORE("editor.lore.enterLine", "Enter new line"),
  EDITOR_JSON_HEADEREXIT_HOVER("editor.exit.hover", "�fEnd Editor + Get Item"),
  EDITOR_JSON_HOVER_INSERTLINE("editor.hover.insertline", "&fInsert line here"),
  EDITOR_JSON_HOVER_REMOVELINE("editor.hover.removeline", "&fRemove line"),
  EDITOR_JSON_HOVER_EDITLINE("editor.hover.editline", "&fEdit this line"),

  EDITOR_ATTRIUBTE_TITLE("editor.attribute.name", "&6Attribute-Editor"),
  EDITOR_ATTRIUBTE_ADD("editor.attribute.add", "&fNew Modifier"),
  EDITOR_ATTRIUBTE_ALLSLOTS("editor.attribute.allslots", "ALL"),
  EDITOR_ATTRIUBTE_SLOTS("editor.attribute.slots", "&6Edit slots for appliance"),
  EDITOR_ATTRIUBTE_TYPE("editor.attribute.type", "&6Edit type of Attribute"),
  EDITOR_ATTRIUBTE_VALUE("editor.attribute.value", "&6Edit value"),
  EDITOR_ATTRIUBTE_OPERATION("editor.attribute.operation", "&6Edit operation type"),
  EDITOR_ATTRIUBTE_LEFT("editor.attribute.left", "&aLeft-Click &fto edit"),
  EDITOR_ATTRIUBTE_RIGHT("editor.attribute.right", "&cRight-Click &fto remove"),
  EDITOR_ATTRIUBTE_APPLY("editor.attribute.apply", "&aApply changes"),

  EDITOR_POTION_TITLE("editor.potion.title", "&6Potion-Editor"),
  EDITOR_POTION_NOPOTION("editor.potion.nopotion", "&fThis is not a Potion."),
  EDITOR_POTION_BREW("editor.potion.brew", "&aBrew"),
  EDITOR_POTION_ADDEFFECT("editor.potion.addeffect", "&fAdd extra PotionEffect"),
  EDITOR_POTION_AMBIENT("editor.potion.ambient", "Ambient effects"),
  EDITOR_POTION_AMPLIFIER("editor.potion.amplifier", "&eEdit Amplifier"),
  EDITOR_POTION_DURATION("editor.potion.duration", "&eEdit Duration"),
  EDITOR_POTION_EFFECT("editor.potion.effect", "&eEdit Effect"),
  EDITOR_POTION_TIER("editor.potion.tier", "Tier"),
  EDITOR_POTION_EDIT("editor.potion.edit", "�aLeft Click �fEdit Effect"),
  EDITOR_POTION_REMOVE("editor.potion.remove", "�cRight Click �fRemove Effect"),

  EDITOR_ENV_TITLE("editor.env.title", "&6Banner/Firework/LeatherArmor - Editor"),
  EDITOR_ENV_FIREWORK_TITLE("editor.env.firework.title", "&6Firework Editor"),
  EDITOR_ENV_FIREWORK_POWER("editor.env.firework.power", "&fEdit Power"),

  EDITOR_NBT_TITLE("editor.nbt.title", "�6NBT-Editor"),
  EDITOR_NBT_ARRAYSPLITTER("editor.nbt.arraysplitter", ":"),
  EDITOR_NBT_ERROR_TYPEFORMAT("editor.nbt.typeformaterror", "&cThe given input was not applicable for this data type."),
  EDITOR_NBT_COLLECTION_LEFT("editor.nbt.collection.left", "�fLeft Click > �aEnter collection"),
  EDITOR_NBT_COLLECTION_MIDDLE("editor.nbt.collection.middle", "�fMiddle Click > �cRemove collection"),
  EDITOR_NBT_ENTRY_LEFT("editor.nbt.entry.left", "�fLeft Click > �aEdit value"),
  EDITOR_NBT_ENTRY_RIGHT("editor.nbt.entry.right", "�fRight Click > �eCycle type"),
  EDITOR_NBT_ENTRY_MIDDLE("editor.nbt.entry.middle", "�fMiddle Click > �cRemove entry"),
  EDITOR_NBT_ENTRY_ADDENTRY("editor.nbt.entry.newentry", "�fAdd NBT Entry"),
  EDITOR_NBT_ENTRY_ADDLIST("editor.nbt.entry.newlist", "�fAdd NBT List"),
  EDITOR_NBT_ENTRY_ADDCOLLECTION("editor.nbt.entry.newcollection", "�fAdd NBT Collection"),
  EDITOR_NBT_ENTRY_RAWDATA("editor.nbt.entry.rawdata", "�fEdit raw data"),

  TITLE("title", "&9Recipe &7> ");


  private final String path;
  private String def;
  private boolean isArray = false;

  private List<String> defArray;
  private static YamlConfiguration LANG;

  /**
   * Lang enum constructor.
   *
   * @param path  The string path.
   * @param start The default string.
   */
  private Language(final String path, final String start) {
    this.path = path;
    this.def = start;
  }

  private Language(final String path, final List<String> start) {
    this.path = path;
    this.defArray = start;
    this.isArray = true;
  }

  /**
   * Set the {@code YamlConfiguration} to use.
   *
   * @param config The config to set.
   */
  public static void setFile(final YamlConfiguration config) {
    LANG = config;
  }

  public static YamlConfiguration getFile() {
    return LANG;
  }

  @Override
  public String toString() {
      if (this == TITLE) {
          return ChatColor.translateAlternateColorCodes('&', LANG.getString(this.path, this.def)) + " ";
      }
    return ChatColor.translateAlternateColorCodes('&', LANG.getString(this.path, this.def));
  }

  /**
   * Get the String with the TITLE
   *
   * @return
   */
  public String toChatString() {
    return TITLE.toString() + ChatColor.translateAlternateColorCodes('&', LANG.getString(this.path, this.def));
  }

  public List<String> getDescriptionArray() {
    return LANG.getStringList(this.path).stream().map(x -> ChatColor.translateAlternateColorCodes('&', x)).collect(Collectors.toList());
  }

  public boolean isArray() {
    return this.isArray;
  }

  public List<String> getDefArray() {
    return this.defArray;
  }

  /**
   * Get the default value of the path.
   *
   * @return The default value of the path.
   */
  public String getDefault() {
    return this.def;
  }

  /**
   * Get the path to the string.
   *
   * @return The path to the string.
   */
  public String getPath() {
    return this.path;
  }

  public static void saveFile() {
    final File lang = new File(JavaPlugin.getPlugin(CustomRecipes.class).getDataFolder(), "language.yml");
    try {
      Language.getFile().save(lang);
    } catch (final IOException e) {
      e.printStackTrace();
    }
  }

  public static Language getByPath(final String path) {
    for (final Language v : values()) {
      if (v.getPath().equals(path)) {
        return v;
      }
    }
    return null;
  }

  private static boolean isValidPath(final String path) {
    return Arrays.stream(values()).anyMatch(lang -> lang.getPath().equals(path));
  }

  public static void initialize(final JavaPlugin plugin) throws IOException {
    final File languageFile = new File(plugin.getDataFolder(), "language.yml");
    if (!languageFile.exists() && !languageFile.createNewFile()) {
      plugin.getLogger().severe("Failed to create language.yml");
      return;
    }

    final YamlConfiguration langCfg = YamlConfiguration.loadConfiguration(languageFile);
    setFile(langCfg);

    int updated = 0;
    for (final Language entry : values()) {
      if (!langCfg.isSet(entry.getPath())) {
        langCfg.set(entry.getPath(), entry.isArray ? entry.getDefArray() : entry.getDefault());
        updated++;
      }
    }

    if (updated > 0) {
      langCfg.save(languageFile);
      plugin.getLogger().info("Updated language.yml with " + updated + " new entries!");
    }

    int removed = 0;
    for (final String key : langCfg.getRoot().getKeys(true)) {

      if (!langCfg.isConfigurationSection(key) && !isValidPath(key)) {
        plugin.getLogger().info(key + " is no longer a valid language translation...removing");
        langCfg.set(key, null);
        removed++;
      }
    }

    if (removed > 0) {
      plugin.getLogger().info("Removed " + removed + " old language entries from your language.yml");
      langCfg.save(languageFile);
    }
  }

}
