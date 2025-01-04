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
import nl.gamedata.data.tables.records.OrganizationRoleRecord;

/**
 * MaintainGame takes care of the game screen.
 * <p>
 * Copyright (c) 2024-2024 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="https://github.com/averbraeck/gamedata-admin/LICENSE">GameData project License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public class TableOrganizationRole
{
    public static void table(final AdminData data, final HttpServletRequest request, final String menuChoice)
    {
        AdminTable table = new AdminTable(data, "User Roles in Organizations", "Organization");
        boolean access = data.isSuperAdmin() || data.isOrganizationAdmin();
        table.setNewButton(access);
        table.setHeader("Organization", "User", "Admin", "Edit", "View");
        List<Record> orList = data.getDSL()
                .selectFrom(Tables.ORGANIZATION_ROLE.join(Tables.ORGANIZATION)
                        .on(Tables.ORGANIZATION_ROLE.ORGANIZATION_ID.eq(Tables.ORGANIZATION.ID)).join(Tables.USER)
                        .on(Tables.ORGANIZATION_ROLE.USER_ID.eq(Tables.USER.ID)))
                .fetch();
        for (var or : orList)
        {
            for (Integer organizationId : data.getOrganizationAccess().keySet())
            {
                if (organizationId.equals(or.getValue(Tables.ORGANIZATION.ID)))
                {
                    int id = or.getValue(Tables.ORGANIZATION_ROLE.ID);
                    String org = or.getValue(Tables.ORGANIZATION.CODE);
                    String user = or.getValue(Tables.USER.NAME);
                    String admin = or.getValue(Tables.ORGANIZATION_ROLE.ADMIN) == 0 ? "N" : "Y";
                    String edit = or.getValue(Tables.ORGANIZATION_ROLE.EDIT) == 0 ? "N" : "Y";
                    String view = or.getValue(Tables.ORGANIZATION_ROLE.VIEW) == 0 ? "N" : "Y";
                    table.addRow(id, false, access, access, org, user, admin, edit, view);
                    break;
                }
            }
        }
        table.process();
    }

    public static void edit(final AdminData data, final HttpServletRequest request, final String click, final int recordId)
    {
        OrganizationRoleRecord or = recordId == 0 ? Tables.ORGANIZATION_ROLE.newRecord()
                : SqlUtils.readRecordFromId(data, Tables.ORGANIZATION_ROLE, recordId);
        boolean reedit = click.contains("reedit");
        data.setEditRecord(or);
        TableForm form = new TableForm(data);
        form.startForm();
        form.setHeader("User-Role in Organization", click, recordId);
        form.addEntry(new TableEntryPickRecord(data, reedit, Tables.ORGANIZATION_ROLE.ORGANIZATION_ID, or)
                .setPickTable(data, data.getOrganizationPicklist(Access.EDIT), Tables.ORGANIZATION.ID, Tables.ORGANIZATION.CODE)
                .setLabel("Organization"));
        form.addEntry(new TableEntryPickRecord(data, reedit, Tables.ORGANIZATION_ROLE.USER_ID, or)
                .setPickTable(data, Tables.USER, Tables.USER.ID, Tables.USER.NAME).setLabel("User"));
        form.addEntry(new TableEntryBoolean(data, reedit, Tables.ORGANIZATION_ROLE.ADMIN, or));
        form.addEntry(new TableEntryBoolean(data, reedit, Tables.ORGANIZATION_ROLE.EDIT, or));
        form.addEntry(new TableEntryBoolean(data, reedit, Tables.ORGANIZATION_ROLE.VIEW, or));
        form.endForm();
        data.setContent(form.process());
    }
}
