""" Test the ability to identify and track an object.

  Our setup used 2 strips of 3M 8800? tape and a green led light ring.
  The calibration code below brings up the track bars which we adjusted until almost the
  only thing visible was the tape rectangles. Those settings were:
  H: 0:179, S: 10:255, V: 244:255
  The 50 frame logic for 2 largest rectangles were able to blip on the tape pieces. This
  was repeated at 1 ft and 35 feet; both worked w/o issue.
"""

import cv2
import numpy as np
import os
import sys
import socket

FRAME_WIDTH  = 640
FRAME_HEIGHT = 480
MAX_OBJS     = 10
MIN_OBJ_AREA = 20*20
MAX_OBJ_AREA = FRAME_WIDTH*FRAME_HEIGHT

# Trackbar stuff
TRACKBARWIN = 'HSV Track Bars'
H_MIN = 0
H_MAX = 179
S_MIN = 0
S_MAX = 255
V_MIN = 0
V_MAX = 255

# A dummy handling routine for the track bars changing
def nothing(x):
  pass

# Setup all the track bars
def createBars():
  cv2.namedWindow(TRACKBARWIN)
  cv2.createTrackbar('H_MIN', TRACKBARWIN, H_MIN, H_MAX, nothing)
  cv2.createTrackbar('H_MAX', TRACKBARWIN, H_MAX, H_MAX, nothing)
  cv2.createTrackbar('S_MIN', TRACKBARWIN, S_MIN, S_MAX, nothing)
  cv2.createTrackbar('S_MAX', TRACKBARWIN, S_MAX, S_MAX, nothing)
  cv2.createTrackbar('V_MIN', TRACKBARWIN, V_MIN, V_MAX, nothing)
  cv2.createTrackbar('V_MAX', TRACKBARWIN, V_MAX, V_MAX, nothing)

# If you want to play with hsv settings to determin the lower/upper limit
# for creating an in-range mask, call this function while you point your
# camera at the target subject. Read off the hsv values from the trackbars
# that make you happy.
def calibrate():
  createBars()
  cap = cv2.VideoCapture(1) # read from this camera
  loops = 0

  ''' Main loop
  
    With each frame, we convert it to hsv and then read the current track bar values.
    Those values are used as lower/upper limits on the in range filter we use on the
    image, trying to zoom in on the color we want to match on.
    
  '''
  while True:
    ret, img = cap.read()
    hsv = cv2.cvtColor(img, cv2.COLOR_BGR2HSV)

    hmin = cv2.getTrackbarPos('H_MIN',TRACKBARWIN)
    hmax = cv2.getTrackbarPos('H_MAX',TRACKBARWIN)
    smin = cv2.getTrackbarPos('S_MIN',TRACKBARWIN)
    smax = cv2.getTrackbarPos('S_MAX',TRACKBARWIN)
    vmin = cv2.getTrackbarPos('V_MIN',TRACKBARWIN)
    vmax = cv2.getTrackbarPos('V_MAX',TRACKBARWIN)
    lower = np.array([hmin, smin, vmin],np.uint8)
    upper = np.array([hmax, smax, vmax],np.uint8)
    mask = cv2.inRange(hsv, lower, upper)
    
    ''' Check contours
    
      We take the mask and apply it to the original image.
    '''
    res = cv2.bitwise_and(img, img, mask=mask)
    loops += 1
    if (50 < loops):
      loops = 1
      ret, thresh = cv2.threshold(mask, 127,255,0)
      _, contours, _ = cv2.findContours( thresh, cv2.RETR_TREE, cv2.CHAIN_APPROX_SIMPLE)
      # print len(contours)
      if (1 < len(contours)):
        areas = [cv2.contourArea(c) for c in contours]
        maxIdxs = np.argsort(areas)[-2:][::-1]
        x,y,w,h = cv2.boundingRect(contours[maxIdxs[0]])
        cv2.rectangle(img, (x,y), (x+w,y+h), (0,255,0), 2)
        x,y,w,h = cv2.boundingRect(contours[maxIdxs[1]])
        cv2.rectangle(img, (x,y), (x+w,y+h), (0,255,0), 2)

    cv2.imshow('img', img)
    cv2.imshow('mask', mask)
    cv2.imshow('res', res)

    # Wait for an <esc> key from user to shut down
    k = cv2.waitKey(30) & 0xff
    if 27 == k:
      break
  cap.release()
  cv2.destroyAllWindows()

if __name__ == '__main__' :
  calibrate()

