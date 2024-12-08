package nl.gamedata.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import lombok.Getter;
import lombok.Setter;
import nl.gamedata.common.CommonData;
import nl.gamedata.common.SqlUtils;
import nl.gamedata.data.Tables;
import nl.gamedata.data.tables.records.GameAccessRecord;
import nl.gamedata.data.tables.records.GameRecord;
import nl.gamedata.data.tables.records.GameRoleRecord;
import nl.gamedata.data.tables.records.GameSessionRecord;
import nl.gamedata.data.tables.records.UserRecord;
import nl.gamedata.data.tables.records.UserRoleRecord;

public class AdminData extends CommonData
{
    /** The name of the user logged in to this session. If null, no user is logged in. */
    @Getter
    @Setter
    private String username;

    /** the User record (static during session). */
    @Getter
    @Setter
    private UserRecord user;

    /** the access rights of the user via organizations. */
    @Getter
    private List<UserRoleRecord> userRoles = new ArrayList<>();

    /** the access right of the user via games. */
    @Getter
    private List<GameRoleRecord> gameRoles = new ArrayList<>();

    /* ================================================ */
    /* PERSISTENT DATA ABOUT CHOICES MADE ON THE SCREEN */
    /* ================================================ */

    /** Which menu has been chosen, to maintain persistence after a POST. */
    @Getter
    @Setter
    private String menuChoice = "";

    /** Which tab has been chosen, to maintain persistence after a POST. */
    @Getter
    @Setter
    private String tabChoice = "";

    /** the page content as built by the appropriate class. */
    @Getter
    @Setter
    private String content = "";

    /** Show popup window or not. */
    @Getter
    @Setter
    private boolean showModalWindow = false;

    /** Modal window content for popup. */
    @Getter
    @Setter
    private String modalWindowHtml = "";

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

    public <R extends org.jooq.UpdatableRecord<R>> int getId(final R record)
    {
        return Provider.getId(record);
    }

    public void retrieveUserRoles()
    {
        DSLContext dslContext = DSL.using(getDataSource(), SQLDialect.MYSQL);
        this.userRoles = dslContext.selectFrom(Tables.USER_ROLE).where(Tables.USER_ROLE.USER_ID.eq(this.user.getId())).fetch();
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
            if (gameRole.getGameAdmin() == 1)
                ret.put(game, true);
            else if (gameRole.getGameViewer() == 1)
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
        for (UserRoleRecord organizationRole : this.userRoles)
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
        for (UserRoleRecord organizationRole : this.userRoles)
        {
            if (organizationRole.getOrganizationAdmin() == 1)
            {
                List<GameAccessRecord> gameAccessRecords = dslContext.selectFrom(Tables.GAME_ACCESS)
                        .where(Tables.GAME_ACCESS.ORGANIZATION_ID.eq(organizationRole.getOrganizationId())).fetch();
                for (GameAccessRecord gameAccess : gameAccessRecords)
                    ret.putAll(getOrganizationAccessToSessionIds(gameAccess.getId(), true));
            }
            else if (organizationRole.getSessionGameAccessId() != null)
            {
                if (organizationRole.getSessionAdmin() == 1)
                    ret.putAll(getOrganizationAccessToSessionIds(organizationRole.getSessionGameAccessId(), true));
                else if (organizationRole.getSessionViewer() == 1)
                    ret.putAll(getOrganizationAccessToSessionIds(organizationRole.getSessionGameAccessId(), false));
            }
            else if (organizationRole.getSessionGameSessionId() != null)
            {
                GameSessionRecord gameSession =
                        SqlUtils.readRecordFromId(this, Tables.GAME_SESSION, organizationRole.getSessionGameSessionId());
                if (organizationRole.getSessionAdmin() == 1)
                    ret.put(gameSession, true);
                else if (organizationRole.getSessionViewer() == 1)
                    ret.put(gameSession, false);
            }
        }
        return ret;
    }
}
