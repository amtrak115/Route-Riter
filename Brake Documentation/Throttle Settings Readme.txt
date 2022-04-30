Make sure you enable "Wordwrap" in the "Format" dropdown menu in your Notepad or
Wordpad applications so you don't have to scroll left and right to read this
document.

This folder contains Throttle percentages for various types of locomotives
mirroring their RW counterparts in percentages of power applied to the wheels with
each throttle position.  Also included are new max Engine Brake percentage values
for both Freight and Passenger locomotives and Composition and Cast Iron Brake
shoe equipped Freight and Passenger locomotives as well.  The max engine brake
cylinder  pressure for composition brake shoe equipped units is 73psi.  The max
engine brake cylinder  pressure for cast iron shoe equipped locomotives is 52psi. 
These parameters are most effective in giving RW performance when used in
conjunction with Bob Boudoion's Engine Physics and my V3.0 Pro and Novice coupler
Packs and my V2.0 Brake packs for both Composition and Cast Iron Brake shoe
equipped locomotives and rollingstock. 

Included you will find throttle and engine brake calibrations for most diesel
locomotive types that can be found for MSTS.  There are both Composition Brake
Shoe Equipped calibration files and Cast Iron Brake Shoe Equipped calibration
files.  Only locomotive types that came originally equipped with Cast Iron Shoes
are listed in the Cast Iron Shoe Equipped sections.  Raildriver owners will have
to watch the percentage figures in the Throttle region of the F-5 display as the
Throttle Notches in the RD throttle may not correspond with the percentage values. 
Keyboard only end users will only have to keep track of actual throttle positions
as each throttle up and down click of the keyboard will automatically use the new
percentages.

MSTS has some quirks (now there's a big surprise) and it has been found that using
different EngineBrakeControllerMaxSystemPressure and
TrainBrakesControllerMaxSystemPressure values causes the Air Compressor to not
disengage when the max main tank pressure is attained in some locomotives.  Using
the correct max systems psi pressures for the engine brakes is one way to set your
engines max brake cylinder pressure but you have the aforementioned bug.  So in
order to get the correct Independant brake (Engine Brake) cylinder max pressure
parameter for all the locomotives listed below it was necessary to change the
value in the second line of the Brake_Engine controller line parameter.  In the
sim your F-5 GUI Loco Brake percentage line values will top out at a less then the
100% value you are presently accustomed to.  When running in the incab view watch
your Independant Brake Guage needle when using the engine brakes as it will now
top out at the correct brake cylinder psi value.  In outside views watch the F-5
values as before, you will become accustomed to the new Loco brake  percentages
with time.  Also the Engine brake white box that appears at the bottom of the
screen will show the correct engine brake percentages to 100%.

Thanks to Bob Boudoin and J-L Chauvin and Joe Morris for allowing me to include
some of their parameters in my work contained in this zip file.  Mike Simpson's
payware version of Route Riter's eng/wag file editor can be used to make wholesale
parameter changes to your EngineBrakesControllerMaxSystemPressure and
TrainBrakeControllerMaxSystemPressure line values.  Just load and hilight the
appropriate eng files you want to modify and use the editor to the left of the
list to find the corresponding parameter lines you want to change.

Instructions:

Make sure your locomotive has the correct EngineBrakesControllerMaxSystemPressure
and TrainBrakesControllerMaxSystemPressure values as stated in each of the
locomotive's Calibration sections.  Copy and paste over the original
EngineController and Brake_Engine regions with the new corrected ones for your
particular locomotive model.  Include the "Comment" line so you can keep track of
locomotives that you have modified.  

FIRST RENAME OR  MAKE BACKUPS OF ALL ORIGINAL ENG FILES YOU MODIFY!!!!! 

These files are most effective when used with the Bin Patch but will also work
well on non Bin Patch equipped MSTS installations.  These parameters have been
tested on my MSTS installation with the latest working release of the Bin Patch
and found to work very well in mimicing these locomotives RW counterparts based on
actual RW experiance running the various types included in this document.  I have
run all the locomotive types listed below with the Baldwin's being the only
exception.  My research shows that the Baldwin's were very similar to the ALCO
locomotives in these areas.  I've run both the ALCO locomotive types listed below
with cast iron brake shoes installed on the RW units.  I've run both the cast iron
and composition brake shoe equipped versions of the EMD locomotives and the
Composition brake shoe equipped GE units except for the GE switchers.  Since both
builders used the same cast iron  brake shoes and max locomotive brake cylinder
pressures, their engine braking performance is virtually the same with actual
locomotive tonnages making the difference between the two builders  actual
stopping distances.

These values are freeware.  If you do use these values in future locomotive or
rollingstock releases please include this readme in your zip file to make sure all
proper persons are given credit for their contributions.  As stated above these
values worked as advertised on my MSTS installation using both the keyboard and
the Raildriver to control your train.  Use at your own risk by perhaps doing one
set of engines and raicars and seeing how they perform in your MSTS installation. 
My computer did not blow up or melt down from using these parameters, I'm not
responsible if your computer does.  I do hope you enjoy the new level of realism
your rolllingstock will exhibit with these new values.

May all your signals be green and your Roll-by's black.

Turbo Bill
aka Bill Prieger


