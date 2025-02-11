package nl.gamedata.admin.table;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.jooq.Record;

import nl.gamedata.admin.AdminData;
import nl.gamedata.admin.AdminTable;
import nl.gamedata.admin.form.FormEntryBoolean;
import nl.gamedata.admin.form.FormEntryDateTime;
import nl.gamedata.admin.form.FormEntryString;
import nl.gamedata.admin.form.WebForm;
import nl.gamedata.common.Access;
import nl.gamedata.common.SqlUtils;
import nl.gamedata.data.Tables;

/**
 * MaintainGame takes care of the mission event screen.
 * <p>
 * Copyright (c) 2024-2024 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="https://github.com/averbraeck/gamedata-admin/LICENSE">GameData project License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public class TableMissionEvent
{
    public static void table(final AdminData data, final HttpServletRequest request, final String menuChoice)
    {
        AdminTable table = new AdminTable(data, "Mission Event", "Session");
        data.getTopbar().addExportButton();
        table.setHeader("Session", "Mission", "Timestamp", "Type", "Key", "Value");
        for (var gameSessionId : data.getGameSessionAccess(Access.VIEW))
        {
            var gameSession = SqlUtils.readRecordFromId(data, Tables.GAME_SESSION, gameSessionId);
            List<Record> meList = data.getDSL()
                    .selectFrom(Tables.MISSION_EVENT.join(Tables.GAME_MISSION)
                            .on(Tables.MISSION_EVENT.GAME_MISSION_ID.eq(Tables.GAME_MISSION.ID)))
                    .where(Tables.MISSION_EVENT.GAME_SESSION_ID.eq(gameSessionId)).fetch();
            for (var me : meList)
            {
                int id = me.getValue(Tables.MISSION_EVENT.ID);
                String mission = me.getValue(Tables.GAME_MISSION.NAME);
                String timestamp = me.getValue(Tables.MISSION_EVENT.TIMESTAMP).toString();
                String type = me.getValue(Tables.MISSION_EVENT.TYPE);
                String key = me.getValue(Tables.MISSION_EVENT.KEY);
                String value = me.getValue(Tables.MISSION_EVENT.VALUE);
                table.addRow(id, false, false, false, gameSession.getName(), mission, timestamp, type, key, value);
            }
        }
        table.process();
    }

    public static void view(final AdminData data, final HttpServletRequest request, final String click, final int recordId)
    {
        var missionEvent = SqlUtils.readRecordFromId(data, Tables.MISSION_EVENT, recordId);
        var gameSession = SqlUtils.readRecordFromId(data, Tables.GAME_SESSION, missionEvent.getGameSessionId());
        var gameMission = SqlUtils.readRecordFromId(data, Tables.GAME_MISSION, missionEvent.getGameMissionId());
        data.setEditRecord(missionEvent);
        WebForm form = new WebForm(data);
        form.startForm();
        form.setHeader("Mission Event");
        form.addEntry(new FormEntryString("Game Session", "game_session").setReadOnly().setInitialValue(gameSession.getName()));
        form.addEntry(new FormEntryString("Game Mission", "game_mission").setReadOnly().setInitialValue(gameMission.getName()));
        form.addEntry(
                new FormEntryDateTime("Timestamp", "timestamp").setReadOnly().setInitialValue(missionEvent.getTimestamp()));
        form.addEntry(new FormEntryString("Type", "type").setReadOnly().setInitialValue(missionEvent.getType()));
        form.addEntry(new FormEntryString("Key", "key").setReadOnly().setInitialValue(missionEvent.getKey()));
        form.addEntry(new FormEntryString("Value", "value").setReadOnly().setInitialValue(missionEvent.getValue()));
        form.addEntry(new FormEntryString("Status", "status").setReadOnly().setInitialValue(missionEvent.getStatus(), "-"));
        form.addEntry(new FormEntryString("Round", "round").setReadOnly().setInitialValue(missionEvent.getRound(), "-"));
        form.addEntry(
                new FormEntryString("Game Time", "game_time").setReadOnly().setInitialValue(missionEvent.getGameTime(), "-"));
        form.addEntry(new FormEntryString("Grouping Code", "grouping_code").setReadOnly()
                .setInitialValue(missionEvent.getGroupingCode(), "-"));
        form.addEntry(new FormEntryBoolean("Facilitator initiated", "facilitator_initiated").setReadOnly()
                .setInitialValue(missionEvent.getFacilitatorInitiated(), (byte) 0));
        form.endForm();
        data.setContent(form.process());
    }
}
