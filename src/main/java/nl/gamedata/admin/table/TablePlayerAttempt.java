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
import nl.gamedata.data.tables.records.PlayerAttemptRecord;
import nl.gamedata.data.tables.records.PlayerRecord;

/**
 * MaintainGame takes care of the player screen.
 * <p>
 * Copyright (c) 2024-2024 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="https://github.com/averbraeck/gamedata-admin/LICENSE">GameData project License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public class TablePlayerAttempt
{
    public static void table(final AdminData data, final HttpServletRequest request, final String menuChoice)
    {
        AdminTable table = new AdminTable(data, "Player Attempt", "Session");
        data.getTopbar().addExportButton();
        table.setHeader("Session", "Player Name", "Display Name", "Attempt", "Status");
        for (var gameSessionId : data.getGameSessionAccess(Access.VIEW))
        {
            var gameSession = SqlUtils.readRecordFromId(data, Tables.GAME_SESSION, gameSessionId);
            List<PlayerRecord> playerList =
                    data.getDSL().selectFrom(Tables.PLAYER).where(Tables.PLAYER.GAME_SESSION_ID.eq(gameSessionId)).fetch();
            for (var player : playerList)
            {
                List<PlayerAttemptRecord> paList = data.getDSL().selectFrom(Tables.PLAYER_ATTEMPT)
                        .where(Tables.PLAYER_ATTEMPT.PLAYER_ID.eq(player.getId())).fetch();
                for (var pa : paList)
                {
                    table.addRow(player.getId(), false, false, false, gameSession.getName(), player.getName(),
                            player.getDisplayName(), String.valueOf(pa.getAttemptNr()), pa.getStatus());
                }
            }
        }
        table.process();
    }

    public static void view(final AdminData data, final HttpServletRequest request, final String click, final int recordId)
    {
        var playerAttempt = SqlUtils.readRecordFromId(data, Tables.PLAYER_ATTEMPT, recordId);
        var player = SqlUtils.readRecordFromId(data, Tables.PLAYER, playerAttempt.getPlayerId());
        var gameSession = SqlUtils.readRecordFromId(data, Tables.GAME_SESSION, player.getGameSessionId());
        data.setEditRecord(player);
        WebForm form = new WebForm(data);
        form.startForm();
        form.setHeader("Player");
        form.addEntry(new FormEntryString("Game Session", "game_session").setReadOnly().setInitialValue(gameSession.getName()));
        form.addEntry(new FormEntryString("Player Name", "name").setReadOnly().setInitialValue(player.getName()));
        form.addEntry(
                new FormEntryString("Display Name", "display_name").setReadOnly().setInitialValue(player.getDisplayName()));
        form.addEntry(new FormEntryInt("Attempt nr", "attempt_nr").setReadOnly().setInitialValue(playerAttempt.getAttemptNr()));
        form.addEntry(new FormEntryString("Status", "status").setReadOnly().setInitialValue(playerAttempt.getStatus()));
        form.endForm();
        data.setContent(form.process());
    }
}
