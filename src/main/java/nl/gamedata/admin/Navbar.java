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
                <a class="navbar-brand ps-3" href="#" onclick="clickMenu('menu-admin-panel')" style="width:240px;">
                  <h2>Game Data</h2>
                </a>

                <!-- Tabs -->
                <div class="gd-nav">
                      """;

    /** active tab with choice; 1=menu name, 2=choice text, 3=close action. */
    private static String tabChoiceActive = """
                  <div class="gd-tab">
                    <div class="gd-tab-item gd-tab-item-active">
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
                  </div>
            """;

    /** inactive tab with choice; 1=onclick menu, 2=menu name, 3=choice text, 4=close action. */
    private static String tabChoiceInactive = """
                  <div class="gd-tab">
                    <div class="gd-tab-item gd-tab-item-inactive">
                      <a href="#" onclick="clickMenu('%s')">%s</a>
                    </div>
                    <div class="gd-tab-choice">
                      <div class="gd-tab-choice-text">"%s</div>
                      <div class="gd-tab-choice-close">
                        <a href="#" onclick="clickMenu('%s')">
                          <i class="fas fa-xmark fa-fw"></i>
                        </a>
                      </div>
                    </div>
                  </div>
            """;

    /** active tab with choice; 1=menu name. */
    private static String tabChoiceActiveEmpty = """
                  <div class="gd-tab">
                    <div class="gd-tab-item gd-tab-item-active">
                      %s
                    </div>
                    <div class="gd-tab-choice">
                      <div class="gd-tab-choice-text">&nbsp;</div>
                    </div>
                  </div>
            """;

    /** inactive tab with choice; 1=onclick menu, 2=menu name. */
    private static String tabChoiceInactiveEmpty = """
                  <div class="gd-tab">
                    <div class="gd-tab-item gd-tab-item-inactive">
                      <a href="#" onclick="clickMenu('%s')">%s</a>
                    </div>
                    <div class="gd-tab-choice">
                      <div class="gd-tab-choice-text">&nbsp;</div>
                    </div>
                  </div>
            """;

    /** active tab without choice; 1=menu name. */
    private static String tabActive = """
                  <div class="gd-tab">
                    <div class="gd-tab-item gd-tab-item-active">
                      %s
                    </div>
                    <div class="gd-tab-nochoice">
                      <div class="gd-tab-choice-text">&nbsp;</div>
                    </div>
                  </div>
            """;

    /** inactive tab without choice; 1=onclick menu, 2=menu name. */
    private static String tabInactive = """
                  <div class="gd-tab">
                    <div class="gd-tab-item gd-tab-item-inactive">
                      <a href="#" onclick="clickMenu('%s')">%s</a>
                    </div>
                    <div class="gd-tab-nochoice">
                      <div class="gd-tab-choice-text">&nbsp;</div>
                    </div>
                  </div>
            """;

    private static String navbarEnd = """
                </div>
              </div>
              <!-- Container wrapper -->

            </nav>
            <!-- Navbar -->
                                            """;

    public static String makeNavbar(final AdminData data)
    {
        StringBuilder s = new StringBuilder();
        s.append(navbarStart);
        if (data.getMenuChoice().equals("menu-organization"))
        {
            tabChoice(s, data, "tab-organization#organization", "Organization");
            tabChoice(s, data, "tab-organization#user", "User");
            tab(s, data, "tab-organization#user-role", "User Role");
            tabChoice(s, data, "tab-organization#game", "Game");
            tab(s, data, "tab-organization#game-access", "Game Access");
            tab(s, data, "tab-organization#private-dashboard", "Dashboard");
            tab(s, data, "tab-organization#access-token", "Token");
            tab(s, data, "tab-organization#game-session", "Session");
        }
        else if (data.getMenuChoice().equals("menu-user"))
        {
            tabChoice(s, data, "tab-user#user", "User");
            tab(s, data, "tab-user#user-role", "User Role");
            tabChoice(s, data, "tab-user#game", "Game");
            tab(s, data, "tab-user#game-role", "Game Role");
        }
        else if (data.getMenuChoice().equals("menu-game"))
        {
            tabChoice(s, data, "tab-game#game", "Game");
            tabChoice(s, data, "tab-game#game-version", "Game Version");
            tabChoice(s, data, "tab-game#game-mission", "Game Mission");
            tab(s, data, "tab-game#public-dashboard", "Dashboard");
            tab(s, data, "tab-game#scale", "Scale");
            tabChoice(s, data, "tab-game#learning-goal", "Learning Goal");
            tab(s, data, "tab-game#player-objective", "Player Objective");
            tab(s, data, "tab-game#group-objective", "Group Objective");
        }
        else if (data.getMenuChoice().equals("menu-game-control"))
        {
            tabChoice(s, data, "tab-game-control#game", "Game");
            tab(s, data, "tab-game-control#game-access", "Game Access");
            tab(s, data, "tab-game-control#game-token", "Game Token");
        }
        else if (data.getMenuChoice().equals("menu-game-session"))
        {
            tabChoice(s, data, "tab-game-session#game", "Game");
            tabChoice(s, data, "tab-game-session#game-version", "Game Version");
            tabChoice(s, data, "tab-game-session#game-session", "Session");
        }
        else if (data.getMenuChoice().equals("menu-data-session"))
        {
            tabChoice(s, data, "tab-data-session#game", "Game");
            tabChoice(s, data, "tab-data-session#game-version", "Game Version");
            tabChoice(s, data, "tab-data-session#game-session", "Session");
            tabChoice(s, data, "tab-data-session#game-mission", "Game Mission");
            tab(s, data, "tab-data-session#mission-event", "Mission Event");
        }
        else if (data.getMenuChoice().equals("menu-data-player"))
        {
            tabChoice(s, data, "tab-data-player#game", "Game");
            tabChoice(s, data, "tab-data-player#game-version", "Game Version");
            tabChoice(s, data, "tab-data-player#game-session", "Session");
            tabChoice(s, data, "tab-data-player#player", "Player");
            tabChoice(s, data, "tab-data-player#player-attempt", "Player Attempt");
            tab(s, data, "tab-data-player#player-score", "Player Score");
            tab(s, data, "tab-data-player#player-event", "Player Event");
            tab(s, data, "tab-data-player#player-group-role", "Group Role");
        }
        else if (data.getMenuChoice().equals("menu-data-group"))
        {
            tabChoice(s, data, "tab-data-group#game", "Game");
            tabChoice(s, data, "tab-data-group#game-version", "Game Version");
            tabChoice(s, data, "tab-data-group#game-session", "Session");
            tabChoice(s, data, "tab-data-group#group", "Group");
            tab(s, data, "tab-data-group#group-player", "Players");
            tabChoice(s, data, "tab-data-group#group-attempt", "Group Attempt");
            tab(s, data, "tab-data-group#group-score", "Group Score");
            tab(s, data, "tab-data-group#group-event", "Group Event");
        }
        s.append(navbarEnd);
        return s.toString();
    }

    private static void tab(final StringBuilder s, final AdminData data, final String tabName, final String tabText)
    {
        if (data.getMenuChoice().equals(tabName))
            s.append(tabActive.formatted(tabText));
        else
            s.append(tabInactive.formatted(tabName, tabText));
    }

    private static void tabChoice(final StringBuilder s, final AdminData data, final String tabName, final String tabText)
    {
        if (!data.getTabFilterChoices().containsKey(tabName))
        {
            if (data.getMenuChoice().equals(tabName))
                s.append(tabChoiceActiveEmpty.formatted(tabText));
            else
                s.append(tabChoiceInactiveEmpty.formatted(tabName, tabText));
        }
        else
        {
            String choice = data.getTabFilterChoices().get(tabName).name();
            if (choice.length() > 12)
                choice = choice.substring(0, 9) + "...";
            if (data.getMenuChoice().equals(tabName))
                s.append(tabChoiceActive.formatted(tabText, choice, tabName + "-close"));
            else
                s.append(tabChoiceInactive.formatted(tabName, tabText, choice, tabName + "-close"));
        }
    }

}
