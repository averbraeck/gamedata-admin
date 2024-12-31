package nl.gamedata.admin.table;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.jooq.Record;

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
public class TableGroupRole
{
    public static void table(final AdminData data, final HttpServletRequest request, final String menuChoice)
    {
        AdminTable table = new AdminTable(data, "Group Roles for Players", "Session");
        table.setNewButton(data.isSuperAdmin() || data.hasGameSessionAccess(Access.VIEW));
        table.setHeader("Session", "Group Name", "Player", "Role");
        for (var gameSessionId : data.getGameSessionAccess(Access.VIEW))
        {
            var gameSession = SqlUtils.readRecordFromId(data, Tables.GAME_SESSION, gameSessionId);
            List<GroupRecord> groupList =
                    data.getDSL().selectFrom(Tables.GROUP).where(Tables.GROUP.GAME_SESSION_ID.eq(gameSessionId)).fetch();
            for (var group : groupList)
            {
                List<Record> prList = data.getDSL()
                        .selectFrom(
                                Tables.PLAYER.join(Tables.GROUP_ROLE).on(Tables.GROUP_ROLE.PLAYER_ID.eq(Tables.PLAYER.ID)))
                        .where(Tables.GROUP_ROLE.GROUP_ID.eq(group.getId())).fetch();
                for (var pr : prList)
                {
                    table.addRow(pr.getValue(Tables.GROUP_ROLE.ID), false, false, false, gameSession.getName(), group.getName(),
                            pr.getValue(Tables.PLAYER.NAME), pr.getValue(Tables.GROUP_ROLE.NAME));
                }
            }
        }
        table.process();
    }

    public static void view(final AdminData data, final HttpServletRequest request, final String click, final int recordId)
    {
        var groupRole = SqlUtils.readRecordFromId(data, Tables.GROUP_ROLE, recordId);
        var player = SqlUtils.readRecordFromId(data, Tables.PLAYER, groupRole.getPlayerId());
        var group = SqlUtils.readRecordFromId(data, Tables.GROUP, groupRole.getGroupId());
        var gameSession = SqlUtils.readRecordFromId(data, Tables.GAME_SESSION, group.getGameSessionId());
        data.setEditRecord(groupRole);
        WebForm form = new WebForm(data);
        form.startForm();
        form.setHeader("Group Role for Player");
        form.addEntry(new FormEntryString("Game Session", "game_session").setReadOnly().setInitialValue(gameSession.getName()));
        form.addEntry(new FormEntryString("Player Name", "player_name").setReadOnly().setInitialValue(player.getName()));
        form.addEntry(
                new FormEntryString("Display Name", "display_name").setReadOnly().setInitialValue(player.getDisplayName()));
        form.addEntry(new FormEntryString("Group Name", "group_name").setReadOnly().setInitialValue(group.getName()));
        form.addEntry(new FormEntryString("Group Role", "role").setReadOnly().setInitialValue(groupRole.getName()));
        form.endForm();
        data.setContent(form.process());
    }
}
