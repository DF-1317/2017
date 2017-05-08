#Vision Tracking Calibration Procedure

1. Uncomment line 789 of robot.java (packetReader.getPacket() which is in the disabledPeriodic() method)
2. Upload latest version of robot code
3. Make sure Raspberry Pi is plugged in and its code is runnig.
4. Make sure the laptop is plugged in
5. Open the Riolog in Eclipse
6. Move the robot to the desired final position.
7. While the robot is disable, click "Pause Display".
8. Look at the "w" values. Set the WidthAtTarget constant in Targeting.Java to that number.
9. Look at the "x" values. Set the TargetX constant in Targeting.Java to that number.
10. Click "Show Packets" in the RioLog.
11. Move the Robot to the farthest point from the object
12. Click "Pause Display" in the RioLog.
13. Look at the "w" values. Set the constant WidthAtFarthestPoint in Targeting.Java to that number
14. Use a measuring tape to measure the distance from the farthest point to the target point in inches. Set the constant FarthestPointInches to that number.