package nl.gamedata.admin.table;

import java.util.List;

import org.jooq.Record;

import jakarta.servlet.http.HttpServletRequest;
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
 * MaintainGame takes care of the group event screen.
 * <p>
 * Copyright (c) 2024-2024 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="https://github.com/averbraeck/gamedata-admin/LICENSE">GameData project License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public class TableGroupEvent
{
    public static void table(final AdminData data, final HttpServletRequest request, final String menuChoice)
    {
        AdminTable table = new AdminTable(data, "Group Event", "Session");
        data.getTopbar().addExportButton();
        table.setHeader("Session", "Group", "Attempt", "Timestamp", "Type", "Key", "Value");
        for (var gameSessionId : data.getGameSessionAccess(Access.VIEW))
        {
            var gameSession = SqlUtils.readRecordFromId(data, Tables.GAME_SESSION, gameSessionId);
            List<Record> peList = data.getDSL()
                    .selectFrom(Tables.GROUP_EVENT.join(Tables.GROUP_ATTEMPT)
                            .on(Tables.GROUP_EVENT.GROUP_ATTEMPT_ID.eq(Tables.GROUP_ATTEMPT.ID)).join(Tables.GROUP)
                            .on(Tables.GROUP_ATTEMPT.GROUP_ID.eq(Tables.GROUP.ID)).join(Tables.GAME_SESSION)
                            .on(Tables.GROUP.GAME_SESSION_ID.eq(Tables.GAME_SESSION.ID)))
                    .where(Tables.GAME_SESSION.ID.eq(gameSessionId)).fetch();
            for (var me : peList)
            {
                int id = me.getValue(Tables.GROUP_EVENT.ID);
                String group = me.getValue(Tables.GROUP.NAME);
                String attempt = String.valueOf(me.getValue(Tables.GROUP_ATTEMPT.ATTEMPT_NR));
                String timestamp = me.getValue(Tables.GROUP_EVENT.TIMESTAMP).toString();
                String type = me.getValue(Tables.GROUP_EVENT.TYPE);
                String key = me.getValue(Tables.GROUP_EVENT.KEY);
                String value = me.getValue(Tables.GROUP_EVENT.VALUE);
                table.addRow(id, false, false, false, gameSession.getName(), group, attempt, timestamp, type, key, value);
            }
        }
        table.process();
    }

    public static void view(final AdminData data, final HttpServletRequest request, final String click, final int recordId)
    {
        var groupEvent = SqlUtils.readRecordFromId(data, Tables.GROUP_EVENT, recordId);
        var groupAttempt = SqlUtils.readRecordFromId(data, Tables.GROUP_ATTEMPT, groupEvent.getGroupAttemptId());
        var group = SqlUtils.readRecordFromId(data, Tables.GROUP, groupAttempt.getGroupId());
        var gameSession = SqlUtils.readRecordFromId(data, Tables.GAME_SESSION, group.getGameSessionId());
        data.setEditRecord(groupEvent);
        WebForm form = new WebForm(data);
        form.startForm();
        form.setHeader("Group Event");
        form.addEntry(new FormEntryString("Game Session", "game_session").setReadOnly().setInitialValue(gameSession.getName()));
        form.addEntry(new FormEntryString("Group", "game_group").setReadOnly().setInitialValue(group.getName()));
        form.addEntry(new FormEntryInt("Attempt", "attempt").setReadOnly().setInitialValue(groupAttempt.getAttemptNr()));
        form.addEntry(new FormEntryDateTime("Timestamp", "timestamp").setReadOnly().setInitialValue(groupEvent.getTimestamp()));
        form.addEntry(new FormEntryString("Type", "type").setReadOnly().setInitialValue(groupEvent.getType()));
        form.addEntry(new FormEntryString("Key", "key").setReadOnly().setInitialValue(groupEvent.getKey()));
        form.addEntry(new FormEntryString("Value", "value").setReadOnly().setInitialValue(groupEvent.getValue()));
        form.addEntry(new FormEntryString("Status", "status").setReadOnly().setInitialValue(groupEvent.getStatus(), "-"));
        form.addEntry(new FormEntryString("Round", "round").setReadOnly().setInitialValue(groupEvent.getRound(), "-"));
        form.addEntry(
                new FormEntryString("Game Time", "game_time").setReadOnly().setInitialValue(groupEvent.getGameTime(), "-"));
        form.addEntry(new FormEntryString("Grouping Code", "grouping_code").setReadOnly()
                .setInitialValue(groupEvent.getGroupingCode(), "-"));
        form.addEntry(new FormEntryBoolean("Group initiated", "group_initiated").setReadOnly()
                .setInitialValue(groupEvent.getGroupInitiated(), (byte) 0));
        form.endForm();
        data.setContent(form.process());
    }
}
