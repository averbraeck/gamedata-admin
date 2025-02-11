package nl.gamedata.admin.table;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.jooq.Record;

import nl.gamedata.admin.AdminData;
import nl.gamedata.admin.AdminTable;
import nl.gamedata.admin.form.table.TableEntryBoolean;
import nl.gamedata.admin.form.table.TableEntryPickRecord;
import nl.gamedata.admin.form.table.TableEntryString;
import nl.gamedata.admin.form.table.TableForm;
import nl.gamedata.common.Access;
import nl.gamedata.common.SqlUtils;
import nl.gamedata.data.Tables;
import nl.gamedata.data.tables.records.OrganizationGameTokenRecord;

/**
 * MaintainOrganizationGame takes care of the game access screen.
 * <p>
 * Copyright (c) 2024-2024 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="https://github.com/averbraeck/gamedata-admin/LICENSE">GameData project License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public class TableOrganizationGameToken
{
    public static void table(final AdminData data, final HttpServletRequest request, final String menuChoice)
    {
        AdminTable table = new AdminTable(data, "Game Access Tokens", "Org-Game");
        boolean access = data.isSuperAdmin() || data.isOrganizationAdmin() || data.hasOrganizationGameAccess(Access.EDIT);
        if (access)
        {
            data.getTopbar().addNewButton();
            data.getTopbar().addImportButton();
        }
        data.getTopbar().addExportButton();

        table.setHeader("Org", "Game", "Org-Game", "Name", "Value", "Reader", "Writer");
        List<Record> ogtList = data.getDSL()
                .selectFrom(Tables.ORGANIZATION_GAME_TOKEN.join(Tables.ORGANIZATION_GAME)
                        .on(Tables.ORGANIZATION_GAME_TOKEN.ORGANIZATION_GAME_ID.eq(Tables.ORGANIZATION_GAME.ID))
                        .join(Tables.ORGANIZATION).on(Tables.ORGANIZATION_GAME.ORGANIZATION_ID.eq(Tables.ORGANIZATION.ID))
                        .join(Tables.GAME).on(Tables.ORGANIZATION_GAME.GAME_ID.eq(Tables.GAME.ID)))
                .fetch();
        for (var ogt : ogtList)
        {
            for (Integer ogId : data.getOrganizationGameAccess().keySet())
            {
                if (ogId.equals(ogt.getValue(Tables.ORGANIZATION_GAME.ID)))
                {
                    int id = ogt.getValue(Tables.ORGANIZATION_GAME_TOKEN.ID);
                    String org = ogt.getValue(Tables.ORGANIZATION.CODE);
                    String game = ogt.getValue(Tables.GAME.CODE);
                    String orgGame = ogt.getValue(Tables.ORGANIZATION_GAME.NAME);
                    String name = ogt.getValue(Tables.ORGANIZATION_GAME_TOKEN.NAME);
                    String value = ogt.getValue(Tables.ORGANIZATION_GAME_TOKEN.VALUE);
                    String writer = ogt.getValue(Tables.ORGANIZATION_GAME_TOKEN.WRITER) == 0 ? "N" : "Y";
                    String reader = ogt.getValue(Tables.ORGANIZATION_GAME_TOKEN.READER) == 0 ? "N" : "Y";
                    table.addRow(id, false, access, access, org, game, orgGame, name, value, writer, reader);
                    break;
                }
            }
        }
        table.process();
    }

    public static void edit(final AdminData data, final HttpServletRequest request, final String click, final int recordId)
    {
        OrganizationGameTokenRecord ogt = recordId == 0 ? Tables.ORGANIZATION_GAME_TOKEN.newRecord()
                : SqlUtils.readRecordFromId(data, Tables.ORGANIZATION_GAME_TOKEN, recordId);
        boolean reedit = click.contains("reedit");
        data.setEditRecord(ogt);
        TableForm form = new TableForm(data);
        form.startForm();
        form.setHeader("Game Access Token", click, recordId);
        form.addEntry(new TableEntryPickRecord(data, reedit, Tables.ORGANIZATION_GAME_TOKEN.ORGANIZATION_GAME_ID, ogt)
                .setPickTable(data, data.getOrganizationGamePicklist(Access.EDIT), Tables.ORGANIZATION_GAME.ID,
                        Tables.ORGANIZATION_GAME.NAME));
        form.addEntry(new TableEntryString(data, reedit, Tables.ORGANIZATION_GAME_TOKEN.NAME, ogt));
        form.addEntry(new TableEntryString(data, reedit, Tables.ORGANIZATION_GAME_TOKEN.VALUE, ogt));
        form.addEntry(new TableEntryBoolean(data, reedit, Tables.ORGANIZATION_GAME_TOKEN.WRITER, ogt));
        form.addEntry(new TableEntryBoolean(data, reedit, Tables.ORGANIZATION_GAME_TOKEN.READER, ogt));
        form.endForm();
        data.setContent(form.process());
    }
}
