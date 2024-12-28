package nl.gamedata.admin.form.table;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

import org.jooq.Record;

import nl.gamedata.admin.AdminData;
import nl.gamedata.admin.form.AbstractFormEntry;
import nl.gamedata.admin.form.WebForm;

public class TableForm extends WebForm
{
    private int recordId;

    private String saveMethod = "";

    private String editMethod = "";

    private String deleteMethod = "";

    private boolean multipart;

    private boolean edit;

    /* **************************************************************************************************************** */
    /* ************************************************* FORM ELEMENTS ************************************************ */
    /* **************************************************************************************************************** */

    /** No tags. */
    private static final String htmlStartMultiPartForm = """
            <div class="gd-form">
              <form id="editForm" action="/gamedata-admin/admin" method="POST" enctype="multipart/form-data">
                <input id="editClick" type="hidden" name="editClick" value="tobefilled" />
                <input id="editRecordId" type="hidden" name="editRecordId" value="0" />
                """;

    /* **************************************************************************************************************** */
    /* **************************************************** METHODS *************************************************** */
    /* **************************************************************************************************************** */

    public TableForm(final AdminData data)
    {
        super(data);
    }

    public TableForm setHeader(final String recordType, final String click, final int recordId)
    {
        setCancelMethod("record-cancel");
        setRecordId(recordId);

        String header;
        if (click.equals("record-new") || click.equals("record-edit"))
        {
            header = (click.equals("record-new") ? "New " : "Edit ") + recordType;
            setEdit(true);
            setSaveMethod("record-save");
            if (!click.equals("record-new"))
                setDeleteMethod("record-delete");
        }
        else
        {
            header = "View " + recordType;
            setEdit(false);
        }

        this.s.append("<div class=\"gd-form-header\">\n");
        this.s.append("  <h3>");
        this.s.append(header);
        this.s.append("</h3>\n");
        this.s.append("</div>\n");
        return this;
    }

    public TableForm startMultipartForm()
    {
        this.multipart = true;
        this.s.append(htmlStartMultiPartForm);
        this.s.append(htmlStartTable);
        return this;
    }

    @Override
    public TableForm startForm()
    {
        this.multipart = false;
        super.startForm();
        return this;
    }

    @Override
    public TableForm endForm()
    {
        super.endForm();
        return this;
    }

    @Override
    protected void buttonRow()
    {
        this.s.append(htmlStartButtonRow);
        this.s.append(htmlButton.formatted(this.cancelMethod, this.recordId, "Cancel"));
        if (this.edit && this.saveMethod.length() > 0)
            this.s.append(htmlButton.formatted(this.saveMethod, this.recordId, "Save"));
        if (!this.edit && this.editMethod.length() > 0)
            this.s.append(htmlButton.formatted(this.editMethod, this.recordId, "Edit"));
        if (this.edit && this.recordId > 0 && this.deleteMethod.length() > 0)
            this.s.append(htmlButton.formatted(this.deleteMethod, this.recordId, "Delete"));
        for (int i = 0; i < this.additionalButtons.size(); i++)
            this.s.append(htmlButton.formatted(this.additionalMethods.get(i), this.recordId, this.additionalButtons.get(i)));
        this.s.append(htmlEndButtonRow);
    }

    @Override
    public TableForm addEntry(final AbstractFormEntry<?, ?> entry)
    {
        this.entries.add(entry);
        entry.setForm(this);
        this.s.append(entry.makeHtml());
        return this;
    }

    @Override
    public TableForm setCancelMethod(final String cancelMethod)
    {
        this.cancelMethod = cancelMethod;
        return this;
    }

    public TableForm setSaveMethod(final String saveMethod)
    {
        this.saveMethod = saveMethod;
        return this;
    }

    public TableForm setEditMethod(final String editMethod)
    {
        this.editMethod = editMethod;
        return this;
    }

    public TableForm setDeleteMethod(final String deleteMethod)
    {
        this.deleteMethod = deleteMethod;
        return this;
    }

    @Override
    public TableForm addAddtionalButton(final String method, final String buttonText)
    {
        super.addAddtionalButton(method, buttonText);
        return this;
    }

    public TableForm setRecordId(final int recordId)
    {
        this.recordId = recordId;
        return this;
    }

    public boolean isMultipart()
    {
        return this.multipart;
    }

    public boolean isEdit()
    {
        return this.edit;
    }

    public TableForm setEdit(final boolean edit)
    {
        this.edit = edit;
        return this;
    }

    @Override
    public TableForm setLabelLength(final String labelLength)
    {
        super.setLabelLength(labelLength);
        return this;
    }

    @Override
    public TableForm setFieldLength(final String fieldLength)
    {
        super.setFieldLength(fieldLength);
        return this;
    }

    // for multipart: https://stackoverflow.com/questions/2422468/how-to-upload-files-to-server-using-jsp-servlet
    public String setFields(final Record record, final HttpServletRequest request, final AdminData data)
    {
        String errors = "";
        for (AbstractFormEntry<?, ?> entry : this.entries)
        {
            if (isMultipart() && entry instanceof TableEntryImage)
            {
                try
                {
                    TableEntryImage imageEntry = (TableEntryImage) entry;
                    Part filePart = request.getPart(imageEntry.getTableField().getName());
                    String reset = request.getParameter(imageEntry.getTableField().getName() + "_reset");
                    boolean delete = reset != null && reset.equals("delete");
                    if (delete)
                    {
                        errors += imageEntry.setRecordValue(record, (byte[]) null);
                    }
                    else if (filePart != null && filePart.getSubmittedFileName() != null
                            && filePart.getSubmittedFileName().length() > 0)
                    {
                        String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                        imageEntry.setFilename(fileName);
                        try (InputStream fileContent = filePart.getInputStream())
                        {
                            byte[] image = fileContent.readAllBytes();
                            errors += imageEntry.setRecordValue(record, image);
                        }
                    }
                }
                catch (ServletException | IOException exception)
                {
                    errors += "<p>Exception: " + exception.getMessage() + "</p>\n";
                }
            }
            else if (entry instanceof AbstractTableEntry)
            {
                AbstractTableEntry<?, ?> tableEntry = (AbstractTableEntry<?, ?>) entry;
                boolean set = false;
                String value = request.getParameter(tableEntry.getTableField().getName());
                if (tableEntry.getTableField().getDataType().nullable())
                {
                    var nullValue = request.getParameter(tableEntry.getTableField().getName() + "-null");
                    if (nullValue != null)
                    {
                        if (nullValue.equals("on") || nullValue.equals("null"))
                        {
                            record.set(tableEntry.getTableField(), null);
                            set = true;
                        }
                    }
                }
                if (!set)
                {
                    errors += tableEntry.setRecordValue(record, value);
                }
            }
        }
        // TODO: if (errors.length() > 0) data.setError(true);
        return errors;
    }

}
