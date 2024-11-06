package nl.gamedata.admin;

public class ModalWindowUtils
{

    public static void popup(AdminData data, String title, String message, String okMethod)
    {
        // make popup
        StringBuilder s = new StringBuilder();
        s.append("<p>");
        s.append(message);
        s.append("</p>\n");
        data.setModalWindowHtml(makeOkModalWindow(title, s.toString(), okMethod));
        data.setShowModalWindow(1);
    }

    public static String makeModalWindow(String title, String content, String onClickClose)
    {
        StringBuilder s = new StringBuilder();
        s.append("    <div class=\"hg-modal\">\n");
        s.append("      <div class=\"hg-modal-window\" id=\"hg-modal-window\">\n");
        s.append("        <div class=\"hg-modal-window-header\">");
        s.append("          <span class=\"hg-modal-close\" onclick=\"");
        s.append(onClickClose);
        s.append("\">");
        s.append("&times;</span>\n");
        s.append("          <p>");
        s.append(title);
        s.append("</p>\n");
        s.append("        </div>\n");
        s.append(content);
        s.append("      </div>\n");
        s.append("    </div>\n");
        s.append("    <script>");
        s.append("      dragElement(document.getElementById(\"hg-modal-window\"));");
        s.append("    </script>");
        return s.toString();
    }

    public static String makeOkModalWindow(String title, String htmlText, String okMethod)
    {
        StringBuilder s = new StringBuilder();
        s.append("        <div class=\"hg-modal-body\">");
        s.append("          <div class=\"hg-modal-text\">\n");
        s.append("            <p>\n");
        s.append(htmlText);
        s.append("            </p>\n");
        s.append("          <div class=\"hg-modal-button-row\">\n");
        s.append("            <div class=\"hg-button-small\" onclick=\"" + okMethod + "\">OK</div>\n");
        s.append("          </div>\n");
        s.append("        </div>\n");
        return makeModalWindow(title, s.toString(), okMethod);
    }

    public static String makeOkModalWindow(String title, String htmlText)
    {
        return makeOkModalWindow(title, htmlText, "clickModalWindowOk()");
    }

    public static void make2ButtonModalWindow(AdminData data, String title, String content, String buttonText1,
            String buttonMethod1, String buttonText2, String buttonMethod2, String closeMethod)
    {
        StringBuilder s = new StringBuilder();
        s.append("    <div class=\"hg-modal\">\n");
        s.append("      <div class=\"hg-modal-window\" id=\"hg-modal-window\">\n");
        s.append("        <div class=\"hg-modal-window-header\">");
        s.append("          <span class=\"hg-modal-close\" onclick=\"");
        s.append(closeMethod);
        s.append("\">");
        s.append("&times;</span>\n");
        s.append("          <p>");
        s.append(title);
        s.append("</p>\n");
        s.append("        </div>\n"); // hg-modal-window-header
        s.append("        <div class=\"hg-modal-body\">");
        s.append("          <div class=\"hg-modal-text\">\n");
        s.append("            <p>\n");
        s.append(content);
        s.append("            </p>\n");
        s.append("          </div>\n"); // hg-modal-text
        s.append("          <div class=\"hg-modal-button-row\">\n");
        s.append("            <div class=\"hg-button-small\" onclick=\"");
        s.append(buttonMethod1);
        s.append("\">");
        s.append(buttonText1);
        s.append("</div>\n");
        s.append("            <div class=\"hg-button-small\" onclick=\"");
        s.append(buttonMethod2);
        s.append("\">");
        s.append(buttonText2);
        s.append("</div>\n");
        s.append("          </div>\n"); // hg-modal-button-row
        s.append("        </div>\n"); // hg-hg-modal-body
        s.append("      </div>\n"); // hg-modal-window
        s.append("    </div>\n"); // hg-modal
        s.append("    <script>");
        s.append("      dragElement(document.getElementById(\"hg-modal-window\"));");
        s.append("    </script>");

        data.setModalWindowHtml(s.toString());
        data.setShowModalWindow(1);
    }

}
