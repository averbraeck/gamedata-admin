package nl.gamedata.admin;

/**
 * Navbar.java.
 * <p>
 * Copyright (c) 2024-2024 Delft University of Technology, PO Box 5, 2600 AA, Delft, the Netherlands. All rights reserved. <br>
 * BSD-style license. See <a href="https://github.com/averbraeck/gamedata-admin/LICENSE">GameData project License</a>.
 * </p>
 * @author <a href="https://github.com/averbraeck">Alexander Verbraeck</a>
 */
public class Navbar
{
    private static String navbar = """
              <!-- Navbar -->
              <nav id="main-navbar" class="navbar navbar-expand-lg navbar-light bg-white fixed-top">

                <!-- Container wrapper -->
                <div class="container-fluid">

                  <!-- Toggle button -->
                  <button data-mdb-button-init class="navbar-toggler" type="button"
                    data-mdb-collapse-init data-mdb-target="#sidebarMenu"
                    aria-controls="sidebarMenu" aria-expanded="false" aria-label="Toggle navigation">
                    <i class="fas fa-bars"></i>
                  </button>

                  <!-- Brand -->
                  <a class="navbar-brand" href="#" onclick="clickMenu('home')">
                    <h2>GameData</h2>
                  </a>

                  <!-- Right links -->
                  <ul class="navbar-nav ms-auto d-flex flex-row">

                    <!-- Icon -->
                    <li class="nav-item">
                      <a class="nav-link me-3 me-lg-0" href="#">
                        %s
                      </a>
                    </li>

                    <li class="nav-item">
                      <a class="nav-link me-3 me-lg-0" href="#" onclick="clickMenu('settings')">
                        <i class="fas fa-user-gear"></i>
                      </a>
                    </li>

                    <!-- Icon -->
                    <li class="nav-item me-3 me-lg-0">
                      <a class="nav-link" href="#" onclick="clickMenu('logoff')">
                        <i class="fas fa-sign-out"></i>
                      </a>
                    </li>

                  </ul>
                </div>
                <!-- Container wrapper -->

              </nav>
              <!-- Navbar -->

            </header>
            <!--Main Navigation-->
                                  """;

    public static String makeNavbar(final AdminData data)
    {
        return navbar.formatted(data.getUsername());
    }

}
