package nl.gamedata.admin.form;

public class FormEntryDouble extends AbstractFormEntry<FormEntryDouble, Double>
{

    double min;

    double max;

    Double step;

    public FormEntryDouble(final String label, final String name)
    {
        super(label, name);
        this.min = -Double.MAX_VALUE;
        this.max = Double.MAX_VALUE;
        this.step = Double.NaN;
    }

    public double getMin()
    {
        return this.min;
    }

    public FormEntryDouble setMin(final double min)
    {
        this.min = min;
        return this;
    }

    public double getMax()
    {
        return this.max;
    }

    public FormEntryDouble setMax(final double max)
    {
        this.max = max;
        return this;
    }

    public double getStep()
    {
        return this.step;
    }

    public FormEntryDouble setStep(final double step)
    {
        this.step = step;
        return this;
    }

    @Override
    public String codeForEdit(final Double value)
    {
        if (value == null)
            return "";
        return value.toString();
    }

    @Override
    public Double codeForType(final String s)
    {
        return Double.valueOf(s);
    }

    @Override
    protected void validate(final String value)
    {
        super.validate(value);
        try
        {
            double v = Double.valueOf(value);
            if (v < getMin())
                addError("Value lower than minimum " + getMin());
            if (v > getMax())
                addError("Value larger than maximum " + getMax());
            // TODO: step
        }
        catch (Exception exception)
        {
            addError("Exception: " + exception.getMessage());
        }
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
        s.append("<input type=\"number\" min=\"");
        s.append(getMin());
        s.append("\" max=\"");
        s.append(getMax());
        if (!Double.isNaN(getStep()))
        {
            s.append("\" step=\"");
            s.append(getStep());
        }
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
