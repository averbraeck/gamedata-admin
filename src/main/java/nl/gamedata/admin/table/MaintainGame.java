package nl.gamedata.admin.table;

import javax.servlet.http.HttpServletRequest;

import nl.gamedata.admin.AdminData;
import nl.gamedata.admin.AdminTable;
import nl.gamedata.admin.form.table.TableEntryBoolean;
import nl.gamedata.admin.form.table.TableEntryImage;
import nl.gamedata.admin.form.table.TableEntryString;
import nl.gamedata.admin.form.table.TableEntryText;
import nl.gamedata.admin.form.table.TableForm;
import nl.gamedata.common.SqlUtils;
import nl.gamedata.data.Tables;
import nl.gamedata.data.tables.records.GameRecord;

/**
 * MaintainGame takes care of the game screen.
 * <p>
 * Copyright (c) 2024-2024 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="https://github.com/averbraeck/gamedata-admin/LICENSE">GameData project License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public class MaintainGame
{
    public static void table(final AdminData data, final HttpServletRequest request, final String menuChoice)
    {
        StringBuilder s = new StringBuilder();
        AdminTable.tableStart(s, "Game", new String[] {"Code", "Name", "Archived"}, data.isSuperAdmin() || data.isGameAdmin(),
                "Code", true);
        for (var game : data.getGameRoles().keySet())
        {
            String archived = game.getArchived() == 0 ? "N" : "Y";
            AdminTable.tableRow(s, game.getId(), new String[] {game.getCode(), game.getName(), archived});
        }
        AdminTable.tableEnd(s);
        data.setContent(s.toString());
    }

    public static void edit(final AdminData data, final HttpServletRequest request, final String click, final int recordId)
    {
        GameRecord game = recordId == 0 ? Tables.GAME.newRecord() : SqlUtils.readRecordFromId(data, Tables.GAME, recordId);
        data.setEditRecord(game);
        TableForm form = new TableForm(data);
        form.startForm();
        form.setHeader("Game", click, recordId);
        form.addEntry(new TableEntryString(Tables.GAME.CODE, game).setMinLength(2));
        form.addEntry(new TableEntryString(Tables.GAME.NAME, game).setMinLength(2));
        form.addEntry(new TableEntryText(Tables.GAME.DESCRIPTION, game));
        form.addEntry(new TableEntryBoolean(Tables.GAME.TOKEN_FORCED, game).setLabel("Token forced?"));
        form.addEntry(new TableEntryBoolean(Tables.GAME.ARCHIVED, game).setLabel("Archived?"));
        form.addEntry(new TableEntryImage(Tables.GAME.LOGO, game));
        form.endForm();
        data.setContent(form.process());
    }
}
