package nl.gamedata.admin;

import java.util.List;

import nl.gamedata.admin.Menus.Tab;

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
                      <div class="gd-tab-choice-text">%s</div>
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
        String menuChoice = data.getMenuChoice();
        List<Tab> tabList = Menus.menuMap.get(menuChoice).tabs();
        for (Tab tab : tabList)
        {
            if (Menus.showTab(data, menuChoice, data.getTabChoice(menuChoice)))
            {
                if (tab.selectField() != null)
                    tabChoice(s, data, tab.tabChoice(), tab.tabText());
                else
                    tab(s, data, tab.tabChoice(), tab.tabText());
            }
        }
        s.append(navbarEnd);
        return s.toString();
    }

    private static void tab(final StringBuilder s, final AdminData data, final String tabName, final String tabText)
    {
        if (tabName.equals(data.getTabChoice(data.getMenuChoice())))
            s.append(tabActive.formatted(tabText));
        else
            s.append(tabInactive.formatted("tab-" + tabName, tabText));
    }

    private static void tabChoice(final StringBuilder s, final AdminData data, final String tabName, final String tabText)
    {
        if (data.getTabFilterChoice(tabName) == null)
        {
            if (tabName.equals(data.getTabChoice(data.getMenuChoice())))
                s.append(tabChoiceActiveEmpty.formatted(tabText));
            else
                s.append(tabChoiceInactiveEmpty.formatted("tab-" + tabName, tabText));
        }
        else
        {
            String choice = data.getTabFilterChoice(tabName).name();
            if (choice.length() > 12)
                choice = choice.substring(0, 9) + "...";
            if (tabName.equals(data.getTabChoice(data.getMenuChoice())))
                s.append(tabChoiceActive.formatted(tabText, choice, "close-" + tabName));
            else
                s.append(tabChoiceInactive.formatted("tab-" + tabName, tabText, choice, "close-" + tabName));
        }
    }
}
