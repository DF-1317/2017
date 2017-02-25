""" Test the ability to identify and track an object.

  Our setup used 2 strips of 3M 8800? tape and a green led light ring.
  We ran a calibration program to see what the min/max HSV values to use for our range filter.
  This code will do a detection for the largest regions that remain from that filter, define
  a larger bounding box to contain those regions, and then track the video based on that bbox.
  A detection is repeated every X [100] number of frames to help out the tracking software stay
  on point.
  We use detection & tracking because tracking takes up so much less processing time/resources.
"""

import cv2
import numpy as np
import os
import sys
import json
import imutils
import socket

DetectFrameRate       = 100  # How often to re-run the detection
SingleObjectThreshold = 0.25 # The size different whereby we think we only see one of our regions
ShowBbox              = True # Should we show the bbox image to the user?
# Network Stuff
IPADDR  = 'localhost'
PORTNUM = 5000
netSock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM, 0)

''' Detect our target

  We are given a mask that has already been filtered for the color range our
  target image falls into. Since we are using green lights, the range check is
  based on green.
  We do additional threshold checks and then find the contours.
  We need to see a few things [2] for our logic to work. If not, we return None as a bounding box.
'''
def detect(mask):
  ret, thresh = cv2.threshold(mask, 127,255,0)
  _, contours, _ = cv2.findContours( thresh, cv2.RETR_TREE, cv2.CHAIN_APPROX_SIMPLE)
  # print len(contours)
  if (2 > len(contours)) return None
  
  areas = [cv2.contourArea(c) for c in contours]
  maxIdxs = np.argsort(areas)[-2:][::-1]
  x,y,w,h = cv2.boundingRect(contours[maxIdxs[0]])
  x2,y2,w2,h2 = cv2.boundingRect(contours[maxIdxs[1]])
  area1 = w*h
  area2 = w2*h2
  print area1, area2
  pct = 0
  if (area1>area2): pct = (area1-area2)/area1
  else: pct = (area2-area1)/area2
  # If close in size, adjust x,y,...; if large difference, use the coords of largest
  if ( SingleObjectThreshold > pct ):
      if (x < x2):
          w = x2+w2-x
      else:
          w = x+w-x2
          x = x2
      if (y < y2):
          h = y2+h2-y
      else:
          h = y+h-y2
          y = y2
          
  # The bounding box for our targets
  return (x,y,w,h)
    
''' Track our target

   We initialize by grabbing a camera, and setting up our loop variable to trigger a
   detection on the very first time through the while(). We also setup our lower/upper
   filter boundaries used to filter the color range we want; the values from from playing
   with the calibration program.
'''
def track():
  cap = cv2.VideoCapture(1)
  loops = DetectFrameRate
  lower = np.array([0,10,230],np.uint8)
  upper = np.array([179,255,255],np.uint8)
  tracker = None
  bbox = None
  
  ''' Main tracking loop
  
    We grab a frame and change it to HSV to work with it in openCV.
    We check our loop to see if it is time to detect our target again; we are initialized
    to do that on the very first time through this code.
  '''
  while True:
    ok, img = cap.read()
    hsv = cv2.cvtColor(img, cv2.COLOR_BGR2HSV)
    loops += 1
    
    ''' Time to detect again
    
      We reset our loop and then create a mask for the colors of our target. The erosion and
      dilation help in erasing some of the noisy parts of the image which will help speed up
      the rest of the processing.
      We finish with creating a new tracker and feeding it the image and the new bounding box
      given us by detect().
    '''
    if (DetectFrameRate < loops):
      loops = 0
      mask = cv2.inRange(hsv, lower, upper)
      mask = cv2.erode(mask,None,iterations=2)
      mask = cv2.dilate(mask,None,iterations=2)
      
      # MIL, BOOSTING, KCF, TLD, MEDIANFLOW or GOTURN
      # MEDIAFLOW seems to work best for our job; all the others
      # had trouble keeping up, or just got lost
      tracker = cv2.Tracker_create("MEDIANFLOW")
      bbox = detect(mask)
      if None == bbox:
          bbox = (50,50,50,50)
      ok = tracker.init(img, bbox)
    
    ''' React to tracking
    
      We send the parameters of the bounding box out onto the network in JSON format.
      We then check to see if we should superimpose a rectangle on the image..
    '''
    ok, bbox = tracker.update(img)
    x,y,w,h = bbox
    pdata = {'x': x, 'y': y, 'w': w, 'h': h}
    netSock.sendto(json.dumps(pdata),(IPADDR,PORTNUM))
    
    if (ShowBbox):
      p1 = (x,y)
      p2 = ((x + w), (y + h))
      cv2.rectangle(img, p1, p2, (0,255,0), 3)
      cv2.imshow('img',img)
  
    ''' Wait forever
    
      We break out of tracking when the <esc> key is hit by the user, cleaning up as
      we leave.
    '''
    k = cv2.waitKey(30) & 0xff
    if 27 == k:
      break
  cap.release()
  cv2.destroyAllWindows()
  #msg = json.dumps(pdata)
  #netSock.sendto(msg,(IPADDR,PORTNUM))

if __name__ == '__main__' :
  track()

""" TODO: things to get tracking

  Like the calibrate, we want to capture each frame, periodically to a 'detect'/'find'
    operation and then track on that rectangle until the next detect time comes along.
    The calibrate code did the detect work every 50 frames; we can vary this, but for
    now that sounds good.
  * setup the RIO software to listen for those packets and then react to coordinates
  * the coordinates reported will be for the center of the bounding rectangle
  * we need to match the coordinate system of the image and use it.
  
  + Needed to: sudo apt-get install python-pip
  + pip install imutils
  
  * Need to read from a network camera, not a USB camera
"""
