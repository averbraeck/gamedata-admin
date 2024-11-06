package nl.gamedata.admin.form;

import org.jooq.EnumType;

public class FormEntryEnum<T extends EnumType> extends AbstractFormEntry<FormEntryEnum<T>, T>
{

    private T[] pickListEntries;

    public FormEntryEnum(final String label, final String name)
    {
        super(label, name);
    }

    @Override
    public String codeForEdit(final T value)
    {
        if (value == null)
            return "";
        return value.toString();
    }

    @Override
    public T codeForType(final String s)
    {
        for (T entry : this.pickListEntries)
        {
            if (entry.getLiteral().equals(s))
                return entry;
        }
        return null;
    }

    public T[] getPickListEntries()
    {
        return this.pickListEntries;
    }

    public FormEntryEnum<T> setPickListEntries(final T[] pickListEntries)
    {
        this.pickListEntries = pickListEntries;
        return this;
    }

    @Override
    public String makeHtml()
    {
        StringBuilder s = new StringBuilder();

        if (isHidden())
        {
            s.append("    <input type=\"hidden\" name=\"");
            s.append(getName());
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
        s.append(getName());
        if (isReadOnly())
            s.append("\" readonly>\n");
        else
            s.append("\">\n");
        for (T entry : getPickListEntries())
        {
            s.append("        <option value=\"");
            s.append(entry.getLiteral());
            s.append("\"");
            if (entry.getLiteral().equals(getLastEnteredValue()))
            {
                s.append(" selected");
            }
            s.append(">");
            s.append(entry.getLiteral());
            s.append("</option>\n");
        }
        s.append("        </select>\n");
        s.append("      </td>\n");
        s.append("    </tr>\n");
        return s.toString();
    }

}
