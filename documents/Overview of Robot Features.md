#Digital Fusion FRC Team 1317 Robot Fact Sheet

This document is meant to provide an overview of how our robot works and details of specific important features.
 


##Programming


There is a safety feature on the Gear Mechanism to prevent the Gear Pusher from extending when the door is closed and to prevent the door from closing when the gear pusher is extended. (There is, however, an override button in case this feature malfunctions). The program knows whether the door is open or closed based on limit switches and the current state of the solenoid. The program also knows whether the gear pusher is in or out based on the state of the solenoid.


In autonomous, the robot uses a camera and vision processing to line up with the peg to deliver the gear. The Raspberry Pi processes the images and sends data to the RoboRIO, which moves the robot accordingly.


##Chassis/Drivetrain


This year we decided to use mecanum wheels for extra maneuverability. Because this year's robot is smaller, we placed air pneumatic tanks on the underside of it to occupy as much empty space as possible. On our mounting plate, we kept each of our mechanical components separate, creating a modular robot, which makes it very easy to remove any components that are broken or need changing.


##Gear Mechanism

The gear mechanism receives gears from the front of the chute and drives to the peg on the airship. The gear is hooked onto the peg before the door is lifted and the gear is ejected from the chute. A switch senses when the door is open, to aid the safety feature in the programming.

There is a camera to aim and a plate in front of the chute to keep the gear from dropping down onto the chassis.


##Climber


The climber has a ratchet to prevent the robot from falling. We kept the climber in the center of the robot to decrease tipping. We used a gearbox and chain and sprockets to get a high gear ratio. We have a plate meant to depress the button at the top of the rope. Around our climber, we built a system to guide the rope. The climber is wrapped in Velcro to aid in grabbing the rope.


##Electronics


The electronics board is completely modular. We used Talon SRX's to control our motors for increased reliability. Our electronics system also includes 2 cameras and a Raspberry Pi.

##Design Process

We had a new design process we used this year to help us design a better robot.

We decided on our strategy for building the robot by calculating the points we would get in different scenarios. We figured out that delivering gears and climbing would get us the most points and, if we did that, we would not have time for much else.

Then we decided to think of different criteria we had for our robot and ranked them as demands, wishes, or preferences. We started brainstorming different designs and then prototyped them and compared the pros and cons. The climber took the longest time to decide on. Ultimately, we chose the simple but effective mechanism that we have on our robot now. Our completed robot meets most of the criteria we outlined. 