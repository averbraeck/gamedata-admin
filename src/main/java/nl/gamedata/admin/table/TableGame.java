package nl.gamedata.admin.table;

import jakarta.servlet.http.HttpServletRequest;
import nl.gamedata.admin.AdminData;
import nl.gamedata.admin.AdminTable;
import nl.gamedata.admin.form.table.TableEntryBoolean;
import nl.gamedata.admin.form.table.TableEntryImage;
import nl.gamedata.admin.form.table.TableEntryString;
import nl.gamedata.admin.form.table.TableEntryText;
import nl.gamedata.admin.form.table.TableForm;
import nl.gamedata.common.Access;
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
public class TableGame
{
    public static void table(final AdminData data, final HttpServletRequest request, final String menuChoice)
    {
        AdminTable table = new AdminTable(data, "Game", "Code");
        if (data.isSuperAdmin() || data.isGameAdmin())
        {
            data.getTopbar().addNewButton();
            data.getTopbar().addImportButton();
        }
        data.getTopbar().addExportButton();
        table.setHeader("Code", "Name", "Archived");
        for (var gameId : data.getGameAccess().keySet())
        {
            var game = SqlUtils.readRecordFromId(data, Tables.GAME, gameId);
            String archived = game.getArchived() == 0 ? "N" : "Y";
            boolean edit = data.isSuperAdmin() || data.isGameAdmin() || data.hasGameAccess(Access.EDIT);
            boolean delete = data.isSuperAdmin() || data.isGameAdmin();
            table.addRow(game.getId(), true, edit, delete, game.getCode(), game.getName(), archived);
        }
        table.process();
    }

    public static void edit(final AdminData data, final HttpServletRequest request, final String click, final int recordId)
    {
        GameRecord game = recordId == 0 ? Tables.GAME.newRecord() : SqlUtils.readRecordFromId(data, Tables.GAME, recordId);
        boolean reedit = click.contains("reedit");
        data.setEditRecord(game);
        TableForm form = new TableForm(data);
        form.startMultipartForm();
        form.setHeader("Game", click, recordId);
        form.addEntry(new TableEntryString(data, reedit, Tables.GAME.CODE, game).setMinLength(2));
        form.addEntry(new TableEntryString(data, reedit, Tables.GAME.NAME, game).setMinLength(2));
        form.addEntry(new TableEntryText(data, reedit, Tables.GAME.DESCRIPTION, game));
        form.addEntry(new TableEntryBoolean(data, reedit, Tables.GAME.TOKEN_FORCED, game).setLabel("Token forced?"));
        form.addEntry(new TableEntryBoolean(data, reedit, Tables.GAME.ARCHIVED, game).setLabel("Archived?"));
        form.addEntry(new TableEntryImage(data, reedit, Tables.GAME.LOGO, game).setImageRecordId(recordId)
                .setImageServlet("imageGame"));
        form.endForm();
        data.setContent(form.process());
    }
}
