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
        menuList.add("ADMIN");
        menuMap.put("ADMIN", new Menu("ADMIN", new ArrayList<>(), Set.of(0, 1, 2, 3, 4, 5, 6)));

        menuList.add("home");
        menuMap.put("home",
                new Menu("fa-house", "home", "home", new ArrayList<>(), Set.of(0, 1, 2, 3, 4, 5, 6), new ArrayList<>()));

        menuList.add("organization");
        List<SubMenu> organizationSubMenus = new ArrayList<>();
        List<Filter> organizationFilters = new ArrayList<>();
        menuMap.put("organization", new Menu("fa-sitemap", "organization", "organizations", organizationSubMenus, Set.of(0, 2),
                organizationFilters));
        organizationSubMenus.add(new SubMenu("organization", "Organization", "organization", Set.of(0, 2),
                TableOrganization::table, TableOrganization::edit));
        organizationSubMenus.add(new SubMenu("user", "User", "user", Set.of(0, 2), TableUser::table, TableUser::edit));
        organizationSubMenus.add(new SubMenu("user-role", "User Role", "organization_role", Set.of(0, 2),
                TableOrganizationRole::table, TableOrganizationRole::edit));
        organizationSubMenus.add(new SubMenu("game", "Game", "game", Set.of(0, 2), TableGame::table, TableGame::edit));
        organizationSubMenus.add(new SubMenu("organization-game", "Game Access", "organization_game", Set.of(0, 2),
                TableOrganizationGame::table, TableOrganizationGame::edit));
        organizationSubMenus.add(new SubMenu("org-game-token", "Access Token", "organization_game_token", Set.of(0, 2),
                TableOrganizationGameToken::table, TableOrganizationGameToken::edit));
        organizationSubMenus.add(new SubMenu("game-session", "Game Session", "game_session", Set.of(0, 2),
                TableGameSession::table, TableGameSession::edit));
        organizationFilters.add(new Filter("organization", "organization", new String[] {"id", "code", "name"}, Set.of(0, 2)));
        organizationFilters.add(new Filter("user", "user", new String[] {"id", "name"}, Set.of(0, 2)));
        organizationFilters.add(new Filter("game", "game", new String[] {"id", "code", "name", "archived"}, Set.of(0, 2)));

        menuList.add("user");
        List<SubMenu> userSubMenus = new ArrayList<>();
        List<Filter> userFilters = new ArrayList<>();
        menuMap.put("user", new Menu("fa-user", "user", "users", userSubMenus, Set.of(0, 1, 2), userFilters));
        userSubMenus.add(new SubMenu("user", "User", "user", Set.of(0, 1, 2), TableUser::table, TableUser::edit));
        userSubMenus.add(new SubMenu("game", "Game", "game", Set.of(0, 1), TableGame::table, TableGame::edit));
        userSubMenus.add(new SubMenu("organization-role", "Organization Role", "organization_role", Set.of(0, 2),
                TableOrganizationRole::table, TableOrganizationRole::edit));
        userSubMenus.add(
                new SubMenu("game-role", "Game Role", "game_role", Set.of(0, 1), TableGameRole::table, TableGameRole::edit));
        userSubMenus.add(new SubMenu("org-game-role", "Org-Game Role", "organization_game_role", Set.of(0, 2),
                TableOrganizationGameRole::table, TableOrganizationGameRole::edit));
        userSubMenus.add(new SubMenu("game-session-role", "Game Session Role", "game_session_role", Set.of(0, 2),
                TableGameSessionRole::table, TableGameSessionRole::edit));
        userSubMenus.add(new SubMenu("dashboard-role", "Dashboard Role", "dashboard_role", Set.of(0, 1, 2),
                TableDashboardRole::table, TableDashboardRole::edit));
        userFilters.add(new Filter("user", "user", new String[] {"id", "name"}, Set.of(0, 1, 2)));
        userFilters.add(new Filter("game", "game", new String[] {"id", "code", "name", "archived"}, Set.of(0, 1)));

        menuList.add("game");
        List<SubMenu> gameSubMenus = new ArrayList<>();
        List<Filter> gameFilters = new ArrayList<>();
        menuMap.put("game", new Menu("fa-dice", "game", "games", gameSubMenus, Set.of(0, 1), gameFilters));
        gameSubMenus.add(new SubMenu("game", "Game", "game", Set.of(0, 1), TableGame::table, TableGame::edit));
        gameSubMenus.add(new SubMenu("game-version", "Game Version", "game_version", Set.of(0, 1), TableGameVersion::table,
                TableGameVersion::edit));
        gameSubMenus.add(new SubMenu("game-mission", "Game Mission", "game_mission", Set.of(0, 1), TableGameMission::table,
                TableGameMission::edit));
        gameSubMenus.add(new SubMenu("scale", "Scale", "scale", Set.of(0, 1), TableScale::table, TableScale::edit));
        gameSubMenus.add(new SubMenu("learning-goal", "Learning Goal", "learning_goal", Set.of(0, 1), TableLearningGoal::table,
                TableLearningGoal::edit));
        gameSubMenus.add(new SubMenu("player-objective", "Player Objective", "player_objective", Set.of(0, 1),
                TablePlayerObjective::table, TablePlayerObjective::edit));
        gameSubMenus.add(new SubMenu("group-objective", "Group Objective", "group_objective", Set.of(0, 1),
                TableGroupObjective::table, TableGroupObjective::edit));
        gameFilters.add(new Filter("game", "game", new String[] {"id", "code", "name", "archived"}, Set.of(0, 1)));
        gameFilters
                .add(new Filter("game_version", "game version", new String[] {"id", "code", "name", "archived"}, Set.of(0, 1)));
        gameFilters.add(new Filter("game_mission", "game mission", new String[] {"id", "code", "name"}, Set.of(0, 1)));

        menuList.add("game-control");
        List<SubMenu> gameControlSubMenus = new ArrayList<>();
        List<Filter> gameControlFilters = new ArrayList<>();
        menuMap.put("game-control", new Menu("fa-square-binary", "game-control", "game access", gameControlSubMenus,
                Set.of(0, 3), gameControlFilters));
        gameControlSubMenus.add(new SubMenu("game", "Game", "game", Set.of(0, 3), TableGame::table, TableGame::edit));
        gameControlSubMenus.add(new SubMenu("organization", "Organization", "organization", Set.of(0, 3),
                TableOrganization::table, TableOrganization::edit));
        gameControlSubMenus.add(new SubMenu("organization-game", "Game Access", "organization_game", Set.of(0, 3),
                TableOrganizationGame::table, TableOrganizationGame::edit));
        gameControlSubMenus.add(new SubMenu("org-game-token", "Access Token", "organization_game_token", Set.of(0, 3),
                TableOrganizationGameToken::table, TableOrganizationGameToken::edit));
        gameControlFilters.add(new Filter("game", "game", new String[] {"id", "code", "name", "archived"}, Set.of(0, 3)));
        gameControlFilters.add(new Filter("organization", "organization", new String[] {"id", "code", "name"}, Set.of(0, 3)));

        menuList.add("game-session");
        List<SubMenu> gameSessionSubMenus = new ArrayList<>();
        List<Filter> gameSessionFilters = new ArrayList<>();
        menuMap.put("game-session", new Menu("fa-calendar-check", "game-session", "game sessions", gameSessionSubMenus,
                Set.of(0, 4), gameSessionFilters));
        gameSessionSubMenus.add(new SubMenu("game", "Game", "game", Set.of(0, 4), TableGame::table, TableGame::edit));
        gameSessionSubMenus.add(new SubMenu("game-version", "Game Version", "game_version", Set.of(0, 4),
                TableGameVersion::table, TableGameVersion::edit));
        gameSessionSubMenus.add(new SubMenu("game-session", "Game Session", "game_version", Set.of(0, 4),
                TableGameSession::table, TableGameSession::edit));
        // gameSessionSubMenus.add(new Tab("session-dashboard", "Session Dashboard", Set.of(0, 4)));
        gameSessionFilters.add(new Filter("game", "game", new String[] {"id", "code", "name", "archived"}, Set.of(0, 4)));
        gameSessionFilters
                .add(new Filter("game_version", "game version", new String[] {"id", "code", "name", "archived"}, Set.of(0, 4)));

        menuList.add("DATA");
        menuMap.put("DATA", new Menu("DATA", new ArrayList<>(), Set.of(0, 1, 2, 4)));

        menuList.add("data-session");
        List<SubMenu> dataSessionSubMenus = new ArrayList<>();
        List<Filter> dataSessionFilters = new ArrayList<>();
        menuMap.put("data-session", new Menu("fa-chart-pie", "data-session", "Data Session", dataSessionSubMenus, Set.of(0, 4),
                dataSessionFilters));
        dataSessionSubMenus.add(new SubMenu("game", "Game", "game", Set.of(0, 4), TableGame::table, TableGame::edit));
        dataSessionSubMenus.add(new SubMenu("game-version", "Game Version", "game_version", Set.of(0, 4),
                TableGameVersion::table, TableGameVersion::edit));
        dataSessionSubMenus.add(new SubMenu("game-session", "Game Session", "game_version", Set.of(0, 4),
                TableGameSession::table, TableGameSession::edit));
        dataSessionSubMenus.add(new SubMenu("game-mission", "Game Mission", "game_mission", Set.of(0, 4),
                TableGameMission::table, TableGameMission::edit));
        dataSessionSubMenus.add(new SubMenu("mission-event", "Mission Event", "mission_event", Set.of(0, 4),
                TableMissionEvent::table, TableMissionEvent::view));
        dataSessionFilters.add(new Filter("game", "game", new String[] {"id", "code", "name", "archived"}, Set.of(0, 4)));
        dataSessionFilters
                .add(new Filter("game_version", "game version", new String[] {"id", "code", "name", "archived"}, Set.of(0, 4)));
        dataSessionFilters.add(new Filter("game_session", "game session",
                new String[] {"id", "code", "name", "play_date", "archived"}, Set.of(0, 4)));
        dataSessionFilters.add(new Filter("game_mission", "game mission", new String[] {"id", "code", "name"}, Set.of(0, 4)));

        menuList.add("data-player");
        List<SubMenu> dataPlayerSubMenus = new ArrayList<>();
        List<Filter> dataPlayerFilters = new ArrayList<>();
        menuMap.put("data-player",
                new Menu("fa-chart-line", "data-player", "Data Player", dataPlayerSubMenus, Set.of(0, 4), dataPlayerFilters));
        dataPlayerSubMenus.add(new SubMenu("game", "Game", "game", Set.of(0, 4), TableGame::table, TableGame::edit));
        dataPlayerSubMenus.add(new SubMenu("game-version", "Game Version", "game_version", Set.of(0, 4),
                TableGameVersion::table, TableGameVersion::edit));
        dataPlayerSubMenus.add(new SubMenu("game-session", "Game Session", "game_version", Set.of(0, 4),
                TableGameSession::table, TableGameSession::edit));
        dataPlayerSubMenus.add(new SubMenu("player", "Player", "player", Set.of(0, 4), TablePlayer::table, TablePlayer::view));
        dataPlayerSubMenus.add(new SubMenu("player-attempt", "Player_Attempt", "player_attempt", Set.of(0, 4),
                TablePlayerAttempt::table, TablePlayerAttempt::view));
        dataPlayerSubMenus.add(new SubMenu("player-score", "Player Score", "player_score", Set.of(0, 4),
                TablePlayerScore::table, TablePlayerScore::view));
        dataPlayerSubMenus.add(new SubMenu("player-event", "Player Event", "player_event", Set.of(0, 4),
                TablePlayerEvent::table, TablePlayerEvent::view));
        dataPlayerSubMenus.add(new SubMenu("player-group-role", "Group Role", "group_role", Set.of(0, 4), TableGroupRole::table,
                TableGroupRole::view));
        dataPlayerFilters.add(new Filter("game", "game", new String[] {"id", "code", "name", "archived"}, Set.of(0, 4)));
        dataPlayerFilters
                .add(new Filter("game_version", "game version", new String[] {"id", "code", "name", "archived"}, Set.of(0, 4)));
        dataPlayerFilters.add(new Filter("game_session", "game session",
                new String[] {"id", "code", "name", "play_date", "archived"}, Set.of(0, 4)));
        dataPlayerFilters.add(new Filter("player", "player", new String[] {"id", "name", "display_name"}, Set.of(0, 4)));
        dataPlayerFilters.add(new Filter("player_attempt", "player attempt", new String[] {"id", "attempt_nr"}, Set.of(0, 4)));

        menuList.add("data-group");
        List<SubMenu> dataGroupSubMenus = new ArrayList<>();
        List<Filter> dataGroupFilters = new ArrayList<>();
        menuMap.put("data-group",
                new Menu("fa-chart-simple", "data-group", "Data Group", dataGroupSubMenus, Set.of(0, 4), dataGroupFilters));
        dataGroupSubMenus.add(new SubMenu("game", "Game", "game", Set.of(0, 4), TableGame::table, TableGame::edit));
        dataGroupSubMenus.add(new SubMenu("game-version", "Game Version", "game_version", Set.of(0, 4), TableGameVersion::table,
                TableGameVersion::edit));
        dataGroupSubMenus.add(new SubMenu("game-session", "Game Session", "game_version", Set.of(0, 4), TableGameSession::table,
                TableGameSession::edit));
        dataGroupSubMenus.add(new SubMenu("group", "Group", "group", Set.of(0, 4), TableGroup::table, TableGroup::view));
        dataGroupSubMenus.add(new SubMenu("group-player", "Group Player", "group_role", Set.of(0, 4), TableGroupRole::table,
                TableGroupRole::view));
        dataGroupSubMenus.add(new SubMenu("group-attempt", "Group Attempt", "group_attempt", Set.of(0, 4),
                TableGroupAttempt::table, TableGroupAttempt::view));
        dataGroupSubMenus.add(new SubMenu("group-score", "Group Score", "group_score", Set.of(0, 4), TableGroupScore::table,
                TableGroupScore::view));
        dataGroupSubMenus.add(new SubMenu("group-event", "Group Event", "group_event", Set.of(0, 4), TableGroupEvent::table,
                TableGroupEvent::view));
        dataGroupFilters.add(new Filter("game", "game", new String[] {"id", "code", "name", "archived"}, Set.of(0, 4)));
        dataGroupFilters
                .add(new Filter("game_version", "game version", new String[] {"id", "code", "name", "archived"}, Set.of(0, 4)));
        dataGroupFilters.add(new Filter("game_session", "game session",
                new String[] {"id", "code", "name", "play_date", "archived"}, Set.of(0, 4)));
        dataGroupFilters.add(new Filter("group", "group", new String[] {"id", "name"}, Set.of(0, 4)));
        dataGroupFilters.add(new Filter("group_attempt", "group attempt", new String[] {"id", "attempt_nr"}, Set.of(0, 4)));

        menuList.add("errors");
        List<SubMenu> errorsSubMenus = new ArrayList<>();
        List<Filter> errorsFilters = new ArrayList<>();
        menuMap.put("errors",
                new Menu("fa-triangle-exclamation", "errors", "Errors", errorsSubMenus, Set.of(0, 1, 2), errorsFilters));
        errorsSubMenus
                .add(new SubMenu("last-100", "Last 100", null, Set.of(0, 1, 2), TableErrors::table100, TableErrors::view));

        menuList.add("DASHBOARDS");
        menuMap.put("DASHBOARDS", new Menu("DASHBOARDS", new ArrayList<>(), Set.of(0, 5)));

        menuList.add("layout");
        List<SubMenu> layoutSubMenus = new ArrayList<>();
        List<Filter> layoutFilters = new ArrayList<>();
        menuMap.put("layout", new Menu("fa-display", "layout", "Layout", layoutSubMenus, Set.of(0), layoutFilters));
        layoutSubMenus.add(new SubMenu("dashboard-layout", "Dashboard Layout", "dashboard_layout", Set.of(0),
                TableDashboardLayout::table, TableDashboardLayout::edit));
        layoutSubMenus.add(new SubMenu("dashboard-element", "Dashboard Element", "dashboard_element", Set.of(0),
                TableDashboardElement::table, TableDashboardElement::edit));
        layoutSubMenus.add(new SubMenu("element-property", "Element Property", "element_property", Set.of(0),
                TableElementProperty::table, TableElementProperty::edit));

        menuList.add("dashboard");
        List<SubMenu> dashboardSubMenus = new ArrayList<>();
        List<Filter> dashboardFilters = new ArrayList<>();
        menuMap.put("dashboard",
                new Menu("fa-table-cells-large", "dashboard", "Dashboard", dashboardSubMenus, Set.of(0, 5), dashboardFilters));
        dashboardSubMenus.add(new SubMenu("game", "Game", "game", Set.of(0, 5), TableGame::table, TableGame::edit));
        dashboardSubMenus.add(new SubMenu("game-version", "Game Version", "game_version", Set.of(0, 5), TableGameVersion::table,
                TableGameVersion::edit));
        // dashboardSubMenus.add(new SubMenu("dashboard-template", "Dashboard Template", Set.of(0, 5)));
        // dashboardSubMenus.add(new SubMenu("template-element", "Template Element", Set.of(0, 5)));
        // dashboardSubMenus.add(new SubMenu("property-value", "Property Value", Set.of(0, 5)));
        // dashboardSubMenus.add(new SubMenu("dashboard", "Dashboard", "name", Set.of(0, 5)));
        // dashboardSubMenus.add(new SubMenu("session-dashboard", "Session Dashboard", Set.of(0, 5)));
        // dashboardSubMenus.add(new SubMenu("dashboard-token", "Dashboard Token", Set.of(0, 5)));

        menuList.add("SETTINGS");
        menuMap.put("SETTINGS", new Menu("SETTINGS", new ArrayList<>(), Set.of(0, 1, 2, 3, 4, 5, 6)));

        menuList.add("settings");
        menuMap.put("settings", new Menu("fa-user-gear", "settings", "Settings", new ArrayList<>(), Set.of(0, 1, 2, 3, 4, 5, 6),
                new ArrayList<>()));

        menuList.add("logoff");
        menuMap.put("logoff",
                new Menu("fa-sign-out", "logoff", "Logoff", new ArrayList<>(), Set.of(0, 1, 2, 3, 4, 5, 6), new ArrayList<>()));
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

    public static SubMenu getSubMenu(final String menuChoice, final String subMenuChoice)
    {
        List<SubMenu> subMenuList = menuMap.get(menuChoice).subMenus;
        for (SubMenu subMenu : subMenuList)
        {
            if (subMenu.subMenuChoice.equals(subMenuChoice))
                return subMenu;
        }
        return null;
    }

    public static boolean showSubMenu(final AdminData data, final String menuChoice, final String subMenuChoice)
    {
        Set<Integer> roles = getRoles(data);
        Set<Integer> access = getSubMenu(menuChoice, subMenuChoice).access();
        roles.retainAll(access);
        return !roles.isEmpty();
    }

    public static void table(final AdminData data, final HttpServletRequest request, final String click)
    {
        String menuChoice = data.getMenuChoice();
        SubMenu subMenu = getSubMenu(menuChoice, data.getSubMenuChoice(menuChoice));
        subMenu.tableRef.table(data, request, menuChoice);
    }

    public static void edit(final AdminData data, final HttpServletRequest request, final String click, final int recordId)
    {
        String menuChoice = data.getMenuChoice();
        SubMenu subMenu = getSubMenu(menuChoice, data.getSubMenuChoice(menuChoice));
        subMenu.editRef.edit(data, request, click, recordId);
    }

    public static SubMenu getActiveSubMenu(final AdminData data)
    {
        String menuChoice = data.getMenuChoice();
        List<SubMenu> subMenuList = menuMap.get(menuChoice).subMenus();
        for (SubMenu subMenu : subMenuList)
        {
            if (subMenu.subMenuChoice().equals(data.getSubMenuChoice(menuChoice)))
                return subMenu;
        }
        System.err.println("Could not find active subMenu");
        return subMenuList.get(0);
    }

    public static void initializeSubMenuChoices(final AdminData data)
    {
        data.putSubMenuChoice("home", "");
        data.putSubMenuChoice("organization", "organization");
        data.putSubMenuChoice("user", "user");
        data.putSubMenuChoice("game", "game");
        data.putSubMenuChoice("game-control", "game");
        data.putSubMenuChoice("game-session", "game");
        data.putSubMenuChoice("layout", "dashboard-layout");
        data.putSubMenuChoice("dashboard", "game");
        data.putSubMenuChoice("data-session", "game");
        data.putSubMenuChoice("data-player", "game");
        data.putSubMenuChoice("data-group", "game");
        data.putSubMenuChoice("errors", "last-100");
        data.putSubMenuChoice("settings", "");
    }

    public static record Menu(boolean header, String icon, String menuChoice, String menuText, List<SubMenu> subMenus,
            Set<Integer> access, List<Filter> filters)
    {
        public Menu(final String icon, final String menuChoice, final String menuText, final List<SubMenu> subMenus,
                final Set<Integer> access, final List<Filter> filters)
        {
            this(false, icon, menuChoice, menuText, subMenus, access, filters);
        }

        public Menu(final String menuText, final List<SubMenu> subMenus, final Set<Integer> access)
        {
            this(true, "", menuText, menuText, subMenus, access, new ArrayList<>());
        }

    }

    public static record SubMenu(String subMenuChoice, String subMenuText, String tableName, Set<Integer> access,
            ITable tableRef, IEdit editRef)
    {
    }

    public static record Filter(String tableName, String tableText, String[] selectFields, Set<Integer> access)
    {
    }
}
