import sys
f = open('Profiles','w')
try:
	while True:
		str = "\nDim "


		service = input("Service_Profile: ")
		spectrum = input("Spectrum_Profile: ")
        
		str = str + service + "_" + spectrum + " As New Prof \n"
		str = str + service + "_" + spectrum + ".Script = (" + service + " + " + spectrum + " + Apply_Script) \n"
		str = str + "Profiles.Add(\"" + service + " + " + spectrum +"\", " + service + "_" + spectrum + ")\n\n"
		f.write(str);
except:
	f.close()
	

