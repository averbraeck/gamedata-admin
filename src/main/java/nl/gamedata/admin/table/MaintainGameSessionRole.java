package nl.gamedata.admin.table;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import nl.gamedata.admin.AdminData;
import nl.gamedata.admin.AdminTable;
import nl.gamedata.admin.form.table.TableEntryBoolean;
import nl.gamedata.admin.form.table.TableEntryPickRecord;
import nl.gamedata.admin.form.table.TableForm;
import nl.gamedata.common.Access;
import nl.gamedata.common.SqlUtils;
import nl.gamedata.data.Tables;
import nl.gamedata.data.tables.records.GameSessionRoleRecord;

/**
 * MaintainGameSessionRole takes care of the gameSession role screen.
 * <p>
 * Copyright (c) 2024-2024 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="https://github.com/averbraeck/gameSessiondata-admin/LICENSE">GameSessionData project
 * License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public class MaintainGameSessionRole
{
    public static void table(final AdminData data, final HttpServletRequest request, final String menuChoice)
    {
        StringBuilder s = new StringBuilder();
        boolean newButton = data.isSuperAdmin() || data.hasOrganizationAccess(Access.ADMIN);
        AdminTable.tableStart(s, "Game Session Roles", new String[] {"Game Session", "User", "Edit", "View"}, newButton,
                "Game Session", true);
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        List<Record> gameSessionRoleList = dslContext.selectFrom(Tables.GAME_SESSION_ROLE.join(Tables.GAME_SESSION)
                .on(Tables.GAME_SESSION_ROLE.GAME_SESSION_ID.eq(Tables.GAME_SESSION.ID)).join(Tables.USER)
                .on(Tables.GAME_SESSION_ROLE.USER_ID.eq(Tables.USER.ID))).fetch();
        for (var gameSessionRole : gameSessionRoleList)
        {
            for (Integer gsId : data.getGameSessionAccess().keySet())
            {
                if (gsId.equals(gameSessionRole.getValue(Tables.GAME_SESSION.ID)))
                {
                    int id = gameSessionRole.getValue(Tables.GAME_SESSION_ROLE.ID);
                    String template = gameSessionRole.getValue(Tables.GAME_SESSION.NAME);
                    String user = gameSessionRole.getValue(Tables.USER.NAME);
                    String edit = gameSessionRole.getValue(Tables.GAME_SESSION_ROLE.EDIT) == 0 ? "N" : "Y";
                    String view = gameSessionRole.getValue(Tables.GAME_SESSION_ROLE.VIEW) == 0 ? "N" : "Y";
                    AdminTable.tableRow(s, id, new String[] {template, user, edit, view});
                    break;
                }
            }
        }
        AdminTable.tableEnd(s);
        data.setContent(s.toString());
    }

    public static void edit(final AdminData data, final HttpServletRequest request, final String click, final int recordId)
    {
        GameSessionRoleRecord gameSessionRole = recordId == 0 ? Tables.GAME_SESSION_ROLE.newRecord()
                : SqlUtils.readRecordFromId(data, Tables.GAME_SESSION_ROLE, recordId);
        data.setEditRecord(gameSessionRole);
        TableForm form = new TableForm(data);
        form.startForm();
        form.setHeader("Game Session Role", click, recordId);
        form.addEntry(new TableEntryPickRecord(Tables.GAME_SESSION_ROLE.GAME_SESSION_ID, gameSessionRole)
                .setPickTable(data, data.getGameSessionPicklist(Access.EDIT), Tables.GAME_SESSION.ID, Tables.GAME_SESSION.NAME)
                .setLabel("Game Session"));
        form.addEntry(new TableEntryPickRecord(Tables.GAME_SESSION_ROLE.USER_ID, gameSessionRole)
                .setPickTable(data, Tables.USER, Tables.USER.ID, Tables.USER.NAME).setLabel("User"));
        form.addEntry(new TableEntryBoolean(Tables.GAME_SESSION_ROLE.EDIT, gameSessionRole));
        form.addEntry(new TableEntryBoolean(Tables.GAME_SESSION_ROLE.VIEW, gameSessionRole));
        form.endForm();
        data.setContent(form.process());
    }
}
