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
import nl.gamedata.admin.table.TableDashboardElement;
import nl.gamedata.admin.table.TableDashboardLayout;
import nl.gamedata.admin.table.TableDashboardRole;
import nl.gamedata.admin.table.TableElementProperty;
import nl.gamedata.admin.table.TableErrors;
import nl.gamedata.admin.table.TableGame;
import nl.gamedata.admin.table.TableGameMission;
import nl.gamedata.admin.table.TableGameRole;
import nl.gamedata.admin.table.TableGameSession;
import nl.gamedata.admin.table.TableGameSessionRole;
import nl.gamedata.admin.table.TableGameVersion;
import nl.gamedata.admin.table.TableGroup;
import nl.gamedata.admin.table.TableGroupAttempt;
import nl.gamedata.admin.table.TableGroupEvent;
import nl.gamedata.admin.table.TableGroupObjective;
import nl.gamedata.admin.table.TableGroupRole;
import nl.gamedata.admin.table.TableGroupScore;
import nl.gamedata.admin.table.TableLearningGoal;
import nl.gamedata.admin.table.TableMissionEvent;
import nl.gamedata.admin.table.TableOrganization;
import nl.gamedata.admin.table.TableOrganizationGame;
import nl.gamedata.admin.table.TableOrganizationGameRole;
import nl.gamedata.admin.table.TableOrganizationGameToken;
import nl.gamedata.admin.table.TableOrganizationRole;
import nl.gamedata.admin.table.TablePlayer;
import nl.gamedata.admin.table.TablePlayerAttempt;
import nl.gamedata.admin.table.TablePlayerEvent;
import nl.gamedata.admin.table.TablePlayerObjective;
import nl.gamedata.admin.table.TablePlayerScore;
import nl.gamedata.admin.table.TableScale;
import nl.gamedata.admin.table.TableUser;

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
        menuMap.put("ADMIN",
                new Menu("", "", "ADMIN", new ArrayList<>(), Set.of(0, 1, 2, 3, 4, 5, 6)));

        menuList.add("home");
        menuMap.put("home",
                new Menu("fa-house", "home", "home", new ArrayList<>(), Set.of(0, 1, 2, 3, 4, 5, 6)));

        menuList.add("organization");
        List<Tab> organizationTabs = new ArrayList<>();
        menuMap.put("organization", new Menu("fa-sitemap", "organization", "organizations", organizationTabs, Set.of(0, 2)));
        organizationTabs.add(new Tab("organization", "Organization", "organization", "code", Set.of(0, 2),
                TableOrganization::table, TableOrganization::edit));
        organizationTabs.add(new Tab("user", "User", "user", "name", Set.of(0, 2), TableUser::table, TableUser::edit));
        organizationTabs.add(new Tab("user-role", "User Role", "organization_role", null, Set.of(0, 2),
                TableOrganizationRole::table, TableOrganizationRole::edit));
        organizationTabs.add(new Tab("game", "Game", "game", "code", Set.of(0, 2), TableGame::table, TableGame::edit));
        organizationTabs.add(new Tab("organization-game", "Game Access", "organization_game", null, Set.of(0, 2),
                TableOrganizationGame::table, TableOrganizationGame::edit));
        organizationTabs.add(new Tab("org-game-token", "Access Token", "organization_game_token", null, Set.of(0, 2),
                TableOrganizationGameToken::table, TableOrganizationGameToken::edit));
        organizationTabs.add(new Tab("game-session", "Game Session", "game_session", null, Set.of(0, 2),
                TableGameSession::table, TableGameSession::edit));

        menuList.add("user");
        List<Tab> userTabs = new ArrayList<>();
        menuMap.put("user", new Menu("fa-user", "user", "users", userTabs, Set.of(0, 1, 2)));
        userTabs.add(new Tab("user", "User", "user", "name", Set.of(0, 1, 2), TableUser::table, TableUser::edit));
        userTabs.add(new Tab("game", "Game", "game", "code", Set.of(0, 1), TableGame::table, TableGame::edit));
        userTabs.add(new Tab("organization-role", "Organization Role", "organization_role", null, Set.of(0, 2),
                TableOrganizationRole::table, TableOrganizationRole::edit));
        userTabs.add(
                new Tab("game-role", "Game Role", "game_role", null, Set.of(0, 1), TableGameRole::table, TableGameRole::edit));
        userTabs.add(new Tab("org-game-role", "Org-Game Role", "organization_game_role", null, Set.of(0, 2),
                TableOrganizationGameRole::table, TableOrganizationGameRole::edit));
        userTabs.add(new Tab("game-session-role", "Game Session Role", "game_session_role", null, Set.of(0, 2),
                TableGameSessionRole::table, TableGameSessionRole::edit));
        userTabs.add(new Tab("dashboard-role", "Dashboard Role", "dashboard_role", null, Set.of(0, 1, 2),
                TableDashboardRole::table, TableDashboardRole::edit));

        menuList.add("game");
        List<Tab> gameTabs = new ArrayList<>();
        menuMap.put("game", new Menu("fa-dice", "game", "games", gameTabs, Set.of(0, 1)));
        gameTabs.add(new Tab("game", "Game", "game", "code", Set.of(0, 1), TableGame::table, TableGame::edit));
        gameTabs.add(new Tab("game-version", "Game Version", "game_version", "code", Set.of(0, 1), TableGameVersion::table,
                TableGameVersion::edit));
        gameTabs.add(new Tab("game-mission", "Game Mission", "game_mission", "code", Set.of(0, 1), TableGameMission::table,
                TableGameMission::edit));
        gameTabs.add(new Tab("scale", "Scale", "scale", null, Set.of(0, 1), TableScale::table, TableScale::edit));
        gameTabs.add(new Tab("learning-goal", "Learning Goal", "learning_goal", "code", Set.of(0, 1), TableLearningGoal::table,
                TableLearningGoal::edit));
        gameTabs.add(new Tab("player-objective", "Player Objective", "player_objective", null, Set.of(0, 1),
                TablePlayerObjective::table, TablePlayerObjective::edit));
        gameTabs.add(new Tab("group-objective", "Group Objective", "group_objective", null, Set.of(0, 1),
                TableGroupObjective::table, TableGroupObjective::edit));

        menuList.add("game-control");
        List<Tab> gameControlTabs = new ArrayList<>();
        menuMap.put("game-control",
                new Menu("fa-square-binary", "game-control", "game access", gameControlTabs, Set.of(0, 3)));
        gameControlTabs.add(new Tab("game", "Game", "game", "code", Set.of(0, 3), TableGame::table, TableGame::edit));
        gameControlTabs.add(new Tab("organization", "Organization", "organization", "code", Set.of(0, 3),
                TableOrganization::table, TableOrganization::edit));
        gameControlTabs.add(new Tab("organization-game", "Game Access", "organization_game", null, Set.of(0, 3),
                TableOrganizationGame::table, TableOrganizationGame::edit));
        gameControlTabs.add(new Tab("org-game-token", "Access Token", "organization_game_token", null, Set.of(0, 3),
                TableOrganizationGameToken::table, TableOrganizationGameToken::edit));

        menuList.add("game-session");
        List<Tab> gameSessionTabs = new ArrayList<>();
        menuMap.put("game-session",
                new Menu("fa-calendar-check", "game-session", "game sessions", gameSessionTabs, Set.of(0, 4)));
        gameSessionTabs.add(new Tab("game", "Game", "game", "code", Set.of(0, 4), TableGame::table, TableGame::edit));
        gameSessionTabs.add(new Tab("game-version", "Game Version", "game_version", "code", Set.of(0, 4),
                TableGameVersion::table, TableGameVersion::edit));
        gameSessionTabs.add(new Tab("game-session", "Game Session", "game_version", "code", Set.of(0, 4),
                TableGameSession::table, TableGameSession::edit));
        // gameSessionTabs.add(new Tab("session-dashboard", "Session Dashboard", null, Set.of(0, 4)));

        menuMap.put("DASHBOARDS",
                new Menu("", "", "DASHBOARDS", new ArrayList<>(), Set.of(0, 1, 2, 4)));

        menuList.add("data-session");
        List<Tab> dataSessionTabs = new ArrayList<>();
        menuMap.put("data-session", new Menu("fa-chart-pie", "data-session", "Data Session", dataSessionTabs, Set.of(0, 4)));
        dataSessionTabs.add(new Tab("game", "Game", "game", "code", Set.of(0, 4), TableGame::table, TableGame::edit));
        dataSessionTabs.add(new Tab("game-version", "Game Version", "game_version", "code", Set.of(0, 4),
                TableGameVersion::table, TableGameVersion::edit));
        dataSessionTabs.add(new Tab("game-session", "Game Session", "game_version", "code", Set.of(0, 4),
                TableGameSession::table, TableGameSession::edit));
        dataSessionTabs.add(new Tab("game-mission", "Game Mission", "game_mission", "code", Set.of(0, 1),
                TableGameMission::table, TableGameMission::edit));
        dataSessionTabs.add(new Tab("mission-event", "Mission Event", "mission_event", null, Set.of(0, 4),
                TableMissionEvent::table, TableMissionEvent::view));

        menuList.add("data-player");
        List<Tab> dataPlayerTabs = new ArrayList<>();
        menuMap.put("data-player", new Menu("fa-chart-line", "data-player", "Data Player", dataPlayerTabs, Set.of(0, 4)));
        dataPlayerTabs.add(new Tab("game", "Game", "game", "code", Set.of(0, 4), TableGame::table, TableGame::edit));
        dataPlayerTabs.add(new Tab("game-version", "Game Version", "game_version", "code", Set.of(0, 4),
                TableGameVersion::table, TableGameVersion::edit));
        dataPlayerTabs.add(new Tab("game-session", "Game Session", "game_version", "code", Set.of(0, 4),
                TableGameSession::table, TableGameSession::edit));
        dataPlayerTabs.add(new Tab("player", "Player", "player", "name", Set.of(0, 4), TablePlayer::table, TablePlayer::view));
        dataPlayerTabs.add(new Tab("player-attempt", "Player_Attempt", "player_attempt", null, Set.of(0, 4),
                TablePlayerAttempt::table, TablePlayerAttempt::view));
        dataPlayerTabs.add(new Tab("player-score", "Player Score", "player_score", null, Set.of(0, 4), TablePlayerScore::table,
                TablePlayerScore::view));
        dataPlayerTabs.add(new Tab("player-event", "Player Event", "player_event", null, Set.of(0, 4), TablePlayerEvent::table,
                TablePlayerEvent::view));
        dataPlayerTabs.add(new Tab("player-group-role", "Group Role", "group_role", null, Set.of(0, 4), TableGroupRole::table,
                TableGroupRole::view));

        menuList.add("data-group");
        List<Tab> dataGroupTabs = new ArrayList<>();
        menuMap.put("data-group", new Menu("fa-chart-simple", "data-group", "Data Group", dataGroupTabs, Set.of(0, 4)));
        dataGroupTabs.add(new Tab("game", "Game", "game", "code", Set.of(0, 4), TableGame::table, TableGame::edit));
        dataGroupTabs.add(new Tab("game-version", "Game Version", "game_version", "code", Set.of(0, 4), TableGameVersion::table,
                TableGameVersion::edit));
        dataGroupTabs.add(new Tab("game-session", "Game Session", "game_version", "code", Set.of(0, 4), TableGameSession::table,
                TableGameSession::edit));
        dataGroupTabs.add(new Tab("group", "Group", "group", "name", Set.of(0, 4), TableGroup::table, TableGroup::view));
        dataGroupTabs.add(new Tab("group-player", "Group Player", "group_role", null, Set.of(0, 4), TableGroupRole::table,
                TableGroupRole::view));
        dataGroupTabs.add(new Tab("group-attempt", "Group Attempt", "group_attempt", null, Set.of(0, 4),
                TableGroupAttempt::table, TableGroupAttempt::view));
        dataGroupTabs.add(new Tab("group-score", "Group Score", "group_score", null, Set.of(0, 4), TableGroupScore::table,
                TableGroupScore::view));
        dataGroupTabs.add(new Tab("group-event", "Group Event", "group_event", null, Set.of(0, 4), TableGroupEvent::table,
                TableGroupEvent::view));

        menuList.add("errors");
        List<Tab> errorsTabs = new ArrayList<>();
        menuMap.put("errors", new Menu("fa-triangle-exclamation", "errors", "Errors", errorsTabs, Set.of(0, 1, 2)));
        errorsTabs.add(new Tab("last-100", "Last 100", null, null, Set.of(0, 1, 2), TableErrors::table100, TableErrors::view));

        menuMap.put("DASHBOARDS",
                new Menu("", "", "DASHBOARDS", new ArrayList<>(), Set.of(0, 5)));

        menuList.add("layout");
        List<Tab> layoutTabs = new ArrayList<>();
        menuMap.put("layout", new Menu("fa-display", "layout", "Layout", layoutTabs, Set.of(0)));
        layoutTabs.add(new Tab("dashboard-layout", "Dashboard Layout", "dashboard_layout", "code", Set.of(0),
                TableDashboardLayout::table, TableDashboardLayout::edit));
        layoutTabs.add(new Tab("dashboard-element", "Dashboard Element", "dashboard_element", "code", Set.of(0),
                TableDashboardElement::table, TableDashboardElement::edit));
        layoutTabs.add(new Tab("element-property", "Element Property", "element_property", "code", Set.of(0),
                TableElementProperty::table, TableElementProperty::edit));

        menuList.add("dashboard");
        List<Tab> dashboardTabs = new ArrayList<>();
        menuMap.put("dashboard", new Menu("fa-table-cells-large", "dashboard", "Dashboard", dashboardTabs, Set.of(0, 5)));
        dashboardTabs.add(new Tab("game", "Game", "game", "code", Set.of(0, 5), TableGame::table, TableGame::edit));
        dashboardTabs.add(new Tab("game-version", "Game Version", "game_version", "code", Set.of(0, 5), TableGameVersion::table,
                TableGameVersion::edit));
        // dashboardTabs.add(new Tab("dashboard-template", "Dashboard Template", "code", Set.of(0, 5)));
        // dashboardTabs.add(new Tab("template-element", "Template Element", null, Set.of(0, 5)));
        // dashboardTabs.add(new Tab("property-value", "Property Value", null, Set.of(0, 5)));
        // dashboardTabs.add(new Tab("dashboard", "Dashboard", "name", Set.of(0, 5)));
        // dashboardTabs.add(new Tab("session-dashboard", "Session Dashboard", null, Set.of(0, 5)));
        // dashboardTabs.add(new Tab("dashboard-token", "Dashboard Token", null, Set.of(0, 5)));

        menuMap.put("SETTINGS",
                new Menu("", "", "SETTINGS", new ArrayList<>(), Set.of(0, 1, 2, 3, 4, 5, 6)));

        menuList.add("settings");
        menuMap.put("settings",
                new Menu("fa-user-gear", "settings", "Settings", new ArrayList<>(), Set.of(0, 1, 2, 3, 4, 5, 6)));

        menuList.add("logoff");
        menuMap.put("logoff", new Menu("fa-sign-out", "logoff", "Logoff", new ArrayList<>(), Set.of(0, 1, 2, 3, 4, 5, 6)));
    }

    /**
     * 0 = super admin<br>
     * 1 = game role (including game admin)<br>
     * 2 = organization role <br>
     * 3 = organization game role <br>
     * 4 = game session role <br>
     * 5 = dashboard role <br>
     * 6 = role for everyone <br>
     * @param data game data information
     * @return set of roles for this user
     */
    public static Set<Integer> getRoles(final AdminData data)
    {
        Set<Integer> roles = new HashSet<>();
        if (data.isSuperAdmin())
            roles.add(0);
        if (data.getGameAccess().size() > 0 || data.isGameAdmin())
            roles.add(1);
        if (data.getOrganizationAccess().size() > 0)
            roles.add(2);
        if (data.getOrganizationGameAccess().size() > 0)
            roles.add(3);
        if (data.getGameSessionAccess().size() > 0)
            roles.add(4);
        if (data.getDashboardTemplateAccess().size() > 0)
            roles.add(5);
        roles.add(6);
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

    public static Tab getActiveTab(final AdminData data)
    {
        String menuChoice = data.getMenuChoice();
        List<Tab> tabList = menuMap.get(menuChoice).tabs();
        for (Tab tab : tabList)
        {
            if (tab.tabChoice().equals(data.getTabChoice(menuChoice)))
                return tab;
        }
        System.err.println("Could not find active tab");
        return tabList.get(0);
    }

    public static void initializeTabChoices(final AdminData data)
    {
        data.putTabChoice("home", "");
        data.putTabChoice("organization", "organization");
        data.putTabChoice("user", "user");
        data.putTabChoice("game", "game");
        data.putTabChoice("game-control", "game");
        data.putTabChoice("game-session", "game");
        data.putTabChoice("layout", "dashboard-layout");
        data.putTabChoice("dashboard", "game");
        data.putTabChoice("data-session", "game");
        data.putTabChoice("data-player", "game");
        data.putTabChoice("data-group", "game");
        data.putTabChoice("errors", "last-100");
        data.putTabChoice("settings", "");
    }

    public static record Menu(String icon, String menuChoice, String menuText, List<Tab> tabs, Set<Integer> access)
    {
    }

    public static record Tab(String tabChoice, String tabText, String tableName, String selectField, Set<Integer> access,
            ITable tableRef, IEdit editRef)
    {
    }
}
