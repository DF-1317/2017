#Overview of Robot Features

This document is meant to provide an overview of how our robot works and provide specific features that we want to tell the judges about.
 


##Programming


There is a safety feature on the Gear Mechanism to prevent the Gear Pusher from extending when the door is closed and to prevent the door from closing when the gear pusher is extended (There is, however, an override button in case this feature malfunctions). The program knows whether the door is open or closed based on limit switches and the current state of the solenoid. The program knows whether the gear pusher is in or out based on the state of the solenoid.


In autonomous, the robot uses a camera and vision processing to line up with the peg to deliver the gear. The Raspberry Pi processes the images and sends data to the RoboRIO, which reacts accordingly.


##Chassis/Drivetrain


This year we decided to use mechanum wheels for extra maneuverability. 

Because this year's robot is smaller we placed air pneumatic tanks on the underside of the robot to ocupy as much empty space as possible. On our mounting plate we kept each of our mechanical components separate, creating a modular robot, which made it vary easy to remove any components that broke or needed changing.


##Gear Mechanism

The gear mechanism receives gears from the front of the chute and drives to the peg on the airship. The gear is hooked onto the peg before the door is lifted and the gear is ejected from the chute.

There is a camera to aim and a plate in front of the chute to keep the gear from dropping down onto the chassis.

A switch senses when the door is open, to aid in the safety feature in the programming.


##Climber


The climber has a ratchet to prevent the robot from falling. We kept the climber in the center of the robot to decrease tipping. We used a gearbox and chain and sprockets to get a high gear ratio. We have a plate meant to depress the button at the top of the rope. Around our climber, we have built a system to guide the rope.


##Electronics


Electronics board is completely modular. We used Talon SRX's to control our motoros for increased reliablility. Our electronics system also includes 2 cameras and a Raspberry Pi.