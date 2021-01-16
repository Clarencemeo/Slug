# SlugLog
SlugLog project to keep track of banana slugs

# Important
- It's a bit buggy on the emulator: it doesn't read the user's location correctly on the emulator
unless they go onto the Google Maps app first and then set Location permissions there by 
clicking "Your Location" there. Features should work as intended on the app if used on a real phone.
- As of right now, there is a feature to allow users to enter in a description and picture for the marker,
although placing down the pin does not actually show this description or picture yet.

# Notices for Teammates 
- Android Studio has built in git functionality, lmk if you need help using that 
- To run the app, you can connect a phone to your laptop and then click run on the top right. 
You could use the emulator but its kinda slow.
- If you wanna edit any of the layouts, just go to the XML file and click on the design tab.
- There are three main activities (aka screens)

     - The first activity is the firstscreen_activity.java which is just the map where 
       the user should be able to see other pins. As of now, they can only see their 
       own location and click the marker on the bottom right to "place" one 
     
     - the next activity, acitivty_uio.java has the form where users put a picture and a description
     
     - the last activity, confirmationscreen.java is where users confirm their inputs that they put in
     activitiy_uio.java.
   
- For now, don't use the drawMarker function. I planned to use this to draw circle markers with pictures
that the user took, however, I'll probably be abandoning this since it is difficult to store images
into a database. As such, when creating a marker, just simply create a marker with the icon_slug as
the icon resource. 
- The getImageUri may also have some bugs in it as well. This is used to convert the bitmap images
from the users camera into a url, but we probably won't be storing the urls anyway.
