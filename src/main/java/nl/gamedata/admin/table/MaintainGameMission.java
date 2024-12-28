package nl.gamedata.admin.table;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import nl.gamedata.admin.AdminData;
import nl.gamedata.admin.AdminTable;
import nl.gamedata.admin.form.table.TableEntryPickRecord;
import nl.gamedata.admin.form.table.TableEntryString;
import nl.gamedata.admin.form.table.TableForm;
import nl.gamedata.common.Access;
import nl.gamedata.common.SqlUtils;
import nl.gamedata.data.Tables;
import nl.gamedata.data.tables.records.GameMissionRecord;

/**
 * MaintainGameVersion takes care of the game version screen.
 * <p>
 * Copyright (c) 2024-2024 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="https://github.com/averbraeck/gamedata-admin/LICENSE">GameData project License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public class MaintainGameMission
{
    public static void table(final AdminData data, final HttpServletRequest request, final String menuChoice)
    {
        AdminTable table = new AdminTable(data, "Game Mission", "Game");
        boolean adminAccess = data.isSuperAdmin() || data.isGameAdmin();
        table.setNewButton(adminAccess);
        table.setHeader("Game", "Version", "Mission");
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        List<Record> gmList = dslContext.selectFrom(
                Tables.GAME_MISSION.join(Tables.GAME_VERSION).on(Tables.GAME_MISSION.GAME_VERSION_ID.eq(Tables.GAME_VERSION.ID))
                        .join(Tables.GAME).on(Tables.GAME_VERSION.GAME_ID.eq(Tables.GAME.ID)))
                .fetch();
        for (var gm : gmList)
        {
            for (Integer gameId : data.getGameAccess().keySet())
            {
                if (gameId.equals(gm.getValue(Tables.GAME.ID)))
                {
                    boolean editAccess = adminAccess | data.getGameAccess().get(gameId).edit();
                    int id = gm.getValue(Tables.GAME_VERSION.ID);
                    String game = gm.getValue(Tables.GAME.CODE);
                    String version = gm.getValue(Tables.GAME_VERSION.NAME);
                    String mission = gm.getValue(Tables.GAME_MISSION.NAME);
                    table.addRow(id, true, editAccess, adminAccess, game, version, mission);
                    break;
                }
            }
        }
        table.process();
    }

    public static void edit(final AdminData data, final HttpServletRequest request, final String click, final int recordId)
    {
        GameMissionRecord gameMission = recordId == 0 ? Tables.GAME_MISSION.newRecord()
                : SqlUtils.readRecordFromId(data, Tables.GAME_MISSION, recordId);
        data.setEditRecord(gameMission);
        TableForm form = new TableForm(data);
        form.startForm();
        form.setHeader("Game Mission", click, recordId);
        form.addEntry(new TableEntryPickRecord(Tables.GAME_MISSION.GAME_VERSION_ID, gameMission)
                .setPickTable(data, data.getGameVersionPicklist(Access.EDIT), Tables.GAME_VERSION.ID, Tables.GAME_VERSION.NAME)
                .setLabel("Game Version"));
        form.addEntry(new TableEntryString(Tables.GAME_MISSION.NAME, gameMission).setMinLength(2));
        form.endForm();
        data.setContent(form.process());
    }
}
