package nl.gamedata.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import nl.gamedata.common.CommonData;
import nl.gamedata.data.Tables;
import nl.gamedata.data.tables.records.GameAccessRecord;
import nl.gamedata.data.tables.records.GameRoleRecord;
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

    /* ================================= */
    /* FULLY DYNAMIC INFO IN THE SESSION */
    /* ================================= */

    /** Which menu has been chosen, to maintain persistence after a POST. */
    private String menuChoice = "";

    /** the header and breadcrumb as built by the admin servlet. */
    private String header = "";

    /** the sidebar as built by the admin servlet. */
    private String sidebar = "";

    /** the page content as built by the appropriate class. */
    private String content = "";

    /** Show popup window or not. */
    private boolean showModalWindow = false;

    /** Modal window content for popup. */
    private String modalWindowHtml = "";

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

    public boolean isSuperAdmin()
    {
        return getUser() == null ? false : getUser().getSuperAdmin() != 0;
    }

    public boolean isShowModalWindow()
    {
        return this.showModalWindow;
    }

    public void setShowModalWindow(final boolean showModalWindow)
    {
        this.showModalWindow = showModalWindow;
    }

    public String getMenuChoice()
    {
        return this.menuChoice;
    }

    public void setMenuChoice(final String menuChoice)
    {
        this.menuChoice = menuChoice;
    }

    public String getSidebar()
    {
        return this.sidebar;
    }

    public void setSidebar(final String sidebar)
    {
        this.sidebar = sidebar;
    }

    public String getHeader()
    {
        return this.header;
    }

    public void setHeader(final String header)
    {
        this.header = header;
    }

    public String getContent()
    {
        return this.content;
    }

    public void setContent(final String content)
    {
        this.content = content;
    }

    public String getModalWindowHtml()
    {
        return this.modalWindowHtml;
    }

    public void setModalWindowHtml(final String modalClientWindowHtml)
    {
        this.modalWindowHtml = modalClientWindowHtml;
    }

    public <R extends org.jooq.UpdatableRecord<R>> int getId(final R record)
    {
        return IdProvider.getId(record);
    }

    public void retrieveOrganizationRoles()
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

    public Map<Integer, Boolean> getAdminAccessToGameIds()
    {
        Map<Integer, Boolean> ret = new HashMap<>();
        for (GameRoleRecord gameRole : this.gameRoles)
        {
            if (gameRole.getGameAdmin() == 1)
                ret.put(gameRole.getGameId(), true);
            else if (gameRole.getGameViewer() == 1)
                ret.put(gameRole.getGameId(), false);
        }
        return ret;
    }

    public Map<Integer, Boolean> getOrganizationAccessToGameIds(final int organizationId)
    {
        Map<Integer, Boolean> ret = new HashMap<>();
        DSLContext dslContext = DSL.using(getDataSource(), SQLDialect.MYSQL);
        List<GameAccessRecord> gameAccessRecords =
                dslContext.selectFrom(Tables.GAME_ACCESS).where(Tables.GAME_ACCESS.ORGANIZATION_ID.eq(organizationId)).fetch();
        for (GameAccessRecord gameAccess : gameAccessRecords)
            ret.put(gameAccess.getGameId(), false);
        return ret;
    }

    public Map<Integer, Boolean> getTotalAccessToGames()
    {
        Map<Integer, Boolean> ret = new HashMap<>();
        ret.putAll(getAdminAccessToGameIds());
        for (OrganizationRoleRecord organizationRole : this.organizationRoles)
        {
            ret.putAll(getOrganizationAccessToGameIds(organizationRole.getOrganizationId()));
        }
        return ret;
    }
}
