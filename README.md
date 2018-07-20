# AntiAFK
Portable app to prevent computer from going idle

Portable java app that moves the mouse when computer is idle for the selected amount of time.  This is useful for public computers
that log you out after inactivity, or just to keep your screen alive when you don't feel like changing the system settings.  The app also
features an auto shutoff function.  If enabled, the app will stop moving the mouse after the selected amount of time.

If using on Windows, you may want to put the jar in an executable wrapper and grant it admin privileges (or make sure to not leave
a window with admin priviliges as the active window), as the program cannot move the mouse without admin priviliges while the active
window has elevated priviliges.

Seems to work fine is on Ubuntu, not sure about Mac.

This is a work in progress.  Not sure when/if I will get more time to work on this.

Some things I hope to do in the future:

-Make a better UI

-Clean up threading, messy recursive calls and otherwise unintuitive/repetitive code

-Add [optional] system tray icon