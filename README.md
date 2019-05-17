# AntiAFK
Portable app to prevent computer from going idle

Portable java app that moves the mouse when computer is idle for the selected amount of time.  This is useful for public computers
that log you out after inactivity, or just to keep your screen alive when you don't feel like changing the system settings.  The app also
features an auto shutoff function.  If enabled, the app will stop moving the mouse after the selected amount of time.

Changing the amount of pixels can be useful if you want your screen to stay alive, while keeping "away" status in other apps (some need
to see a certain number of pixels of movement before registering the user as present).

If using on Windows, you may want to put the jar in an executable wrapper and grant it admin privileges (or make sure to not leave
a window with admin privileges as the active window), as the program cannot move the mouse without admin privileges while the currently focused
window has elevated privileges.

# Disclaimer
This software is distributed AS IS with no warranty expressed or implied.  It the end user's sole responsibility to read and understand the code in this project before executing it on any machine.  I hereby release myself from any and all liabilities!