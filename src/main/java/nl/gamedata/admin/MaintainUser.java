package nl.gamedata.admin;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;

import jakarta.xml.bind.DatatypeConverter;
import nl.gamedata.admin.form.table.TableEntryBoolean;
import nl.gamedata.admin.form.table.TableEntryPickRecord;
import nl.gamedata.admin.form.table.TableEntryString;
import nl.gamedata.admin.form.table.TableForm;
import nl.gamedata.data.Tables;
import nl.gamedata.data.tables.records.UserRecord;

/**
 * MaintainUser handles the maintenance of the user records in the database.
 * <p>
 * Copyright (c) 2024-2024 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="https://github.com/averbraeck/gamedata-admin/LICENSE">GameData project License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public class MaintainUser
{
    public static void handleMenu(final HttpServletRequest request, final String click, int recordId)
    {
        HttpSession session = request.getSession();
        AdminData data = SessionUtils.getData(session);
        
        if (click.equals("user"))
        {
            data.clearColumns("25%", "User", "25%", "GameRole");
            data.clearFormColumn("50%", "Edit Properties");
            showUser(session, data, 0, true, false);
        }

        else if (click.endsWith("User") || click.endsWith("UserOk"))
        {
            if (click.startsWith("save"))
            {
                recordId = saveUser(request, data, recordId);
            }
            else if (click.startsWith("delete"))
            {
                UserRecord user = AdminUtils.readRecordFromId(data, Tables.USER, recordId);
                if (click.endsWith("Ok"))
                    data.deleteRecordOk(user, "user");
                else
                {
                    if (recordId == data.getUserId() && data.isSuperAdmin())
                        ModalWindowUtils.makeModalWindow("Error", "Super users cannot delete themselves", "user");
                    else
                        data.askDeleteRecord(user, "User", user.getName(), "deleteUserOk", "user");
                }
                recordId = 0;
            }
            if (!data.isError())
            {
                showUser(session, data, recordId, true, !click.startsWith("view"));
                if (click.startsWith("new"))
                    editUser(session, data, 0, true);
            }
        }

        AdminServlet.makeColumnContent(data);
    }

    /*
     * *********************************************************************************************************
     * ********************************************** USER *************************************************
     * *********************************************************************************************************
     */

    public static void showUser(final HttpSession session, final AdminData data, final int recordId, final boolean editButton,
            final boolean editRecord)
    {
        if (data.isSuperAdmin()) // show all user records
        {
            data.showColumn("User", 0, recordId, editButton, Tables.USER, Tables.USER.NAME, "name", true);
        }
        else if (data.isOrgAdmin()) // show only records of users from the own organization
        {
            Integer orgId = data.getUser().getOrganizationId();
            if (orgId == null)
                orgId = 0;
            data.showColumn("User", 0, recordId, editButton, Tables.USER.where(Tables.USER.ORGANIZATION_ID.eq(orgId)),
                    Tables.USER.NAME, "name", true);
        }
        data.resetFormColumn();
        if (recordId != 0)
        {
            editUser(session, data, recordId, editRecord);
        }
    }

    /**
     * Both an organizational admin and a super admin have access to this servlet and can create new users, as well as deleting
     * them. The following rules apply:
     * <ul>
     * <li>An org admin can only create, change or delete users in their own organization.</li>
     * <li>An org admin can give a user in their organization an org user or a game user role, but never a super user one.</li>
     * <li>A super admin can make another super admin.</li>
     * <li>A super admin can belong to an organization, but it does not have to.</li>
     * <li>A super admin can demote others, but not themselves.</li>
     * <li>When a super admin makes or changes an org admin, this org admin has to have an organization.</li>
     * </ul>
     * @param session HttpSession; the session
     * @param data data; administration data with access to logged in user
     * @param userId int; the userId to edit; 0 for new user
     * @param edit boolean; whether to edit (true) or only show (false)
     */
    public static void editUser(final HttpSession session, final AdminData data, final int userId, final boolean edit)
    {
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        UserRecord user = userId == 0 ? dslContext.newRecord(Tables.USER)
                : dslContext.selectFrom(Tables.USER).where(Tables.USER.ID.eq(userId)).fetchOne();
        //@formatter:off
        TableForm form = new TableForm()
                .setEdit(edit)
                .setCancelMethod("user", data.getColumn(0).getSelectedRecordId())
                .setEditMethod("editUser")
                .setSaveMethod("saveUser")
                .setDeleteMethod("deleteUser", "Delete", "<br>Note: Do not delete user when"
                        + "<br> it is in use for a game role")
                .setRecordNr(userId)
                .startForm()
                .addEntry(new TableEntryString(Tables.USER.NAME)
                        .setRequired()
                        .setInitialValue(user.getName(), "")
                        .setLabel("User name")
                        .setMaxChars(45))
                .addEntry(new TableEntryString(Tables.USER.EMAIL)
                        .setRequired(false)
                        .setInitialValue(user.getEmail(), "")
                        .setLabel("Email")
                        .setMaxChars(255))
                .addEntry(new TableEntryString(Tables.USER.PASSWORD)
                        .setLabel("Password")
                        .setRequired(userId == 0) // only required when no password yet
                        .setInitialValue("", "")  // old password is never shown
                        .setMaxChars(255))
                .addEntry(new TableEntryString(Tables.USER.SALT)
                        .setRequired(false)
                        .setInitialValue(user.getSalt(), makeSalt())
                        .setLabel("Salt")
                        .setHidden(true)
                        .setMaxChars(45));

        if (data.isSuperAdmin())
        {
            form.addEntry(new TableEntryBoolean(Tables.USER.SUPER_ADMIN)
                    .setRequired()
                    .setInitialValue(user.getSuperAdmin(), Byte.valueOf((byte) 0))
                    .setLabel("Super admin?"));
        }
        else
        {
            form.addEntry(new TableEntryBoolean(Tables.USER.SUPER_ADMIN)
                    .setRequired()
                    .setInitialValue(user.getSuperAdmin(), Byte.valueOf((byte) 0))
                    .setReadOnly()
                    .setLabel("Super admin?"));
        }

        if (data.isSuperAdmin())
        {
            form.addEntry(new TableEntryBoolean(Tables.USER.ORG_ADMIN)
                    .setRequired()
                    .setInitialValue(user.getOrgAdmin(), Byte.valueOf((byte) 0))
                    .setLabel("Organization admin?"));
            form.addEntry(new TableEntryPickRecord(Tables.USER.ORGANIZATION_ID)
                    .setRequired(false)
                    .setInitialValue(user.getOrganizationId(), null)
                    .setPickTable(data, Tables.ORGANIZATION, Tables.ORGANIZATION.ID, Tables.ORGANIZATION.NAME)
                    .setLabel("Organization"));
        }
        else if (data.isOrgAdmin())
        {
            form.addEntry(new TableEntryBoolean(Tables.USER.ORG_ADMIN)
                    .setRequired()
                    .setInitialValue(user.getOrgAdmin(), Byte.valueOf((byte) 0))
                    .setLabel("Organization admin?"));
        }
        else
        {
            form.addEntry(new TableEntryBoolean(Tables.USER.ORG_ADMIN)
                    .setRequired()
                    .setInitialValue(user.getOrgAdmin(), Byte.valueOf((byte) 0))
                    .setReadOnly()
                    .setLabel("Organization admin?"));
        }

        form.endForm();
        //@formatter:on
        data.getFormColumn().setHeaderForm("Edit User", form);
    }

    public static int saveUser(HttpServletRequest request, AdminData data, int userId)
    {
        DSLContext dslContext = DSL.using(data.getDataSource(), SQLDialect.MYSQL);
        UserRecord user = userId == 0 ? dslContext.newRecord(Tables.USER)
                : dslContext.selectFrom(Tables.USER).where(Tables.USER.ID.eq(userId)).fetchOne();
        String hashedPassword = userId == 0 ? "" : user.getPassword(); // has to be BEFORE setFields
        String errors = data.getFormColumn().getForm().setFields(user, request, data);
        if (errors.length() > 0)
        {
            ModalWindowUtils.popup(data, "Error storing record", errors,
                    "clickRecordId('viewUser'," + data.getColumn(0).getSelectedRecordId() + ")");
            data.setError(true);
            return -1;
        }
        else
        {
            if (user.getPassword().length() > 0)
            {
                MessageDigest md;
                try
                {
                    // https://www.baeldung.com/java-md5
                    md = MessageDigest.getInstance("MD5");
                    md.update(user.getPassword().getBytes());
                    byte[] digest = md.digest();
                    hashedPassword = DatatypeConverter.printHexBinary(digest).toLowerCase();
                }
                catch (NoSuchAlgorithmException e1)
                {
                    throw new RuntimeException(e1);
                }
            }
            user.set(Tables.USER.PASSWORD, hashedPassword); // restore old password if not changed

            try
            {
                user.store();
            }
            catch (DataAccessException exception)
            {
                ModalWindowUtils.popup(data, "Error storing record", "<p>" + exception.getMessage() + "</p>",
                        "clickRecordId('showUsers'," + data.getColumn(0).getSelectedRecordId() + ")");
                return -1;
            }
            return user.getId();
        }
    }
    
    private static String makeSalt()
    {
        return "";
    }

}
