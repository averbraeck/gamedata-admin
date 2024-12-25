package nl.gamedata.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import nl.gamedata.admin.table.IEdit;
import nl.gamedata.admin.table.ITable;
import nl.gamedata.admin.table.MaintainDashboardRole;
import nl.gamedata.admin.table.MaintainGame;
import nl.gamedata.admin.table.MaintainGameRole;
import nl.gamedata.admin.table.MaintainGameSessionRole;
import nl.gamedata.admin.table.MaintainGameVersion;
import nl.gamedata.admin.table.MaintainOrganization;
import nl.gamedata.admin.table.MaintainOrganizationGame;
import nl.gamedata.admin.table.MaintainOrganizationGameRole;
import nl.gamedata.admin.table.MaintainOrganizationRole;
import nl.gamedata.admin.table.MaintainUser;

/**
 * AdminMenus.java.
 * <p>
 * Copyright (c) 2024-2024 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="https://github.com/averbraeck/gamedata-admin/LICENSE">GameData project License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public class Menus
{
    public static final Map<String, Menu> menuMap = new HashMap<>();

    public static final List<String> menuList = new ArrayList<>();

    static
    {
        menuList.add("admin-panel");
        menuMap.put("admin-panel",
                new Menu("fa-house", "admin-panel", "Admin panel", new ArrayList<>(), Set.of(0, 1, 2, 3, 4, 5)));

        menuList.add("organization");
        List<Tab> organizationTabs = new ArrayList<>();
        menuMap.put("organization", new Menu("fa-sitemap", "organization", "Organization", organizationTabs, Set.of(0, 2)));
        organizationTabs.add(new Tab("organization", "Organization", true, Set.of(0, 2), MaintainOrganization::table,
                MaintainOrganization::edit));
        organizationTabs.add(new Tab("user", "User", true, Set.of(0, 2), MaintainUser::table, MaintainUser::edit));
        organizationTabs.add(new Tab("user-role", "User Role", false, Set.of(0, 2), MaintainOrganizationRole::table,
                MaintainOrganizationRole::edit));
        organizationTabs.add(new Tab("game", "Game", true, Set.of(0, 2), MaintainGame::table, MaintainGame::edit));
        organizationTabs.add(new Tab("organization-game", "Game Access", false, Set.of(0, 2), MaintainOrganizationGame::table,
                MaintainOrganizationGame::edit));
        // organizationTabs.add(new Tab("org-game-token", "Access Token", false, Set.of(0, 2)));
        // organizationTabs.add(new Tab("game-session", "Game Session", false, Set.of(0, 2)));

        menuList.add("user");
        List<Tab> userTabs = new ArrayList<>();
        menuMap.put("user", new Menu("fa-user", "user", "User", userTabs, Set.of(0, 1, 2)));
        userTabs.add(new Tab("user", "User", true, Set.of(0, 1, 2), MaintainUser::table, MaintainUser::edit));
        userTabs.add(new Tab("game", "Game", true, Set.of(0, 1), MaintainGame::table, MaintainGame::edit));
        userTabs.add(new Tab("organization-role", "Organization Role", false, Set.of(0, 2), MaintainOrganizationRole::table,
                MaintainOrganizationRole::edit));
        userTabs.add(new Tab("game-role", "Game Role", false, Set.of(0, 1), MaintainGameRole::table, MaintainGameRole::edit));
        userTabs.add(new Tab("org-game-role", "Org-Game Role", false, Set.of(0, 2), MaintainOrganizationGameRole::table,
                MaintainOrganizationGameRole::edit));
        userTabs.add(new Tab("game-session-role", "Game Session Role", false, Set.of(0, 2), MaintainGameSessionRole::table,
                MaintainGameSessionRole::edit));
        userTabs.add(new Tab("dashboard-role", "Dashboard Role", false, Set.of(0, 1, 2), MaintainDashboardRole::table,
                MaintainDashboardRole::edit));

        menuList.add("game");
        List<Tab> gameTabs = new ArrayList<>();
        menuMap.put("game", new Menu("fa-dice", "game", "Game", gameTabs, Set.of(0, 1)));
        gameTabs.add(new Tab("game", "Game", true, Set.of(0, 1), MaintainGame::table, MaintainGame::edit));
        gameTabs.add(new Tab("game-version", "Game Version", true, Set.of(0, 1), MaintainGameVersion::table,
                MaintainGameVersion::edit));
        // gameTabs.add(new Tab("game-mission", "Game Mission", true, Set.of(0, 1)));
        // gameTabs.add(new Tab("scale", "Scale", false, Set.of(0, 1)));
        // gameTabs.add(new Tab("learning-goal", "Learning Goal", true, Set.of(0, 1)));
        // gameTabs.add(new Tab("player-objective", "Player Objective", false, Set.of(0, 1)));
        // gameTabs.add(new Tab("group-objective", "Group Objective", false, Set.of(0, 1)));

        menuList.add("game-control");
        List<Tab> gameControlTabs = new ArrayList<>();
        menuMap.put("game-control",
                new Menu("fa-square-binary", "game-control", "Game Control", gameControlTabs, Set.of(0, 3)));
        gameControlTabs.add(new Tab("game", "Game", true, Set.of(0, 3), MaintainGame::table, MaintainGame::edit));
        gameControlTabs.add(new Tab("organization", "Organization", true, Set.of(0, 3), MaintainOrganization::table,
                MaintainOrganization::edit));
        gameControlTabs.add(new Tab("organization-game", "Game Access", true, Set.of(0, 3), MaintainOrganizationGame::table,
                MaintainOrganizationGame::edit));
        // gameControlTabs.add(new Tab("org-game-token", "Access Token", false, Set.of(0, 3)));

        menuList.add("game-session");
        List<Tab> gameSessionTabs = new ArrayList<>();
        menuMap.put("game-session",
                new Menu("fa-calendar-check", "game-session", "Game Session", gameSessionTabs, Set.of(0, 4)));
        gameSessionTabs.add(new Tab("game", "Game", true, Set.of(0, 4), MaintainGame::table, MaintainGame::edit));
        gameSessionTabs.add(new Tab("game-version", "Game Version", true, Set.of(0, 4), MaintainGameVersion::table,
                MaintainGameVersion::edit));
        // gameSessionTabs.add(new Tab("game-session", "Game Session", true, Set.of(0, 4)));
        // gameSessionTabs.add(new Tab("session-dashboard", "Session Dashboard", false, Set.of(0, 4)));

        menuList.add("layout");
        List<Tab> layoutTabs = new ArrayList<>();
        menuMap.put("layout", new Menu("fa-display", "layout", "Layout", layoutTabs, Set.of(0)));
        // layoutTabs.add(new Tab("dashboard-layout", "Dashboard Layout", true, Set.of(0)));
        // layoutTabs.add(new Tab("dashboard-element", "Dashboard Element", true, Set.of(0)));
        // layoutTabs.add(new Tab("element-property", "Element Property", true, Set.of(0)));

        menuList.add("dashboard");
        List<Tab> dashboardTabs = new ArrayList<>();
        menuMap.put("dashboard", new Menu("fa-table-cells-large", "dashboard", "Dashboard", dashboardTabs, Set.of(0, 5)));
        dashboardTabs.add(new Tab("game", "Game", true, Set.of(0, 5), MaintainGame::table, MaintainGame::edit));
        dashboardTabs.add(new Tab("game-version", "Game Version", true, Set.of(0, 5), MaintainGameVersion::table,
                MaintainGameVersion::edit));
        // dashboardTabs.add(new Tab("dashboard-template", "Dashboard Template", true, Set.of(0, 5)));
        // dashboardTabs.add(new Tab("template-element", "Template Element", true, Set.of(0, 5)));
        // dashboardTabs.add(new Tab("property-value", "Property Value", false, Set.of(0, 5)));
        // dashboardTabs.add(new Tab("dashboard", "Dashboard", true, Set.of(0, 5)));
        // dashboardTabs.add(new Tab("session-dashboard", "Session Dashboard", false, Set.of(0, 5)));
        // dashboardTabs.add(new Tab("dashboard-token", "Dashboard Token", false, Set.of(0, 5)));

        menuList.add("data-session");
        List<Tab> dataSessionTabs = new ArrayList<>();
        menuMap.put("data-session", new Menu("fa-chart-pie", "data-session", "Data Session", dataSessionTabs, Set.of(0, 4)));
        dataSessionTabs.add(new Tab("game", "Game", true, Set.of(0, 4), MaintainGame::table, MaintainGame::edit));
        dataSessionTabs.add(new Tab("game-version", "Game Version", true, Set.of(0, 4), MaintainGameVersion::table,
                MaintainGameVersion::edit));
        // dataSessionTabs.add(new Tab("game-session", "Game Session", true, Set.of(0, 4)));
        // dataSessionTabs.add(new Tab("game-mission", "Game Mission", true, Set.of(0, 4)));
        // dataSessionTabs.add(new Tab("mission-event", "Mission Event", false, Set.of(0, 4)));

        menuList.add("data-player");
        List<Tab> dataPlayerTabs = new ArrayList<>();
        menuMap.put("data-player", new Menu("fa-chart-line", "data-player", "Data Player", dataPlayerTabs, Set.of(0, 4)));
        dataPlayerTabs.add(new Tab("game", "Game", true, Set.of(0, 4), MaintainGame::table, MaintainGame::edit));
        dataPlayerTabs.add(new Tab("game-version", "Game Version", true, Set.of(0, 4), MaintainGameVersion::table,
                MaintainGameVersion::edit));
        // dataPlayerTabs.add(new Tab("game-session", "Game Session", true, Set.of(0, 4)));
        // dataPlayerTabs.add(new Tab("player", "Player", true, Set.of(0, 4)));
        // dataPlayerTabs.add(new Tab("player-attempt", "Player Attempt", true, Set.of(0, 4)));
        // dataPlayerTabs.add(new Tab("player-score", "Player Score", false, Set.of(0, 4)));
        // dataPlayerTabs.add(new Tab("player-event", "Player Event", false, Set.of(0, 4)));
        // dataPlayerTabs.add(new Tab("player-group-role", "Group Role", false, Set.of(0, 4)));

        menuList.add("data-group");
        List<Tab> dataGroupTabs = new ArrayList<>();
        menuMap.put("data-group", new Menu("fa-chart-simple", "data-group", "Data Group", dataGroupTabs, Set.of(0, 4)));
        dataGroupTabs.add(new Tab("game", "Game", true, Set.of(0, 4), MaintainGame::table, MaintainGame::edit));
        dataGroupTabs.add(new Tab("game-version", "Game Version", true, Set.of(0, 4), MaintainGameVersion::table,
                MaintainGameVersion::edit));
        // dataGroupTabs.add(new Tab("game-session", "Game Session", true, Set.of(0, 4)));
        // dataGroupTabs.add(new Tab("group", "Group", true, Set.of(0, 4)));
        // dataGroupTabs.add(new Tab("group-player", "Group Player", false, Set.of(0, 4)));
        // dataGroupTabs.add(new Tab("group-attempt", "Group Attempt", true, Set.of(0, 4)));
        // dataGroupTabs.add(new Tab("group-score", "Group Score", false, Set.of(0, 4)));
        // dataGroupTabs.add(new Tab("group-event", "Group Event", false, Set.of(0, 4)));

        menuList.add("settings");
        menuMap.put("settings", new Menu("fa-user-gear", "settings", "Settings", new ArrayList<>(), Set.of(0, 1, 2, 3, 4, 5)));

        menuList.add("logoff");
        menuMap.put("logoff", new Menu("fa-sign-out", "logoff", "Logoff", new ArrayList<>(), Set.of(0, 1, 2, 3, 4, 5)));
    }

    /**
     * 0 = super admin<br>
     * 1 = game role (including game admin)<br>
     * 2 = organization role <br>
     * 3 = organization game role <br>
     * 4 = game session role <br>
     * 5 = dashboard role <br>
     * @param data game data information
     * @return set of roles for this user
     */
    public static Set<Integer> getRoles(final AdminData data)
    {
        Set<Integer> roles = new HashSet<>();
        if (data.isSuperAdmin())
            roles.add(0);
        if (data.getGameRoles().size() > 0 || data.isGameAdmin())
            roles.add(1);
        if (data.getOrganizationAccess().size() > 0)
            roles.add(2);
        if (data.getOrganizationGameRoles().size() > 0)
            roles.add(3);
        if (data.getGameSessionRoles().size() > 0)
            roles.add(4);
        if (data.getDashboardRoles().size() > 0)
            roles.add(5);
        return roles;
    }

    public static boolean showMenu(final AdminData data, final String menuChoice)
    {
        Set<Integer> roles = getRoles(data);
        Set<Integer> access = menuMap.get(menuChoice).access();
        roles.retainAll(access);
        return !roles.isEmpty();
    }

    public static Tab getTab(final String menuChoice, final String tabChoice)
    {
        List<Tab> tabList = menuMap.get(menuChoice).tabs;
        for (Tab tab : tabList)
        {
            if (tab.tabChoice.equals(tabChoice))
                return tab;
        }
        return null;
    }

    public static boolean showTab(final AdminData data, final String menuChoice, final String tabChoice)
    {
        Set<Integer> roles = getRoles(data);
        Set<Integer> access = getTab(menuChoice, tabChoice).access();
        roles.retainAll(access);
        return !roles.isEmpty();
    }

    public static void table(final AdminData data, final HttpServletRequest request, final String click)
    {
        String menuChoice = data.getMenuChoice();
        Tab tab = getTab(menuChoice, data.getTabChoice(menuChoice));
        tab.tableRef.table(data, request, menuChoice);
    }

    public static void edit(final AdminData data, final HttpServletRequest request, final String click, final int recordId)
    {
        String menuChoice = data.getMenuChoice();
        Tab tab = getTab(menuChoice, data.getTabChoice(menuChoice));
        tab.editRef.edit(data, request, click, recordId);
    }

    public static void initializeTabChoices(final AdminData data)
    {
        data.putTabChoice("admin-panel", "");
        data.putTabChoice("organization", "organization");
        data.putTabChoice("user", "user");
        data.putTabChoice("game", "game");
        data.putTabChoice("game-control", "game");
        data.putTabChoice("game-session", "game");
        data.putTabChoice("layout", "dashboard-layout");
        data.putTabChoice("dashboard", "dashboard-template");
        data.putTabChoice("data-session", "game");
        data.putTabChoice("data-player", "game");
        data.putTabChoice("data-group", "game");
        data.putTabChoice("settings", "");

    }

    public static record Menu(String icon, String menuChoice, String menuText, List<Tab> tabs, Set<Integer> access)
    {
    }

    public static record Tab(String tabChoice, String tabText, boolean select, Set<Integer> access, ITable tableRef,
            IEdit editRef)
    {
    }
}
