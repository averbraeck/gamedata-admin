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
        if (data.getMenuChoice().equals("organization"))
        {
            tabChoice(s, data, "organization", "Organization", "TUD");
            tabChoice(s, data, "user", "User", "");
            tab(s, data, "user-role", "User Role");
            tabChoice(s, data, "game", "Game", "");
            tab(s, data, "game-access", "Game Access");
            tab(s, data, "private-dashboard", "Dashboard");
            tab(s, data, "access-token", "Token");
            tab(s, data, "game-session", "Session");
        }
        else if (data.getMenuChoice().equals("user"))
        {
            tabChoice(s, data, "user", "User", "");
            tab(s, data, "user-role", "User Role");
            tabChoice(s, data, "game", "Game", "");
            tab(s, data, "game-role", "Game Role");
        }
        else if (data.getMenuChoice().equals("game"))
        {
            tabChoice(s, data, "game", "Game", "");
            tabChoice(s, data, "game-version", "Game Version", "");
            tabChoice(s, data, "game-mission", "Game Mission", "");
            tab(s, data, "public-dashboard", "Dashboard");
            tab(s, data, "scale", "Scale");
            tabChoice(s, data, "learning-goal", "Learning Goal", "");
            tab(s, data, "player-objective", "Player Objective");
            tab(s, data, "group-objective", "Group Objective");
        }
        else if (data.getMenuChoice().equals("game-control"))
        {
            tabChoice(s, data, "game", "Game", "");
            tab(s, data, "game-access", "Game Access");
            tab(s, data, "game-token", "Game Token");
        }
        else if (data.getMenuChoice().equals("game-session"))
        {
            tabChoice(s, data, "game", "Game", "");
            tabChoice(s, data, "game-version", "Game Version", "");
            tabChoice(s, data, "game-session", "Session", "");
        }
        else if (data.getMenuChoice().equals("data-session"))
        {
            tabChoice(s, data, "game", "Game", "");
            tabChoice(s, data, "game-version", "Game Version", "");
            tabChoice(s, data, "game-session", "Session", "");
            tabChoice(s, data, "game-mission", "Game Mission", "");
            tab(s, data, "mission-event", "Mission Event");
        }
        else if (data.getMenuChoice().equals("data-player"))
        {
            tabChoice(s, data, "game", "Game", "");
            tabChoice(s, data, "game-version", "Game Version", "");
            tabChoice(s, data, "game-session", "Session", "");
            tabChoice(s, data, "player", "Player", "");
            tabChoice(s, data, "player-attempt", "Player Attempt", "");
            tab(s, data, "player-score", "Player Score");
            tab(s, data, "player-event", "Player Event");
            tab(s, data, "player-group-role", "Group Role");
        }
        else if (data.getMenuChoice().equals("data-group"))
        {
            tabChoice(s, data, "game", "Game", "");
            tabChoice(s, data, "game-version", "Game Version", "");
            tabChoice(s, data, "game-session", "Session", "");
            tabChoice(s, data, "group", "Group", "");
            tab(s, data, "group-player", "Players");
            tabChoice(s, data, "group-attempt", "Group Attempt", "");
            tab(s, data, "group-score", "Group Score");
            tab(s, data, "group-event", "Group Event");
        }
        s.append(navbarEnd);
        return s.toString();
    }

    private static void tab(final StringBuilder s, final AdminData data, final String tabChoice, final String tabText)
    {
        if (data.getMenuChoice().equals(tabChoice))
            s.append(tabActive.formatted(tabText));
        else
            s.append(tabInactive.formatted(tabChoice, tabText));
    }

    private static void tabChoice(final StringBuilder s, final AdminData data, final String tabChoice, final String tabText,
            final String choice)
    {
        if (choice.length() == 0)
        {
            if (data.getMenuChoice().equals(tabChoice))
                s.append(tabChoiceActiveEmpty.formatted(tabText));
            else
                s.append(tabChoiceInactiveEmpty.formatted(tabChoice, tabText));
        }
        else
        {
            if (data.getMenuChoice().equals(tabChoice))
                s.append(tabChoiceActive.formatted(tabText, choice, tabChoice + "-close"));
            else
                s.append(tabChoiceInactive.formatted(tabChoice, tabText, choice, tabChoice + "-close"));
        }
    }

}
