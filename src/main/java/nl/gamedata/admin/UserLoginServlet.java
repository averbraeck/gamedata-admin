package nl.gamedata.admin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.xml.bind.DatatypeConverter;
import nl.gamedata.common.SqlUtils;
import nl.gamedata.data.tables.records.UserRecord;

@WebServlet("/login")
public class UserLoginServlet extends HttpServlet
{

    /** */
    private static final long serialVersionUID = 1L;

    @Override
    public void init() throws ServletException
    {
        super.init();
        System.getProperties().setProperty("org.jooq.no-logo", "true");

        // retrieve the username and password for the database
        String homeFolder = System.getProperty("user.home");
        if (homeFolder == null)
        {
            throw new ServletException("Home folder to retrieve database credentials not found");
        }
        String configDir = homeFolder + File.separator + "gamedata";
        File configFile = new File(configDir, "gamedata.properties");
        Properties gamedataProperties = new Properties();
        try
        {
            InputStream stream = new FileInputStream(configFile);
            gamedataProperties.load(stream);
        }
        catch (FileNotFoundException fnfe)
        {
            throw new ServletException(
                    "File with database credentials not found at " + configDir + "/" + "gamedata.properties");
        }
        catch (IOException ioe)
        {
            throw new ServletException("Error when reading database credentials at " + configDir + "/" + "gamedata.properties");
        }
        String dbUser = gamedataProperties.getProperty("dbUser");
        String dbPassword = gamedataProperties.getProperty("dbPassword");
        if (dbUser == null || dbPassword == null)
        {
            throw new ServletException(
                    "Properties dbUser or dbPassword not found in " + configDir + "/" + "gamedata.properties");
        }

        // determine the connection pool, and create one if it does not yet exist (first use after server restart)
        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver");
        }
        catch (ClassNotFoundException e)
        {
            throw new ServletException(e);
        }

        try
        {
            Context ctx = new InitialContext();
            try
            {
                ctx.lookup("/gamedata-admin_datasource");
            }
            catch (NamingException ne)
            {
                final HikariConfig config = new HikariConfig();
                config.setJdbcUrl("jdbc:mysql://localhost:3306/gamedata");
                config.setUsername(dbUser);
                config.setPassword(dbPassword);
                config.setMaximumPoolSize(2);
                config.setDriverClassName("com.mysql.cj.jdbc.Driver");
                DataSource dataSource = new HikariDataSource(config);
                ctx.bind("/gamedata-admin_datasource", dataSource);
            }
        }
        catch (NamingException e)
        {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException
    {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        HttpSession session = request.getSession();
        AdminData data = new AdminData();
        session.setAttribute("adminData", data);
        try
        {
            data.setDataSource((DataSource) new InitialContext().lookup("/gamedata-admin_datasource"));
        }
        catch (NamingException e)
        {
            throw new ServletException(e);
        }

        UserRecord user = SqlUtils.readUserFromUsername(data, username);
        if (user != null)
        {
            MessageDigest md;
            String hashedPassword;
            try
            {
                // https://www.baeldung.com/java-md5
                md = MessageDigest.getInstance("MD5");
                String saltedPassword = password + user.getSalt();
                md.update(saltedPassword.getBytes());
                byte[] digest = md.digest();
                hashedPassword = DatatypeConverter.printHexBinary(digest).toLowerCase();
            }
            catch (NoSuchAlgorithmException e1)
            {
                throw new ServletException(e1);
            }

            String userPassword = user == null ? "" : user.getPassword() == null ? "" : user.getPassword();
            if (user != null && userPassword.equals(hashedPassword))
            {
                data.setUsername(user.getName());
                data.setUser(user);
                data.setMenuChoice("home");
                data.putSubMenuChoice("home", "");
                response.sendRedirect("jsp/admin/admin.jsp");
                return;
            }
        }
        session.removeAttribute("adminData");
        response.sendRedirect("jsp/admin/login.jsp");
    }

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException
    {
        response.sendRedirect("jsp/admin/login.jsp");
    }

}
