package nl.gamedata.admin.form.table;

import org.jooq.TableField;

public class TableEntryText extends AbstractTableEntry<TableEntryText, String>
{

    int maxChars;

    int rows;

    public TableEntryText(final TableField<?, String> tableField)
    {
        super(tableField);
        this.maxChars = 65535;
        this.rows = 10;
    }

    public int getMaxChars()
    {
        return this.maxChars;
    }

    public TableEntryText setMaxChars(final int maxChars)
    {
        this.maxChars = maxChars;
        return this;
    }

    public int getRows()
    {
        return this.rows;
    }

    public TableEntryText setRows(final int rows)
    {
        this.rows = rows;
        return this;
    }

    @Override
    public void validate(final String s)
    {
        super.validate(s);
        if (s.length() > getMaxChars())
            addError("Length is over " + getMaxChars() + " characters");
    }

    @Override
    public String codeForEdit(final String value)
    {
        String s = value;
        if (s == null)
            s = "";
        s = s.replaceAll("[&]", "&amp;").replaceAll("[<]", "&lt;").replaceAll("[>]", "&gt;");
        return s;
    }

    @Override
    public String codeForType(final String s)
    {
        return s.strip();
    }

    @Override
    public String makeHtml()
    {
        StringBuilder s = new StringBuilder();

        if (isHidden())
        {
            s.append("    <input type=\"hidden\" name=\"");
            s.append(getTableField().getName());
            s.append("\" value=\"");
            s.append(getLastEnteredValue() == null ? "" : getLastEnteredValue());
            s.append("\" />\n");
            return s.toString();
        }

        s.append("    <tr>\n");
        String labelLength = getForm() == null ? "25%" : getForm().getLabelLength();
        s.append("      <td width=\"" + labelLength + "\">");
        s.append(getLabel());
        if (isRequired())
            s.append(" *");
        s.append("      </td>");
        String fieldLength = getForm() == null ? "75%" : getForm().getFieldLength();
        s.append("      <td width=\"" + fieldLength + "\">");
        s.append("<textarea rows=\"");
        s.append(getRows());
        s.append("\" style=\"width:97%;\" maxlength=\"");
        s.append(getMaxChars());
        if (isRequired())
            s.append("\" required name=\"");
        else
            s.append("\" name=\"");
        s.append(getTableField().getName());
        if (isReadOnly())
            s.append("\" readonly>\n");
        else
            s.append("\">\n");
        s.append(getLastEnteredValue() == null ? "" : getLastEnteredValue());
        s.append("\n</textarea>\n");
        s.append("</td>\n");
        s.append("    </tr>\n");
        return s.toString();
    }

}
