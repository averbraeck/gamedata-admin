package nl.gamedata.admin.form;

import nl.gamedata.admin.form.table.TableForm;

public abstract class AbstractFormEntry<F extends AbstractFormEntry<F, T>, T>
{

    private TableForm form;

    private String label;

    private String name;

    // private String type;

    private boolean required;

    private boolean readOnly;

    private boolean hidden = false;

    protected String errors; // cumulative error register

    protected int minLength; // minimum "typed" length in nr of characters

    protected T initialValue; // to be able to reset the form

    protected String lastEnteredValue; // to restore the form after error

    public AbstractFormEntry(final String label, final String name)
    {
        this.label = label;
        this.name = name;
        this.required = false;
        this.readOnly = false;
        this.errors = "";
        this.minLength = 0;
    }

    public TableForm getForm()
    {
        return this.form;
    }

    public void setForm(final TableForm form)
    {
        this.form = form;
    }

    public String getLabel()
    {
        return this.label;
    }

    @SuppressWarnings("unchecked")
    public F setLabel(final String label)
    {
        this.label = label;
        return (F) this;
    }

    public String getName()
    {
        return this.name;
    }

    @SuppressWarnings("unchecked")
    public F setName(final String name)
    {
        this.name = name;
        return (F) this;
    }

    public boolean isRequired()
    {
        return this.required;
    }

    @SuppressWarnings("unchecked")
    public F setRequired(final boolean required)
    {
        this.required = required;
        return (F) this;
    }

    public F setRequired()
    {
        return setRequired(true);
    }

    public boolean isHidden()
    {
        return this.hidden;
    }

    @SuppressWarnings("unchecked")
    public F setHidden(final boolean hidden)
    {
        this.hidden = hidden;
        return (F) this;
    }

    public F setHidden()
    {
        return setHidden(true);
    }

    public int getMinLength()
    {
        return this.minLength;
    }

    @SuppressWarnings("unchecked")
    public F setMinLength(final int minLength)
    {
        this.minLength = minLength;
        return (F) this;
    }

    public T getInitialValue()
    {
        return this.initialValue;
    }

    @SuppressWarnings("unchecked")
    protected F setInitialValue(final T initialValue, final T valueWhenNull)
    {
        this.initialValue = initialValue != null ? initialValue : valueWhenNull;
        setLastEnteredValue(codeForEdit(this.initialValue));
        return (F) this;
    }

    public String getLastEnteredValue()
    {
        return this.lastEnteredValue;
    }

    public void setLastEnteredValue(final String lastEnteredValue)
    {
        this.lastEnteredValue = lastEnteredValue;
    }

    public boolean isReadOnly()
    {
        return this.readOnly;
    }

    @SuppressWarnings("unchecked")
    public F setReadOnly(final boolean readOnly)
    {
        this.readOnly = readOnly;
        return (F) this;
    }

    @SuppressWarnings("unchecked")
    public F setReadOnly()
    {
        this.readOnly = true;
        return (F) this;
    }

    public String getErrors()
    {
        return this.errors;
    }

    public abstract String codeForEdit(T value);

    public abstract T codeForType(String s);

    protected void addError(final String error)
    {
        this.errors += "<p>Field: '" + getLabel() + "' " + error + "</p>\n";
    }

    protected void validate(final String value)
    {
        this.lastEnteredValue = value;
        this.errors = "";
        if (value == null && isRequired())
            addError("should not be null");
        else if (value != null)
        {
            if (value.length() < this.minLength)
                addError("should not be empty");
        }
    }

    public abstract String makeHtml();

}
