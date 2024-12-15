package nl.gamedata.admin.table;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import nl.gamedata.admin.AdminData;
import nl.gamedata.admin.Table;
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
    public static void tableGame(final AdminData data, final HttpServletRequest request, final String menuChoice,
            final int recordId)
    {
        StringBuilder s = new StringBuilder();
        Table.tableStart(s, "Game", new String[] {"Code", "Name", "Archived"}, true, "Code", true);
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        List<GameRecord> gameRecords = dslContext.selectFrom(Tables.GAME).fetch();
        for (var game : gameRecords)
        {
            String archived = game.getArchived() == 0 ? "N" : "Y";
            Table.tableRow(s, recordId, new String[] {game.getCode(), game.getName(), archived});
        }
        Table.tableEnd(s);
        data.setContent(s.toString());
    }

    public static void edit(final AdminData data, final HttpServletRequest request, final String click, final int recordId)
    {
        GameRecord game = recordId == 0 ? Tables.GAME.newRecord() : SqlUtils.readRecordFromId(data, Tables.GAME, recordId);
        TableForm form = new TableForm();
        form.startForm();
        form.setHeader("Game", click);
        form.addEntry(new TableEntryString(Tables.GAME.CODE).setInitialValue(game.getCode(), "").setLabel("Code"));
        form.addEntry(new TableEntryString(Tables.GAME.NAME).setInitialValue(game.getName(), "").setLabel("Name"));
        form.addEntry(
                new TableEntryText(Tables.GAME.DESCRIPTION).setInitialValue(game.getDescription(), "").setLabel("Description"));
        form.addEntry(new TableEntryBoolean(Tables.GAME.TOKEN_FORCED).setInitialValue(game.getTokenForced(), false)
                .setLabel("Token forced?"));
        form.addEntry(
                new TableEntryBoolean(Tables.GAME.ARCHIVED).setInitialValue(game.getArchived(), false).setLabel("Archived?"));
        form.addEntry(new TableEntryImage(Tables.GAME.LOGO).setInitialValue(game.getLogo(), null).setLabel("Logo"));
        form.endForm();
        data.setContent(form.process());
    }
}
