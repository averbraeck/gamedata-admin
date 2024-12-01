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
            <nav id="sidebarMenu" class="collapse d-lg-block sidebar collapse bg-white">
              <div class="position-sticky">
                <div class="list-group list-group-flush mx-3 mt-4">
                                """;

    /** Sidebar item with: 1. active/blank, 2. true/false, 3. onclick menu, 4. fa-icon, 5. text */
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
        s.append(sidebarItem.formatted("active", "true", "home", "fa-house", "Admin panel"));
        s.append(sidebarItem.formatted("", "false", "organization", "fa-sitemap", "Organization"));
        s.append(sidebarItem.formatted("", "false", "user", "fa-user", "User"));
        s.append(sidebarItem.formatted("", "false", "game", "fa-dice", "Game"));
        s.append(sidebarItem.formatted("", "false", "gamesession", "fa-chart-line", "Game Session"));
        s.append(sidebarBottom);
        return s.toString();
    }
}
