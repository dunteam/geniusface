Instruction: Launch the app, make funny face to the camera preview, click capture button, and you will see the result. 

Here is a demo script to demonstrate what the app can do now

1) When the app is started it shows the camera preview and two buttons which are for switching camera and taking picture. The camera preview has a oval shape which helps the user to place his head when taking photo. The camera switch button only appears when the number of cameras available is more than one. Both of the buttons have no text on it. Instead it shows images. If there is a front camera, application starts with front camera by default.

2) When the user clicks capture button it takes the user to the activity which shows the picture taken but with eyes that were distorted using pincushion filter. This makes picture look funny. There are two buttons named "Save" and "Cancel". 

3) Cancel and back buttons will cause the activity to send RESULT_CANCELED result to the main activity. After clicking these button the main activity will appear without any change.

4) Save button will cause the activity to return RESULT_OK results to the main activity. When this button is clicked the image will be saved to the /DCIM/Camera folder with a name that is generated using current timestamp.

Thank you
