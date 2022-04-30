The "Pro" series is a concept in RW based physics for MSTS.  Areas that are included in this series include coupler
regions, brake regions, Throttle percentages, Brake_Engine values and an new 24RL brake system.  This series was
designed based on Bob Boudoin's engine physics and J-L Chauvin's brake theories Otto Wipfel's DEAFULT wag file as well
as Theories included by Joe Morris.

Method of Implimentation:

First install and copy over your original DEFAULT wag file with the one included in this pack!!!  You must do this or
your couplers may not work properly.  This includes using these couplers on non Bin equipped MSTS systems.

Locomotives:

-First update your locomotives using Bob Boudoin's latest physics version for the particular locomotive you are
upgrading.

-Upgrade to the appropriate coupler for your engine using the tables in the Pro-Coupler folder.  You can also use the
latest version of Route Riter to do this upgrade and save a ton of time.  If upgrading a switch engine you will have to
manually upgrade the coupler using the manual button in R_Riter.

-Upgrade your top brake region for your particular engine using the appropriatemaxbrakeforce  brake table in either the
Pro composition or Pro cast iron brake folder.  You can also use Route Riter to do this task for you as well.  If you
are upgrading a switch engine then you will have to manually change the brake region using the manual button in R_Riter.

-next make sure you modify the following lines to the correct values for your particular engine's brake system operating
pressure:

	EngineBrakesControllerMinSystemPressure( 0 )
	TrainBrakesControllerMinSystemPressure( 0 )
	EngineBrakesControllerMaxSystemPressure(90 )
	TrainBrakesControllerMaxSystemPressure( 90 )


-now scroll down to the Engine_controller/Engine_brake regions and copy and paste from the Pro Throttle percentages
folder the correct Engine_Controller and Brake_egnine regions for the locomotive you are upgrading.  Make sure you use
the correct brake pipe pressure file.

That's it your locomotive is now as realistic in RW base physics as I can provide.

Railcars:

-first upgrade your cars friction values using Joe Realmato's F2 Calc program.  I've included some Friction values for
various railcars to save you time computing the friction values.  These are approximate friction values so if you want
to be exact use the calculator to compute each cars loaded and unloaded friction values.

-next upgrade the coupler region using the tables in the Pro Coupler folder.  There's a detailed readme included in this
physics pack that explains which railcars use which coupler to implement in the Pro Coupler folder. You can use the
latest version of Route Riter to do this task quickly for you.  Just hi-light the cars that you want to install a
particular coulper on and then press the manual button and find the appropriate coupler in the Pro folder and RR will
install it in seconds.  Just work you way thru your railcar fleet hilightin and installing the appropriate coupler and
you set in this area.

-next upgrade your railcar's brake region using the appropriate table from either the Pro Composition brake shoe folder
or the Pro cast iron brake shoe folder depending on the car vintage.  Again you can use Route Riter to do this task
quickly and automatically.  You can upgrade hundreds of cars in seconds with the donationware version of Route Riter.

Until Mike Simpson releases a patch with the new brake files added for Route Riter, you can copy and past the Pro
version for the various composition brake files into the Route_Riters root folder labeled brake files.  To add the Pro
version of the Cast Iron brake files copy and past the Pro version into the Cast Iron shoe folder located in the
Brakefiles folder in R_R's root folder.


That's it your fleet is now ready to challenge your trainhandling skills just like the RW guys.

May all your signals be green and your rollby's black.

Bill Prieger
