Included in this package are the latest MSTSBin coupler files from Bill Prieger and his team.  I have also included my simpler coupler package which works with non-Bin MSTS as well as with MSTSBin.  All the packages contain a default.wag file that needs to be placed in the Default folder in the Trainset folder.  Rename your original default.wag before installing, please.  If you do not use the updated default.wag file, you will have problems with these coupler packs.

In the "standard" Bin coupler package, there are directions on which type of cars and locomitves use which type of coupler.  All that is required is that you copy and past these into your files.  Also included are changes to brake value settings for the different types of cars and locomotives.  Edit your files to use these values. 

The "Pro" package includes two additional coupler setting files which replace many, but not all, of the couplers in the "standard" package.  Naming is the same except for the "Pro" designation, so it is easy to tell which ones to replace if you want to use the "Pro" settings.

The "pre-Bin" package contains just a default.wag file and a simple .wag file from which you can copy and paste values into your couplers.  There is just one section for locomotives, one for most wagons, and one for passenger, articulated cars, etc. ( which have no slack action ).


These three coupler packages all work best when combined with Bob Boudoin's ( aka Plainsman ) physics, and J. Realmuto's FCalc 2.0 friction lines.

My preferred order of making the changes is to first ensure that Plainsman's physics are in place, then modify the friction line using FCalc 2.0 calculations.  Next edit to put in Jean-Louis's braking parameters.  Finally install the coupling sections.

Mike Simpson's great program, Route Riter, has the ability to install Bill's values for you.  Though these features require a donation to be activated, I highly recommend using RR to update your fleet.  Use these packages to do a few cars and locomotives that you can test, and if you are happy, then get Route Riter to do the big job of converting all your stock.  

SPECIAL NOTES:

There is nothing in these packs that is totally incompatible with pre-Bin MSTS.  In pre-Bin MSTS, my simple values will give you the best possible front coupler performance, but testing shows that Bill's locomotive values give reasonable front coupler use with pre-Bin MSTS.  And, all of these packages work excellently in MSTSBin.  It is up to you as the user to decide what you want.  I must recommend that you use one of the packages, as "standardization" makes MSTS more stable.  When you get your physics, friction, braking, and couplers updated to one of these standards, you will have a more enjoyable MSTS experience.  If you don't want to attempt to get "as real as possible", then just use the simple package.  The effort to install the correct physics, braking, and friction will be worth it, no matter which coupling "level" you choose.  

There has been some confusion with Bill's terminology.  He is a real world locomotive engineer, so his experience was used to refine the settings in these packages.  His "rigid" couplers are NOT slackless couplers.  His use of "rigid" relates to the level of ( lack of ) cushioning inherent in the coupler in the real world.  These should not be confused with MSTS "rigid" couplers ( where there is an active "rigid" line in all the coupler sections present in the file ).  MSTS "rigid" couplers are equivalent to the nearly slackless couplers used between passenger cars and other types of cars that exhibit little to no slack action.  Default MSTS used only one coupler section in wagons, and the "rigid" statement was apparently supposed to be used to make a "slackless" coupling, especially in BAR type couplers.  These coupler packs do not use the "rigid" parameter for it's original intended use.  We have taken advantage of how MSTS handles this parameter to "break up" the springs that MSTS uses for it's couplers.  The advantage of double sections in wagons is that one section can have a nearly "springless" setting by use of the "rigid" parameter.  This allows one to set the other section for proper behavior of that car's couplers.  Otherwise, the entire train's couplers mimic one large spring, which while giving slack action of sorts, is not correct slack action.




 
