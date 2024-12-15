package nl.gamedata.admin.form.table;

import org.jooq.Record;
import org.jooq.TableField;

import nl.gamedata.admin.form.AbstractFormEntry;

public abstract class AbstractTableEntry<F extends AbstractTableEntry<F, T>, T> extends AbstractFormEntry<F, T>
{

    protected final TableField<?, T> tableField;

    private String type;

    private boolean noWrite = false;

    public AbstractTableEntry(final TableField<?, T> tableField)
    {
        super(tableField.getName(), tableField.getName());
        this.tableField = tableField;
        this.type = this.tableField.getType().getName().toUpperCase();
        setRequired(!this.tableField.getDataType().nullable());
        char[] name = this.tableField.getName().toLowerCase().toCharArray();
        boolean upper = true;
        for (int i = 0; i < name.length; i++)
        {
            if (upper)
            {
                name[i] = Character.toUpperCase(Character.valueOf(name[i]));
                upper = false;
            }
            else if (name[i] == '-' || name[i] == '_')
            {
                name[i] = ' ';
                upper = true;
            }
        }
        setLabel(String.valueOf(name));
        setReadOnly(false);
        this.errors = "";
    }

    public String getType()
    {
        return this.type;
    }

    @SuppressWarnings("unchecked")
    public F setType(final String type)
    {
        this.type = type;
        return (F) this;
    }

    public TableField<?, ?> getTableField()
    {
        return this.tableField;
    }

    @SuppressWarnings("unchecked")
    public F setNoWrite()
    {
        this.noWrite = true;
        return (F) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public F setInitialValue(final T initialValue, final T valueWhenNull)
    {
        if (this.tableField.getDataType().nullable() && initialValue == null)
        {
            this.initialValue = null;
            setLastEnteredValue(null);
        }
        else
        {
            this.initialValue = initialValue != null ? initialValue : valueWhenNull;
            setLastEnteredValue(codeForEdit(this.initialValue));
        }
        return (F) this;
    }

    @Override
    protected void validate(final String value)
    {
        setLastEnteredValue(value);
        this.errors = "";
        if (value == null && !this.tableField.getDataType().nullable())
            addError("should not be null");
        else if (value.length() == 0 && isRequired())
            addError("should not be empty");
    }

    public String setRecordValue(final Record record, final String value)
    {
        if (this.noWrite)
            return "";
        validate(value);
        if (this.errors.length() == 0)
        {
            try
            {
                record.set((TableField<?, T>) this.tableField, codeForType(value));
            }
            catch (Exception exception)
            {
                addError("Exception: " + exception.getMessage());
            }
        }
        return this.errors;
    }

}
