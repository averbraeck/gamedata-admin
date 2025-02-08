package nl.gamedata.admin;

import nl.gamedata.admin.Menus.Menu;

/**
 * Sidebar.java.
 * <p>
 * Copyright (c) 2024-2024 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="https://github.com/averbraeck/gamedata-admin/LICENSE">GameData project License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public class Sidebar
{
    /** top of the sidebar. */
    private static String sidebarTop = """
            <!-- Sidebar -->
            <div id="sidebarMenu" class="gd-sidebar">
                          """;

    /** Sidebar group item with: 1. text. */
    private static String sidebarGroup = """
            <div class="gd-sidebar-menu-group">%s</div>
                          """;

    /** Sidebar item with: 1. active/blank, 2. true/false, 3. onclick menu, 4. fa-icon, 5. text. */
    private static String sidebarItem = """
            <a href="#" class="gd-sidebar-menu-item %s" aria-current="%s"
                onclick="clickMenu('%s')">
              <i class="fas %s fa-fw"></i><span>%s</span>
            </a>
                          """;

    /** bottom of the sidebar. */
    private static String sidebarBottom = """
            </div>
            <!-- Sidebar -->
                          """;

    public static String makeSidebar(final AdminData data)
    {
        StringBuilder s = new StringBuilder();
        s.append(sidebarTop);
        for (String menuName : Menus.menuList)
        {
            Menu menu = Menus.menuMap.get(menuName);
            if (menu.header())
            {
                if (Menus.showMenu(data, menu.menuChoice()))
                    s.append(sidebarGroup.formatted(menu.menuText()));
            }
            else
                item(s, data, menu.icon(), menu.menuChoice(), menu.menuText());
        }
        s.append(sidebarBottom);
        return s.toString();
    }

    private static void item(final StringBuilder s, final AdminData data, final String faIcon, final String menuChoice,
            final String menuText)
    {
        if (Menus.showMenu(data, menuChoice))
        {
            if (data.getMenuChoice().equals(menuChoice))
                s.append(sidebarItem.formatted("gd-active", "true", "menu-" + menuChoice, faIcon, menuText));
            else
                s.append(sidebarItem.formatted("", "false", "menu-" + menuChoice, faIcon, menuText));
        }
    }
}
