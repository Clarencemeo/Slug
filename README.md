# SlugLog
SlugLog project to keep track of banana slugs

# Remaining Tasks
- Brush up the ui.
- Add filters and dropdowns on the start menu.
- Allow users to see most recently logged slug 
- Communicate with back end to display all the markers
    - Store the location, description, and image data into the database 
    - Place markers from the database
- Vision AI from Google Cloud to make sure all images are safe
- Maybe calculate nearest slug based on distance 
- Maybe allowing pins to disappear after a set amount of time to prevent clutter 
- Maybe a thumbs up/thumbs down system where people can vote on how accurate a pin is 
- Probably more I'm forgetting 

# Notices 
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
