package nl.gamedata.admin.table;

import java.util.List;

import org.jooq.Record;

import jakarta.servlet.http.HttpServletRequest;
import nl.gamedata.admin.AdminData;
import nl.gamedata.admin.AdminTable;
import nl.gamedata.admin.form.table.TableEntryBoolean;
import nl.gamedata.admin.form.table.TableEntryPickRecord;
import nl.gamedata.admin.form.table.TableForm;
import nl.gamedata.common.Access;
import nl.gamedata.common.SqlUtils;
import nl.gamedata.data.Tables;
import nl.gamedata.data.tables.records.DashboardRoleRecord;

/**
 * MaintainDashboardRole takes care of the dashboard role screen.
 * <p>
 * Copyright (c) 2024-2024 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="https://github.com/averbraeck/dashboarddata-admin/LICENSE">DashboardData project License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public class TableDashboardRole
{
    public static void table(final AdminData data, final HttpServletRequest request, final String menuChoice)
    {
        AdminTable table = new AdminTable(data, "Dashboard Roles", "Template");
        boolean access = data.isSuperAdmin() || data.isGameAdmin() || data.isOrganizationAdmin();
        if (access)
        {
            data.getTopbar().addNewButton();
            data.getTopbar().addImportButton();
        }
        data.getTopbar().addExportButton();
        table.setHeader("Template", "User", "Edit", "View");
        List<Record> dashboardRoleList = data.getDSL()
                .selectFrom(Tables.DASHBOARD_ROLE.join(Tables.DASHBOARD_TEMPLATE)
                        .on(Tables.DASHBOARD_ROLE.DASHBOARD_TEMPLATE_ID.eq(Tables.DASHBOARD_TEMPLATE.ID)).join(Tables.USER)
                        .on(Tables.DASHBOARD_ROLE.USER_ID.eq(Tables.USER.ID)))
                .fetch();
        for (var dashboardRole : dashboardRoleList)
        {
            for (Integer dtId : data.getDashboardTemplateAccess().keySet())
            {
                if (dtId.equals(dashboardRole.getValue(Tables.DASHBOARD_TEMPLATE.ID)))
                {
                    int id = dashboardRole.getValue(Tables.DASHBOARD_ROLE.ID);
                    String template = dashboardRole.getValue(Tables.DASHBOARD_TEMPLATE.NAME);
                    String user = dashboardRole.getValue(Tables.USER.NAME);
                    String edit = dashboardRole.getValue(Tables.DASHBOARD_ROLE.EDIT) == 0 ? "N" : "Y";
                    String view = dashboardRole.getValue(Tables.DASHBOARD_ROLE.VIEW) == 0 ? "N" : "Y";
                    table.addRow(id, false, access, access, template, user, edit, view);
                    break;
                }
            }
        }
        table.process();
    }

    public static void edit(final AdminData data, final HttpServletRequest request, final String click, final int recordId)
    {
        DashboardRoleRecord dashboardRole = recordId == 0 ? Tables.DASHBOARD_ROLE.newRecord()
                : SqlUtils.readRecordFromId(data, Tables.DASHBOARD_ROLE, recordId);
        boolean reedit = click.contains("reedit");
        data.setEditRecord(dashboardRole);
        TableForm form = new TableForm(data);
        form.startForm();
        form.setHeader("Dashboard Role", click, recordId);
        form.addEntry(new TableEntryPickRecord(data, reedit, Tables.DASHBOARD_ROLE.DASHBOARD_TEMPLATE_ID, dashboardRole).setPickTable(data,
                data.getDashboardTemplatePicklist(Access.EDIT), Tables.DASHBOARD_TEMPLATE.ID, Tables.DASHBOARD_TEMPLATE.NAME)
                .setLabel("Template"));
        form.addEntry(new TableEntryPickRecord(data, reedit, Tables.DASHBOARD_ROLE.USER_ID, dashboardRole)
                .setPickTable(data, Tables.USER, Tables.USER.ID, Tables.USER.NAME).setLabel("User"));
        form.addEntry(new TableEntryBoolean(data, reedit, Tables.DASHBOARD_ROLE.EDIT, dashboardRole));
        form.addEntry(new TableEntryBoolean(data, reedit, Tables.DASHBOARD_ROLE.VIEW, dashboardRole));
        form.endForm();
        data.setContent(form.process());
    }
}
