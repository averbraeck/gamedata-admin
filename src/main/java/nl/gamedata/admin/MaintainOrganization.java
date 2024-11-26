package nl.gamedata.admin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import nl.gamedata.admin.form.table.TableEntryString;
import nl.gamedata.admin.form.table.TableForm;
import nl.gamedata.data.Tables;
import nl.gamedata.data.tables.records.GameAccessRecord;
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
            data.clearColumns("25%", "Organization");
            data.clearColumns("25%", "GameAccess");
            data.clearFormColumn("50%", "Edit Properties");
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
                    data.askDeleteRecord(organization, "Organization", organization.getCode(), "deleteOrganizationOk",
                            "organization");
                recordId = 0;
            }
            if (!data.isError())
            {
                showOrganization(session, data, recordId, true, !click.startsWith("view"));
                if (click.startsWith("new"))
                    editOrganization(session, data, 0, true);
            }
        }

        else if (click.endsWith("GameAccess") || click.endsWith("GameAccessOk"))
        {
            if (click.startsWith("save"))
                recordId = data.saveRecord(request, recordId, Tables.GAME_ACCESS, "gameaccess");
            else if (click.startsWith("delete"))
            {
                GameAccessRecord gameAccess = AdminUtils.readRecordFromId(data, Tables.GAME_ACCESS, recordId);
                if (click.endsWith("Ok"))
                    data.deleteRecordOk(gameAccess, "gameaccess");
                else
                    data.askDeleteRecord(gameAccess, "GameAccess", gameAccess.getName(), "deleteGameAccessOk", "gameaccess");
                recordId = 0;
            }
            if (!data.isError())
            {
                showGameAccess(session, data, recordId, true, !click.startsWith("view"));
                if (click.startsWith("new"))
                    editGameAccess(session, data, 0, true);
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
        data.resetColumn(1);
        data.resetFormColumn();
        if (recordId != 0)
        {
            editOrganization(session, data, recordId, editRecord);
        }
    }

    public static void editOrganization(final HttpSession session, final AdminData data, final int organizationId,
            final boolean edit)
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
                        .setMaxChars(16))
                .addEntry(new TableEntryString(Tables.ORGANIZATION.NAME)
                        .setRequired()
                        .setInitialValue(organization.getName(), "")
                        .setLabel("Organization name")
                        .setMaxChars(45))
                .endForm();
        //@formatter:on
        data.getFormColumn().setHeaderForm("Edit Organization", form);
    }

    /*
     * *********************************************************************************************************
     * ********************************************** GAME_ACCESS *************************************************
     * *********************************************************************************************************
     */

    public static void showGameAccess(final HttpSession session, final AdminData data, final int recordId,
            final boolean editButton, final boolean editRecord)
    {
        data.showColumn("GameAccess", 0, recordId, editButton, Tables.GAME_ACCESS, Tables.GAME_ACCESS.NAME, "name", true);
        data.resetFormColumn();
        if (recordId != 0)
        {
            editGameAccess(session, data, recordId, editRecord);
        }
    }

    public static void editGameAccess(final HttpSession session, final AdminData data, final int gameAccessId,
            final boolean edit)
    {
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        GameAccessRecord organization = gameAccessId == 0 ? dslContext.newRecord(Tables.GAME_ACCESS)
                : dslContext.selectFrom(Tables.GAME_ACCESS).where(Tables.GAME_ACCESS.ID.eq(gameAccessId)).fetchOne();
        //@formatter:off
        TableForm form = new TableForm()
                .setEdit(edit)
                .setCancelMethod("organization", data.getColumn(0).getSelectedRecordId())
                .setEditMethod("editGameAccess")
                .setSaveMethod("saveGameAccess")
                .setDeleteMethod("deleteGameAccess", "Delete", "<br>Note: Do not delete game access when"
                        + "<br> it is in use")
                .setRecordNr(gameAccessId)
                .startForm()
                .addEntry(new TableEntryString(Tables.GAME_ACCESS.NAME)
                        .setRequired()
                        .setInitialValue(organization.getName(), "")
                        .setLabel("GameAccess name")
                        .setMaxChars(16))
                .endForm();
        //@formatter:on
        data.getFormColumn().setHeaderForm("Edit GameAccess", form);
    }

}
