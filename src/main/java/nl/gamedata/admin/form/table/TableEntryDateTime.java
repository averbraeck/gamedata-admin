package nl.gamedata.admin.form.table;

import java.time.LocalDateTime;

import org.jooq.TableField;
import org.jooq.UpdatableRecord;

import nl.gamedata.admin.AdminData;

public class TableEntryDateTime extends AbstractTableEntry<TableEntryDateTime, LocalDateTime>
{

    public <R extends UpdatableRecord<R>> TableEntryDateTime(final AdminData data, final boolean reedit,
            final TableField<R, LocalDateTime> tableField, final UpdatableRecord<R> record)
    {
        super(data, reedit, tableField, record);
    }

    @Override
    protected LocalDateTime getDefaultValue()
    {
        return LocalDateTime.now();
    }

    @Override
    public String codeForEdit(final LocalDateTime value)
    {
        if (value == null)
            return "";
        return value.toString();
    }

    @Override
    public LocalDateTime codeForType(final String s)
    {
        if (s != null && s.length() > 0)
        {
            return LocalDateTime.parse(s);
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
                LocalDateTime.parse(value);
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
        s.append("<input type=\"datetime-local\" ");
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
