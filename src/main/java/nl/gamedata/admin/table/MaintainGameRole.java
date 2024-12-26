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
import nl.gamedata.data.tables.records.GameRoleRecord;

/**
 * MaintainGameRole takes care of the game role screen.
 * <p>
 * Copyright (c) 2024-2024 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="https://github.com/averbraeck/gamedata-admin/LICENSE">GameData project License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public class MaintainGameRole
{
    public static void table(final AdminData data, final HttpServletRequest request, final String menuChoice)
    {
        StringBuilder s = new StringBuilder();
        boolean newButton = data.isSuperAdmin() || data.isGameAdmin();
        AdminTable.tableStart(s, "Game Roles", new String[] {"Game", "User", "Edit", "View"}, newButton, "Game", true);
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        List<Record> gameRoleList =
                dslContext.selectFrom(Tables.GAME_ROLE.join(Tables.GAME).on(Tables.GAME_ROLE.GAME_ID.eq(Tables.GAME.ID))
                        .join(Tables.USER).on(Tables.GAME_ROLE.USER_ID.eq(Tables.USER.ID))).fetch();
        for (var gameRole : gameRoleList)
        {
            for (Integer gameId : data.getGameAccess().keySet())
            {
                if (gameId.equals(gameRole.getValue(Tables.GAME.ID)))
                {
                    int id = gameRole.getValue(Tables.GAME_ROLE.ID);
                    String game = gameRole.getValue(Tables.GAME.CODE);
                    String user = gameRole.getValue(Tables.USER.NAME);
                    String edit = gameRole.getValue(Tables.GAME_ROLE.EDIT) == 0 ? "N" : "Y";
                    String view = gameRole.getValue(Tables.GAME_ROLE.VIEW) == 0 ? "N" : "Y";
                    AdminTable.tableRow(s, id, new String[] {game, user, edit, view});
                    break;
                }
            }
        }
        AdminTable.tableEnd(s);
        data.setContent(s.toString());
    }

    public static void edit(final AdminData data, final HttpServletRequest request, final String click, final int recordId)
    {
        GameRoleRecord gameRole =
                recordId == 0 ? Tables.GAME_ROLE.newRecord() : SqlUtils.readRecordFromId(data, Tables.GAME_ROLE, recordId);
        data.setEditRecord(gameRole);
        TableForm form = new TableForm(data);
        form.startForm();
        form.setHeader("Game Role", click, recordId);
        form.addEntry(new TableEntryPickRecord(Tables.GAME_ROLE.GAME_ID, gameRole)
                .setPickTable(data, data.getGamePicklist(Access.EDIT), Tables.GAME.ID, Tables.GAME.CODE).setLabel("Game"));
        form.addEntry(new TableEntryPickRecord(Tables.GAME_ROLE.USER_ID, gameRole)
                .setPickTable(data, Tables.USER, Tables.USER.ID, Tables.USER.NAME).setLabel("User"));
        form.addEntry(new TableEntryBoolean(Tables.GAME_ROLE.EDIT, gameRole));
        form.addEntry(new TableEntryBoolean(Tables.GAME_ROLE.VIEW, gameRole));
        form.endForm();
        data.setContent(form.process());
    }
}
