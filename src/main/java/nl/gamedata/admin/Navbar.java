package nl.gamedata.admin;

/**
 * Navbar.java.
 * <p>
 * Copyright (c) 2024-2024 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="https://github.com/averbraeck/gamedata-admin/LICENSE">GameData project License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public class Navbar
{
    private static String navbarStart = """
            <!-- Navbar -->
            <nav id="main-navbar" class="navbar navbar-expand-lg navbar-light bg-white fixed-top">

              <!-- Container wrapper -->
              <div class="container-fluid d-flex flex-row" style="justify-content: flex-start">

                <!-- Hamburger toggle button -->
                <button data-mdb-button-init class="navbar-toggler" type="button"
                  data-mdb-collapse-init data-mdb-target="#sidebarMenu"
                  aria-controls="sidebarMenu" aria-expanded="false" aria-label="Toggle navigation">
                  <i class="fas fa-bars"></i>
                </button>

                <!-- Brand -->
                <a class="navbar-brand ps-3" href="#" onclick="clickMenu('home')" style="width:240px;">
                  <h2>Game Data</h2>
                </a>

                <!-- Tabs -->
                <ul class="nav nav-tabs">
                      """;

    /** Inactive clickable tab; 1=onclick menu, 2=text. */
    private static String tabInactive = """
                  <li class="nav-item">
                    <a class="nav-link" href="#" onclick="clickMenu('%s')">%s</a>
                  </li>
            """;

    /** Active clickable tab; 1=onclick menu, 2=text. */
    private static String tabActive = """
                  <li class="nav-item">
                    <a class="nav-link active" aria-current="page" href="#" onclick="clickMenu('%s')">%s</a>
                  </li>
            """;

    /** tab; 1=text. */
    private static String tabDisabled = """
                  <li class="nav-item">
                    <a class="nav-link disabled" href="#" tabindex="-1" aria-disabled="true">%s</a>
                  </li>
            """;

    private static String navbarEnd = """
                </ul>
              </div>
              <!-- Container wrapper -->

            </nav>
            <!-- Navbar -->
                                            """;

    public static String makeNavbar(final AdminData data)
    {
        StringBuilder s = new StringBuilder();
        s.append(navbarStart);
        if (data.getMenuChoice().equals("user"))
        {
            tab(s, data, "user", "User");
            tab(s, data, "user-role", "User Role");
            tab(s, data, "game-role", "Game Role");
        }
        else if (data.getMenuChoice().equals("organization"))
        {
            tab(s, data, "organization", "Organization");
            tab(s, data, "org-user", "Org Users");
            tab(s, data, "game-access", "Game Access");
            tab(s, data, "private-dashboard", "Dashboard");
            tab(s, data, "access-token", "Token");
            tab(s, data, "game-session", "Session");
        }
        s.append(navbarEnd);
        return s.toString();
    }

    private static void tab(final StringBuilder s, final AdminData data, final String tabChoice,
            final String tabText)
    {
        if (data.getMenuChoice().equals(tabChoice))
            s.append(tabActive.formatted(tabChoice, tabText));
        else
            s.append(tabInactive.formatted(tabChoice, tabText));
    }
}
