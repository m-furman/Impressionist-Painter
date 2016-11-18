# Impressionist-Painter
A simple app which allows the user to create an impressionist painting out of their favorite photo. Created for CMSC434 (Introduction to 
Human-Computer Interaction) at the University of Maryland.

//TODO: Put in screenshots

## How to run the program:

Run the app on your device by clicking the "Run" button in Android Studio or by clicking on the app icon. After the app launches, you will see all options that are directly tied to the action of painting in the lower left-hand corner of the app. These options include downloading test images from the web, loading an image from the device, selecting a brush type, and clearing the current canvas. In the upper right hand corner of the app, you will see the share and save menu options. The save option saves the current painting to the user's device, and the share option allows the user to share the painting using their desired image sharing app.

## Online sources:

Used this stackoverflow page to help with the implementation of the sharing functionality:
http://stackoverflow.com/questions/9049143/android-share-intent-for-a-bitmap-is-it-possible-not-to-save-it-prior-sharing

Used this stackoverflow page to help with saving the bitmap to the user's device:
http://stackoverflow.com/questions/649154/save-bitmap-to-location

Used this stackoverflow page to assist with the use of the VelocityTracker:
http://stackoverflow.com/questions/5815975/get-speed-of-a-ontouch-action-move-event-in-android

Used this page from Android Developers in order to help me figure out how to draw to a canvas:
https://developer.android.com/guide/topics/graphics/2d-graphics.html

Used this page from Android Developers in order to figure out how to use a bitmap:
https://developer.android.com/reference/android/graphics/Bitmap.html

Used the following two Android Developers pages to help me figure out how to track the velocity of motion events:
https://developer.android.com/reference/android/view/VelocityTracker.html
https://developer.android.com/training/gestures/movement.html
