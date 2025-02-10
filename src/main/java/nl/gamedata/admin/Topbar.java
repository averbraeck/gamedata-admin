package nl.gamedata.admin;

/**
 * Topbar.java.
 * <p>
 * Copyright (c) 2024-2024 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="https://github.com/averbraeck/gamedata-admin/LICENSE">GameData project License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public class Topbar
{
    private static String topbarStart = """
            <!-- Topbar -->
            <div id="main-topbar" class="gd-topbar">
              <img src="images/header.png">
                      """;

    private static String topbarEnd = """
            </div>
            <!-- Topbar -->
                      """;

    public static String makeTopbar(final AdminData data)
    {
        StringBuilder s = new StringBuilder();
        s.append(topbarStart);
        s.append(topbarEnd);
        return s.toString();
    }
}
