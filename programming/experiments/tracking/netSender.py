""" Test sending a stream of encoded double bytes via udp

"""
import socket
import time
import json

IPADDR  = 'localhost'
PORTNUM = 5000

netSock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM, 0)
# netSock.connect((IPADDR,PORTNUM))
dArray = {'x': 52.0, 'y': 123.11 }

while(1):
  msg = json.dumps(dArray)
  netSock.sendto(msg,(IPADDR,PORTNUM))
  time.sleep(3)
