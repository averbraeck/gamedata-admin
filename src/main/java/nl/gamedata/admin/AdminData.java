package nl.gamedata.admin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.jooq.Record;
import org.jooq.Table;
import org.jooq.TableRecord;
import org.jooq.UpdatableRecord;

import nl.gamedata.admin.form.WebForm;
import nl.gamedata.admin.form.table.TableForm;
import nl.gamedata.common.Access;
import nl.gamedata.common.CommonData;
import nl.gamedata.common.SqlUtils;
import nl.gamedata.data.Tables;
import nl.gamedata.data.tables.records.DashboardRoleRecord;
import nl.gamedata.data.tables.records.DashboardTemplateRecord;
import nl.gamedata.data.tables.records.GameRecord;
import nl.gamedata.data.tables.records.GameRoleRecord;
import nl.gamedata.data.tables.records.GameSessionRecord;
import nl.gamedata.data.tables.records.GameSessionRoleRecord;
import nl.gamedata.data.tables.records.GameVersionRecord;
import nl.gamedata.data.tables.records.OrganizationGameRecord;
import nl.gamedata.data.tables.records.OrganizationGameRoleRecord;
import nl.gamedata.data.tables.records.OrganizationRecord;
import nl.gamedata.data.tables.records.OrganizationRoleRecord;
import nl.gamedata.data.tables.records.UserRecord;

public class AdminData extends CommonData
{
    /** The name of the user logged in to this session. If null, no user is logged in. */
    private String username;

    /** the User record (static during session). */
    private UserRecord user;

    /** the access rights of the user via OrganizationRole. Lazy loading. */
    private Map<Integer, Access> organizationAccess = null;

    /** the access right of the user via GameRole. Lazy loading. */
    private Map<Integer, Access> gameAccess = null;

    /** the access right of the user via OrganizationGameRole. Lazy loading. */
    private Map<Integer, Access> organizationGameAccess = null;

    /** the access right of the user via GameSessionRole. Lazy loading. */
    private Map<Integer, Access> gameSessionAccess = null;

    /** the access right of the user via DashboardRole. Lazy loading. */
    private Map<Integer, Access> dashboardTemplateAccess = null;

    /* ================================================ */
    /* PERSISTENT DATA ABOUT CHOICES MADE ON THE SCREEN */
    /* ================================================ */

    /** Which menu has been chosen, to maintain persistence after a POST. */
    private String menuChoice = "";

    /** Which tab has been chosen, to maintain persistence after a POST. */
    private Map<String, String> tabChoice = new HashMap<>();

    /** Map that links the menu#tab name to a potential filter choice (record and display name) in the navbar. */
    private Map<String, FilterChoice> tabFilterChoices = new HashMap<>();

    /** The sorting order of columns in the tables. The map is from menu#tab to column header to A-Z / Z-A */
    private Map<String, ColumnSort> tableColumnSort = new HashMap<>();

    /** the page content as built by the appropriate class. */
    private String content = "";

    /** Show popup window or not. */
    private boolean showModalWindow = false;

    /** Modal window content for popup. */
    private String modalWindowHtml = "";

    /** The form that is currently being used. */
    private WebForm editForm = null;

    /** The record that is currently being edited. */
    private UpdatableRecord<?> editRecord = null;

    /** An error occurred during save, delete, or cancel. */
    private boolean error = false;

    /** Record that has the field name and the direction of sorting; A-Z is true, Z-A is false. */
    public record ColumnSort(String fieldName, boolean az)
    {
    }

    /** A filter choice (record and display name), used in the filtering of records in the navbar. */
    public record FilterChoice(TableRecord<?> record, String name)
    {
    }

    /* =================================== */
    /* GENERIC METHODS FOR THE DATA OBJECT */
    /* =================================== */

    public AdminData()
    {
        Menus.initializeTabChoices(this);
    }

    public String getSidebar()
    {
        return Sidebar.makeSidebar(this);
    }

    public String getNavbar()
    {
        return Navbar.makeNavbar(this);
    }

    public <R extends UpdatableRecord<R>> int getId(final R record)
    {
        return Provider.getId(record);
    }

    /* *********************** */
    /* ACCESS RIGHTS AND ROLES */
    /* *********************** */

    public boolean isSuperAdmin()
    {
        return getUser() == null ? false : getUser().getSuperAdmin() != 0;
    }

    public boolean isGameAdmin()
    {
        return getUser() == null ? false : getUser().getGameAdmin() != 0;
    }

    public boolean isOrganizationAdmin()
    {
        return hasOrganizationAccess(Access.ADMIN);
    }

    public boolean hasOrganizationAccess(final Access access)
    {
        for (Access oa : getOrganizationAccess().values())
        {
            if (oa.ordinal() <= access.ordinal())
                return true;
        }
        return false;
    }

    public boolean hasOrganizationGameAccess(final Access access)
    {
        for (Access oga : getOrganizationGameAccess().values())
        {
            if (oga.ordinal() <= access.ordinal())
                return true;
        }
        return false;
    }

    public boolean hasGameAccess(final Access access)
    {
        if (isGameAdmin())
            return true;
        for (Access ga : getGameAccess().values())
        {
            if (ga.ordinal() <= access.ordinal())
                return true;
        }
        return false;
    }

    public boolean hasGameSessionAccess(final Access access)
    {
        for (Access gsa : getGameSessionAccess().values())
        {
            if (gsa.ordinal() <= access.ordinal())
                return true;
        }
        return false;
    }

    public boolean hasDashboardTemplateAccess(final Access access)
    {
        for (Access dta : getDashboardTemplateAccess().values())
        {
            if (dta.ordinal() <= access.ordinal())
                return true;
        }
        return false;
    }

    /**
     * Call this method after adding or deleting users, organizations, games, organization-game combinations, game sessions,
     * dashboard templates, or after adding, changing or deleting roles.
     */
    public void resetRoles()
    {
        this.organizationAccess = null;
        this.gameAccess = null;
        this.organizationGameAccess = null;
        this.gameSessionAccess = null;
        this.dashboardTemplateAccess = null;
    }

    public Map<Integer, Access> getOrganizationAccess()
    {
        if (this.organizationAccess == null)
        {
            this.organizationAccess = new HashMap<>();
            if (isSuperAdmin())
            {
                List<OrganizationRecord> orgList = getDSL().selectFrom(Tables.ORGANIZATION).fetch();
                for (var organization : orgList)
                {
                    this.organizationAccess.put(organization.getId(), Access.ADMIN);
                }
            }
            else
            {
                List<OrganizationRoleRecord> orList = getDSL().selectFrom(Tables.ORGANIZATION_ROLE)
                        .where(Tables.ORGANIZATION_ROLE.USER_ID.eq(this.user.getId())).fetch();
                for (var or : orList)
                {
                    if (or.getAdmin() != 0)
                        this.organizationAccess.put(or.getOrganizationId(), Access.ADMIN);
                    else if (or.getEdit() != 0)
                        this.organizationAccess.put(or.getOrganizationId(), Access.EDIT);
                    else if (or.getView() != 0)
                        this.organizationAccess.put(or.getOrganizationId(), Access.VIEW);
                }
            }
        }
        return this.organizationAccess;
    }

    public Set<Integer> getOrganizationAccess(final Access access)
    {
        Set<Integer> ret = new HashSet<>();
        for (var entry : getOrganizationAccess().entrySet())
        {
            if (entry.getValue().ordinal() <= access.ordinal())
                ret.add(entry.getKey());
        }
        return ret;
    }

    public Set<OrganizationRecord> getOrganizationPicklist(final Access access)
    {
        Set<OrganizationRecord> ret = new HashSet<>();
        for (var organizationEntry : getOrganizationAccess().entrySet())
        {
            if (organizationEntry.getValue().ordinal() <= access.ordinal())
            {
                var organization = SqlUtils.readRecordFromId(this, Tables.ORGANIZATION, organizationEntry.getKey());
                ret.add(organization);
            }
        }
        return ret;
    }

    public Map<Integer, Access> getGameAccess()
    {
        if (this.gameAccess == null)
        {
            this.gameAccess = new HashMap<>();

            if (isSuperAdmin())
            {
                List<GameRecord> gameList = getDSL().selectFrom(Tables.GAME).fetch();
                for (var game : gameList)
                {
                    this.gameAccess.put(game.getId(), Access.EDIT);
                }
            }

            else

            {
                // direct game roles
                List<GameRoleRecord> grList =
                        getDSL().selectFrom(Tables.GAME_ROLE).where(Tables.GAME_ROLE.USER_ID.eq(this.user.getId())).fetch();
                for (var gr : grList)
                {
                    if (gr.getEdit() != 0)
                        addGameAccess(gr.getGameId(), Access.EDIT);
                    else if (gr.getView() != 0)
                        addGameAccess(gr.getGameId(), Access.VIEW);
                }

                // indirect game roles via game_access for all organizations where user is a member
                List<OrganizationRoleRecord> orList = getDSL().selectFrom(Tables.ORGANIZATION_ROLE)
                        .where(Tables.ORGANIZATION_ROLE.USER_ID.eq(this.user.getId())).fetch();
                for (var or : orList)
                {
                    List<OrganizationGameRecord> ogList = getDSL().selectFrom(Tables.ORGANIZATION_GAME)
                            .where(Tables.ORGANIZATION_GAME.ORGANIZATION_ID.eq(or.getOrganizationId())).fetch();
                    for (var og : ogList)
                    {
                        if (or.getEdit() != 0 || or.getAdmin() != 0 || or.getView() != 0)
                            addGameAccess(og.getGameId(), Access.VIEW);
                    }
                }

                // indirect game roles via direct game_access role
                List<OrganizationGameRoleRecord> ogrList = getDSL().selectFrom(Tables.ORGANIZATION_GAME_ROLE)
                        .where(Tables.ORGANIZATION_GAME_ROLE.USER_ID.eq(this.user.getId())).fetch();
                for (var ogr : ogrList)
                {
                    OrganizationGameRecord ga =
                            SqlUtils.readRecordFromId(this, Tables.ORGANIZATION_GAME, ogr.getOrganizationGameId());
                    if (ogr.getEdit() != 0 || ogr.getView() != 0)
                        addGameAccess(ga.getGameId(), Access.VIEW);
                }

                // indirect game roles via session_role
                List<GameSessionRoleRecord> gsrList = getDSL().selectFrom(Tables.GAME_SESSION_ROLE)
                        .where(Tables.GAME_SESSION_ROLE.USER_ID.eq(this.user.getId())).fetch();
                for (var gsr : gsrList)
                {
                    GameSessionRecord gs = SqlUtils.readRecordFromId(this, Tables.GAME_SESSION, gsr.getGameSessionId());
                    GameVersionRecord gv = SqlUtils.readRecordFromId(this, Tables.GAME_VERSION, gs.getGameVersionId());
                    if (gsr.getEdit() != 0 || gsr.getView() != 0)
                        addGameAccess(gv.getGameId(), Access.VIEW);
                }

                // indirect game roles via dashboard_role
                List<DashboardRoleRecord> drList = getDSL().selectFrom(Tables.DASHBOARD_ROLE)
                        .where(Tables.DASHBOARD_ROLE.USER_ID.eq(this.user.getId())).fetch();
                for (var dr : drList)
                {
                    DashboardTemplateRecord dt =
                            SqlUtils.readRecordFromId(this, Tables.DASHBOARD_TEMPLATE, dr.getDashboardTemplateId());
                    GameVersionRecord gv = SqlUtils.readRecordFromId(this, Tables.GAME_VERSION, dt.getGameVersionId());
                    if (dr.getEdit() != 0 || dr.getView() != 0)
                        addGameAccess(gv.getGameId(), Access.VIEW);
                }
            }
        }
        return this.gameAccess;
    }

    private void addGameAccess(final Integer gameId, final Access access)
    {
        Access oldAccess = this.gameAccess.get(gameId);
        if (oldAccess == null)
            this.gameAccess.put(gameId, access);
        else if (oldAccess.ordinal() > access.ordinal())
            this.gameAccess.put(gameId, access);
    }

    public Set<Integer> getGameAccess(final Access access)
    {
        Set<Integer> ret = new HashSet<>();
        for (var entry : getGameAccess().entrySet())
        {
            if (entry.getValue().ordinal() <= access.ordinal())
                ret.add(entry.getKey());
        }
        return ret;
    }

    public Set<GameRecord> getGamePicklist(final Access access)
    {
        Set<GameRecord> ret = new HashSet<>();
        for (var gameEntry : getGameAccess().entrySet())
        {
            if (gameEntry.getValue().ordinal() <= access.ordinal())
            {
                var game = SqlUtils.readRecordFromId(this, Tables.GAME, gameEntry.getKey());
                ret.add(game);
            }
        }
        return ret;
    }

    public Set<GameRecord> getGamePicklist(final int organizationId, final Access access)
    {
        Set<GameRecord> ret = new HashSet<>();
        for (var gameEntry : getGameAccess().entrySet())
        {
            if (gameEntry.getValue().ordinal() <= access.ordinal())
            {
                for (var orgGameId : getOrganizationGameAccess(Access.EDIT))
                {
                    OrganizationGameRecord og = SqlUtils.readRecordFromId(this, Tables.ORGANIZATION_GAME, orgGameId);
                    if (og.getOrganizationId().equals(organizationId) && gameEntry.getKey().equals(og.getGameId()))
                    {
                        var game = SqlUtils.readRecordFromId(this, Tables.GAME, gameEntry.getKey());
                        ret.add(game);
                    }
                }
            }
        }
        return ret;
    }

    public Map<Integer, String> getGameVersionPicklist(final Access access)
    {
        Map<Integer, String> ret = new HashMap<>();
        List<Record> gvList = getDSL()
                .selectFrom(Tables.GAME_VERSION.join(Tables.GAME).on(Tables.GAME_VERSION.GAME_ID.eq(Tables.GAME.ID))).fetch();
        for (var gv : gvList)
        {
            for (var gameEntry : getGameAccess().entrySet())
            {
                if (gameEntry.getValue().ordinal() <= access.ordinal()
                        && gameEntry.getKey().equals(gv.getValue(Tables.GAME.ID)))
                {
                    ret.put(gv.getValue(Tables.GAME_VERSION.ID),
                            gv.getValue(Tables.GAME.CODE) + "-" + gv.getValue(Tables.GAME_VERSION.NAME));
                }
            }
        }
        return ret;
    }

    public Map<Integer, String> getGameVersionPicklist(final int gameId, final Access access)
    {
        Map<Integer, String> ret = new HashMap<>();
        List<Record> gvList =
                getDSL().selectFrom(Tables.GAME_VERSION.join(Tables.GAME).on(Tables.GAME_VERSION.GAME_ID.eq(Tables.GAME.ID)))
                        .where(Tables.GAME.ID.eq(gameId)).fetch();
        for (var gv : gvList)
        {
            for (var gameEntry : getGameAccess().entrySet())
            {
                if (gameEntry.getValue().ordinal() <= access.ordinal()
                        && gameEntry.getKey().equals(gv.getValue(Tables.GAME.ID)))
                {
                    ret.put(gv.getValue(Tables.GAME_VERSION.ID),
                            gv.getValue(Tables.GAME.CODE) + "-" + gv.getValue(Tables.GAME_VERSION.NAME));
                }
            }
        }
        return ret;
    }

    public Map<Integer, Access> getOrganizationGameAccess()
    {
        if (this.organizationGameAccess == null)
        {
            this.organizationGameAccess = new HashMap<>();

            if (isSuperAdmin())
            {
                List<OrganizationGameRecord> ogList = getDSL().selectFrom(Tables.ORGANIZATION_GAME).fetch();
                for (var og : ogList)
                {
                    this.organizationGameAccess.put(og.getId(), Access.EDIT);
                }
            }

            else

            {
                // direct game access roles
                List<OrganizationGameRoleRecord> ogrList = getDSL().selectFrom(Tables.ORGANIZATION_GAME_ROLE)
                        .where(Tables.ORGANIZATION_GAME_ROLE.USER_ID.eq(this.user.getId())).fetch();
                for (var ogr : ogrList)
                {
                    if (ogr.getEdit() != 0)
                        addOrganizationGameAccess(ogr.getOrganizationGameId(), Access.EDIT);
                    else if (ogr.getView() != 0)
                        addOrganizationGameAccess(ogr.getOrganizationGameId(), Access.VIEW);
                }

                // indirect game_access roles for all organizations where user is a member
                List<OrganizationRoleRecord> orList = getDSL().selectFrom(Tables.ORGANIZATION_ROLE)
                        .where(Tables.ORGANIZATION_ROLE.USER_ID.eq(this.user.getId())).fetch();
                for (var or : orList)
                {
                    List<OrganizationGameRecord> ogList = getDSL().selectFrom(Tables.ORGANIZATION_GAME)
                            .where(Tables.ORGANIZATION_GAME.ORGANIZATION_ID.eq(or.getOrganizationId())).fetch();
                    for (var og : ogList)
                    {
                        if (or.getEdit() != 0 || or.getAdmin() != 0)
                            addOrganizationGameAccess(og.getId(), Access.EDIT);
                        if (or.getView() != 0)
                            addOrganizationGameAccess(og.getId(), Access.VIEW);
                    }
                }
            }
        }
        return this.organizationGameAccess;
    }

    public Set<Integer> getOrganizationGameAccess(final Access access)
    {
        Set<Integer> ret = new HashSet<>();
        for (var entry : getOrganizationGameAccess().entrySet())
        {
            if (entry.getValue().ordinal() <= access.ordinal())
                ret.add(entry.getKey());
        }
        return ret;
    }

    public Set<OrganizationGameRecord> getOrganizationGamePicklist(final Access access)
    {
        Set<OrganizationGameRecord> ret = new HashSet<>();
        for (var entry : getOrganizationGameAccess().entrySet())
        {
            if (entry.getValue().ordinal() <= access.ordinal())
            {
                var og = SqlUtils.readRecordFromId(this, Tables.ORGANIZATION_GAME, entry.getKey());
                ret.add(og);
            }
        }
        return ret;
    }

    private void addOrganizationGameAccess(final Integer organizationGameId, final Access access)
    {
        Access oldAccess = this.organizationGameAccess.get(organizationGameId);
        if (oldAccess == null)
            this.organizationGameAccess.put(organizationGameId, access);
        else if (oldAccess.ordinal() > access.ordinal())
            this.organizationGameAccess.put(organizationGameId, access);
    }

    public Map<Integer, Access> getGameSessionAccess()
    {
        if (this.gameSessionAccess == null)
        {
            this.gameSessionAccess = new HashMap<>();

            if (isSuperAdmin())
            {
                List<GameSessionRecord> gsList = getDSL().selectFrom(Tables.GAME_SESSION).fetch();
                for (var gs : gsList)
                {
                    this.gameSessionAccess.put(gs.getId(), Access.EDIT);
                }
            }

            else

            {
                // direct game session roles
                List<GameSessionRoleRecord> gsrList = getDSL().selectFrom(Tables.GAME_SESSION_ROLE)
                        .where(Tables.GAME_SESSION_ROLE.USER_ID.eq(this.user.getId())).fetch();
                for (var gsr : gsrList)
                {
                    if (gsr.getEdit() != 0)
                        addGameGameSessionAccess(gsr.getGameSessionId(), Access.EDIT);
                    else if (gsr.getView() != 0)
                        addGameGameSessionAccess(gsr.getGameSessionId(), Access.VIEW);
                }

                // indirect game session roles via organization_game roles
                List<Record> ogrList = getDSL()
                        .selectFrom(Tables.ORGANIZATION_GAME.join(Tables.ORGANIZATION_GAME_ROLE)
                                .on(Tables.ORGANIZATION_GAME_ROLE.ORGANIZATION_GAME_ID.eq(Tables.ORGANIZATION_GAME.ID)))
                        .where(Tables.ORGANIZATION_GAME_ROLE.USER_ID.eq(this.user.getId())).fetch();
                for (var ogr : ogrList)
                {
                    List<Record> gsList = getDSL()
                            .selectFrom(Tables.GAME_SESSION.join(Tables.GAME_VERSION)
                                    .on(Tables.GAME_SESSION.GAME_VERSION_ID.eq(Tables.GAME_VERSION.ID)))
                            .where(Tables.GAME_SESSION.ORGANIZATION_ID
                                    .eq(ogr.getValue(Tables.ORGANIZATION_GAME.ORGANIZATION_ID))
                                    .and(Tables.GAME_VERSION.GAME_ID.eq(ogr.getValue(Tables.ORGANIZATION_GAME.GAME_ID))))
                            .fetch();
                    for (var gs : gsList)
                    {
                        if (ogr.getValue(Tables.ORGANIZATION_GAME_ROLE.EDIT) != 0)
                            addGameGameSessionAccess(gs.getValue(Tables.GAME_SESSION.ID), Access.EDIT);
                        else if (ogr.getValue(Tables.ORGANIZATION_GAME_ROLE.VIEW) != 0)
                            addGameGameSessionAccess(gs.getValue(Tables.GAME_SESSION.ID), Access.VIEW);
                    }
                }

                // indirect game session roles for all organizations where user is a member
                List<OrganizationRoleRecord> orList = getDSL().selectFrom(Tables.ORGANIZATION_ROLE)
                        .where(Tables.ORGANIZATION_ROLE.USER_ID.eq(this.user.getId())).fetch();
                for (var or : orList)
                {
                    List<OrganizationGameRecord> ogList = getDSL().selectFrom(Tables.ORGANIZATION_GAME)
                            .where(Tables.ORGANIZATION_GAME.ORGANIZATION_ID.eq(or.getOrganizationId())).fetch();
                    for (var og : ogList)
                    {
                        List<GameSessionRecord> gsList = getDSL().selectFrom(Tables.GAME_SESSION)
                                .where(Tables.GAME_SESSION.ORGANIZATION_ID.eq(og.getOrganizationId())).fetch();
                        for (var gs : gsList)
                        {
                            if (or.getEdit() != 0)
                                addGameGameSessionAccess(gs.getId(), Access.EDIT);
                            else if (or.getView() != 0)
                                addGameGameSessionAccess(gs.getId(), Access.VIEW);
                        }
                    }
                }
            }
        }
        return this.gameSessionAccess;
    }

    private void addGameGameSessionAccess(final Integer gameSessionId, final Access access)
    {
        Access oldAccess = this.gameSessionAccess.get(gameSessionId);
        if (oldAccess == null)
            this.gameSessionAccess.put(gameSessionId, access);
        else if (oldAccess.ordinal() > access.ordinal())
            this.gameSessionAccess.put(gameSessionId, access);
    }

    public Set<Integer> getGameSessionAccess(final Access access)
    {
        Set<Integer> ret = new HashSet<>();
        for (var entry : getGameSessionAccess().entrySet())
        {
            if (entry.getValue().ordinal() <= access.ordinal())
                ret.add(entry.getKey());
        }
        return ret;
    }

    public Set<GameSessionRecord> getGameSessionPicklist(final Access access)
    {
        Set<GameSessionRecord> ret = new HashSet<>();
        for (var entry : getGameSessionAccess().entrySet())
        {
            if (entry.getValue().ordinal() <= access.ordinal())
            {
                var gameSession = SqlUtils.readRecordFromId(this, Tables.GAME_SESSION, entry.getKey());
                ret.add(gameSession);
            }
        }
        return ret;
    }

    public Map<Integer, Access> getDashboardTemplateAccess()
    {
        if (this.dashboardTemplateAccess == null)
        {
            this.dashboardTemplateAccess = new HashMap<>();

            if (isSuperAdmin())
            {
                List<DashboardTemplateRecord> dashboardList = getDSL().selectFrom(Tables.DASHBOARD_TEMPLATE).fetch();
                for (var dt : dashboardList)
                {
                    this.dashboardTemplateAccess.put(dt.getId(), Access.EDIT);
                }
            }

            else

            {
                // direct dashboard roles
                List<DashboardRoleRecord> drList = getDSL().selectFrom(Tables.DASHBOARD_ROLE)
                        .where(Tables.DASHBOARD_ROLE.USER_ID.eq(this.user.getId())).fetch();
                for (var dr : drList)
                {
                    if (dr.getEdit() != 0)
                        addDashboardTemplateAccess(dr.getDashboardTemplateId(), Access.EDIT);
                    else if (dr.getView() != 0)
                        addDashboardTemplateAccess(dr.getDashboardTemplateId(), Access.VIEW);
                }

                // indirect dashboard roles via game_access roles
                List<OrganizationGameRoleRecord> ogrList = getDSL().selectFrom(Tables.ORGANIZATION_GAME_ROLE)
                        .where(Tables.ORGANIZATION_GAME_ROLE.USER_ID.eq(this.user.getId())).fetch();
                for (var ogr : ogrList)
                {
                    List<DashboardTemplateRecord> dtList = getDSL().selectFrom(Tables.DASHBOARD_TEMPLATE)
                            .where(Tables.DASHBOARD_TEMPLATE.ORGANIZATION_GAME_ID.eq(ogr.getOrganizationGameId())).fetch();
                    for (var dt : dtList)
                    {
                        if (ogr.getEdit() != 0)
                            addDashboardTemplateAccess(dt.getId(), Access.EDIT);
                        else if (ogr.getView() != 0)
                            addDashboardTemplateAccess(dt.getId(), Access.VIEW);
                    }

                    // add the public templates for the accessible games as well (view-only)
                    OrganizationGameRecord og =
                            SqlUtils.readRecordFromId(this, Tables.ORGANIZATION_GAME, ogr.getOrganizationGameId());
                    addDashboardTemplateAccess(og);
                }

                // indirect dashboard roles for all organizations where user is a member
                List<OrganizationRoleRecord> orList = getDSL().selectFrom(Tables.ORGANIZATION_ROLE)
                        .where(Tables.ORGANIZATION_ROLE.USER_ID.eq(this.user.getId())).fetch();
                for (var or : orList)
                {
                    List<OrganizationGameRecord> ogList = getDSL().selectFrom(Tables.ORGANIZATION_GAME)
                            .where(Tables.ORGANIZATION_GAME.ORGANIZATION_ID.eq(or.getOrganizationId())).fetch();
                    for (var og : ogList)
                    {
                        List<DashboardTemplateRecord> dtList = getDSL().selectFrom(Tables.DASHBOARD_TEMPLATE)
                                .where(Tables.DASHBOARD_TEMPLATE.ORGANIZATION_GAME_ID.eq(og.getId())).fetch();
                        for (var dt : dtList)
                        {
                            if (or.getEdit() != 0)
                                addDashboardTemplateAccess(dt.getId(), Access.EDIT);
                            else if (or.getView() != 0)
                                addDashboardTemplateAccess(dt.getId(), Access.VIEW);
                        }

                        // add the public templates for the accessible games as well (view-only)
                        addDashboardTemplateAccess(og);
                    }
                }
            }
        }
        return this.dashboardTemplateAccess;
    }

    private void addDashboardTemplateAccess(final OrganizationGameRecord og)
    {
        List<GameVersionRecord> gvList =
                getDSL().selectFrom(Tables.GAME_VERSION).where(Tables.GAME_VERSION.GAME_ID.eq(og.getGameId())).fetch();
        for (var gv : gvList)
        {
            List<DashboardTemplateRecord> dtList = getDSL().selectFrom(Tables.DASHBOARD_TEMPLATE)
                    .where(Tables.DASHBOARD_TEMPLATE.GAME_VERSION_ID.eq(gv.getId())).fetch();
            for (var dt : dtList)
            {
                // check that the template does not belong to another organization and is not private
                if (dt.getOrganizationGameId() == null && dt.getPrivate() == 0)
                {
                    addDashboardTemplateAccess(dt.getId(), Access.VIEW);
                }
            }
        }
    }

    private void addDashboardTemplateAccess(final Integer dashboardTemplateId, final Access access)
    {
        Access oldAccess = this.dashboardTemplateAccess.get(dashboardTemplateId);
        if (oldAccess == null)
            this.dashboardTemplateAccess.put(dashboardTemplateId, access);
        else if (oldAccess.ordinal() > access.ordinal())
            this.dashboardTemplateAccess.put(dashboardTemplateId, access);
    }

    public Set<Integer> getDashboardTemplateAccess(final Access access)
    {
        Set<Integer> ret = new HashSet<>();
        for (var entry : getDashboardTemplateAccess().entrySet())
        {
            if (entry.getValue().ordinal() <= access.ordinal())
                ret.add(entry.getKey());
        }
        return ret;
    }

    public Set<DashboardTemplateRecord> getDashboardTemplatePicklist(final Access access)
    {
        Set<DashboardTemplateRecord> ret = new HashSet<>();
        for (var entry : getDashboardTemplateAccess().entrySet())
        {
            if (entry.getValue().ordinal() <= access.ordinal())
            {
                var dashboardTemplate = SqlUtils.readRecordFromId(this, Tables.DASHBOARD_TEMPLATE, entry.getKey());
                ret.add(dashboardTemplate);
            }
        }
        return ret;
    }

    /* ************************ */
    /* DATABASE AND FORM ACCESS */
    /* ************************ */

    public void setEditForm(final WebForm editForm)
    {
        this.editForm = editForm;
    }

    public void setEditRecord(final UpdatableRecord<?> editRecord)
    {
        this.editRecord = editRecord;
    }

    @SuppressWarnings("unchecked")
    public <R extends org.jooq.UpdatableRecord<R>> int saveRecord(final HttpServletRequest request, final int recordId)
    {
        Table<R> table = (Table<R>) this.editRecord.getTable();
        R record = recordId == 0 ? getDSL().newRecord(table) : (R) this.editRecord;
        // getDSL().selectFrom(table).where(((TableField<R, Integer>) table.field("id")).eq(recordId)).fetchOne();
        String errors = ((TableForm) this.editForm).setFields(record, request, this);
        String backToMenu = "clickMenu('menu-" + getMenuChoice() + "')";
        if (errors.length() > 0)
        {
            System.err.println(errors);
            ModalWindowUtils.popup(this, "Error storing record (1)", errors, backToMenu);
            setError(true);
            return -1;
        }
        else
        {
            try
            {
                record.store();
            }
            catch (Exception exception)
            {
                System.err.println(exception.getMessage());
                System.err.println(record);
                ModalWindowUtils.popup(this, "Error storing record (2)", "<p>" + exception.getMessage() + "</p>", backToMenu);
                setError(true);
                return -1;
            }
        }
        setError(false);
        return Integer.valueOf(record.get("id").toString());
    }

    public <R extends org.jooq.UpdatableRecord<R>> void askDeleteRecord()
    {
        String backToMenu = "clickMenu('menu-" + getMenuChoice() + "')";
        ModalWindowUtils.make2ButtonModalWindow(this, "Delete " + this.editRecord.getTable().getName(),
                "<p>Delete " + this.editRecord.getTable().getName() + "?</p>", "DELETE",
                "clickRecordId('OK', " + Provider.getId(this.editRecord) + ")", "Cancel", backToMenu, backToMenu);
        setShowModalWindow(true);
    }

    public <R extends org.jooq.UpdatableRecord<R>> void deleteRecordOk()
    {
        String backToMenu = "clickMenu('menu-" + getMenuChoice() + "')";
        try
        {
            this.editRecord.delete();
            setError(false);
        }
        catch (Exception exception)
        {
            ModalWindowUtils.popup(this, "Error deleting record", "<p>" + exception.getMessage() + "</p>", backToMenu);
            setError(true);
        }
    }

    /* ******************* */
    /* GETTERS AND SETTERS */
    /* ******************* */

    public String getUsername()
    {
        return this.username;
    }

    public void setUsername(final String username)
    {
        this.username = username;
    }

    public UserRecord getUser()
    {
        return this.user;
    }

    public void setUser(final UserRecord user)
    {
        this.user = user;
    }

    public String getMenuChoice()
    {
        return this.menuChoice;
    }

    public void setMenuChoice(final String menuChoice)
    {
        this.menuChoice = menuChoice;
    }

    public String getTabChoice(final String menuChoice)
    {
        return this.tabChoice.get(menuChoice);
    }

    public void putTabChoice(final String menuChoice, final String tabChoice)
    {
        this.tabChoice.put(menuChoice, tabChoice);
    }

    public String getContent()
    {
        return this.content;
    }

    public void setContent(final String content)
    {
        this.content = content;
    }

    public boolean isShowModalWindow()
    {
        return this.showModalWindow;
    }

    public void setShowModalWindow(final boolean showModalWindow)
    {
        this.showModalWindow = showModalWindow;
    }

    public String getModalWindowHtml()
    {
        return this.modalWindowHtml;
    }

    public void setModalWindowHtml(final String modalWindowHtml)
    {
        this.modalWindowHtml = modalWindowHtml;
    }

    public ColumnSort getTableColumnSort()
    {
        return this.tableColumnSort.get(this.menuChoice + "#" + getTabChoice(this.menuChoice));
    }

    public void selectTableColumnSort(final String fieldName)
    {
        String fn = fieldName.toLowerCase().replace(' ', '-');
        String key = this.menuChoice + "#" + getTabChoice(this.menuChoice);
        ColumnSort oldColumnSort = getTableColumnSort();
        if (oldColumnSort != null && fn.equals(oldColumnSort.fieldName()))
            this.tableColumnSort.put(key, new ColumnSort(fn, !oldColumnSort.az()));
        else
            this.tableColumnSort.put(key, new ColumnSort(fn, true));
    }

    public FilterChoice getTabFilterChoice(final String tabChoice)
    {
        return this.tabFilterChoices.get(this.menuChoice + "#" + tabChoice);
    }

    public void setTabFilterChoice(final String tabChoice, final TableRecord<?> record, final String displayName)
    {
        this.tabFilterChoices.put(this.menuChoice + "#" + tabChoice, new FilterChoice(record, displayName));
    }

    public void clearTabFilterChoice(final String tabChoice)
    {
        this.tabFilterChoices.remove(this.menuChoice + "#" + tabChoice);
    }

    public boolean isError()
    {
        return this.error;
    }

    public void setError(final boolean error)
    {
        this.error = error;
    }

    public WebForm getEditForm()
    {
        return this.editForm;
    }

    public UpdatableRecord<?> getEditRecord()
    {
        return this.editRecord;
    }

}
