package nl.gamedata.admin.table;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import nl.gamedata.admin.AdminData;
import nl.gamedata.admin.AdminTable;
import nl.gamedata.admin.form.table.TableEntryPickRecord;
import nl.gamedata.admin.form.table.TableEntryString;
import nl.gamedata.admin.form.table.TableEntryText;
import nl.gamedata.admin.form.table.TableForm;
import nl.gamedata.common.SqlUtils;
import nl.gamedata.data.Tables;
import nl.gamedata.data.tables.records.ElementPropertyRecord;

/**
 * MaintainGame takes care of the game screen.
 * <p>
 * Copyright (c) 2024-2024 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="https://github.com/averbraeck/gamedata-admin/LICENSE">GameData project License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public class TableElementProperty
{
    public static void table(final AdminData data, final HttpServletRequest request, final String menuChoice)
    {
        AdminTable table = new AdminTable(data, "Dashboard Element Property", "Code");
        table.setNewButton(data.isSuperAdmin());
        table.setHeader("Element", "Code", "Name", "Type");
        List<ElementPropertyRecord> epList = data.getDSL().selectFrom(Tables.ELEMENT_PROPERTY).fetch();
        for (var ep : epList)
        {
            var dashboardElement = SqlUtils.readRecordFromId(data, Tables.DASHBOARD_ELEMENT, ep.getDashboardElementId());
            boolean edit = data.isSuperAdmin();
            boolean delete = data.isSuperAdmin();
            table.addRow(ep.getId(), true, edit, delete, dashboardElement.getCode(), ep.getCode(), ep.getName(), ep.getType());
        }
        table.process();
    }

    public static void edit(final AdminData data, final HttpServletRequest request, final String click, final int recordId)
    {
        ElementPropertyRecord elementProperty = recordId == 0 ? Tables.ELEMENT_PROPERTY.newRecord()
                : SqlUtils.readRecordFromId(data, Tables.ELEMENT_PROPERTY, recordId);
        boolean reedit = click.contains("reedit");
        data.setEditRecord(elementProperty);
        TableForm form = new TableForm(data);
        form.startForm();
        form.setHeader("Dashboard Element Property", click, recordId);
        form.addEntry(new TableEntryPickRecord(data, reedit, Tables.ELEMENT_PROPERTY.DASHBOARD_ELEMENT_ID, elementProperty)
                .setPickTable(data, Tables.DASHBOARD_ELEMENT, Tables.DASHBOARD_ELEMENT.ID, Tables.DASHBOARD_ELEMENT.NAME));
        form.addEntry(new TableEntryString(data, reedit, Tables.ELEMENT_PROPERTY.CODE, elementProperty).setMinLength(2));
        form.addEntry(new TableEntryString(data, reedit, Tables.ELEMENT_PROPERTY.NAME, elementProperty).setMinLength(2));
        form.addEntry(new TableEntryText(data, reedit, Tables.ELEMENT_PROPERTY.DESCRIPTION, elementProperty));
        form.addEntry(new TableEntryString(data, reedit, Tables.ELEMENT_PROPERTY.TYPE, elementProperty).setMinLength(2));
        form.endForm();
        data.setContent(form.process());
    }
}
