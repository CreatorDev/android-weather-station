![](docs/img.png)

## The WeatherStation mobile app
WeatherStation mobile app is part of a bigger project. Using code from this respository You will
be able to read sensor values from all the sensors connected to You weather station.

## Environment for Weather Station project
The complete IoT Environment is built with following components:
* LWM2M cloud server
* Ci40 application which allows usage of clicks in mirkroBUS sockets.
* Contiki based applications build for clicker platform:
  *  [Temperature sensor](https://github.com/CreatorKit/temperature-sensor)
* a mobile application to present Weather measurements.

## Mobile application functionality
This mobile app presents data read from sensors in a user friendly way. Main screen of the app consists
of number of tiles on which current measurements are presented. Each tile has an pictogram different
for various sensors, current value, value unit aw well as minimum and maximum measurements within last 24 hours.

By default all tiles are put into one group but user can create new groups of sensors. Tiles
can be moved to specific group using drag abd drop technique. Those groups persists on a device but
are not propagated to cloud. Renaming and removing groups is possible as well.