[comment]: <> (This readme is based on the legacy readme.)
# Campus App for Android
The Campus App is an open source project based on the [TUM Campus App](https://github.com/TUM-Dev/Campus-Android/). In comparison, this app is a generic version of the TUM Campus App with the goal of reusability. The app can be configured and extended according to the requirements of a university.

## Configuration:
The extensive configurations of the app are located in the `Config.kt` file of the `de.uos.campusapp.config` package. All configurations are documented in this file.

## Features:
- Lecture calendar (with in-app view and a syncing service to the device's calendar)
- Meal plans of cafeterias 
- View all your grades
- Read your messages
- Find empty study rooms
- Station departure times
- News of your university
- Roomfinder
- Automatic muting while lectures are held
- Available in English and German

## Used Permissions:
+ Location services: So we can show you the nearest cafeteria / station departures
+ Access calendar: To sync your lecture calendar with your local calendar
+ Access contacts: So you can add people from the person search to your addressbook
+ Detailed WiFi information: So we can help you set up eduroam access
+ Camera: So you can join chat rooms by scanning a QR code

## License:
[GNU GPL v3](http://www.gnu.org/licenses/gpl.html)
