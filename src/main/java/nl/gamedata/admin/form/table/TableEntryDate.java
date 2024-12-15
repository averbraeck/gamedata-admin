package nl.gamedata.admin.form.table;

import java.time.LocalDate;

import org.jooq.TableField;
import org.jooq.UpdatableRecord;

public class TableEntryDate extends AbstractTableEntry<TableEntryDate, LocalDate>
{

    public <R extends UpdatableRecord<R>> TableEntryDate(final TableField<R, LocalDate> tableField, final UpdatableRecord<R> record)
    {
        super(tableField, record);
    }

    @Override
    protected LocalDate getDefaultValue()
    {
        return LocalDate.now();
    }

    @Override
    public String codeForEdit(final LocalDate value)
    {
        if (value == null)
            return "";
        return value.toString();
    }

    @Override
    public LocalDate codeForType(final String s)
    {
        if (s != null && s.length() > 0)
        {
            return LocalDate.parse(s);
        }
        return null;
    }

    @Override
    protected void validate(final String value)
    {
        super.validate(value);
        if (value != null && value.length() > 0)
        {
            try
            {
                LocalDate.parse(value);
            }
            catch (Exception exception)
            {
                addError("Exception: " + exception.getMessage());
            }
        }
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
        s.append("<input type=\"date\" ");
        if (isRequired())
            s.append("required name=\"");
        else
            s.append("name=\"");
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
