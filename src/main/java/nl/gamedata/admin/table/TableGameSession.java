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
import nl.gamedata.admin.form.table.TableEntryBoolean;
import nl.gamedata.admin.form.table.TableEntryDate;
import nl.gamedata.admin.form.table.TableEntryDateTime;
import nl.gamedata.admin.form.table.TableEntryInt;
import nl.gamedata.admin.form.table.TableEntryPickRecord;
import nl.gamedata.admin.form.table.TableEntryString;
import nl.gamedata.admin.form.table.TableEntryText;
import nl.gamedata.admin.form.table.TableForm;
import nl.gamedata.common.Access;
import nl.gamedata.common.SqlUtils;
import nl.gamedata.data.Tables;
import nl.gamedata.data.tables.records.GameRecord;
import nl.gamedata.data.tables.records.GameSessionRecord;
import nl.gamedata.data.tables.records.GameVersionRecord;
import nl.gamedata.data.tables.records.OrganizationRecord;

/**
 * MaintainGameSession takes care of the game version screen.
 * <p>
 * Copyright (c) 2024-2024 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="https://github.com/averbraeck/gamedata-admin/LICENSE">GameData project License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public class TableGameSession
{
    public static void table(final AdminData data, final HttpServletRequest request, final String menuChoice)
    {
        AdminTable table = new AdminTable(data, "Game Session", "Code");
        boolean access = data.isSuperAdmin() || data.isOrganizationAdmin() || data.hasGameSessionAccess(Access.CREATE);
        if (access)
        {
            data.getTopbar().addNewButton();
            data.getTopbar().addImportButton();
        }
        data.getTopbar().addExportButton();
        table.setHeader("Game", "Version", "Code", "Name", "Valid", "Archived");
        List<Record> gsList = data.getDSL()
                .selectFrom(Tables.GAME_SESSION.join(Tables.GAME_VERSION)
                        .on(Tables.GAME_SESSION.GAME_VERSION_ID.eq(Tables.GAME_VERSION.ID)).join(Tables.GAME)
                        .on(Tables.GAME_VERSION.GAME_ID.eq(Tables.GAME.ID)))
                .fetch();
        for (var gs : gsList)
        {
            for (Integer gameId : data.getGameAccess().keySet())
            {
                if (gameId.equals(gs.getValue(Tables.GAME.ID)))
                {
                    int id = gs.getValue(Tables.GAME_SESSION.ID);
                    String game = gs.getValue(Tables.GAME.CODE);
                    String version = gs.getValue(Tables.GAME_VERSION.NAME);
                    String code = gs.getValue(Tables.GAME_SESSION.CODE);
                    String name = gs.getValue(Tables.GAME_SESSION.NAME);
                    String valid = gs.getValue(Tables.GAME_SESSION.VALID) == 0 ? "N" : "Y";
                    String archived = gs.getValue(Tables.GAME_SESSION.ARCHIVED) == 0 ? "N" : "Y";
                    boolean edit = data.getGameSessionAccess().get(id) != null && data.getGameSessionAccess().get(id).edit();
                    boolean delete =
                            data.getGameSessionAccess().get(id) != null && data.getGameSessionAccess().get(id).create();
                    table.addRow(id, false, edit, delete, game, version, code, name, valid, archived);
                    break;
                }
            }
        }
        table.process();
    }

    public static void edit(final AdminData data, final HttpServletRequest request, final String click, final int recordId)
    {
        int phase = WebForm.getPhase(request);
        Integer organizationId = WebForm.getIntParameter(request, "organization_id");
        if (click.equals("record-new") && (phase == 0 || organizationId == null))
        {
            WebForm form = new WebForm(data);
            form.startForm();
            form.setHeader("Game Session");
            form.setPhase(1);
            form.addEntry(new FormEntryPickRecord("Organization", "organization_id").setPickTable(data,
                    data.getOrganizationPicklist(Access.VIEW), Tables.ORGANIZATION.ID, Tables.ORGANIZATION.CODE)
                    .setLabel("Organization"));
            form.setOkMethod("record-new");
            form.endForm();
            data.setContent(form.process());
            return;
        }

        Integer gameId = WebForm.getIntParameter(request, "game_id");
        if (click.equals("record-new") && organizationId != null && (phase == 1 || gameId == null))
        {
            WebForm form = new WebForm(data);
            form.startForm();
            form.setHeader("Game Session");
            form.setPhase(2);
            OrganizationRecord organization = SqlUtils.readRecordFromId(data, Tables.ORGANIZATION, organizationId);
            form.addEntry(new FormEntryInt("Organization id", "organization_id").setHidden().setReadOnly()
                    .setInitialValue(organizationId, organizationId));
            form.addEntry(new FormEntryString("Organization", "organization").setReadOnly()
                    .setInitialValue(organization.getCode(), organization.getCode()));
            form.addEntry(new FormEntryPickRecord("Game", "game_id")
                    .setPickTable(data, data.getGamePicklist(organizationId, Access.EDIT), Tables.GAME.ID, Tables.GAME.CODE)
                    .setLabel("Game"));
            form.setOkMethod("record-new");
            form.endForm();
            data.setContent(form.process());
            return;
        }

        if ((phase == 2 && organizationId != null && gameId != null) || !click.equals("record-new"))
        {
            GameSessionRecord gameSession = recordId == 0 ? Tables.GAME_SESSION.newRecord()
                    : SqlUtils.readRecordFromId(data, Tables.GAME_SESSION, recordId);
            boolean reedit = click.contains("reedit");
            if (!click.equals("record-new"))
            {
                organizationId = gameSession.getOrganizationId();
                GameVersionRecord gameVersion =
                        SqlUtils.readRecordFromId(data, Tables.GAME_VERSION, gameSession.getGameVersionId());
                gameId = gameVersion.getGameId();
            }
            TableForm form = new TableForm(data);
            form.startForm();
            data.setEditRecord(gameSession);
            form.setHeader("Game Session", click, recordId);
            form.setPhase(1);
            OrganizationRecord organization = SqlUtils.readRecordFromId(data, Tables.ORGANIZATION, organizationId);
            form.addEntry(new TableEntryInt(data, reedit, Tables.GAME_SESSION.ORGANIZATION_ID, gameSession)
                    .setInitialValue(organizationId).setHidden().setReadOnly());
            form.addEntry(new FormEntryString("Organization", "organization").setReadOnly()
                    .setInitialValue(organization.getCode(), organization.getCode()));
            GameRecord game = SqlUtils.readRecordFromId(data, Tables.GAME, gameId);
            form.addEntry(new FormEntryInt("Game id", "game_id").setHidden().setReadOnly().setInitialValue(gameId, gameId));
            form.addEntry(new FormEntryString("Game", "game").setReadOnly().setInitialValue(game.getCode(), game.getCode()));
            form.addEntry(new TableEntryPickRecord(data, reedit, Tables.GAME_SESSION.GAME_VERSION_ID, gameSession)
                    .setPickTable(data, data.getGameVersionPicklist(gameId, Access.VIEW)).setLabel("Game Version"));
            form.addEntry(new TableEntryString(data, reedit, Tables.GAME_SESSION.CODE, gameSession).setMinLength(2));
            form.addEntry(new TableEntryString(data, reedit, Tables.GAME_SESSION.NAME, gameSession).setMinLength(2));
            form.addEntry(new TableEntryText(data, reedit, Tables.GAME_SESSION.DESCRIPTION, gameSession));
            form.addEntry(new TableEntryString(data, reedit, Tables.GAME_SESSION.SESSION_TOKEN, gameSession).setMinLength(2));
            form.addEntry(new TableEntryString(data, reedit, Tables.GAME_SESSION.SESSION_STATUS, gameSession).setMinLength(2));
            form.addEntry(new TableEntryBoolean(data, reedit, Tables.GAME_SESSION.TOKEN_FORCED, gameSession));
            form.addEntry(new TableEntryDate(data, reedit, Tables.GAME_SESSION.PLAY_DATE, gameSession));
            form.addEntry(new TableEntryBoolean(data, reedit, Tables.GAME_SESSION.VALID, gameSession));
            form.addEntry(new TableEntryDateTime(data, reedit, Tables.GAME_SESSION.VALID_FROM, gameSession));
            form.addEntry(new TableEntryDateTime(data, reedit, Tables.GAME_SESSION.VALID_UNTIL, gameSession));
            form.addEntry(new TableEntryBoolean(data, reedit, Tables.GAME_SESSION.TOKEN_FOR_DASHBOARD, gameSession));
            form.addEntry(new TableEntryBoolean(data, reedit, Tables.GAME_SESSION.ARCHIVED, gameSession).setLabel("Archived?"));
            form.endForm();
            data.setContent(form.process());
            return;
        }

        String s = "Unknown state, pahse=" + phase + ", gameId=" + gameId + ", click=" + click;
        data.setContent(s);
        System.err.println(s);
    }
}
