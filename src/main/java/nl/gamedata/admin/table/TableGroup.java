package nl.gamedata.admin.table;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import nl.gamedata.admin.AdminData;
import nl.gamedata.admin.AdminTable;
import nl.gamedata.admin.form.FormEntryString;
import nl.gamedata.admin.form.WebForm;
import nl.gamedata.common.Access;
import nl.gamedata.common.SqlUtils;
import nl.gamedata.data.Tables;
import nl.gamedata.data.tables.records.GroupRecord;

/**
 * MaintainGame takes care of the group screen.
 * <p>
 * Copyright (c) 2024-2024 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="https://github.com/averbraeck/gamedata-admin/LICENSE">GameData project License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public class TableGroup
{
    public static void table(final AdminData data, final HttpServletRequest request, final String menuChoice)
    {
        AdminTable table = new AdminTable(data, "Group", "Name");
        table.setNewButton(data.isSuperAdmin() || data.hasGameSessionAccess(Access.VIEW));
        table.setHeader("Session", "Group Name", "Display Name");
        for (var gameSessionId : data.getGameSessionAccess(Access.VIEW))
        {
            var gameSession = SqlUtils.readRecordFromId(data, Tables.GAME_SESSION, gameSessionId);
            List<GroupRecord> groupList =
                    data.getDSL().selectFrom(Tables.GROUP).where(Tables.GROUP.GAME_SESSION_ID.eq(gameSessionId)).fetch();
            for (var group : groupList)
            {
                table.addRow(group.getId(), false, false, false, gameSession.getName(), group.getName());
            }
        }
        table.process();
    }

    public static void view(final AdminData data, final HttpServletRequest request, final String click, final int recordId)
    {
        var group = SqlUtils.readRecordFromId(data, Tables.GROUP, recordId);
        var gameSession = SqlUtils.readRecordFromId(data, Tables.GAME_SESSION, group.getGameSessionId());
        data.setEditRecord(group);
        WebForm form = new WebForm(data);
        form.startForm();
        form.setHeader("Group");
        form.addEntry(new FormEntryString("Game Session", "game_session").setReadOnly().setInitialValue(gameSession.getName()));
        form.addEntry(new FormEntryString("Group Name", "name").setReadOnly().setInitialValue(group.getName()));
        form.endForm();
        data.setContent(form.process());
    }
}
