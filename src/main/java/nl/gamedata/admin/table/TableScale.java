package nl.gamedata.admin.table;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.jooq.Record;

import nl.gamedata.admin.AdminData;
import nl.gamedata.admin.AdminTable;
import nl.gamedata.admin.form.table.TableEntryDouble;
import nl.gamedata.admin.form.table.TableEntryPickRecord;
import nl.gamedata.admin.form.table.TableEntryString;
import nl.gamedata.admin.form.table.TableForm;
import nl.gamedata.common.Access;
import nl.gamedata.common.SqlUtils;
import nl.gamedata.data.Tables;
import nl.gamedata.data.tables.records.ScaleRecord;

/**
 * MaintainGameVersion takes care of the game version screen.
 * <p>
 * Copyright (c) 2024-2024 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="https://github.com/averbraeck/gamedata-admin/LICENSE">GameData project License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public class TableScale
{
    public static void table(final AdminData data, final HttpServletRequest request, final String menuChoice)
    {
        AdminTable table = new AdminTable(data, "Scale", "Game");
        boolean adminAccess = data.isSuperAdmin() || data.isGameAdmin();
        if (adminAccess)
        {
            data.getTopbar().addNewButton();
            data.getTopbar().addImportButton();
        }
        data.getTopbar().addExportButton();

        table.setHeader("Game", "Scale");
        List<Record> scaleList =
                data.getDSL().selectFrom(Tables.SCALE.join(Tables.GAME).on(Tables.SCALE.GAME_ID.eq(Tables.GAME.ID))).fetch();
        for (var scale : scaleList)
        {
            for (Integer gameId : data.getGameAccess().keySet())
            {
                if (gameId.equals(scale.getValue(Tables.GAME.ID)))
                {
                    int id = scale.getValue(Tables.SCALE.ID);
                    String game = scale.getValue(Tables.GAME.CODE);
                    String type = scale.getValue(Tables.SCALE.TYPE);
                    table.addRow(id, false, adminAccess, adminAccess, game, type);
                    break;
                }
            }
        }
        table.process();
    }

    public static void edit(final AdminData data, final HttpServletRequest request, final String click, final int recordId)
    {
        ScaleRecord scale = recordId == 0 ? Tables.SCALE.newRecord() : SqlUtils.readRecordFromId(data, Tables.SCALE, recordId);
        boolean reedit = click.contains("reedit");
        data.setEditRecord(scale);
        TableForm form = new TableForm(data);
        form.startForm();
        form.setHeader("Scale", click, recordId);
        form.addEntry(new TableEntryPickRecord(data, reedit, Tables.SCALE.GAME_ID, scale)
                .setPickTable(data, data.getGamePicklist(Access.EDIT), Tables.GAME.ID, Tables.GAME.CODE).setLabel("Game"));
        form.addEntry(new TableEntryString(data, reedit, Tables.SCALE.TYPE, scale).setMinLength(2));
        form.addEntry(new TableEntryDouble(data, reedit, Tables.SCALE.MIN_VALUE, scale));
        form.addEntry(new TableEntryDouble(data, reedit, Tables.SCALE.MAX_VALUE, scale));
        form.addEntry(new TableEntryString(data, reedit, Tables.SCALE.VALUE_LIST, scale));
        form.addEntry(new TableEntryString(data, reedit, Tables.SCALE.VALUE_SCORES, scale));
        form.endForm();
        data.setContent(form.process());
    }
}
