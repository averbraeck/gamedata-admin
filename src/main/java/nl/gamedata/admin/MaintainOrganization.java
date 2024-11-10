package nl.gamedata.admin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import nl.gamedata.admin.form.table.TableEntryString;
import nl.gamedata.admin.form.table.TableForm;
import nl.gamedata.data.Tables;
import nl.gamedata.data.tables.records.OrganizationRecord;

/**
 * MaintainOrganization handles the maintenance of the organization records in the database.
 * <p>
 * Copyright (c) 2024-2024 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="https://github.com/averbraeck/gamedata-admin/LICENSE">GameData project License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public class MaintainOrganization
{
    public static void handleMenu(final HttpServletRequest request, final String click, int recordId)
    {
        HttpSession session = request.getSession();
        AdminData data = SessionUtils.getData(session);

        if (click.equals("organization"))
        {
            data.clearColumns("30%", "Organization");
            data.clearFormColumn("70%", "Edit Properties");
            showOrganization(session, data, 0, true, false);
        }

        else if (click.endsWith("Organization") || click.endsWith("OrganizationOk"))
        {
            if (click.startsWith("save"))
                recordId = data.saveRecord(request, recordId, Tables.ORGANIZATION, "organization");
            else if (click.startsWith("delete"))
            {
                OrganizationRecord organization = AdminUtils.readRecordFromId(data, Tables.ORGANIZATION, recordId);
                if (click.endsWith("Ok"))
                    data.deleteRecordOk(organization, "organization");
                else
                    data.askDeleteRecord(organization, "Organization", organization.getCode(), "deleteOrganizationOk", "organization");
                recordId = 0;
            }
            if (!data.isError())
            {
                showOrganization(session, data, recordId, true, !click.startsWith("view"));
                if (click.startsWith("new"))
                    editOrganization(session, data, 0, true);
            }
        }

        AdminServlet.makeColumnContent(data);
    }

    /*
     * *********************************************************************************************************
     * ********************************************** ORGANIZATION *************************************************
     * *********************************************************************************************************
     */

    public static void showOrganization(final HttpSession session, final AdminData data, final int recordId,
            final boolean editButton, final boolean editRecord)
    {
        data.showColumn("Organization", 0, recordId, editButton, Tables.ORGANIZATION, Tables.ORGANIZATION.CODE, "code", true);
        data.resetFormColumn();
        if (recordId != 0)
        {
            editOrganization(session, data, recordId, editRecord);
        }
    }

    public static void editOrganization(final HttpSession session, final AdminData data, final int organizationId, final boolean edit)
    {
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        OrganizationRecord organization = organizationId == 0 ? dslContext.newRecord(Tables.ORGANIZATION)
                : dslContext.selectFrom(Tables.ORGANIZATION).where(Tables.ORGANIZATION.ID.eq(organizationId)).fetchOne();
        //@formatter:off
        TableForm form = new TableForm()
                .setEdit(edit)
                .setCancelMethod("organization", data.getColumn(0).getSelectedRecordId())
                .setEditMethod("editOrganization")
                .setSaveMethod("saveOrganization")
                .setDeleteMethod("deleteOrganization", "Delete", "<br>Note: Do not delete organization when"
                        + "<br> it is in use for a user or token")
                .setRecordNr(organizationId)
                .startForm()
                .addEntry(new TableEntryString(Tables.ORGANIZATION.CODE)
                        .setRequired()
                        .setInitialValue(organization.getCode(), "")
                        .setLabel("Organization code")
                        .setMaxChars(2))
                .addEntry(new TableEntryString(Tables.ORGANIZATION.NAME)
                        .setRequired()
                        .setInitialValue(organization.getName(), "")
                        .setLabel("Organization name")
                        .setMaxChars(45))
                .endForm();
        //@formatter:on
        data.getFormColumn().setHeaderForm("Edit Organization", form);
    }

}
