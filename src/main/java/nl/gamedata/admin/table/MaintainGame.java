package nl.gamedata.admin.table;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

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
    public static void tableGame(final AdminData data, final HttpServletRequest request, final String menuChoice)
    {
        StringBuilder s = new StringBuilder();
        AdminTable.tableStart(s, "Game", new String[] {"Code", "Name", "Archived"}, true, "Code", true);
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        List<GameRecord> gameRecords = dslContext.selectFrom(Tables.GAME).fetch();
        for (var game : gameRecords)
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
        form.addEntry(new TableEntryString(Tables.GAME.CODE, game));
        form.addEntry(new TableEntryString(Tables.GAME.NAME, game));
        form.addEntry(new TableEntryText(Tables.GAME.DESCRIPTION, game));
        form.addEntry(new TableEntryBoolean(Tables.GAME.TOKEN_FORCED, game));
        form.addEntry(new TableEntryBoolean(Tables.GAME.ARCHIVED, game).setLabel("Archived?"));
        form.addEntry(new TableEntryImage(Tables.GAME.LOGO, game));
        form.endForm();
        data.setContent(form.process());
    }
}
