package nl.gamedata.admin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.Table;
import org.jooq.TableRecord;
import org.jooq.UpdatableRecord;
import org.jooq.impl.DSL;

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
    private Map<OrganizationRecord, Access> organizationRoles = null;

    /** the access right of the user via GameRole. Lazy loading. */
    private Map<GameRecord, Access> gameRoles = null;

    /** the access right of the user via OrganizationGameRole. Lazy loading. */
    private Map<OrganizationGameRecord, Access> organizationGameRoles = null;

    /** the access right of the user via GameSessionTole. Lazy loading. */
    private Map<GameSessionRecord, Access> gameSessionRoles = null;

    /** the access right of the user via DashboardRole. Lazy loading. */
    private Map<DashboardTemplateRecord, Access> dashboardRoles = null;

    /* ================================================ */
    /* PERSISTENT DATA ABOUT CHOICES MADE ON THE SCREEN */
    /* ================================================ */

    /** Which menu has been chosen, to maintain persistence after a POST. */
    private String menuChoice = "";

    /** Which tab has been chosen, to maintain persistence after a POST. */
    private Map<String, String> tabChoice = new HashMap<>();

    /** Map that links the tab name to a potential filter choice (record and display name) in the navbar. */
    private Map<String, FilterChoice> tabFilterChoices = new HashMap<>();

    /** The sorting order of columns in the tables. The map is from table (tab) to column header to A-Z / Z-A */
    private Map<String, ColumnSort> tableColumnSort = new HashMap<>();

    /** the page content as built by the appropriate class. */
    private String content = "";

    /** Show popup window or not. */
    private boolean showModalWindow = false;

    /** Modal window content for popup. */
    private String modalWindowHtml = "";

    /** The form that is currently being used. */
    private TableForm editForm = null;

    /** The record that is currently being edited. */
    private UpdatableRecord<?> editRecord = null;

    /** An error occurred during save, delete, or cancel. */
    private boolean error = false;

    /** Record that has the field name and the direction of sorting; A-Z is true, Z-A is false. */
    public record ColumnSort(String fieldName, boolean az)
    {
    }

    /** A filter choice (record and display name), used in the filtering of records in he navbar. */
    public record FilterChoice(TableRecord<?> record, String name)
    {
    }

    /* =================================== */
    /* GENERIC METHODS FOR THE DATA OBJECT */
    /* =================================== */

    public AdminData()
    {
        this.tabChoice.put("admin-panel", "");
        this.tabChoice.put("organization", "organization");
        this.tabChoice.put("user", "user");
        this.tabChoice.put("game", "game");
        this.tabChoice.put("game-control", "game");
        this.tabChoice.put("game-session", "game");
        this.tabChoice.put("data-session", "game");
        this.tabChoice.put("data-player", "game");
        this.tabChoice.put("data-group", "game");
        this.tabChoice.put("settings", "");
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

    public Map<OrganizationRecord, Access> getOrganizationRoles()
    {
        if (this.organizationRoles == null)
        {
            this.organizationRoles = new HashMap<>();
            DSLContext dslContext = DSL.using(getDataSource(), SQLDialect.MYSQL);
            List<OrganizationRoleRecord> orList = dslContext.selectFrom(Tables.ORGANIZATION_ROLE)
                    .where(Tables.ORGANIZATION_ROLE.USER_ID.eq(this.user.getId())).fetch();
            for (var or : orList)
            {
                OrganizationRecord organization = SqlUtils.readRecordFromId(this, Tables.ORGANIZATION, or.getOrganizationId());
                if (or.getAdmin() != 0)
                    this.organizationRoles.put(organization, Access.ADMIN);
                else if (or.getEdit() != 0)
                    this.organizationRoles.put(organization, Access.EDIT);
                else if (or.getView() != 0)
                    this.organizationRoles.put(organization, Access.VIEW);
            }
        }
        return this.organizationRoles;
    }

    public Map<GameRecord, Access> getGameRoles()
    {
        if (this.gameRoles == null)
        {
            this.gameRoles = new HashMap<>();
            DSLContext dslContext = DSL.using(getDataSource(), SQLDialect.MYSQL);

            // direct game roles
            List<GameRoleRecord> grList =
                    dslContext.selectFrom(Tables.GAME_ROLE).where(Tables.GAME_ROLE.USER_ID.eq(this.user.getId())).fetch();
            for (var gr : grList)
            {
                GameRecord game = SqlUtils.readRecordFromId(this, Tables.GAME, gr.getGameId());
                if (gr.getEdit() != 0)
                    addGameRole(game, Access.EDIT);
                else if (gr.getView() != 0)
                    addGameRole(game, Access.VIEW);
            }

            // indirect game roles via game_access for all organizations where user is a member
            List<OrganizationRoleRecord> orList = dslContext.selectFrom(Tables.ORGANIZATION_ROLE)
                    .where(Tables.ORGANIZATION_ROLE.USER_ID.eq(this.user.getId())).fetch();
            for (var or : orList)
            {
                List<OrganizationGameRecord> ogList = dslContext.selectFrom(Tables.ORGANIZATION_GAME)
                        .where(Tables.ORGANIZATION_GAME.ORGANIZATION_ID.eq(or.getOrganizationId())).fetch();
                for (var og : ogList)
                {
                    GameRecord game = SqlUtils.readRecordFromId(this, Tables.GAME, og.getGameId());
                    if (or.getEdit() != 0 || or.getAdmin() != 0 || or.getView() != 0)
                        addGameRole(game, Access.VIEW);
                }
            }

            // indirect game roles via direct game_access role
            List<OrganizationGameRoleRecord> ogrList = dslContext.selectFrom(Tables.ORGANIZATION_GAME_ROLE)
                    .where(Tables.ORGANIZATION_GAME_ROLE.USER_ID.eq(this.user.getId())).fetch();
            for (var ogr : ogrList)
            {
                OrganizationGameRecord ga = SqlUtils.readRecordFromId(this, Tables.ORGANIZATION_GAME, ogr.getOrganizationGameId());
                GameRecord game = SqlUtils.readRecordFromId(this, Tables.GAME, ga.getGameId());
                if (ogr.getEdit() != 0 || ogr.getView() != 0)
                    addGameRole(game, Access.VIEW);
            }

            // indirect game roles via session_role
            List<GameSessionRoleRecord> gsrList =
                    dslContext.selectFrom(Tables.GAME_SESSION_ROLE).where(Tables.GAME_SESSION_ROLE.USER_ID.eq(this.user.getId())).fetch();
            for (var gsr : gsrList)
            {
                GameSessionRecord gs = SqlUtils.readRecordFromId(this, Tables.GAME_SESSION, gsr.getGameSessionId());
                GameVersionRecord gv = SqlUtils.readRecordFromId(this, Tables.GAME_VERSION, gs.getGameVersionId());
                GameRecord game = SqlUtils.readRecordFromId(this, Tables.GAME, gv.getGameId());
                if (gsr.getEdit() != 0 || gsr.getView() != 0)
                    addGameRole(game, Access.VIEW);
            }

            // indirect game roles via dashboard_role
            List<DashboardRoleRecord> drList = dslContext.selectFrom(Tables.DASHBOARD_ROLE)
                    .where(Tables.DASHBOARD_ROLE.USER_ID.eq(this.user.getId())).fetch();
            for (var dr : drList)
            {
                DashboardTemplateRecord dt =
                        SqlUtils.readRecordFromId(this, Tables.DASHBOARD_TEMPLATE, dr.getDashboardTemplateId());
                GameVersionRecord gv = SqlUtils.readRecordFromId(this, Tables.GAME_VERSION, dt.getGameVersionId());
                GameRecord game = SqlUtils.readRecordFromId(this, Tables.GAME, gv.getGameId());
                if (dr.getEdit() != 0 || dr.getView() != 0)
                    addGameRole(game, Access.VIEW);
            }
        }
        return this.gameRoles;
    }

    private void addGameRole(final GameRecord game, final Access access)
    {
        Access oldAccess = this.gameRoles.get(game);
        if (oldAccess == null)
            this.gameRoles.put(game, access);
        else if (oldAccess.ordinal() > access.ordinal())
            this.gameRoles.put(game, access);
    }

    public Map<OrganizationGameRecord, Access> getOrganizationGameRoles()
    {
        if (this.organizationGameRoles == null)
        {
            this.organizationGameRoles = new HashMap<>();
            DSLContext dslContext = DSL.using(getDataSource(), SQLDialect.MYSQL);

            // direct game access roles
            List<OrganizationGameRoleRecord> ogrList = dslContext.selectFrom(Tables.ORGANIZATION_GAME_ROLE)
                    .where(Tables.ORGANIZATION_GAME_ROLE.USER_ID.eq(this.user.getId())).fetch();
            for (var ogr : ogrList)
            {
                OrganizationGameRecord organizationGame = SqlUtils.readRecordFromId(this, Tables.ORGANIZATION_GAME, ogr.getOrganizationGameId());
                if (ogr.getEdit() != 0)
                    addOrganizationGameRole(organizationGame, Access.EDIT);
                else if (ogr.getView() != 0)
                    addOrganizationGameRole(organizationGame, Access.VIEW);
            }

            // indirect game_access roles for all organizations where user is a member
            List<OrganizationRoleRecord> orList = dslContext.selectFrom(Tables.ORGANIZATION_ROLE)
                    .where(Tables.ORGANIZATION_ROLE.USER_ID.eq(this.user.getId())).fetch();
            for (var or : orList)
            {
                List<OrganizationGameRecord> ogList = dslContext.selectFrom(Tables.ORGANIZATION_GAME)
                        .where(Tables.ORGANIZATION_GAME.ORGANIZATION_ID.eq(or.getOrganizationId())).fetch();
                for (var og : ogList)
                {
                    if (or.getEdit() != 0 || or.getAdmin() != 0)
                        addOrganizationGameRole(og, Access.EDIT);
                    if (or.getView() != 0)
                        addOrganizationGameRole(og, Access.VIEW);
                }
            }
        }
        return this.organizationGameRoles;
    }

    private void addOrganizationGameRole(final OrganizationGameRecord organizationGame, final Access access)
    {
        Access oldAccess = this.organizationGameRoles.get(organizationGame);
        if (oldAccess == null)
            this.organizationGameRoles.put(organizationGame, access);
        else if (oldAccess.ordinal() > access.ordinal())
            this.organizationGameRoles.put(organizationGame, access);
    }

    public Map<GameSessionRecord, Access> getGameSessionRoles()
    {
        if (this.gameSessionRoles == null)
        {
            this.gameSessionRoles = new HashMap<>();
            DSLContext dslContext = DSL.using(getDataSource(), SQLDialect.MYSQL);

            // direct game session roles
            List<GameSessionRoleRecord> gsrList =
                    dslContext.selectFrom(Tables.GAME_SESSION_ROLE).where(Tables.GAME_SESSION_ROLE.USER_ID.eq(this.user.getId())).fetch();
            for (var gsr : gsrList)
            {
                GameSessionRecord gameSession = SqlUtils.readRecordFromId(this, Tables.GAME_SESSION, gsr.getGameSessionId());
                if (gsr.getEdit() != 0)
                    addGameGameSessionRole(gameSession, Access.EDIT);
                else if (gsr.getView() != 0)
                    addGameGameSessionRole(gameSession, Access.VIEW);
            }

            // indirect game session roles via game_access roles
            List<OrganizationGameRoleRecord> ogrList = dslContext.selectFrom(Tables.ORGANIZATION_GAME_ROLE)
                    .where(Tables.ORGANIZATION_GAME_ROLE.USER_ID.eq(this.user.getId())).fetch();
            for (var ogr : ogrList)
            {
                List<GameSessionRecord> gsList = dslContext.selectFrom(Tables.GAME_SESSION)
                        .where(Tables.GAME_SESSION.ORGANIZATION_GAME_ID.eq(ogr.getOrganizationGameId())).fetch();
                for (var gs : gsList)
                {
                    if (ogr.getEdit() != 0)
                        addGameGameSessionRole(gs, Access.EDIT);
                    else if (ogr.getView() != 0)
                        addGameGameSessionRole(gs, Access.VIEW);
                }
            }

            // indirect game session roles for all organizations where user is a member
            List<OrganizationRoleRecord> orList = dslContext.selectFrom(Tables.ORGANIZATION_ROLE)
                    .where(Tables.ORGANIZATION_ROLE.USER_ID.eq(this.user.getId())).fetch();
            for (var or : orList)
            {
                List<OrganizationGameRecord> ogList = dslContext.selectFrom(Tables.ORGANIZATION_GAME)
                        .where(Tables.ORGANIZATION_GAME.ORGANIZATION_ID.eq(or.getOrganizationId())).fetch();
                for (var og : ogList)
                {
                    List<GameSessionRecord> gsList = dslContext.selectFrom(Tables.GAME_SESSION)
                            .where(Tables.GAME_SESSION.ORGANIZATION_GAME_ID.eq(og.getId())).fetch();
                    for (var gs : gsList)
                    {
                        if (or.getEdit() != 0)
                            addGameGameSessionRole(gs, Access.EDIT);
                        else if (or.getView() != 0)
                            addGameGameSessionRole(gs, Access.VIEW);
                    }
                }
            }
        }
        return this.gameSessionRoles;
    }

    private void addGameGameSessionRole(final GameSessionRecord gameSession, final Access access)
    {
        Access oldAccess = this.gameSessionRoles.get(gameSession);
        if (oldAccess == null)
            this.gameSessionRoles.put(gameSession, access);
        else if (oldAccess.ordinal() > access.ordinal())
            this.gameSessionRoles.put(gameSession, access);
    }

    public Map<DashboardTemplateRecord, Access> getDashboardRoles()
    {
        if (this.dashboardRoles == null)
        {
            this.dashboardRoles = new HashMap<>();
            DSLContext dslContext = DSL.using(getDataSource(), SQLDialect.MYSQL);

            // direct dashboard roles
            List<DashboardRoleRecord> drList = dslContext.selectFrom(Tables.DASHBOARD_ROLE)
                    .where(Tables.DASHBOARD_ROLE.USER_ID.eq(this.user.getId())).fetch();
            for (var dr : drList)
            {
                DashboardTemplateRecord dt =
                        SqlUtils.readRecordFromId(this, Tables.DASHBOARD_TEMPLATE, dr.getDashboardTemplateId());
                if (dr.getEdit() != 0)
                    addDashboardRole(dt, Access.EDIT);
                else if (dr.getView() != 0)
                    addDashboardRole(dt, Access.VIEW);
            }

            // indirect dashboard roles via game_access roles
            List<OrganizationGameRoleRecord> ogrList = dslContext.selectFrom(Tables.ORGANIZATION_GAME_ROLE)
                    .where(Tables.ORGANIZATION_GAME_ROLE.USER_ID.eq(this.user.getId())).fetch();
            for (var ogr : ogrList)
            {
                List<DashboardTemplateRecord> dtList = dslContext.selectFrom(Tables.DASHBOARD_TEMPLATE)
                        .where(Tables.DASHBOARD_TEMPLATE.ORGANIZATION_GAME_ID.eq(ogr.getOrganizationGameId())).fetch();
                for (var dt : dtList)
                {
                    if (ogr.getEdit() != 0)
                        addDashboardRole(dt, Access.EDIT);
                    else if (ogr.getView() != 0)
                        addDashboardRole(dt, Access.VIEW);
                }

                // add the public templates for the accessible games as well (view-only)
                OrganizationGameRecord og = SqlUtils.readRecordFromId(this, Tables.ORGANIZATION_GAME, ogr.getOrganizationGameId());
                addDashboardRole(og);
            }

            // indirect dashboard roles for all organizations where user is a member
            List<OrganizationRoleRecord> orList = dslContext.selectFrom(Tables.ORGANIZATION_ROLE)
                    .where(Tables.ORGANIZATION_ROLE.USER_ID.eq(this.user.getId())).fetch();
            for (var or : orList)
            {
                List<OrganizationGameRecord> ogList = dslContext.selectFrom(Tables.ORGANIZATION_GAME)
                        .where(Tables.ORGANIZATION_GAME.ORGANIZATION_ID.eq(or.getOrganizationId())).fetch();
                for (var og : ogList)
                {
                    List<DashboardTemplateRecord> dtList = dslContext.selectFrom(Tables.DASHBOARD_TEMPLATE)
                            .where(Tables.DASHBOARD_TEMPLATE.ORGANIZATION_GAME_ID.eq(og.getId())).fetch();
                    for (var dt : dtList)
                    {
                        if (or.getEdit() != 0)
                            addDashboardRole(dt, Access.EDIT);
                        else if (or.getView() != 0)
                            addDashboardRole(dt, Access.VIEW);
                    }

                    // add the public templates for the accessible games as well (view-only)
                    addDashboardRole(og);
                }
            }
        }
        return this.dashboardRoles;
    }

    private void addDashboardRole(final OrganizationGameRecord og)
    {
        DSLContext dslContext = DSL.using(getDataSource(), SQLDialect.MYSQL);
        List<GameVersionRecord> gvList =
                dslContext.selectFrom(Tables.GAME_VERSION).where(Tables.GAME_VERSION.GAME_ID.eq(og.getGameId())).fetch();
        for (var gv : gvList)
        {
            List<DashboardTemplateRecord> dtList = dslContext.selectFrom(Tables.DASHBOARD_TEMPLATE)
                    .where(Tables.DASHBOARD_TEMPLATE.GAME_VERSION_ID.eq(gv.getId())).fetch();
            for (var dt : dtList)
            {
                // check that the template does not belong to another organization and is not private
                if (dt.getOrganizationGameId() == null && dt.getPrivate() == 0)
                {
                    addDashboardRole(dt, Access.VIEW);
                }
            }
        }
    }

    private void addDashboardRole(final DashboardTemplateRecord gameSession, final Access access)
    {
        Access oldAccess = this.dashboardRoles.get(gameSession);
        if (oldAccess == null)
            this.dashboardRoles.put(gameSession, access);
        else if (oldAccess.ordinal() > access.ordinal())
            this.dashboardRoles.put(gameSession, access);
    }

    /* ************************ */
    /* DATABASE AND FORM ACCESS */
    /* ************************ */

    public void setEditForm(final TableForm editForm)
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
        DSLContext dslContext = DSL.using(getDataSource(), SQLDialect.MYSQL);
        R record = recordId == 0 ? dslContext.newRecord(table) : (R) this.editRecord;
        // dslContext.selectFrom(table).where(((TableField<R, Integer>) table.field("id")).eq(recordId)).fetchOne();
        String errors = this.editForm.setFields(record, request, this);
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

    public Map<String, ColumnSort> getTableColumnSort()
    {
        return this.tableColumnSort;
    }

    public Map<String, FilterChoice> getTabFilterChoices()
    {
        return this.tabFilterChoices;
    }

    public boolean isError()
    {
        return this.error;
    }

    public void setError(final boolean error)
    {
        this.error = error;
    }

    public TableForm getEditForm()
    {
        return this.editForm;
    }

    public UpdatableRecord<?> getEditRecord()
    {
        return this.editRecord;
    }

}
