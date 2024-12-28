package nl.gamedata.admin.table;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.jooq.Record;

import nl.gamedata.admin.AdminData;
import nl.gamedata.admin.AdminTable;
import nl.gamedata.admin.form.FormEntryBoolean;
import nl.gamedata.admin.form.FormEntryDateTime;
import nl.gamedata.admin.form.FormEntryInt;
import nl.gamedata.admin.form.FormEntryString;
import nl.gamedata.admin.form.WebForm;
import nl.gamedata.common.Access;
import nl.gamedata.common.SqlUtils;
import nl.gamedata.data.Tables;

/**
 * MaintainGame takes care of the player event screen.
 * <p>
 * Copyright (c) 2024-2024 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="https://github.com/averbraeck/gamedata-admin/LICENSE">GameData project License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public class TablePlayerEvent
{
    public static void table(final AdminData data, final HttpServletRequest request, final String menuChoice)
    {
        AdminTable table = new AdminTable(data, "Player Event", "Session");
        table.setNewButton(data.isSuperAdmin() || data.hasGameSessionAccess(Access.VIEW));
        table.setHeader("Session", "Player", "Attempt", "Timestamp", "Type", "Key", "Value");
        for (var gameSessionId : data.getGameSessionAccess(Access.VIEW))
        {
            var gameSession = SqlUtils.readRecordFromId(data, Tables.GAME_SESSION, gameSessionId);
            List<Record> peList = data.getDSL()
                    .selectFrom(Tables.PLAYER_EVENT.join(Tables.PLAYER_ATTEMPT)
                            .on(Tables.PLAYER_EVENT.PLAYER_ATTEMPT_ID.eq(Tables.PLAYER_ATTEMPT.ID)).join(Tables.PLAYER)
                            .on(Tables.PLAYER_ATTEMPT.PLAYER_ID.eq(Tables.PLAYER.ID)).join(Tables.GAME_SESSION)
                            .on(Tables.PLAYER.GAME_SESSION_ID.eq(Tables.GAME_SESSION.ID)))
                    .where(Tables.GAME_SESSION.ID.eq(gameSessionId)).fetch();
            for (var me : peList)
            {
                int id = me.getValue(Tables.PLAYER_EVENT.ID);
                String player = me.getValue(Tables.PLAYER.NAME);
                String attempt = String.valueOf(me.getValue(Tables.PLAYER_ATTEMPT.ATTEMPT_NR));
                String timestamp = me.getValue(Tables.PLAYER_EVENT.TIMESTAMP).toString();
                String type = me.getValue(Tables.PLAYER_EVENT.TYPE);
                String key = me.getValue(Tables.PLAYER_EVENT.KEY);
                String value = me.getValue(Tables.PLAYER_EVENT.VALUE);
                table.addRow(id, false, false, false, gameSession.getName(), player, attempt, timestamp, type, key, value);
            }
        }
        table.process();
    }

    public static void view(final AdminData data, final HttpServletRequest request, final String click, final int recordId)
    {
        var playerEvent = SqlUtils.readRecordFromId(data, Tables.PLAYER_EVENT, recordId);
        var playerAttempt = SqlUtils.readRecordFromId(data, Tables.PLAYER_ATTEMPT, playerEvent.getPlayerAttemptId());
        var player = SqlUtils.readRecordFromId(data, Tables.PLAYER, playerAttempt.getPlayerId());
        var gameSession = SqlUtils.readRecordFromId(data, Tables.GAME_SESSION, player.getGameSessionId());
        data.setEditRecord(playerEvent);
        WebForm form = new WebForm(data);
        form.startForm();
        form.setHeader("Player Event");
        form.addEntry(new FormEntryString("Game Session", "game_session").setReadOnly().setInitialValue(gameSession.getName()));
        form.addEntry(new FormEntryString("Player", "game_player").setReadOnly().setInitialValue(player.getName()));
        form.addEntry(new FormEntryInt("Attempt", "attempt").setReadOnly().setInitialValue(playerAttempt.getAttemptNr()));
        form.addEntry(
                new FormEntryDateTime("Timestamp", "timestamp").setReadOnly().setInitialValue(playerEvent.getTimestamp()));
        form.addEntry(new FormEntryString("Type", "type").setReadOnly().setInitialValue(playerEvent.getType()));
        form.addEntry(new FormEntryString("Key", "key").setReadOnly().setInitialValue(playerEvent.getKey()));
        form.addEntry(new FormEntryString("Value", "value").setReadOnly().setInitialValue(playerEvent.getValue()));
        form.addEntry(new FormEntryInt("Mission Attempt", "mission_attempt").setReadOnly()
                .setInitialValue(playerEvent.getMissionAttempt()));
        form.addEntry(new FormEntryString("Status", "status").setReadOnly().setInitialValue(playerEvent.getStatus(), "-"));
        form.addEntry(new FormEntryString("Round", "round").setReadOnly().setInitialValue(playerEvent.getRound(), "-"));
        form.addEntry(
                new FormEntryString("Game Time", "game_time").setReadOnly().setInitialValue(playerEvent.getGameTime(), "-"));
        form.addEntry(new FormEntryString("Grouping Code", "grouping_code").setReadOnly()
                .setInitialValue(playerEvent.getGroupingCode(), "-"));
        form.addEntry(new FormEntryBoolean("Player initiated", "player_initiated").setReadOnly()
                .setInitialValue(playerEvent.getPlayerInitiated(), (byte) 0));
        form.endForm();
        data.setContent(form.process());
    }
}
