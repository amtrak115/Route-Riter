
The following is the contents of the readme that is included with my standard coupler pack as 
released at Train-Sim.com and now included in our updated fleet.  Please read it as it contains 
valuable information on the new slack physics included in this coupler pack.

These coupler values are the combined work of Otto Wipfel, Joe Morris, and Bill Prieger along 
with the work of many MSTS simmers who have contributed in this area to this great sim.  These coupler values
are freeware.  They are designed to specifically work with the Bin patch but will work on non Bin upgraded MSTS 
installations.

These coupler values basically removed the extended portion of the coupler spring as modelled in MSTS
from the equation of how slack action is modelled.  The reason being that MSTS's spring is
too long and also models stretch as well as bunched spring resistances.  However on RW cushioned
drawbars, the drawbar spring only cushions buff forces.  The excessive stretch force control
is built into the failure value of the coupler knuckle itself.  RW engineers are trained extensively
in slack control especially control in how gently trains are stretched to minimize coupler knuckle
failure.

Some of the charecteristics these coupler regions exhibit are:  realistic coupling speeds are 
required (between .9mph and 4.0mph) for couplings to "take".  This mirrors how rw couplers function
when hooks are made between railcars.  In the RW couplers are not lubricated smooth working mechanisms.
They are rusty, dirty and in most cases very old mechanisms.  In almost all cases (close to 90% or more)
coupler pins will not fall locking the knuckle shut when couplings are made below 1mph.  This usually requires
stretching the train apart and recoupling at the slightly higher speed to shake the pins loose
allowing them to fall and lock the knuckle shut.  In the RW couplings above 4mph are considered
excessive and could cause load shifting and freight damage.  Also the pins tend to jamb up from the
cutlever mechanisms binding and preventing the pins from falling again requiring a stretch and recouple at
appropriate speeds.  Smooth coupling and uncoupling features without the dreaded recoupling issues
exhibited by pre-Bin front couplers and post Bin rear couplers.   

It should be noted that I did add a small extra amount of cushioning to the compression side of the slack action on 
both  the rigid and cushioned couplers.  This was done to relieve some of the harshness of truely modelled 
compressioned and uncushioned slack.  If you want to really challenge yourself in your trainhandling skills, install the 
"Pro Coupler Pack" version but be forewarned that the accelerometer will swing wildely in it's readings and some 
cars will slightly bounce for and aft on very sharp diverging switches.  Advanced trainhandling skills are required with 
the Pro Pack to prevent derailments and unwanted speed changes.  But that slack will really effect your train.  There are only
two new coupler templates in the Pro pack.  These are the two I tamed down in the novice/standard coupler pack.  Only use 
on railcars that would have received the standard versions of these two couplers only.  Do not use them on engines and long
railcars and passenger/roadrailer equipment.

A lot of effort and testing was done to make the pack workable with any MSTS railcar out there for North American 
railroading.  Unfortunetly it is impossible to make a specific coupler work perfectly with every car out there as every 
modeler both freeware and payware use different bounding box and other values for their specific car.  It may be 
necessary to reset the bounding box values on some cars to get the desired look and effect with the new coupler.  But 
the pack has been found to work on most cars out there and with various lengths as well except for the excessive 
length railcars which require their own coupler listed below.

It should also be noted that Otto Wipfel's  DEFAULT.wag file is needed with these
coupler and is included in this pack.  Below is a brief description on how the various types of couplers found on freight 
and passenger cars perform and also some railcar examples that each type of coupler can be found on.

Uncushioned Slack Type Couplers:

These coupler exhibit a slack length of about 6 to 8 inches per coupling.  Obviously slack action on Uncusioned slack type couplers is
uncushioned and can put quite a bit of slack force on the headend (locomotive/s) of your train.  These couplers
actually transmit a noticable reaction on your locomotives very closely mirroring RW charecteristics.  These couplers
can be found on all steam era freight equipment, all tankcars, modern hopper cars including grain and cement hopper cars, older flatcars, 
gondolas, coil cars, all 40 and some older 50 foot boxcars, MOW equipment such as cranes, snowplows,
and Jordan spreaders and Rotary snow plows.  It should be noted that I did add a very small amount of cushioning to the compression side of the slack action in this pack.  This was done to releave some of the harshness of uncushioned slack for novice simmers.  

Cushioned Drawbar couplers:

These couplers can exhibit slack up to close to four feet on some couplings.  They are designed to control and cushion buff forces
on a railcar.  These are generally used on cars where load shifting or freight damage can occure from harsh slack action.
These couplers will compress in relation to load weight that is forcing it's compression.  On short cuts of empty cushioned cars, you will notice
very little bunching of the drawbars, but add loads to the mix and the more you add the more the couplers will compress..  With a train with more then a few loads, the drawbars compress as their 
RW counterparts do.  Some cars that this type of coupler system can be found on are Bulkhead and Centerbeam flatcars, newer flatcars measuring over 60feet in length, older short TOFC flatcars
newer boxcars measuring 50feet or more,cabooses made after 1965 or there-a-bouts,and any special cars requiring load senstive coupler shock control.  Autoracks and boxcars in length exceeding 70 feet and TOFC long flatcars have their own specific coupler type listed next.  It should be added that most Boxcars do not have cushioned drawbars but in fact float on a spine that has springs mounted in it.  As a train is bunched or stretched the spine or frame, will move first with the train and the springs will then eat up the harshness and bring the railcar along.  Obviously something of this sort would be very hard to model as all cars are modeled in MSTS with the wheels being the only moving parts along with the trucks.  In this case the Cushion drawbar couplers is the best compromise I could come up with without having an unsightly spacing of the couplers within two cars of the same type.  This coupler also has some added cushioning to reduce the effects of bunched conditions on your train.  This was done to make the train playable for novices and those who run for fun.  If you want the closer and more realistic values to challange your train handling skills then grab the "Pro Coupler Pack" version.  Remember that advanced train handling skills are required to prevent unwanted derailments.

Installation instructions:

Copy and paste the coupler template from and including the comment line to the last parenthesis to your eng/wag/carriage 
file rplacing the entire coupler region between the in Inertiatensor line and the Adhiesion line.  Context is a great program
for doing this manually.  You can also use Mike Simpson's donation ware Route Riter program to manually upgrade
several wagons/engines and carriages to these new coupler templates.  RR's donation ware version will have a patch
that will include these latest final template releases.

Route Riter will automatically upgrade your Brake system templates as well.  If you want, you can use Context or any
unicode editor to edit your eng/wag/carriage files to the new brake system templates.  You can easily see which brake 
region gets replaced with these templates as well.  Make sure you include the comment line so you can see if a item has
been upgraded as you go thru your fleet.

May all your signals be green and you rollby's black.

Bill Prieger
priegerklan@yahoocom