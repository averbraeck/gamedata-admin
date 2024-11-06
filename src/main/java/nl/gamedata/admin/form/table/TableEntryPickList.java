package nl.gamedata.admin.form.table;

import java.util.List;

import org.jooq.TableField;

public class TableEntryPickList extends AbstractTableEntry<TableEntryPickList, String>
{

    private String[] pickListEntries;

    public TableEntryPickList(final TableField<?, String> tableField)
    {
        super(tableField);
    }

    @Override
    public String codeForEdit(final String value)
    {
        if (value == null)
            return "";
        return value.toString();
    }

    @Override
    public String codeForType(final String s)
    {
        for (String entry : this.pickListEntries)
        {
            if (entry.equals(s))
                return entry;
        }
        return null;
    }

    public String[] getPickListEntries()
    {
        return this.pickListEntries;
    }

    public TableEntryPickList setPickListEntries(final String[] pickListEntries)
    {
        this.pickListEntries = pickListEntries;
        return this;
    }

    public TableEntryPickList setPickListEntries(final List<String> pickListEntries)
    {
        this.pickListEntries = pickListEntries.toArray(new String[] {});
        return this;
    }

    public TableEntryPickList setPickListEntries(final Class<? extends Enum<?>> pickListClass)
    {
        var enumArray = pickListClass.getEnumConstants();
        this.pickListEntries = new String[enumArray.length];
        for (int i=0; i<enumArray.length; i++)
            this.pickListEntries[i] = enumArray[i].toString();
        return this;
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
        s.append("        <select ");
        if (isRequired())
            s.append(" required name=\"");
        else
            s.append(" name=\"");
        s.append(getTableField().getName());
        if (isReadOnly())
            s.append("\" readonly>\n");
        else
            s.append("\">\n");
        for (String entry : getPickListEntries())
        {
            s.append("        <option value=\"");
            s.append(entry);
            s.append("\"");
            if (entry.equals(getLastEnteredValue()))
            {
                s.append(" selected");
            }
            s.append(">");
            s.append(entry);
            s.append("</option>\n");
        }
        s.append("        </select>\n");

        if (getTableField().getDataType().nullable())
        {
            s.append("&nbsp;&nbsp;<input type=\"checkbox\" name=\"");
            s.append(getTableField().getName() + "-null\" value=\"null\"");
            s.append(getLastEnteredValue() == null ? " checked" : "");
            s.append(" />");
        }

        s.append("      </td>\n");
        s.append("    </tr>\n");
        return s.toString();
    }

}
