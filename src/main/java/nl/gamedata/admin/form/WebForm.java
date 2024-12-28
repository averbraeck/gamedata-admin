package nl.gamedata.admin.form;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import nl.gamedata.admin.AdminData;

public class WebForm
{
    protected StringBuilder s;

    private String okMethod = "";

    protected String cancelMethod = "";

    protected List<String> additionalButtons = new ArrayList<>();

    protected List<String> additionalMethods = new ArrayList<>();

    protected List<AbstractFormEntry<?, ?>> entries = new ArrayList<>();

    protected String labelLength = "25%";

    protected String fieldLength = "75%";

    /* **************************************************************************************************************** */
    /* ************************************************* FORM ELEMENTS ************************************************ */
    /* **************************************************************************************************************** */

    /** start buttonrow. */
    protected static final String htmlStartButtonRow = """
              <div class="gd-admin-form-buttons">
            """;

    /** button. 1 = submit string, 2 = record nr, 3 = button text */
    protected static final String htmlButton = """
              <div class="gd-button">
                <button type="button" class="btn btn-primary" onClick="submitEditForm('%s', %d); return false;">%s</button>
              </div>
            """;

    /** end buttonrow. */
    protected static final String htmlEndButtonRow = """
              </div>
            """;

    /** No tags. */
    protected static final String htmlStartForm = """
            <div class="gd-form">
              <form id="editForm" action="/gamedata-admin/admin" method="POST">
                <input id="editClick" type="hidden" name="editClick" value="tobefilled" />
                <input id="editRecordId" type="hidden" name="editRecordId" value="0" />
                """;

    /** No tags. */
    protected static final String htmlStartTable = """
                <table>
            """;

    /** end table. */
    protected static final String htmlEndTable = """
                </table>
            """;

    /** end form. */
    protected static final String htmlEndForm = """
               </form>
             </div>
            """;

    /* **************************************************************************************************************** */
    /* **************************************************** METHODS *************************************************** */
    /* **************************************************************************************************************** */

    public WebForm(final AdminData data)
    {
        this.s = new StringBuilder();
        data.setEditForm(this);
    }

    public WebForm setHeader(final String header)
    {
        setCancelMethod("record-cancel");
        setOkMethod("record-ok");
        this.s.append("<div class=\"gd-form-header\">\n");
        this.s.append("  <h3>");
        this.s.append(header);
        this.s.append("</h3>\n");
        this.s.append("</div>\n");
        return this;
    }

    public WebForm startForm()
    {
        this.s.append(htmlStartForm);
        this.s.append(htmlStartTable);
        return this;
    }

    public WebForm endForm()
    {
        this.s.append(htmlEndTable);
        buttonRow();
        this.s.append(htmlEndForm);
        return this;
    }

    public int getPhase(final HttpServletRequest request)
    {
        if (request.getParameter("phase-internal-use") == null)
            return 0;
        return Integer.valueOf(request.getParameter("phase-internal-use"));
    }

    public WebForm setPhase(final int phase)
    {
        addEntry(new FormEntryInt("phase", "phase-internal-use").setHidden().setReadOnly().setInitialValue(phase, phase));
        return this;
    }

    protected void buttonRow()
    {
        this.s.append(htmlStartButtonRow);
        this.s.append(htmlButton.formatted(this.cancelMethod, 0, "Cancel"));
        this.s.append(htmlButton.formatted(this.okMethod, 0, "Ok"));
        for (int i = 0; i < this.additionalButtons.size(); i++)
            this.s.append(htmlButton.formatted(this.additionalMethods.get(i), 0, this.additionalButtons.get(i)));
        this.s.append(htmlEndButtonRow);
    }

    public WebForm addEntry(final AbstractFormEntry<?, ?> entry)
    {
        this.entries.add(entry);
        entry.setForm(this);
        this.s.append(entry.makeHtml());
        return this;
    }

    public WebForm setCancelMethod(final String cancelMethod)
    {
        this.cancelMethod = cancelMethod;
        return this;
    }

    public WebForm setOkMethod(final String okMethod)
    {
        this.okMethod = okMethod;
        return this;
    }

    public WebForm addAddtionalButton(final String method, final String buttonText)
    {
        this.additionalButtons.add(buttonText);
        this.additionalMethods.add(method);
        return this;
    }

    public String getLabelLength()
    {
        return this.labelLength;
    }

    public WebForm setLabelLength(final String labelLength)
    {
        this.labelLength = labelLength;
        return this;
    }

    public String getFieldLength()
    {
        return this.fieldLength;
    }

    public WebForm setFieldLength(final String fieldLength)
    {
        this.fieldLength = fieldLength;
        return this;
    }

    public String process()
    {
        return this.s.toString();
    }

}
