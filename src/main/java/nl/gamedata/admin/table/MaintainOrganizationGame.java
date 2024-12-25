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
import nl.gamedata.admin.form.table.TableEntryString;
import nl.gamedata.admin.form.table.TableForm;
import nl.gamedata.common.Access;
import nl.gamedata.common.SqlUtils;
import nl.gamedata.data.Tables;
import nl.gamedata.data.tables.records.OrganizationGameRecord;

/**
 * MaintainOrganizationGame takes care of the game access screen.
 * <p>
 * Copyright (c) 2024-2024 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="https://github.com/averbraeck/gamedata-admin/LICENSE">GameData project License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public class MaintainOrganizationGame
{
    public static void table(final AdminData data, final HttpServletRequest request, final String menuChoice)
    {
        StringBuilder s = new StringBuilder();
        AdminTable.tableStart(s, "Game Access for Organizations", new String[] {"Name", "Organization", "Game"}, true, "Name",
                true);
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        List<Record> ogList = dslContext.selectFrom(Tables.ORGANIZATION_GAME.join(Tables.ORGANIZATION)
                .on(Tables.ORGANIZATION_GAME.ORGANIZATION_ID.eq(Tables.ORGANIZATION.ID)).join(Tables.GAME)
                .on(Tables.ORGANIZATION_GAME.GAME_ID.eq(Tables.GAME.ID))).fetch();
        for (var og : ogList)
        {
            for (OrganizationGameRecord ogAccess : data.getOrganizationGameRoles().keySet())
            {
                if (ogAccess.getId().equals(og.getValue(Tables.ORGANIZATION_GAME.ID)))
                {
                    int id = og.getValue(Tables.ORGANIZATION_GAME.ID);
                    String name = og.getValue(Tables.ORGANIZATION_GAME.NAME);
                    String org = og.getValue(Tables.ORGANIZATION.CODE);
                    String game = og.getValue(Tables.GAME.CODE);
                    AdminTable.tableRow(s, id, new String[] {name, org, game});
                    break;
                }
            }
        }
        AdminTable.tableEnd(s);
        data.setContent(s.toString());
    }

    public static void edit(final AdminData data, final HttpServletRequest request, final String click, final int recordId)
    {
        OrganizationGameRecord og = recordId == 0 ? Tables.ORGANIZATION_GAME.newRecord()
                : SqlUtils.readRecordFromId(data, Tables.ORGANIZATION_GAME, recordId);
        data.setEditRecord(og);
        TableForm form = new TableForm(data);
        form.startForm();
        form.setHeader("Game Access for Organization", click, recordId);
        form.addEntry(new TableEntryString(Tables.ORGANIZATION_GAME.NAME, og));
        form.addEntry(new TableEntryPickRecord(Tables.ORGANIZATION_GAME.ORGANIZATION_ID, og).setPickTable(data,
                data.getOrganizationPicklist(Access.EDIT), Tables.ORGANIZATION.ID, Tables.ORGANIZATION.CODE));
        form.addEntry(new TableEntryPickRecord(Tables.ORGANIZATION_GAME.GAME_ID, og).setPickTable(data,
                data.getGamePicklist(Access.VIEW), Tables.GAME.ID, Tables.GAME.CODE));
        form.addEntry(new TableEntryBoolean(Tables.ORGANIZATION_GAME.TOKEN_FORCED, og));
        form.addEntry(new TableEntryBoolean(Tables.ORGANIZATION_GAME.ANONYMOUS_SESSIONS, og));
        form.endForm();
        data.setContent(form.process());
    }
}
