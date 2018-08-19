import sqlite3
import datetime
from neo import easyGpio
from neo_adc import ADC
from threading import Thread
from threading import Lock
from time import sleep, time

class SensorServer(Thread):
###########################  i  n  i  t  ###############################################
	def __init__(self, db_file):
		Thread.__init__(self)
		#############  A  D  C
		self.A0 = ADC(0)
		self.A1 = ADC(1)
		############# M  U  X
		self.mux = [easyGpio(24), easyGpio(25), easyGpio(26), easyGpio(27)]
		############# S  Q  L  i  t  e
		self.sensor_output = {"temp": 0.,"NO2": 0.,
			"O3": 0.,"CO": 0.,"SO2": 0., "date": 0, "PM25": 0.}
		############# SENSOR CALI TABLE INIT
		self.WE_NO2 = 0
		self.WE_O3 = 0
		self.WE_CO = 0
		self.WE_SO2 = 0
		self.AE_NO2 = 0
		self.AE_O3 = 0
		self.AE_CO = 0
		self.AE_SO2 = 0
		self.SE_NO2 = 0.
		self.SE_O3 = 0.
		self.SE_CO = 0.
		self.SE_SO2 = 0.

		############# RESET SENSOR_NUM
		self.sensor_num = 0
		self.mac = ""

		self.sensor_output_lock = Lock()
		self.db_file = db_file

		self.send_status = 1

		try:
			self.db_conn = sqlite3.connect(self.db_file)
			self.db_cur = self.db_conn.cursor()

		except Exception as e:
			print "ERROR (sensor.py): {}".format(repr(e))
			self.__del__()

		# execute query to create table using IF NOT EXISTS keywords
		self.db_cur.execute("CREATE TABLE IF NOT EXISTS history (date INT PRIMARY KEY NOT NULL, TEMP REAL, NO2 REAL, O3 REAL, CO REAL, SO2 REAL, PM25 REAL)")
		self.db_conn.commit()
############################### GET NUM

	def get_mac(self):
		return self.mac

################################ READY TO SEND?

	def ready_send(self):
		return self.send_status

###############################  CLOSE THE DB  #######################################

	def __del__(self):
		self.db_conn.close()
		# if you're using a mux, reset all selector pins to LOW

#############################  DB DIC OUTPUT  ##########################################

	def get_sensor_output(self):
		return self.sensor_output.copy()

###############################  M  U  X  ##############################################

	def set_mux_channel(self, ch):
		bits = "{0:04b}".format(ch)
		for i in range(0, 4):
			bit = int(bits[3-i])
			sel = self.mux[3-i]
			sel.on() if bit else sel.off()

###############################  SENSOR NUM ########################################

	def cali_num(self, num):
		if num == 1:
			self.WE_NO2 = 220
			self.WE_O3 = 414
			self.WE_CO = 346
			self.WE_SO2 = 300
			self.AE_NO2 = 260
			self.AE_O3 = 400
			self.AE_CO = 274
			self.AE_SO2 = 294
			self.SE_NO2 = 0.207
			self.SE_O3 = 0.256
			self.SE_CO = 0.276
			self.SE_SO2 = 0.3

		elif num == 2:
			self.WE_NO2 = 215
			self.WE_O3 = 390
			self.WE_CO = 326
			self.WE_SO2 = 280
			self.AE_NO2 = 246
			self.AE_O3 = 393
			self.AE_CO = 280
			self.AE_SO2 = 306
			self.SE_NO2 = 0.212
			self.SE_O3 = 0.276
			self.SE_CO = 0.266
			self.SE_SO2 = 0.296

		elif num == 3:
			self.WE_NO2 = 295
			self.WE_O3 = 391
			self.WE_CO = 347
			self.WE_SO2 = 345
			self.AE_NO2 = 282
			self.AE_O3 = 390
			self.AE_CO = 296
			self.AE_SO2 = 255
			self.SE_NO2 = 0.228
			self.SE_O3 = 0.399
			self.SE_CO = 0.267
			self.SE_SO2 = 0.318

		elif num == 4:
			self.WE_NO2 = 287
			self.WE_O3 = 418
			self.WE_CO = 345
			self.WE_SO2 = 333
			self.AE_NO2 = 292
			self.AE_O3 = 404
			self.AE_CO = 315
			self.AE_SO2 = 274
			self.SE_NO2 = 0.258
			self.SE_O3 = 0.393
			self.SE_CO = 0.292
			self.SE_SO2 = 0.288

########################### M A I N ########################################

	def run(self):
		print "|       Sensor number list       |"
		print "|NUM    |   1  |  2  |  3  |  4  |"
		print "|Sensor |  14  | 15  | 160 | 161 |"
		while True:
			self.sensor_num = input("Enter your sensor NUM : ")
			if self.sensor_num == 1:
				#TEAM C
				self.cali_num(self.sensor_num)
				self.mac = "E0:E5:CF:01:35:78"
				break
			elif self.sensor_num == 2:
				#TEAM G
				self.cali_num(self.sensor_num)
				self.mac = "5C:31:3E:27:63"
				break
			elif self.sensor_num == 3:
				#TEAM A
				self.cali_num(self.sensor_num)
				self.mac = "5C:31:3E:27:CF:69"
				break
			elif self.sensor_num == 4:
				#TEAM D
				self.cali_num(self.sensor_num)
				self.mac = "E0:E5:CD:01:33:B0"
				break
			print "Pliz enter the number in the list(1, 2, 3, 4)"

		while True:
			####################### START THREAD
			self.sensor_output_lock.acquire()
			self.db_conn = sqlite3.connect(self.db_file)
			self.db_cur = self.db_conn.cursor()
			######################## READ SENSOR
			self.set_mux_channel(0)
			sleep(0.5)
			NO2_WE = self.A0.get_mvolts()

			self.set_mux_channel(1)
			sleep(0.5)
			NO2_AE = self.A0.get_mvolts()

			self.set_mux_channel(2)
			sleep(0.5)
			O3_WE = self.A0.get_mvolts()

			self.set_mux_channel(3)
			sleep(0.5)
			O3_AE = self.A0.get_mvolts()

			self.set_mux_channel(4)
			sleep(0.5)
			CO_WE = self.A0.get_mvolts()

			self.set_mux_channel(5)
			sleep(0.5)
			CO_AE = self.A0.get_mvolts()

			self.set_mux_channel(6)
			sleep(0.5)
			SO2_WE = self.A0.get_mvolts()

			self.set_mux_channel(7)
			sleep(0.5)
			SO2_AE = self.A0.get_mvolts()

			self.set_mux_channel(8)
			sleep(0.5)
			PM = self.A0.get_mvolts()

			###################### set temp
			temp_v = self.A1.get_mvolts()
			temp = round(((((temp_v * 3.3) / 1024) - 0.5) * 10), 2)
			###################### set date
			date_str = datetime.datetime.now().strftime("%Y-%m-%d %H:%M:%S")
			date_int = int(datetime.datetime.now().strftime("%Y%m%d%H%M%S"))
			###################### calculate ppb or ug/m^3
			NO2 = round(((NO2_WE - self.WE_NO2) - 0.70 * (NO2_AE - self.AE_NO2)) / self.SE_NO2, 2)
			O3 = round(((O3_WE - self.WE_O3) + 0.35 * (O3_AE - self.AE_O3)) / self.SE_O3, 2)
			CO = round(((CO_WE - self.WE_CO) + 0.32 * (CO_AE - self.AE_CO)) / self.SE_CO, 2)
			SO2 = round(((SO2_WE - self.WE_SO2) - 1.00 * (SO2_AE - self.AE_SO2)) / self.SE_SO2, 2)
			mV = PM/1000
			hppcf = (240. * (mV ** 6)) - (2491.3 * (mV ** 5)) + (944.87 * (mV ** 4)) - (14840 * (mV ** 3)) + (10684 * (mV ** 2)) + (2211.8 * mV) + 7.9623
			PM25 = round(0.518 + 0.00274 * hppcf, 2)

			sensor_error = ""
			if NO2 < 0:
				sensor_error += "[NO2]"
				NO2 = 0.
			if O3  < 0:
				sensor_error += "[O3]"
				O3 = 0.
			if CO < 0:
				sensor_error += "[CO]"
				CO = 0.
			if SO2 < 0:
				sensor_error += "[SO2]"
				SO2 = 0.
			if PM25 < 0:
				sensor_error += "[PM]"
				PM25 = round(abs(PM25) % 4, 2)
				#PM25 = 0.
			if len(sensor_error) > 1:
				print sensor_error + " : BAD DATA"
			###################### update the dictionary
			self.sensor_output["date"] = date_str
			self.sensor_output["temp"] = temp
			self.sensor_output["NO2"] = NO2
			self.sensor_output["O3"] = O3
			self.sensor_output["CO"] = CO
			self.sensor_output["SO2"] = SO2
			self.sensor_output["PM25"] = PM25
			# PRINT WHAT WE MESURED
			print "[SENSOR]:({})Temp:{} NO2:{}\r\n         O3:{} CO:{} SO2:{} PM:{}".format(date_str, temp, NO2, O3, CO, SO2, PM25)
			#######################insert database
			self.db_cur.execute("INSERT INTO history VALUES (?, ?, ?, ?, ?, ?, ?)", (date_int, temp, NO2, O3, CO, SO2, PM25))
			self.db_conn.commit()
			######################## END THREAD
			self.sensor_output_lock.release()
			print "[BOARD ]:Store data in DB"
			######################## REAL TIME FLAG
			self.send_status = 1
			sleep(1)
			self.send_status = 0

############################  T  E  S  T  ############################################
#			print get_mac()
#			print int(date_str[0:4] + date_str[5:7] + date_str[8:10] + date_str[11:13] + date_str[14:16] + date_str[17:19] + date_str[20:22])
#			print date_str

#			print str(date_int)[0:4] + "-" +  str(date_int)[4:6] + "-" +  str(date_int)[6:8] + " " +  str(date_int)[8:10] + ":" +  str(date_int)[10:12] + ":" +  str(date_int)[12:14]

#			print int(time.mktime(datetime.utcnow().timetuple()))
#			print date_t
#			print current_time
#			print "[Sensor]:\r\nDate = {}, Temp = {}, NO2 = {}, O3 = {}, CO = {}, SO2= {}, PM = {}".format(date, temp, NO2, O3, CO, SO2, PM25)
