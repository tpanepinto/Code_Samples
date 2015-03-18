import socket
from time import time

hostName = input("Please Enter Hostname to be Resolved: ")
try:
    start = time()
    ipAddr = socket.gethostbyname(hostName)
    stop = time()
    print(ipAddr + " resolved in " + str((stop-start)) + " seconds")
except socket.error:
    print("Hostname cannot be resolved")

