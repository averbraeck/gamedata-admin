package nl.gamedata.admin.form.table;

import org.jooq.TableField;

public class TableEntryString extends AbstractTableEntry<TableEntryString, String>
{

    int maxChars;

    public TableEntryString(final TableField<?, String> tableField)
    {
        super(tableField);
        this.maxChars = tableField.getDataType().length();
    }

    public int getMaxChars()
    {
        return this.maxChars;
    }

    public TableEntryString setMaxChars(final int maxChars)
    {
        this.maxChars = maxChars;
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
        if (value == null)
            return "";
        return value;
    }

    @Override
    public String codeForType(final String s)
    {
        return s;
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
        s.append("<input type=\"text\" style=\"width:97%;\" maxlength=\"");
        s.append(getMaxChars());
        if (isRequired())
            s.append("\" required name=\"");
        else
            s.append("\" name=\"");
        s.append(getTableField().getName());
        s.append("\" value=\"");
        s.append(getLastEnteredValue() == null ? "" : getLastEnteredValue());
        if (isReadOnly() || !getForm().isEdit())
            s.append("\" readonly />");
        else
            s.append("\" />");

        if (getTableField().getDataType().nullable())
        {
            s.append("&nbsp;&nbsp;<input type=\"checkbox\" name=\"");
            s.append(getTableField().getName() + "-null\" value=\"null\"");
            s.append(getLastEnteredValue() == null ? " checked" : "");
            if (isReadOnly() || !getForm().isEdit())
                s.append(" readonly />");
            else
                s.append(" />");
        }

        s.append("</td>\n");
        s.append("    </tr>\n");
        return s.toString();
    }

}
