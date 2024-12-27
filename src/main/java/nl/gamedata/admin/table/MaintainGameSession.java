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
import nl.gamedata.admin.form.table.TableEntryDate;
import nl.gamedata.admin.form.table.TableEntryDateTime;
import nl.gamedata.admin.form.table.TableEntryPickRecord;
import nl.gamedata.admin.form.table.TableEntryString;
import nl.gamedata.admin.form.table.TableForm;
import nl.gamedata.common.Access;
import nl.gamedata.common.SqlUtils;
import nl.gamedata.data.Tables;
import nl.gamedata.data.tables.records.GameSessionRecord;

/**
 * MaintainGameSession takes care of the game version screen.
 * <p>
 * Copyright (c) 2024-2024 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="https://github.com/averbraeck/gamedata-admin/LICENSE">GameData project License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public class MaintainGameSession
{
    public static void table(final AdminData data, final HttpServletRequest request, final String menuChoice)
    {
        AdminTable table = new AdminTable(data, "Game Session", "Code");
        boolean newButton = data.isSuperAdmin() || data.isOrganizationAdmin() || data.hasGameSessionAccess(Access.CREATE);
        table.setNewButton(newButton);
        table.setHeader("Game", "Version", "Code", "Name", "Valid", "Archived");
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        List<Record> gsList = dslContext.selectFrom(
                Tables.GAME_SESSION.join(Tables.GAME_VERSION).on(Tables.GAME_SESSION.GAME_VERSION_ID.eq(Tables.GAME_VERSION.ID))
                        .join(Tables.GAME).on(Tables.GAME_VERSION.GAME_ID.eq(Tables.GAME.ID)))
                .fetch();
        for (var gs : gsList)
        {
            for (Integer gameId : data.getGameAccess().keySet())
            {
                if (gameId.equals(gs.getValue(Tables.GAME.ID)))
                {
                    int id = gs.getValue(Tables.GAME_SESSION.ID);
                    String game = gs.getValue(Tables.GAME.CODE);
                    String version = gs.getValue(Tables.GAME_VERSION.NAME);
                    String code = gs.getValue(Tables.GAME_SESSION.CODE);
                    String name = gs.getValue(Tables.GAME_SESSION.NAME);
                    String valid = gs.getValue(Tables.GAME_SESSION.VALID) == 0 ? "N" : "Y";
                    String archived = gs.getValue(Tables.GAME_SESSION.ARCHIVED) == 0 ? "N" : "Y";
                    boolean edit = data.getGameSessionAccess().get(id) != null && data.getGameSessionAccess().get(id).edit();
                    boolean delete =
                            data.getGameSessionAccess().get(id) != null && data.getGameSessionAccess().get(id).create();
                    table.addRow(id, false, edit, delete, game, version, code, name, valid, archived);
                    break;
                }
            }
        }
        table.process();
    }

    public static void edit(final AdminData data, final HttpServletRequest request, final String click, final int recordId)
    {
        GameSessionRecord gameSession = recordId == 0 ? Tables.GAME_SESSION.newRecord()
                : SqlUtils.readRecordFromId(data, Tables.GAME_SESSION, recordId);
        data.setEditRecord(gameSession);
        TableForm form = new TableForm(data);
        form.startForm();
        form.setHeader("Game Session", click, recordId);
        // TODO: Choose organization - game - gameversion
        form.addEntry(new TableEntryPickRecord(Tables.GAME_SESSION.GAME_VERSION_ID, gameSession)
                .setPickTable(data, data.getGameVersionPicklist(Access.VIEW), Tables.GAME_VERSION.ID, Tables.GAME_VERSION.NAME)
                .setLabel("Game Version"));
        form.addEntry(new TableEntryString(Tables.GAME_SESSION.CODE, gameSession).setMinLength(2));
        form.addEntry(new TableEntryString(Tables.GAME_SESSION.NAME, gameSession).setMinLength(2));
        form.addEntry(new TableEntryString(Tables.GAME_SESSION.SESSION_TOKEN, gameSession).setMinLength(2));
        form.addEntry(new TableEntryString(Tables.GAME_SESSION.SESSION_STATUS, gameSession).setMinLength(2));
        form.addEntry(new TableEntryDate(Tables.GAME_SESSION.PLAY_DATE, gameSession));
        form.addEntry(new TableEntryBoolean(Tables.GAME_SESSION.VALID, gameSession));
        form.addEntry(new TableEntryDateTime(Tables.GAME_SESSION.VALID_FROM, gameSession));
        form.addEntry(new TableEntryDateTime(Tables.GAME_SESSION.VALID_UNTIL, gameSession));
        form.addEntry(new TableEntryBoolean(Tables.GAME_SESSION.TOKEN_FOR_DASHBOARD, gameSession));
        form.addEntry(new TableEntryBoolean(Tables.GAME_SESSION.ARCHIVED, gameSession).setLabel("Archived?"));
        form.endForm();
        data.setContent(form.process());
    }
}
