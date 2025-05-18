package nl.gamedata.admin.table;

import java.util.List;

import org.jooq.Record;

import jakarta.servlet.http.HttpServletRequest;
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
import nl.gamedata.data.tables.records.GroupObjectiveRecord;
import nl.gamedata.data.tables.records.ScaleRecord;

/**
 * MaintainGame takes care of the group score screen.
 * <p>
 * Copyright (c) 2024-2024 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="https://github.com/averbraeck/gamedata-admin/LICENSE">GameData project License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public class TableGroupScore
{
    public static void table(final AdminData data, final HttpServletRequest request, final String menuChoice)
    {
        AdminTable table = new AdminTable(data, "Group Score", "Session");
        data.getTopbar().addExportButton();
        table.setHeader("Session", "Group", "Attempt", "Timestamp", "Type", "Delta", "New(nr)", "New(str)");
        for (var gameSessionId : data.getGameSessionAccess(Access.VIEW))
        {
            var gameSession = SqlUtils.readRecordFromId(data, Tables.GAME_SESSION, gameSessionId);
            List<Record> psList = data.getDSL()
                    .selectFrom(Tables.GROUP_SCORE.join(Tables.GROUP_ATTEMPT)
                            .on(Tables.GROUP_SCORE.GROUP_ATTEMPT_ID.eq(Tables.GROUP_ATTEMPT.ID)).join(Tables.GROUP)
                            .on(Tables.GROUP_ATTEMPT.GROUP_ID.eq(Tables.GROUP.ID)).join(Tables.GAME_SESSION)
                            .on(Tables.GROUP.GAME_SESSION_ID.eq(Tables.GAME_SESSION.ID)))
                    .where(Tables.GAME_SESSION.ID.eq(gameSessionId)).fetch();
            for (var ps : psList)
            {
                int id = ps.getValue(Tables.GROUP_SCORE.ID);
                String group = ps.getValue(Tables.GROUP.NAME);
                String attempt = String.valueOf(ps.getValue(Tables.GROUP_ATTEMPT.ATTEMPT_NR));
                String timestamp = ps.getValue(Tables.GROUP_SCORE.TIMESTAMP).toString();
                String type = ps.getValue(Tables.GROUP_SCORE.SCORE_TYPE);
                Double deltaDb = ps.getValue(Tables.GROUP_SCORE.DELTA);
                String delta = deltaDb == null ? "-" : String.valueOf(deltaDb);
                Double newNrDb = ps.getValue(Tables.GROUP_SCORE.NEW_SCORE_NUMBER);
                String newNr = newNrDb == null ? "-" : String.valueOf(newNrDb);
                String newStrDb = ps.getValue(Tables.GROUP_SCORE.NEW_SCORE_STRING);
                String newStr = newStrDb == null ? "-" : newStrDb;
                table.addRow(id, false, false, false, gameSession.getName(), group, attempt, timestamp, type, delta, newNr,
                        newStr);
            }
        }
        table.process();
    }

    public static void view(final AdminData data, final HttpServletRequest request, final String click, final int recordId)
    {
        var groupScore = SqlUtils.readRecordFromId(data, Tables.GROUP_SCORE, recordId);
        var groupAttempt = SqlUtils.readRecordFromId(data, Tables.GROUP_ATTEMPT, groupScore.getGroupAttemptId());
        var group = SqlUtils.readRecordFromId(data, Tables.GROUP, groupAttempt.getGroupId());
        var gameSession = SqlUtils.readRecordFromId(data, Tables.GAME_SESSION, group.getGameSessionId());
        data.setEditRecord(groupScore);
        WebForm form = new WebForm(data);
        form.startForm();
        form.setHeader("Group Score");
        form.addEntry(new FormEntryString("Game Session", "game_session").setReadOnly().setInitialValue(gameSession.getName()));
        form.addEntry(new FormEntryString("Group", "game_group").setReadOnly().setInitialValue(group.getName()));
        form.addEntry(new FormEntryInt("Attempt", "attempt").setReadOnly().setInitialValue(groupAttempt.getAttemptNr()));
        form.addEntry(new FormEntryDateTime("Timestamp", "timestamp").setReadOnly().setInitialValue(groupScore.getTimestamp()));
        form.addEntry(new FormEntryString("Score Type", "score_type").setReadOnly().setInitialValue(groupScore.getScoreType()));
        if (groupScore.getScaleId() != null)
        {
            ScaleRecord scale = SqlUtils.readRecordFromId(data, Tables.SCALE, groupScore.getScaleId());
            form.addEntry(new FormEntryString("Scale", "scale").setReadOnly().setInitialValue(scale.getType()));
        }
        form.addEntry(new FormEntryDouble("Delta", "delta").setReadOnly().setInitialValue(groupScore.getDelta(), Double.NaN));
        form.addEntry(new FormEntryDouble("New score (nr)", "new_score_number").setReadOnly()
                .setInitialValue(groupScore.getNewScoreNumber(), Double.NaN));
        form.addEntry(new FormEntryString("New score (str)", "new_score_string").setReadOnly()
                .setInitialValue(groupScore.getNewScoreString(), "-"));
        form.addEntry(new FormEntryBoolean("Final Score", "final_score").setReadOnly()
                .setInitialValue(groupScore.getFinalScore(), (byte) 0));
        form.addEntry(new FormEntryString("Status", "status").setReadOnly().setInitialValue(groupScore.getStatus(), "-"));
        form.addEntry(new FormEntryString("Round", "round").setReadOnly().setInitialValue(groupScore.getRound(), "-"));
        form.addEntry(
                new FormEntryString("Game Time", "game_time").setReadOnly().setInitialValue(groupScore.getGameTime(), "-"));
        form.addEntry(new FormEntryString("Grouping Code", "grouping_code").setReadOnly()
                .setInitialValue(groupScore.getGroupingCode(), "-"));
        if (groupScore.getGroupObjectiveId() != null)
        {
            GroupObjectiveRecord groupObjective =
                    SqlUtils.readRecordFromId(data, Tables.GROUP_OBJECTIVE, groupScore.getGroupObjectiveId());
            form.addEntry(new FormEntryString("Group objective", "group_objective").setReadOnly()
                    .setInitialValue(groupObjective.getName()));
            form.addEntry(new FormEntryString("Threshold", "group_objective_threshold").setReadOnly()
                    .setInitialValue(groupObjective.getThreshold()));
        }
        form.endForm();
        data.setContent(form.process());
    }
}
