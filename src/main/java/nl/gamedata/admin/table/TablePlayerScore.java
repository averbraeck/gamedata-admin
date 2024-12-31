package nl.gamedata.admin.table;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.jooq.Record;

import nl.gamedata.admin.AdminData;
import nl.gamedata.admin.AdminTable;
import nl.gamedata.admin.form.FormEntryBoolean;
import nl.gamedata.admin.form.FormEntryDateTime;
import nl.gamedata.admin.form.FormEntryDouble;
import nl.gamedata.admin.form.FormEntryInt;
import nl.gamedata.admin.form.FormEntryString;
import nl.gamedata.admin.form.WebForm;
import nl.gamedata.common.Access;
import nl.gamedata.common.SqlUtils;
import nl.gamedata.data.Tables;
import nl.gamedata.data.tables.records.PlayerObjectiveRecord;
import nl.gamedata.data.tables.records.ScaleRecord;

/**
 * MaintainGame takes care of the player score screen.
 * <p>
 * Copyright (c) 2024-2024 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="https://github.com/averbraeck/gamedata-admin/LICENSE">GameData project License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public class TablePlayerScore
{
    public static void table(final AdminData data, final HttpServletRequest request, final String menuChoice)
    {
        AdminTable table = new AdminTable(data, "Player Score", "Session");
        table.setNewButton(data.isSuperAdmin() || data.hasGameSessionAccess(Access.VIEW));
        table.setHeader("Session", "Player", "Attempt", "Timestamp", "Type", "Delta", "New(nr)", "New(str)");
        for (var gameSessionId : data.getGameSessionAccess(Access.VIEW))
        {
            var gameSession = SqlUtils.readRecordFromId(data, Tables.GAME_SESSION, gameSessionId);
            List<Record> psList = data.getDSL()
                    .selectFrom(Tables.PLAYER_SCORE.join(Tables.PLAYER_ATTEMPT)
                            .on(Tables.PLAYER_SCORE.PLAYER_ATTEMPT_ID.eq(Tables.PLAYER_ATTEMPT.ID)).join(Tables.PLAYER)
                            .on(Tables.PLAYER_ATTEMPT.PLAYER_ID.eq(Tables.PLAYER.ID)).join(Tables.GAME_SESSION)
                            .on(Tables.PLAYER.GAME_SESSION_ID.eq(Tables.GAME_SESSION.ID)))
                    .where(Tables.GAME_SESSION.ID.eq(gameSessionId)).fetch();
            for (var ps : psList)
            {
                int id = ps.getValue(Tables.PLAYER_SCORE.ID);
                String player = ps.getValue(Tables.PLAYER.NAME);
                String attempt = String.valueOf(ps.getValue(Tables.PLAYER_ATTEMPT.ATTEMPT_NR));
                String timestamp = ps.getValue(Tables.PLAYER_SCORE.TIMESTAMP).toString();
                String type = ps.getValue(Tables.PLAYER_SCORE.SCORE_TYPE);
                Double deltaDb = ps.getValue(Tables.PLAYER_SCORE.DELTA);
                String delta = deltaDb == null ? "-" : String.valueOf(deltaDb);
                Double newNrDb = ps.getValue(Tables.PLAYER_SCORE.NEW_SCORE_NUMBER);
                String newNr = newNrDb == null ? "-" : String.valueOf(newNrDb);
                String newStrDb = ps.getValue(Tables.PLAYER_SCORE.NEW_SCORE_STRING);
                String newStr = newStrDb == null ? "-" : newStrDb;
                table.addRow(id, false, false, false, gameSession.getName(), player, attempt, timestamp, type, delta, newNr,
                        newStr);
            }
        }
        table.process();
    }

    public static void view(final AdminData data, final HttpServletRequest request, final String click, final int recordId)
    {
        var playerScore = SqlUtils.readRecordFromId(data, Tables.PLAYER_SCORE, recordId);
        var playerAttempt = SqlUtils.readRecordFromId(data, Tables.PLAYER_ATTEMPT, playerScore.getPlayerAttemptId());
        var player = SqlUtils.readRecordFromId(data, Tables.PLAYER, playerAttempt.getPlayerId());
        var gameSession = SqlUtils.readRecordFromId(data, Tables.GAME_SESSION, player.getGameSessionId());
        data.setEditRecord(playerScore);
        WebForm form = new WebForm(data);
        form.startForm();
        form.setHeader("Player Score");
        form.addEntry(new FormEntryString("Game Session", "game_session").setReadOnly().setInitialValue(gameSession.getName()));
        form.addEntry(new FormEntryString("Player", "game_player").setReadOnly().setInitialValue(player.getName()));
        form.addEntry(new FormEntryInt("Attempt", "attempt").setReadOnly().setInitialValue(playerAttempt.getAttemptNr()));
        form.addEntry(
                new FormEntryDateTime("Timestamp", "timestamp").setReadOnly().setInitialValue(playerScore.getTimestamp()));
        form.addEntry(
                new FormEntryString("Score Type", "score_type").setReadOnly().setInitialValue(playerScore.getScoreType()));
        if (playerScore.getScaleId() != null)
        {
            ScaleRecord scale = SqlUtils.readRecordFromId(data, Tables.SCALE, playerScore.getScaleId());
            form.addEntry(new FormEntryString("Scale", "scale").setReadOnly().setInitialValue(scale.getType()));
        }
        form.addEntry(new FormEntryDouble("Delta", "delta").setReadOnly().setInitialValue(playerScore.getDelta(), Double.NaN));
        form.addEntry(new FormEntryDouble("New score (nr)", "new_score_number").setReadOnly()
                .setInitialValue(playerScore.getNewScoreNumber(), Double.NaN));
        form.addEntry(new FormEntryString("New score (str)", "new_score_string").setReadOnly()
                .setInitialValue(playerScore.getNewScoreString(), "-"));
        form.addEntry(new FormEntryBoolean("Final Score", "final_score").setReadOnly()
                .setInitialValue(playerScore.getFinalScore(), (byte) 0));
        form.addEntry(new FormEntryString("Status", "status").setReadOnly().setInitialValue(playerScore.getStatus(), "-"));
        form.addEntry(new FormEntryString("Round", "round").setReadOnly().setInitialValue(playerScore.getRound(), "-"));
        form.addEntry(
                new FormEntryString("Game Time", "game_time").setReadOnly().setInitialValue(playerScore.getGameTime(), "-"));
        form.addEntry(new FormEntryString("Grouping Code", "grouping_code").setReadOnly()
                .setInitialValue(playerScore.getGroupingCode(), "-"));
        if (playerScore.getPlayerObjectiveId() != null)
        {
            PlayerObjectiveRecord playerObjective =
                    SqlUtils.readRecordFromId(data, Tables.PLAYER_OBJECTIVE, playerScore.getPlayerObjectiveId());
            form.addEntry(new FormEntryString("Player objective", "player_objective").setReadOnly()
                    .setInitialValue(playerObjective.getName()));
            form.addEntry(new FormEntryString("Threshold", "player_objective_threshold").setReadOnly()
                    .setInitialValue(playerObjective.getThreshold()));
        }
        form.endForm();
        data.setContent(form.process());
    }
}
