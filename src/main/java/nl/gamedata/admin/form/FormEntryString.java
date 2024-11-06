package nl.gamedata.admin.form;

public class FormEntryString extends AbstractFormEntry<FormEntryString, String>
{

    int maxChars;

    public FormEntryString(final String label, final String name)
    {
        super(label, name);
        this.maxChars = 65535;
    }

    public int getMaxChars()
    {
        return this.maxChars;
    }

    public FormEntryString setMaxChars(final int maxChars)
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
        s.append("<input type=\"text\" style=\"width:97%;\" maxlength=\"");
        s.append(getMaxChars());
        if (isRequired())
            s.append("\" required name=\"");
        else
            s.append("\" name=\"");
        s.append(getName());
        s.append("\" value=\"");
        s.append(getLastEnteredValue() == null ? "" : getLastEnteredValue());
        if (isReadOnly())
            s.append("\" readonly />");
        else
            s.append("\" />");
        s.append("</td>\n");
        s.append("    </tr>\n");
        return s.toString();
    }

}
