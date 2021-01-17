# SlugLog
This project allows a user to see all the banana slugs that other users have found, and it
allows the user to place down their own pins on a Google Map where they have found a banana slag.

# How to Run
After getting the repository onto Android Studio, either connect your Android phone or set up
the emulator through the AVD Manager. Then, click run on the top right to run the application.

# How to Use 

It should open the app and bring you to the first screen which has Google Maps alongside a
red marker for your location. You may see other banana slug pins, which are other markers
that other users have placed down. There's a search bar to go to different parts of the map,
however you can only place a pin at your current location. 

The second screen asks you to enter a description and image. The description will be
displayed on your marker when you place it, and we unfortunately did not find a way
to display the image as well with the marker.

# Notices

- There is a function to take a picture of the banana slug that you encounter,
however we did not manage to implement the picture into the actual marker.
The image did sucessfully get stored into the Firebase database, though. 

- We attempted to incorporate machine learning into our project by having the program
sense if the picture taken was a banana slug or not, but we were unable to incorporate
it in time.

- If this project is run on the emulator, there may be some issues retrieving 
the users location. A possible fix is to open the Google Maps app on the emulator,
click the bottom right icon to go to a location, and then click "Your Location"
so that the emulator is able to access your location. Afterwards, open the app.

