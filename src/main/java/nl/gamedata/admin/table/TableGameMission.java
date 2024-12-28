package nl.gamedata.admin.table;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.jooq.Record;

import nl.gamedata.admin.AdminData;
import nl.gamedata.admin.AdminTable;
import nl.gamedata.admin.form.FormEntryInt;
import nl.gamedata.admin.form.FormEntryPickRecord;
import nl.gamedata.admin.form.FormEntryString;
import nl.gamedata.admin.form.WebForm;
import nl.gamedata.admin.form.table.TableEntryPickRecord;
import nl.gamedata.admin.form.table.TableEntryString;
import nl.gamedata.admin.form.table.TableForm;
import nl.gamedata.common.Access;
import nl.gamedata.common.SqlUtils;
import nl.gamedata.data.Tables;
import nl.gamedata.data.tables.records.GameMissionRecord;
import nl.gamedata.data.tables.records.GameRecord;
import nl.gamedata.data.tables.records.GameVersionRecord;

/**
 * MaintainGameVersion takes care of the game version screen.
 * <p>
 * Copyright (c) 2024-2024 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="https://github.com/averbraeck/gamedata-admin/LICENSE">GameData project License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public class TableGameMission
{
    public static void table(final AdminData data, final HttpServletRequest request, final String menuChoice)
    {
        AdminTable table = new AdminTable(data, "Game Mission", "Game");
        boolean adminAccess = data.isSuperAdmin() || data.isGameAdmin();
        table.setNewButton(adminAccess);
        table.setHeader("Game", "Version", "Mission");
        List<Record> gmList = data.getDSL()
                .selectFrom(Tables.GAME_MISSION.join(Tables.GAME_VERSION)
                        .on(Tables.GAME_MISSION.GAME_VERSION_ID.eq(Tables.GAME_VERSION.ID)).join(Tables.GAME)
                        .on(Tables.GAME_VERSION.GAME_ID.eq(Tables.GAME.ID)))
                .fetch();
        for (var gm : gmList)
        {
            for (Integer gameId : data.getGameAccess().keySet())
            {
                if (gameId.equals(gm.getValue(Tables.GAME.ID)))
                {
                    boolean editAccess = adminAccess | data.getGameAccess().get(gameId).edit();
                    int id = gm.getValue(Tables.GAME_MISSION.ID);
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
        int phase = WebForm.getPhase(request);
        Integer gameId = WebForm.getIntParameter(request, "game_id");
        if (click.equals("record-new") && (phase == 0 || gameId == null))
        {
            WebForm form = new WebForm(data);
            form.startForm();
            form.setHeader("Game Mission");
            form.setPhase(1);
            form.addEntry(new FormEntryPickRecord("Game", "game_id")
                    .setPickTable(data, data.getGamePicklist(Access.EDIT), Tables.GAME.ID, Tables.GAME.CODE).setLabel("Game"));
            form.setOkMethod("record-new");
            form.endForm();
            data.setContent(form.process());
            return;
        }

        if ((phase == 1 && gameId != null) || !click.equals("record-new"))
        {
            GameMissionRecord gameMission = recordId == 0 ? Tables.GAME_MISSION.newRecord()
                    : SqlUtils.readRecordFromId(data, Tables.GAME_MISSION, recordId);
            if (!click.equals("record-new"))
            {
                GameVersionRecord gameVersion = SqlUtils.readRecordFromId(data, Tables.GAME_VERSION, gameMission.getGameVersionId());
                gameId = gameVersion.getGameId();
            }
            TableForm form = new TableForm(data);
            form.startForm();
            data.setEditRecord(gameMission);
            form.setHeader("Game Mission", click, recordId);
            form.setPhase(1);
            GameRecord game = SqlUtils.readRecordFromId(data, Tables.GAME, gameId);
            form.addEntry(new FormEntryInt("Game id", "game_id").setHidden().setReadOnly().setInitialValue(gameId, gameId));
            form.addEntry(new FormEntryString("Game", "game").setReadOnly().setInitialValue(game.getCode(), game.getCode()));
            form.addEntry(new TableEntryPickRecord(Tables.GAME_MISSION.GAME_VERSION_ID, gameMission)
                    .setPickTable(data, data.getGameVersionPicklist(gameId, Access.EDIT)).setLabel("Game Version"));
            form.addEntry(new TableEntryString(Tables.GAME_MISSION.NAME, gameMission).setMinLength(2));
            form.endForm();
            data.setContent(form.process());
            return;
        }

        String s = "Unknown state, pahse=" + phase + ", gameId=" + gameId + ", click=" + click;
        data.setContent(s);
        System.err.println(s);
    }
}
