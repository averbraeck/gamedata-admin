package nl.gamedata.admin;

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
            <nav id="sidebarMenu" class="d-lg-block sidebar collapse bg-white">
              <div class="position-sticky">
                <div class="list-group list-group-flush mx-3 mt-4">
                                """;

    /** User item with: text. */
    private static String sidebarUser = """
            <a href="#" class="list-group-item py-2">
              <i class="fas fa-circle-user fa-fw me-3"></i><span>%s</span>
            </a>
            <a href="#" class="list-group-item py-2"></a>
                          """;

    /** Sidebar item with: 1. active/blank, 2. true/false, 3. onclick menu, 4. fa-icon, 5. text. */
    private static String sidebarItem = """
            <a href="#" class="list-group-item list-group-item-action py-2 ripple %s" aria-current="%s"
                onclick="clickMenu('%s')">
              <i class="fas %s fa-fw me-3"></i><span>%s</span>
            </a>
                          """;

    /** bottom of the sidebar. */
    private static String sidebarBottom = """
                </div>
              </div>
            </nav>
            <!-- Sidebar -->
                                """;

    public static String makeSidebar(final AdminData data)
    {
        StringBuilder s = new StringBuilder();
        s.append(sidebarTop);
        s.append(sidebarUser.formatted(data.getUsername()));
        item(s, data, "fa-house", "admin-panel", "Admin panel");
        item(s, data, "fa-sitemap", "organization", "Organization");
        item(s, data, "fa-user", "user", "User");
        item(s, data, "fa-dice", "game", "Game");
        item(s, data, "fa-square-binary", "game-control", "Game Control");
        item(s, data, "fa-calendar-check", "game-session", "Game Session");
        item(s, data, "fa-chart-pie", "data-session", "Data Session");
        item(s, data, "fa-chart-line", "data-player", "Data Player");
        item(s, data, "fa-chart-simple", "data-group", "Data Group");
        item(s, data, "fa-user-gear", "settings", "Settings");
        item(s, data, "fa-sign-out", "logoff", "Logoff");
        s.append(sidebarBottom);
        return s.toString();
    }

    private static void item(final StringBuilder s, final AdminData data, final String faIcon, final String menuChoice,
            final String menuText)
    {
        if (data.getMenuChoice().equals(menuChoice))
            s.append(sidebarItem.formatted("active", "true", "menu-" + menuChoice, faIcon, menuText));
        else
            s.append(sidebarItem.formatted("", "false", "menu-" + menuChoice, faIcon, menuText));
    }
}
