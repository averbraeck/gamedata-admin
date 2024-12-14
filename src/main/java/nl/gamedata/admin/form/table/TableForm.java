package nl.gamedata.admin.form.table;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

import org.jooq.Record;

import nl.gamedata.admin.AdminData;

public class TableForm
{

    private StringBuilder s;

    private int recordNr;

    private String cancelMethod = "";

    private String saveMethod = "";

    private String editMethod = "";

    private String deleteMethod = "";

    private List<String> additionalButtons = new ArrayList<>();

    private List<String> additionalMethods = new ArrayList<>();

    List<AbstractTableEntry<?, ?>> entries = new ArrayList<>();

    private boolean multipart;

    private boolean edit;

    private String labelLength = "25%";

    private String fieldLength = "75%";

    /* **************************************************************************************************************** */
    /* ************************************************* FORM ELEMENTS ************************************************ */
    /* **************************************************************************************************************** */

    /** start buttonrow. */
    private static final String htmlStartButtonRow = """
              <div class="gd-admin-form-buttons">
            """;

    /** button. 1 = submit string, 2 = record nr, 3 = button text */
    private static final String htmlButton = """
              <div class="gd-button">
                <button type="button" class="btn btn-primary" onClick="submitEditForm('%s', %d); return false;">%s</button>
              </div>
            """;

    /** end buttonrow. */
    private static final String htmlEndButtonRow = """
              </div>
            """;

    /** No tags. */
    private static final String htmlStartMultiPartForm = """
            <div class="gd-form">
              <form id="editForm" action="/gamedata-admin/admin" method="POST" enctype="multipart/form-data">
                <input id="editClick" type="hidden" name="editClick" value="tobefilled" />
                <input id="editRecordNr" type="hidden" name="editRecordNr" value="0" />
                """;

    /** No tags. */
    private static final String htmlStartForm = """
            <div class="gd-form">
              <form id="editForm" action="/gamedata-admin/admin" method="POST">
                <input id="editClick" type="hidden" name="editClick" value="tobefilled" />
                <input id="editRecordNr" type="hidden" name="editRecordNr" value="0" />
                """;

    /** No tags. */
    private static final String htmlStartTable = """
                <table>
            """;

    /** end table. */
    private static final String htmlEndTable = """
                </table>
            """;

    /** end form. */
    private static final String htmlEndForm = """
               </form>
             </div>
            """;

    /* **************************************************************************************************************** */
    /* **************************************************** METHODS *************************************************** */
    /* **************************************************************************************************************** */

    public TableForm()
    {
        this.s = new StringBuilder();
    }

    public TableForm setHeader(final String recordType, final String click)
    {
        setCancelMethod("record-cancel");

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
        buttonRow();
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

    public TableForm startForm()
    {
        this.multipart = false;
        this.s.append(htmlStartForm);
        this.s.append(htmlStartTable);
        return this;
    }

    public TableForm endForm()
    {
        this.s.append(htmlEndTable);
        this.s.append(htmlEndForm);
        return this;
    }

    private void buttonRow()
    {
        this.s.append(htmlStartButtonRow);
        this.s.append(htmlButton.formatted(this.cancelMethod, this.recordNr, "Cancel"));
        if (this.edit && this.saveMethod.length() > 0)
            this.s.append(htmlButton.formatted(this.saveMethod, this.recordNr, "Save"));
        if (!this.edit && this.editMethod.length() > 0)
            this.s.append(htmlButton.formatted(this.editMethod, this.recordNr, "Edit"));
        if (this.edit && this.recordNr > 0 && this.deleteMethod.length() > 0)
            this.s.append(htmlButton.formatted(this.deleteMethod, this.recordNr, "Delete"));
        for (int i = 0; i < this.additionalButtons.size(); i++)
            this.s.append(htmlButton.formatted(this.additionalMethods.get(i), this.recordNr, this.additionalButtons.get(i)));
        this.s.append(htmlEndButtonRow);
    }

    public TableForm addEntry(final AbstractTableEntry<?, ?> entry)
    {
        this.entries.add(entry);
        entry.setForm(this);
        this.s.append(entry.makeHtml());
        return this;
    }

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

    public TableForm addAddtionalButton(final String method, final String buttonText)
    {
        this.additionalButtons.add(buttonText);
        this.additionalMethods.add(method);
        return this;
    }

    public TableForm setRecordNr(final int recordNr)
    {
        this.recordNr = recordNr;
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

    public String getLabelLength()
    {
        return this.labelLength;
    }

    public TableForm setLabelLength(final String labelLength)
    {
        this.labelLength = labelLength;
        return this;
    }

    public String getFieldLength()
    {
        return this.fieldLength;
    }

    public TableForm setFieldLength(final String fieldLength)
    {
        this.fieldLength = fieldLength;
        return this;
    }

    public String process()
    {
        return this.s.toString();
    }

    // for multipart: https://stackoverflow.com/questions/2422468/how-to-upload-files-to-server-using-jsp-servlet
    public String setFields(final Record record, final HttpServletRequest request, final AdminData data)
    {
        String errors = "";
        for (AbstractTableEntry<?, ?> entry : this.entries)
        {
            if (isMultipart() && entry instanceof TableEntryImage)
            {
                try
                {
                    TableEntryImage imageEntry = (TableEntryImage) entry;
                    Part filePart = request.getPart(imageEntry.getTableField().getName());
                    String reset = request.getParameter(entry.getTableField().getName() + "_reset");
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
            else
            {
                boolean set = false;
                String value = request.getParameter(entry.getTableField().getName());
                if (entry.getTableField().getDataType().nullable())
                {
                    var nullValue = request.getParameter(entry.getTableField().getName() + "-null");
                    if (nullValue != null)
                    {
                        if (nullValue.equals("on") || nullValue.equals("null"))
                        {
                            record.set(entry.getTableField(), null);
                            set = true;
                        }
                    }
                }
                if (!set)
                {
                    errors += entry.setRecordValue(record, value);
                }
            }
        }
        // TODO: if (errors.length() > 0) data.setError(true);
        return errors;
    }

}
