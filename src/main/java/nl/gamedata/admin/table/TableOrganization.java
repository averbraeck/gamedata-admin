package nl.gamedata.admin.table;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import nl.gamedata.admin.AdminData;
import nl.gamedata.admin.AdminTable;
import nl.gamedata.admin.form.table.TableEntryImage;
import nl.gamedata.admin.form.table.TableEntryString;
import nl.gamedata.admin.form.table.TableForm;
import nl.gamedata.common.SqlUtils;
import nl.gamedata.data.Tables;
import nl.gamedata.data.tables.records.OrganizationRecord;

/**
 * MaintainOrganization takes care of the organization screen. The organization screen is only visible for SuperAdmin and
 * OrganizationAdmin users.
 * <p>
 * Copyright (c) 2024-2024 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="https://github.com/averbraeck/gamedata-admin/LICENSE">GameData project License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public class TableOrganization
{
    /**
     * Filter the Organization table based on access rights and filter settings.
     * @param data the admin data with the filters and access rights
     * @return a list of Organization records
     */
    public static List<OrganizationRecord> filterOrganization(final AdminData data)
    {
        return null;
    }

    public static void table(final AdminData data, final HttpServletRequest request, final String menuChoice)
    {
        AdminTable table = new AdminTable(data, "Organization", "Code");
        table.setNewButton(data.isSuperAdmin());
        table.setHeader("Code", "Name");
        for (var organizationId : data.getOrganizationAccess().keySet())
        {
            OrganizationRecord organization = SqlUtils.readRecordFromId(data, Tables.ORGANIZATION, organizationId);
            boolean edit = data.isSuperAdmin();
            boolean delete = data.isSuperAdmin();
            table.addRow(organizationId, true, edit, delete, organization.getCode(), organization.getName());
        }
        table.process();
    }

    public static void edit(final AdminData data, final HttpServletRequest request, final String click, final int recordId)
    {
        OrganizationRecord organization = recordId == 0 ? Tables.ORGANIZATION.newRecord()
                : SqlUtils.readRecordFromId(data, Tables.ORGANIZATION, recordId);
        data.setEditRecord(organization);
        TableForm form = new TableForm(data);
        form.startMultipartForm();
        form.setHeader("Organization", click, recordId);
        form.addEntry(new TableEntryString(Tables.ORGANIZATION.CODE, organization).setMinLength(2));
        form.addEntry(new TableEntryString(Tables.ORGANIZATION.NAME, organization).setMinLength(2));
        form.addEntry(new TableEntryImage(Tables.ORGANIZATION.LOGO, organization));
        form.endForm();
        data.setContent(form.process());
    }
}
