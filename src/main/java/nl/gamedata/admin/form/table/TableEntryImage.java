package nl.gamedata.admin.form.table;

import org.jooq.Record;
import org.jooq.TableField;

public class TableEntryImage extends AbstractTableEntry<TableEntryImage, byte[]>
{

    /** the picture itself is going to/from the database; the String codes for the filename. */
    String filename;

    /** the bytes of the file after reading. */
    byte[] image;

    /** the name of servlet to retrieve the current image; if null or empty: no current image. */
    private String imageServlet;

    /** the record number for retrieving the current image. */
    private int imageRecordNr;

    /** the image number to retrieve if multiple images exist (0 means do not query). */
    private int imageNr;

    /** large image (200x200). */
    private boolean largeImage;

    public TableEntryImage(final TableField<?, byte[]> tableField)
    {
        super(tableField);
        this.filename = "";
        this.imageServlet = "";
        this.largeImage = false;
        this.imageNr = 0;
    }

    @Override
    public void validate(final String s)
    {
        // Nothing to validate on the filename itself
    }

    @Override
    public String codeForEdit(final byte[] image)
    {
        this.image = image;
        return this.filename;
    }

    @Override
    public byte[] codeForType(final String s)
    {
        this.filename = s;
        return this.image;
    }

    public byte[] getImage()
    {
        return this.image;
    }

    public void setImage(final byte[] image)
    {
        this.image = image;
    }

    public String getFilename()
    {
        return this.filename;
    }

    public void setFilename(final String filename)
    {
        this.filename = filename;
    }

    public String getImageServlet()
    {
        return this.imageServlet;
    }

    public TableEntryImage setImageServlet(final String imageServlet)
    {
        this.imageServlet = imageServlet;
        return this;
    }

    public int getImageRecordNr()
    {
        return this.imageRecordNr;
    }

    public TableEntryImage setImageRecordNr(final int imageRecordNr)
    {
        this.imageRecordNr = imageRecordNr;
        return this;
    }

    public boolean isLargeImage()
    {
        return this.largeImage;
    }

    public TableEntryImage setLargeImage(final boolean largeImage)
    {
        this.largeImage = largeImage;
        return this;
    }

    public TableEntryImage setLargeImage()
    {
        this.largeImage = true;
        return this;
    }

    public int getImageNr()
    {
        return this.imageNr;
    }

    public TableEntryImage setImageNr(final int imageNr)
    {
        this.imageNr = imageNr;
        return this;
    }

    @SuppressWarnings("unchecked")
    public String setRecordValue(final Record record, final byte[] value)
    {
        this.image = value;
        this.errors = "";
        try
        {
            record.set((TableField<?, byte[]>) getTableField(), this.image);
        }
        catch (Exception exception)
        {
            addError("Exception: " + exception.getMessage());
        }
        return this.errors;
    }

    @Override
    public String makeHtml()
    {
        StringBuilder s = new StringBuilder();
        s.append("    <tr>\n");
        String labelLength = getForm() == null ? "25%" : getForm().getLabelLength();
        s.append("      <td width=\"" + labelLength + "\">");
        s.append(getLabel());
        if (isRequired())
            s.append(" *");
        s.append("      </td>");
        String fieldLength = getForm() == null ? "75%" : getForm().getFieldLength();
        s.append("      <td width=\"" + fieldLength + "\">");
        if (!isRequired())
        {
            s.append("        <input id=\"");
            s.append(getTableField().getName() + "_reset");
            s.append("\" type=\"hidden\" name=\"");
            s.append(getTableField().getName() + "_reset");
            s.append("\" value=\"normal\" />\n");
            s.append("        <button type=\"button\" onclick=\"resetImage('");
            s.append(getTableField().getName());
            s.append("')\">Remove Image</button>&nbsp; &nbsp; \n");
        }
        s.append("        <input type=\"file\" accept=\"image/*\" onchange=\"previewImage(event, '");
        s.append(getTableField().getName());
        s.append("')\" name=\"");
        s.append(getTableField().getName());
        s.append("\" id=\"");
        s.append(getTableField().getName());
        if (isReadOnly())
            s.append("'\" readonly />\n");
        else
            s.append("'\" />\n");
        if (isLargeImage())
            s.append("        <div class=\"gd-preview-image-200\">\n");
        else
            s.append("        <div class=\"gd-preview-image-100\">\n");
        s.append("          <img id=\"");
        s.append(getTableField().getName());
        if (getImageServlet().length() > 0 && getImageRecordNr() > 0)
        {
            s.append("\" src=\"/gamedata-admin/");
            s.append(getImageServlet());
            s.append("?id=");
            s.append(getImageRecordNr());
            if (this.imageNr > 0)
            {
                s.append("&image=");
                s.append(getImageNr());
            }
        }
        s.append("\" />\n");
        s.append("        </div>\n");
        s.append("      </td>\n");
        s.append("    </tr>\n");
        return s.toString();
    }

}
