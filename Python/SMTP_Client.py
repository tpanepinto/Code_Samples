__author__ = 'tim'

import socket
import time
print("Welcome to Tim's SMTP Client please fill out the information as it is presented\n")

servAddr = raw_input("Please enter the Address of the SMTP server you would like to use:")
sendAddr = raw_input("Please enter the To address in the format user@domain.com:")
fromAddr = raw_input("Please enter the From address in the format you@domain.com:")
subjectLine = raw_input("Please enter the subject of the email:")
data = raw_input("Please enter the body of your email:")

skt = socket.socket()
try:
    print "Server Addr:" + servAddr
    skt = socket.create_connection((servAddr, 25))

    connectAccept = skt.recv(1024)
    print connectAccept
    if "220" not in connectAccept:
        print "Server did not accept the connection please try again later, Bye!"
        quit()
    time.sleep(2)
    skt.send("HELO smtp.program\r\n")

    hello = skt.recv(1024)
    print hello
    if "250" not in hello:
        print "Server hello problem please try again later, Bye!"
        quit()
    skt.send("MAIL FROM:<" + sendAddr + ">\r\n")
    fromAccept = skt.recv(4096)
    print fromAccept
    if "250" not in fromAccept:
        print "Problem with from address please try again later, Bye!"
        quit()
    skt.send("RCPT TO:<" + sendAddr + ">\r\n")
    toAccept = skt.recv(4096)
    print toAccept
    if "250" not in toAccept:
        print "Problem with to address please try again later, Bye!"
        quit()
    skt.send("DATA\r\n")
    sendData = skt.recv(4096)
    print sendData
    if "354" not in sendData:
        print "Problem with to address please try again later, Bye!"
        quit()
    datStr = "From: <" + sendAddr + ">\r\n"
    datStr += "To: <" + fromAddr + ">\r\n"
    datStr += "Date: Wednesday, 11 March 2015 0:00 -0500" + "\r\n"
    datStr += "Subject: " + subjectLine + "\r\n"
    datStr += data + "\r\n"
    datStr += "." + "\r\n"

    skt.send(datStr)
    dataAccept = skt.recv(4096)
    print dataAccept
    if "250" not in dataAccept:
        print "Problem with message data please try again later, Bye!"
        quit()
    skt.send("QUIT")
    skt.close()
except socket.error:
    print "Server address incorrect please try again later, Bye!"
    quit()


