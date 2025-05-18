package nl.gamedata.admin.table;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import nl.gamedata.admin.AdminData;
import nl.gamedata.admin.AdminTable;
import nl.gamedata.admin.form.FormEntryBoolean;
import nl.gamedata.admin.form.FormEntryDateTime;
import nl.gamedata.admin.form.FormEntryString;
import nl.gamedata.admin.form.FormEntryText;
import nl.gamedata.admin.form.WebForm;
import nl.gamedata.common.SqlUtils;
import nl.gamedata.data.Tables;
import nl.gamedata.data.tables.records.ErrorRecord;

/**
 * TableErrors takes care of displaying errors for insertion of records into the database.
 * <p>
 * Copyright (c) 2024-2024 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="https://github.com/averbraeck/gamedata-admin/LICENSE">GameData project License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public class TableErrors
{
    public static void table100(final AdminData data, final HttpServletRequest request, final String menuChoice)
    {
        AdminTable table = new AdminTable(data, "Errors", "Timestamp");
        data.getTopbar().addExportButton();
        table.setHeader("Timestamp", "Error type", "Stored?", "Data type", "Message");
        List<ErrorRecord> errorList =
                data.getDSL().selectFrom(Tables.ERROR).orderBy(Tables.ERROR.TIMESTAMP.desc()).limit(100).fetch();
        for (var error : errorList)
        {
            String dataType = error.getDataType() == null ? "-" : error.getDataType();
            table.addRow(error.getId(), false, false, false, error.getTimestamp().toString(), error.getErrorType(),
                    error.getRecordStored() == 0 ? "N" : "Y", dataType, error.getMessage());
        }
        table.process();
    }

    public static void view(final AdminData data, final HttpServletRequest request, final String click, final int recordId)
    {
        var error = SqlUtils.readRecordFromId(data, Tables.ERROR, recordId);
        data.setEditRecord(error);
        WebForm form = new WebForm(data);
        form.startForm();
        form.setHeader("Error");
        form.addEntry(new FormEntryDateTime("Timestamp", "timestamp").setReadOnly().setInitialValue(error.getTimestamp()));
        form.addEntry(new FormEntryString("Error type", "error_type").setReadOnly().setInitialValue(error.getErrorType()));
        form.addEntry(
                new FormEntryBoolean("Record stored?", "record_stored").setReadOnly().setInitialValue(error.getRecordStored()));
        form.addEntry(new FormEntryString("Message", "message").setReadOnly().setInitialValue(error.getMessage()));
        form.addEntry(new FormEntryText("Content", "content").setReadOnly().setInitialValue(error.getContent()));
        form.addEntry(new FormEntryString("Data type", "data_type").setReadOnly().setInitialValue(error.getDataType(), "-"));
        form.addEntry(new FormEntryString("Session token", "session_token").setReadOnly()
                .setInitialValue(error.getSessionToken(), "-"));
        form.addEntry(new FormEntryString("Session code", "session_code").setReadOnly()
                .setInitialValue(error.getGameSessionCode(), "-"));
        form.addEntry(new FormEntryString("Version code", "version_code").setReadOnly()
                .setInitialValue(error.getGameVersionCode(), "-"));
        form.addEntry(new FormEntryString("Organization", "organization").setReadOnly()
                .setInitialValue(error.getOrganizationCode(), "-"));
        form.endForm();
        data.setContent(form.process());
    }
}
