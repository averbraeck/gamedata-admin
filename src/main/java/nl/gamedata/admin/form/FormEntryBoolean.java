package nl.gamedata.admin.form;

public class FormEntryBoolean extends AbstractFormEntry<FormEntryBoolean, Byte>
{

    // assumes boolean is coded as TINYINT
    public FormEntryBoolean(final String label, final String name)
    {
        super(label, name);
    }

    @Override
    public String codeForEdit(final Byte value)
    {
        if (value == null)
            return "0";
        return value.byteValue() == 1 ? "1" : "0";
    }

    @Override
    public Byte codeForType(final String s)
    {
        return "1".equals(s) ? (byte) 1 : (byte) 0;
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
            s.append(getLastEnteredValue() == null ? "0" : getLastEnteredValue());
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
        s.append("<input type=\"checkbox\" name=\"");
        s.append(getName());
        s.append("\" ");
        s.append(getLastEnteredValue() == null || "0".equals(getLastEnteredValue()) ? "" : "checked");
        s.append(" value=\"1\"");
        if (isReadOnly())
            s.append(" readonly />");
        else
            s.append(" />");
        s.append("</td>\n");
        s.append("    </tr>\n");
        return s.toString();
    }

}
