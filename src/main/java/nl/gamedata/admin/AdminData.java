package nl.gamedata.admin;

import java.util.ArrayList;
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
import nl.gamedata.common.CommonData;
import nl.gamedata.common.SqlUtils;
import nl.gamedata.data.Tables;
import nl.gamedata.data.tables.records.GameAccessRecord;
import nl.gamedata.data.tables.records.GameRecord;
import nl.gamedata.data.tables.records.GameRoleRecord;
import nl.gamedata.data.tables.records.GameSessionRecord;
import nl.gamedata.data.tables.records.OrganizationRoleRecord;
import nl.gamedata.data.tables.records.UserRecord;

public class AdminData extends CommonData
{
    /** The name of the user logged in to this session. If null, no user is logged in. */
    private String username;

    /** the User record (static during session). */
    private UserRecord user;

    /** the access rights of the user via organizations. */
    private List<OrganizationRoleRecord> organizationRoles = new ArrayList<>();

    /** the access right of the user via games. */
    private List<GameRoleRecord> gameRoles = new ArrayList<>();

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

    public boolean isSuperAdmin()
    {
        return getUser() == null ? false : getUser().getSuperAdmin() != 0;
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

    public void retrieveUserRoles()
    {
        DSLContext dslContext = DSL.using(getDataSource(), SQLDialect.MYSQL);
        this.organizationRoles = dslContext.selectFrom(Tables.ORGANIZATION_ROLE)
                .where(Tables.ORGANIZATION_ROLE.USER_ID.eq(this.user.getId())).fetch();
    }

    public void retrieveGameRoles()
    {
        DSLContext dslContext = DSL.using(getDataSource(), SQLDialect.MYSQL);
        this.gameRoles = dslContext.selectFrom(Tables.GAME_ROLE).where(Tables.GAME_ROLE.USER_ID.eq(this.user.getId())).fetch();
    }

    public Map<GameRecord, Boolean> getAdminAccessToGames()
    {
        Map<GameRecord, Boolean> ret = new HashMap<>();
        for (GameRoleRecord gameRole : this.gameRoles)
        {
            GameRecord game = SqlUtils.readRecordFromId(this, Tables.GAME, gameRole.getGameId());
            if (gameRole.getEdit() == 1)
                ret.put(game, true);
            else if (gameRole.getView() == 1)
                ret.put(game, false);
        }
        return ret;
    }

    public Map<GameRecord, Boolean> getOrganizationAccessToGames(final int organizationId)
    {
        Map<GameRecord, Boolean> ret = new HashMap<>();
        DSLContext dslContext = DSL.using(getDataSource(), SQLDialect.MYSQL);
        List<GameAccessRecord> gameAccessRecords =
                dslContext.selectFrom(Tables.GAME_ACCESS).where(Tables.GAME_ACCESS.ORGANIZATION_ID.eq(organizationId)).fetch();
        for (GameAccessRecord gameAccess : gameAccessRecords)
        {
            GameRecord game = SqlUtils.readRecordFromId(this, Tables.GAME, gameAccess.getGameId());
            ret.put(game, false);
        }
        return ret;
    }

    public Map<GameRecord, Boolean> getAccessToGames()
    {
        Map<GameRecord, Boolean> ret = new HashMap<>();
        ret.putAll(getAdminAccessToGames());
        for (OrganizationRoleRecord organizationRole : this.organizationRoles)
        {
            // TODO: org_admin all, session_admin dependent on games via sessions?
            ret.putAll(getOrganizationAccessToGames(organizationRole.getOrganizationId()));
        }
        return ret;
    }

    public Map<GameSessionRecord, Boolean> getOrganizationAccessToSessionIds(final int gameAccessId, final boolean admin)
    {
        Map<GameSessionRecord, Boolean> ret = new HashMap<>();
        DSLContext dslContext = DSL.using(getDataSource(), SQLDialect.MYSQL);
        List<GameSessionRecord> gameSessionRecords =
                dslContext.selectFrom(Tables.GAME_SESSION).where(Tables.GAME_SESSION.GAME_ACCESS_ID.eq(gameAccessId)).fetch();
        for (GameSessionRecord gameSesion : gameSessionRecords)
            ret.put(gameSesion, admin);
        return ret;
    }

    public Map<GameSessionRecord, Boolean> getAccessToGameSessionIds()
    {
        Map<GameSessionRecord, Boolean> ret = new HashMap<>();
        DSLContext dslContext = DSL.using(getDataSource(), SQLDialect.MYSQL);
        for (OrganizationRoleRecord organizationRole : this.organizationRoles)
        {
            if (organizationRole.getAdmin() == 1)
            {
                List<GameAccessRecord> gameAccessRecords = dslContext.selectFrom(Tables.GAME_ACCESS)
                        .where(Tables.GAME_ACCESS.ORGANIZATION_ID.eq(organizationRole.getOrganizationId())).fetch();
                for (GameAccessRecord gameAccess : gameAccessRecords)
                    ret.putAll(getOrganizationAccessToSessionIds(gameAccess.getId(), true));
            }
            // else if (organizationRole.getSessionGameAccessId() != null)
            // {
            // if (organizationRole.getSessionAdmin() == 1)
            // ret.putAll(getOrganizationAccessToSessionIds(organizationRole.getSessionGameAccessId(), true));
            // else if (organizationRole.getSessionViewer() == 1)
            // ret.putAll(getOrganizationAccessToSessionIds(organizationRole.getSessionGameAccessId(), false));
            // }
            // else if (organizationRole.getSessionGameSessionId() != null)
            // {
            // GameSessionRecord gameSession =
            // SqlUtils.readRecordFromId(this, Tables.GAME_SESSION, organizationRole.getSessionGameSessionId());
            // if (organizationRole.getSessionAdmin() == 1)
            // ret.put(gameSession, true);
            // else if (organizationRole.getSessionViewer() == 1)
            // ret.put(gameSession, false);
            // }
        }
        return ret;
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

    public List<OrganizationRoleRecord> getOrganizationRoles()
    {
        return this.organizationRoles;
    }

    public List<GameRoleRecord> getGameRoles()
    {
        return this.gameRoles;
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
