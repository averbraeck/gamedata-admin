package nl.gamedata.admin.table;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import nl.gamedata.admin.AdminData;
import nl.gamedata.admin.AdminTable;
import nl.gamedata.admin.form.FormEntryInt;
import nl.gamedata.admin.form.FormEntryString;
import nl.gamedata.admin.form.WebForm;
import nl.gamedata.common.Access;
import nl.gamedata.common.SqlUtils;
import nl.gamedata.data.Tables;
import nl.gamedata.data.tables.records.GroupAttemptRecord;
import nl.gamedata.data.tables.records.GroupRecord;

/**
 * MaintainGame takes care of the group screen.
 * <p>
 * Copyright (c) 2024-2024 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="https://github.com/averbraeck/gamedata-admin/LICENSE">GameData project License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public class TableGroupAttempt
{
    public static void table(final AdminData data, final HttpServletRequest request, final String menuChoice)
    {
        AdminTable table = new AdminTable(data, "Group Attempt", "Session");
        data.getTopbar().addExportButton();
        table.setHeader("Session", "Group Name", "Attempt", "Status");
        for (var gameSessionId : data.getGameSessionAccess(Access.VIEW))
        {
            var gameSession = SqlUtils.readRecordFromId(data, Tables.GAME_SESSION, gameSessionId);
            List<GroupRecord> groupList =
                    data.getDSL().selectFrom(Tables.GROUP).where(Tables.GROUP.GAME_SESSION_ID.eq(gameSessionId)).fetch();
            for (var group : groupList)
            {
                List<GroupAttemptRecord> paList = data.getDSL().selectFrom(Tables.GROUP_ATTEMPT)
                        .where(Tables.GROUP_ATTEMPT.GROUP_ID.eq(group.getId())).fetch();
                for (var pa : paList)
                {
                    table.addRow(group.getId(), false, false, false, gameSession.getName(), group.getName(),
                            String.valueOf(pa.getAttemptNr()), pa.getStatus());
                }
            }
        }
        table.process();
    }

    public static void view(final AdminData data, final HttpServletRequest request, final String click, final int recordId)
    {
        var groupAttempt = SqlUtils.readRecordFromId(data, Tables.GROUP_ATTEMPT, recordId);
        var group = SqlUtils.readRecordFromId(data, Tables.GROUP, groupAttempt.getGroupId());
        var gameSession = SqlUtils.readRecordFromId(data, Tables.GAME_SESSION, group.getGameSessionId());
        data.setEditRecord(group);
        WebForm form = new WebForm(data);
        form.startForm();
        form.setHeader("Group");
        form.addEntry(new FormEntryString("Game Session", "game_session").setReadOnly().setInitialValue(gameSession.getName()));
        form.addEntry(new FormEntryString("Group Name", "name").setReadOnly().setInitialValue(group.getName()));
        form.addEntry(new FormEntryInt("Attempt nr", "attempt_nr").setReadOnly().setInitialValue(groupAttempt.getAttemptNr()));
        form.addEntry(new FormEntryString("Status", "status").setReadOnly().setInitialValue(groupAttempt.getStatus()));
        form.endForm();
        data.setContent(form.process());
    }
}
