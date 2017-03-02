import cv2
import numpy as np
import os
import sys
import base64
import time
import urllib2

# Our camera is at IP 10.13.17.69

class IpCam(object):

    def __init__(self, url, user=None, password=None):
        self.url = url
        auth_encoded = base64.encodestring('%s:%s' % (user, password))[:-1]

        self.req = urllib2.Request(self.url)
        # self.req.add_header('Authorization', 'Basic %s' % auth_encoded)

    def get_frame(self):
        response = urllib2.urlopen(self.req)
        img_array = np.asarray(bytearray(response.read()), dtype=np.uint8)
        frame = cv2.imdecode(img_array, 1)
        return frame
    
 
if __name__ == '__main__' :
 
    # Set up tracker.
    # Instead of MIL, you can also use
    # BOOSTING, KCF, TLD, MEDIANFLOW or GOTURN
     
    tracker = cv2.Tracker_create("MIL")
    # ipcam = IpCam('http://10.13.17.69/view/viewer_index.shtml')
    # ipcam = IpCam('http://10.13.17.69/mjpg/video.mjpg')
    #ipcam = IpCam('http://10.13.17.69/axis-cgi/mjpeg/video.cgi?camera=1&resolution=640x480')
    # Read video
    # video = cv2.VideoCapture("videos/chaplin.mp4")
    # video = cv2.VideoCapture(0)
    # video = cv2.VideoCapture('http://10.13.17.69/axis-cgi/mjpeg/video.cgi?camera=1&resolution=640x480')
    video = cv2.VideoCapture('http://10.13.17.69/mjpg/video.mjpg')
    
    # Exit if video not opened.
    if not video.isOpened():
        print "Could not open video"
        sys.exit()
 
    # Read first frame.
    # frame = ipcam.get_frame()
    ok, frame = video.read()
    if not ok:
        print 'Cannot read video file'
        sys.exit()
     
    # Define an initial bounding box
    bbox = (287, 23, 86, 320)
 
    # Uncomment the line below to select a different bounding box
    # bbox = cv2.selectROI(frame, False)
 
    # Initialize tracker with first frame and bounding box
    #ok = tracker.init(frame, bbox)
 
    while True:
        # Read a new frame
        ok, frame = video.read()
        # frame = ipcam.get_frame()
        if not ok:
            break
         
        # Update tracker
        #ok, bbox = tracker.update(frame)
 
        # Draw bounding box
        if ok:
            p1 = (int(bbox[0]), int(bbox[1]))
            p2 = (int(bbox[0] + bbox[2]), int(bbox[1] + bbox[3]))
            cv2.rectangle(frame, p1, p2, (0,0,255))
 
        # Display result
        cv2.imshow("Tracking", frame)
 
        # Exit if ESC pressed
        k = cv2.waitKey(1) & 0xff
        if k == 27 : break
