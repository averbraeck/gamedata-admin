package nl.gamedata.admin;

import java.util.List;

import nl.gamedata.admin.Menus.Filter;
import nl.gamedata.admin.Menus.SubMenu;

/**
 * SubMenubar.java.
 * <p>
 * Copyright (c) 2024-2024 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="https://github.com/averbraeck/gamedata-admin/LICENSE">GameData project License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public class SubMenubar
{
    private static String subMenuBarStart = """
            <!-- SubMenubar -->
            <div id="main-submenubar" class="gd-submenubar">
                      """;

    /** SubMenubar item with: 1. active/blank, 2. true/false, 3. onclick menu, 4. fa-icon, 5. text. */
    private static String subMenuBarItem = """
              <a href="#" class="gd-submenubar-menu-item %s" aria-current="%s"
                  onclick="clickMenu('%s')">
                <i class="fa-regular %s fa-fw"></i><span>%s</span>
              </a>
            """;

    /** SubMenubar group item with: 1. text. */
    private static String subMenuBarGroup = """
              <div class="gd-submenubar-menu-group">%s</div>
            """;

    /** SubMenubar filter with: 1. field-text. */
    private static String subMenuBarFilterEmpty = """
              <div class="gd-submenubar-filter-text">
                 %s
              </div>
              <div class="gd-tab-choice">
                <div class="gd-tab-choice-text">&nbsp;</div>
              </div>
            """;

    /** SubMenubar filter with: 1. field-text, 2 = filter-text, 3=close action. */
    private static String subMenuBarFilter = """
              <div class="gd-submenubar-filter-text">
                 %s
              </div>
              <div class="gd-tab-choice">
                <div class="gd-tab-choice-text">%s</div>
                <div class="gd-tab-choice-close">
                  <a href="#" onclick="clickMenu('%s')">
                    <i class="fas fa-xmark fa-fw"></i>
                  </a>
                </div>
              </div>
            """;

    private static String submenubarEnd = """
            </div>
            <!-- SubMenubar -->
            """;

    public static String makeSubMenubar(final AdminData data)
    {
        StringBuilder s = new StringBuilder();
        s.append(subMenuBarStart);
        String menuChoice = data.getMenuChoice();
        List<SubMenu> subMenuList = Menus.menuMap.get(menuChoice).subMenus();
        s.append(subMenuBarGroup.formatted("TABLES"));
        for (SubMenu subMenu : subMenuList)
        {
            if (Menus.showSubMenu(data, menuChoice, data.getSubMenuChoice(menuChoice)))
                showSubMenu(s, data, subMenu.subMenuChoice(), subMenu.subMenuText());
        }
        s.append(subMenuBarGroup.formatted(""));
        s.append(subMenuBarGroup.formatted("FILTERS"));
        for (Filter filter : Menus.menuMap.get(menuChoice).filters())
        {
            showFilter(s, data, filter);
        }
        s.append(submenubarEnd);
        return s.toString();
    }

    private static void showSubMenu(final StringBuilder s, final AdminData data, final String tabName, final String tabText)
    {
        if (tabName.equals(data.getSubMenuChoice(data.getMenuChoice())))
            s.append(subMenuBarItem.formatted("gd-active", "true", "tab-" + tabName, "fa-circle", tabText));
        else
            s.append(subMenuBarItem.formatted("", "false", "tab-" + tabName, "fa-circle", tabText));
    }

    private static void showFilter(final StringBuilder s, final AdminData data, final Filter filter)
    {
        if (data.getMenuFilterChoice(filter.tableName()) == null)
        {
            s.append(subMenuBarFilterEmpty.formatted(filter.tableName()));
        }
        else
        {
            String choice = data.getMenuFilterChoice(filter.tableName()).name();
            if (choice.length() > 12)
                choice = choice.substring(0, 9) + "...";
            s.append(subMenuBarFilter.formatted(filter.tableName(), choice, "close-" + filter.tableName()));
        }
    }
}
