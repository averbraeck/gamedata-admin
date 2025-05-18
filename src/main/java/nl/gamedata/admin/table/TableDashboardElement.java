package nl.gamedata.admin.table;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import nl.gamedata.admin.AdminData;
import nl.gamedata.admin.AdminTable;
import nl.gamedata.admin.form.table.TableEntryString;
import nl.gamedata.admin.form.table.TableEntryText;
import nl.gamedata.admin.form.table.TableForm;
import nl.gamedata.common.SqlUtils;
import nl.gamedata.data.Tables;
import nl.gamedata.data.tables.records.DashboardElementRecord;

/**
 * MaintainGame takes care of the game screen.
 * <p>
 * Copyright (c) 2024-2024 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="https://github.com/averbraeck/gamedata-admin/LICENSE">GameData project License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public class TableDashboardElement
{
    public static void table(final AdminData data, final HttpServletRequest request, final String menuChoice)
    {
        AdminTable table = new AdminTable(data, "Dashboard Element", "Code");
        if (data.isSuperAdmin())
            data.getTopbar().addNewButton();
        table.setHeader("Code", "Name");
        List<DashboardElementRecord> deList = data.getDSL().selectFrom(Tables.DASHBOARD_ELEMENT).fetch();
        for (var de : deList)
        {
            boolean edit = data.isSuperAdmin();
            boolean delete = data.isSuperAdmin();
            table.addRow(de.getId(), true, edit, delete, de.getCode(), de.getName());
        }
        table.process();
    }

    public static void edit(final AdminData data, final HttpServletRequest request, final String click, final int recordId)
    {
        DashboardElementRecord dashboardElement = recordId == 0 ? Tables.DASHBOARD_ELEMENT.newRecord()
                : SqlUtils.readRecordFromId(data, Tables.DASHBOARD_ELEMENT, recordId);
        boolean reedit = click.contains("reedit");
        data.setEditRecord(dashboardElement);
        TableForm form = new TableForm(data);
        form.startForm();
        form.setHeader("Dashboard Element", click, recordId);
        form.addEntry(new TableEntryString(data, reedit, Tables.DASHBOARD_ELEMENT.CODE, dashboardElement).setMinLength(2));
        form.addEntry(new TableEntryString(data, reedit, Tables.DASHBOARD_ELEMENT.NAME, dashboardElement).setMinLength(2));
        form.addEntry(new TableEntryText(data, reedit, Tables.DASHBOARD_ELEMENT.DESCRIPTION, dashboardElement));
        form.endForm();
        data.setContent(form.process());
    }
}
