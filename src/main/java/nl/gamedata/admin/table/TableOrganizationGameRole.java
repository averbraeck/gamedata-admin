package nl.gamedata.admin.table;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.jooq.Record;

import nl.gamedata.admin.AdminData;
import nl.gamedata.admin.AdminTable;
import nl.gamedata.admin.form.table.TableEntryBoolean;
import nl.gamedata.admin.form.table.TableEntryPickRecord;
import nl.gamedata.admin.form.table.TableForm;
import nl.gamedata.common.Access;
import nl.gamedata.common.SqlUtils;
import nl.gamedata.data.Tables;
import nl.gamedata.data.tables.records.OrganizationGameRoleRecord;

/**
 * MaintainGame takes care of the game screen.
 * <p>
 * Copyright (c) 2024-2024 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="https://github.com/averbraeck/gamedata-admin/LICENSE">GameData project License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public class TableOrganizationGameRole
{
    public static void table(final AdminData data, final HttpServletRequest request, final String menuChoice)
    {
        AdminTable table = new AdminTable(data, "User Roles for Organization Games", "Org-Game");
        boolean access = data.isSuperAdmin() || data.isOrganizationAdmin();
        table.setNewButton(access);
        table.setHeader("Org-Game", "User", "Edit", "View");
        List<Record> ogrList = data.getDSL()
                .selectFrom(Tables.ORGANIZATION_GAME_ROLE.join(Tables.ORGANIZATION_GAME)
                        .on(Tables.ORGANIZATION_GAME_ROLE.ORGANIZATION_GAME_ID.eq(Tables.ORGANIZATION_GAME.ID))
                        .join(Tables.USER).on(Tables.ORGANIZATION_GAME_ROLE.USER_ID.eq(Tables.USER.ID)))
                .fetch();
        for (var ogr : ogrList)
        {
            for (Integer ogId : data.getOrganizationGameAccess().keySet())
            {
                if (ogId.equals(ogr.getValue(Tables.ORGANIZATION_GAME.ID)))
                {
                    int id = ogr.getValue(Tables.ORGANIZATION_GAME_ROLE.ID);
                    String org = ogr.getValue(Tables.ORGANIZATION_GAME.NAME);
                    String user = ogr.getValue(Tables.USER.NAME);
                    String edit = ogr.getValue(Tables.ORGANIZATION_GAME_ROLE.EDIT) == 0 ? "N" : "Y";
                    String view = ogr.getValue(Tables.ORGANIZATION_GAME_ROLE.VIEW) == 0 ? "N" : "Y";
                    table.addRow(id, false, access, access, org, user, edit, view);
                    break;
                }
            }
        }
        table.process();
    }

    public static void edit(final AdminData data, final HttpServletRequest request, final String click, final int recordId)
    {
        OrganizationGameRoleRecord ogr = recordId == 0 ? Tables.ORGANIZATION_GAME_ROLE.newRecord()
                : SqlUtils.readRecordFromId(data, Tables.ORGANIZATION_GAME_ROLE, recordId);
        boolean reedit = click.contains("reedit");
        data.setEditRecord(ogr);
        TableForm form = new TableForm(data);
        form.startForm();
        form.setHeader("User-Role for Organization-Game", click, recordId);
        form.addEntry(new TableEntryPickRecord(data, reedit, Tables.ORGANIZATION_GAME_ROLE.ORGANIZATION_GAME_ID, ogr)
                .setPickTable(data, data.getOrganizationGamePicklist(Access.EDIT), Tables.ORGANIZATION_GAME.ID,
                        Tables.ORGANIZATION_GAME.NAME)
                .setLabel("Organization-Game"));
        form.addEntry(new TableEntryPickRecord(data, reedit, Tables.ORGANIZATION_GAME_ROLE.USER_ID, ogr)
                .setPickTable(data, Tables.USER, Tables.USER.ID, Tables.USER.NAME).setLabel("User"));
        form.addEntry(new TableEntryBoolean(data, reedit, Tables.ORGANIZATION_GAME_ROLE.EDIT, ogr));
        form.addEntry(new TableEntryBoolean(data, reedit, Tables.ORGANIZATION_GAME_ROLE.VIEW, ogr));
        form.endForm();
        data.setContent(form.process());
    }
}
