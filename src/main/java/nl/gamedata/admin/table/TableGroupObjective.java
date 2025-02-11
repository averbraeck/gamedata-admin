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
import nl.gamedata.admin.form.table.TableEntryInt;
import nl.gamedata.admin.form.table.TableEntryPickRecord;
import nl.gamedata.admin.form.table.TableEntryString;
import nl.gamedata.admin.form.table.TableEntryText;
import nl.gamedata.admin.form.table.TableForm;
import nl.gamedata.common.Access;
import nl.gamedata.common.SqlUtils;
import nl.gamedata.data.Tables;
import nl.gamedata.data.tables.records.GameMissionRecord;
import nl.gamedata.data.tables.records.GameRecord;
import nl.gamedata.data.tables.records.GameVersionRecord;
import nl.gamedata.data.tables.records.GroupObjectiveRecord;
import nl.gamedata.data.tables.records.LearningGoalRecord;
import nl.gamedata.data.tables.records.ScaleRecord;

/**
 * MaintainGameSession takes care of the game version screen.
 * <p>
 * Copyright (c) 2024-2024 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="https://github.com/averbraeck/gamedata-admin/LICENSE">GameData project License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public class TableGroupObjective
{
    public static void table(final AdminData data, final HttpServletRequest request, final String menuChoice)
    {
        AdminTable table = new AdminTable(data, "Group Objective", "Name");
        boolean admin = data.isSuperAdmin() || data.isGameAdmin() || data.hasGameAccess(Access.EDIT);
        if (admin)
        {
            data.getTopbar().addNewButton();
            data.getTopbar().addImportButton();
        }
        data.getTopbar().addExportButton();
        table.setHeader("Game", "Version", "Mission", "Learning Goal", "Objective", "Scale", "Threshold");
        List<Record> poList = data.getDSL()
                .selectFrom(Tables.GROUP_OBJECTIVE.join(Tables.LEARNING_GOAL)
                        .on(Tables.GROUP_OBJECTIVE.LEARNING_GOAL_ID.eq(Tables.LEARNING_GOAL.ID)).join(Tables.GAME_MISSION)
                        .on(Tables.LEARNING_GOAL.GAME_MISSION_ID.eq(Tables.GAME_MISSION.ID)).join(Tables.GAME_VERSION)
                        .on(Tables.GAME_MISSION.GAME_VERSION_ID.eq(Tables.GAME_VERSION.ID)).join(Tables.GAME)
                        .on(Tables.GAME_VERSION.GAME_ID.eq(Tables.GAME.ID)))
                .fetch();
        for (var po : poList)
        {
            for (Integer gameId : data.getGameAccess().keySet())
            {
                if (gameId.equals(po.getValue(Tables.GAME.ID)))
                {
                    int id = po.getValue(Tables.GROUP_OBJECTIVE.ID);
                    String game = po.getValue(Tables.GAME.CODE);
                    String version = po.getValue(Tables.GAME_VERSION.NAME);
                    String mission = po.getValue(Tables.GAME_MISSION.NAME);
                    String learningGoal = po.getValue(Tables.LEARNING_GOAL.NAME);
                    String code = po.getValue(Tables.GROUP_OBJECTIVE.CODE);
                    ScaleRecord scale =
                            SqlUtils.readRecordFromId(data, Tables.SCALE, po.getValue(Tables.GROUP_OBJECTIVE.SCALE_ID));
                    String threshold = po.getValue(Tables.GROUP_OBJECTIVE.THRESHOLD);
                    boolean edit = data.getGameAccess().get(id).edit();
                    table.addRow(id, false, edit, admin, game, version, mission, learningGoal, code, scale.getType(),
                            threshold);
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
            form.setHeader("Group Objective");
            form.setPhase(1);
            form.addEntry(new FormEntryPickRecord("Game", "game_id")
                    .setPickTable(data, data.getGamePicklist(Access.EDIT), Tables.GAME.ID, Tables.GAME.CODE).setLabel("Game"));
            form.setOkMethod("record-new");
            form.endForm();
            data.setContent(form.process());
            return;
        }

        Integer gameVersionId = WebForm.getIntParameter(request, "game_version_id");
        if (click.equals("record-new") && gameId != null && (phase == 1 || gameVersionId == null))
        {
            WebForm form = new WebForm(data);
            form.startForm();
            form.setHeader("Group Objective");
            form.setPhase(2);
            GameRecord game = SqlUtils.readRecordFromId(data, Tables.GAME, gameId);
            form.addEntry(new FormEntryInt("Game id", "game_id").setHidden().setReadOnly().setInitialValue(gameId, gameId));
            form.addEntry(new FormEntryString("Game", "game").setReadOnly().setInitialValue(game.getCode(), game.getCode()));
            form.addEntry(new FormEntryPickRecord("Game Version", "game_version_id").setPickTable(data,
                    data.getGameVersionPicklist(gameId, Access.EDIT)));
            form.setOkMethod("record-new");
            form.endForm();
            data.setContent(form.process());
            return;
        }

        Integer gameMissionId = WebForm.getIntParameter(request, "game_mission_id");
        if (click.equals("record-new") && gameId != null && gameVersionId != null && (phase == 2 || gameMissionId == null))
        {
            WebForm form = new WebForm(data);
            form.startForm();
            form.setHeader("Group Objective");
            form.setPhase(3);
            GameRecord game = SqlUtils.readRecordFromId(data, Tables.GAME, gameId);
            form.addEntry(new FormEntryInt("Game id", "game_id").setHidden().setReadOnly().setInitialValue(gameId, gameId));
            form.addEntry(new FormEntryString("Game", "game").setReadOnly().setInitialValue(game.getCode(), game.getCode()));
            GameVersionRecord gameVersion = SqlUtils.readRecordFromId(data, Tables.GAME_VERSION, gameVersionId);
            form.addEntry(new FormEntryInt("Game Version id", "game_version_id").setHidden().setReadOnly()
                    .setInitialValue(gameVersionId, gameVersionId));
            form.addEntry(new FormEntryString("Game Version", "game_version").setReadOnly()
                    .setInitialValue(gameVersion.getName(), gameVersion.getName()));
            form.addEntry(new FormEntryPickRecord("Game Mission", "game_mission_id").setPickTable(data, Tables.GAME_MISSION,
                    Tables.GAME_MISSION.ID, Tables.GAME_MISSION.NAME, Tables.GAME_MISSION.GAME_VERSION_ID.eq(gameVersionId)));
            form.setOkMethod("record-new");
            form.endForm();
            data.setContent(form.process());
            return;
        }

        Integer learningGoalId = WebForm.getIntParameter(request, "learning_goal_id");
        if (click.equals("record-new") && gameId != null && gameVersionId != null && gameMissionId != null
                && (phase == 3 || learningGoalId == null))
        {
            WebForm form = new WebForm(data);
            form.startForm();
            form.setHeader("Group Objective");
            form.setPhase(4);
            GameRecord game = SqlUtils.readRecordFromId(data, Tables.GAME, gameId);
            form.addEntry(new FormEntryInt("Game id", "game_id").setHidden().setReadOnly().setInitialValue(gameId, gameId));
            form.addEntry(new FormEntryString("Game", "game").setReadOnly().setInitialValue(game.getCode(), game.getCode()));
            GameVersionRecord gameVersion = SqlUtils.readRecordFromId(data, Tables.GAME_VERSION, gameVersionId);
            form.addEntry(new FormEntryInt("Game Version id", "game_version_id").setHidden().setReadOnly()
                    .setInitialValue(gameVersionId, gameVersionId));
            form.addEntry(new FormEntryString("Game Version", "game_version").setReadOnly()
                    .setInitialValue(gameVersion.getName(), gameVersion.getName()));
            GameMissionRecord gameMission = SqlUtils.readRecordFromId(data, Tables.GAME_MISSION, gameMissionId);
            form.addEntry(new FormEntryInt("Game Mission id", "game_mission_id").setHidden().setReadOnly()
                    .setInitialValue(gameMissionId, gameMissionId));
            form.addEntry(new FormEntryString("Game Mission", "game_mission").setReadOnly()
                    .setInitialValue(gameMission.getName(), gameMission.getName()));
            form.addEntry(new FormEntryPickRecord("Learning Goal", "learning_goal_id").setPickTable(data, Tables.LEARNING_GOAL,
                    Tables.LEARNING_GOAL.ID, Tables.LEARNING_GOAL.NAME,
                    Tables.LEARNING_GOAL.GAME_MISSION_ID.eq(gameMissionId)));
            form.setOkMethod("record-new");
            form.endForm();
            data.setContent(form.process());
            return;
        }

        if ((phase == 4 && gameId != null && gameVersionId != null && gameMissionId != null && learningGoalId != null)
                || !click.equals("record-new"))
        {
            GroupObjectiveRecord groupObjective = recordId == 0 ? Tables.GROUP_OBJECTIVE.newRecord()
                    : SqlUtils.readRecordFromId(data, Tables.GROUP_OBJECTIVE, recordId);

            if (learningGoalId == null)
                learningGoalId = groupObjective.getLearningGoalId();
            LearningGoalRecord learningGoal = SqlUtils.readRecordFromId(data, Tables.LEARNING_GOAL, learningGoalId);
            if (gameMissionId == null)
                gameMissionId = learningGoal.getGameMissionId();
            GameMissionRecord gameMission = SqlUtils.readRecordFromId(data, Tables.GAME_MISSION, gameMissionId);
            if (gameVersionId == null)
                gameVersionId = gameMission.getGameVersionId();
            GameVersionRecord gameVersion = SqlUtils.readRecordFromId(data, Tables.GAME_VERSION, gameVersionId);
            if (gameId == null)
                gameId = gameVersion.getGameId();
            GameRecord game = SqlUtils.readRecordFromId(data, Tables.GAME, gameId);

            TableForm form = new TableForm(data);
            form.startForm();
            boolean reedit = click.contains("reedit");
            data.setEditRecord(groupObjective);
            form.setHeader("Larning Goal", click, recordId);
            form.setPhase(1);
            form.addEntry(new FormEntryInt("Game id", "game_id").setHidden().setReadOnly().setInitialValue(gameId, gameId));
            form.addEntry(new FormEntryString("Game", "game").setReadOnly().setInitialValue(game.getCode(), game.getCode()));
            form.addEntry(new FormEntryInt("Game Version id", "game_version_id").setHidden().setReadOnly()
                    .setInitialValue(gameVersionId, gameVersionId));
            form.addEntry(new FormEntryString("Game Version", "game_version").setReadOnly()
                    .setInitialValue(gameVersion.getName(), gameVersion.getName()));
            form.addEntry(new FormEntryInt("Game Mission id", "game_mission_id").setHidden().setReadOnly()
                    .setInitialValue(gameMissionId, gameMissionId));
            form.addEntry(new FormEntryString("Game Mission", "game_mission").setReadOnly()
                    .setInitialValue(gameMission.getName(), gameMission.getName()));
            form.addEntry(new TableEntryInt(data, reedit, Tables.GROUP_OBJECTIVE.LEARNING_GOAL_ID, groupObjective)
                    .setInitialValue(learningGoalId).setHidden().setReadOnly());
            form.addEntry(new FormEntryString("Learning Goal", "learning_goal").setReadOnly()
                    .setInitialValue(learningGoal.getName(), learningGoal.getName()));
            form.addEntry(new TableEntryString(data, reedit, Tables.GROUP_OBJECTIVE.CODE, groupObjective).setMinLength(2));
            form.addEntry(new TableEntryString(data, reedit, Tables.GROUP_OBJECTIVE.NAME, groupObjective).setMinLength(2));
            form.addEntry(new TableEntryText(data, reedit, Tables.GROUP_OBJECTIVE.DESCRIPTION, groupObjective));
            form.addEntry(new TableEntryPickRecord(data, reedit, Tables.GROUP_OBJECTIVE.SCALE_ID, groupObjective)
                    .setPickTable(data, Tables.SCALE, Tables.SCALE.ID, Tables.SCALE.TYPE, Tables.SCALE.GAME_ID.eq(gameId)));
            form.addEntry(new TableEntryString(data, reedit, Tables.GROUP_OBJECTIVE.THRESHOLD, groupObjective));
            form.endForm();
            data.setContent(form.process());
            return;
        }

        String s = "Unknown state, pahse=" + phase + ", gameId=" + gameVersionId + ", click=" + click;
        data.setContent(s);
        System.err.println(s);
    }
}
