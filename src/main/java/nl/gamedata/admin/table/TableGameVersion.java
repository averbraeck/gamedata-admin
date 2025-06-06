package nl.gamedata.admin.table;

import java.util.List;

import org.jooq.Record;

import jakarta.servlet.http.HttpServletRequest;
import nl.gamedata.admin.AdminData;
import nl.gamedata.admin.AdminTable;
import nl.gamedata.admin.form.table.TableEntryBoolean;
import nl.gamedata.admin.form.table.TableEntryPickRecord;
import nl.gamedata.admin.form.table.TableEntryString;
import nl.gamedata.admin.form.table.TableEntryText;
import nl.gamedata.admin.form.table.TableForm;
import nl.gamedata.common.Access;
import nl.gamedata.common.SqlUtils;
import nl.gamedata.data.Tables;
import nl.gamedata.data.tables.records.GameVersionRecord;

/**
 * MaintainGameVersion takes care of the game version screen.
 * <p>
 * Copyright (c) 2024-2024 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="https://github.com/averbraeck/gamedata-admin/LICENSE">GameData project License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public class TableGameVersion
{
    public static void table(final AdminData data, final HttpServletRequest request, final String menuChoice)
    {
        AdminTable table = new AdminTable(data, "Game Version", "Game");
        boolean access = data.isSuperAdmin() || data.isGameAdmin();
        if (access)
        {
            data.getTopbar().addNewButton();
            data.getTopbar().addImportButton();
        }
        data.getTopbar().addExportButton();
        table.setHeader("Game", "Version", "Name", "Archived");
        List<Record> gvList = data.getDSL()
                .selectFrom(Tables.GAME_VERSION.join(Tables.GAME).on(Tables.GAME_VERSION.GAME_ID.eq(Tables.GAME.ID))).fetch();
        for (var gv : gvList)
        {
            for (Integer gameId : data.getGameAccess().keySet())
            {
                if (gameId.equals(gv.getValue(Tables.GAME.ID)))
                {
                    int id = gv.getValue(Tables.GAME_VERSION.ID);
                    String game = gv.getValue(Tables.GAME.CODE);
                    String code = gv.getValue(Tables.GAME_VERSION.CODE);
                    String name = gv.getValue(Tables.GAME_VERSION.NAME);
                    String archived = gv.getValue(Tables.GAME_VERSION.ARCHIVED) == 0 ? "N" : "Y";
                    table.addRow(id, false, access, access, game, code, name, archived);
                    break;
                }
            }
        }
        table.process();
    }

    public static void edit(final AdminData data, final HttpServletRequest request, final String click, final int recordId)
    {
        GameVersionRecord gameVersion = recordId == 0 ? Tables.GAME_VERSION.newRecord()
                : SqlUtils.readRecordFromId(data, Tables.GAME_VERSION, recordId);
        boolean reedit = click.contains("reedit");
        data.setEditRecord(gameVersion);
        TableForm form = new TableForm(data);
        form.startForm();
        form.setHeader("Game Version", click, recordId);
        form.addEntry(new TableEntryPickRecord(data, reedit, Tables.GAME_VERSION.GAME_ID, gameVersion)
                .setPickTable(data, data.getGamePicklist(Access.EDIT), Tables.GAME.ID, Tables.GAME.CODE).setLabel("Game"));
        form.addEntry(new TableEntryString(data, reedit, Tables.GAME_VERSION.CODE, gameVersion).setMinLength(2));
        form.addEntry(new TableEntryString(data, reedit, Tables.GAME_VERSION.NAME, gameVersion).setMinLength(2));
        form.addEntry(new TableEntryText(data, reedit, Tables.GAME_VERSION.DESCRIPTION, gameVersion));
        form.addEntry(new TableEntryBoolean(data, reedit, Tables.GAME_VERSION.ARCHIVED, gameVersion).setLabel("Archived?"));
        form.endForm();
        data.setContent(form.process());
    }
}
