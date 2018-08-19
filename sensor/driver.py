from threading import Thread
from time import sleep, time
from btserver import BTServer
from sensor import SensorServer

import datetime
import sqlite3
import asyncore
import json
##################### STING TO INT ###############################################

def str_to_int(ss):
	try:
		ii = int(ss[0:4] + ss[5:7] + ss[8:10] + ss[11:13] + ss[14:16] + ss[17:19] + ss[20:22])
	except Exception as e:
		print "[ERROR ]:App send wrong date type"
		return int(datetime.datetime.now().strftime("%Y%m%d%H%M%S"))
	return ii

#################### INT TO STRING ##############################################

def int_to_str(ii):
	ss = str(ii)[0:4] + "-" +  str(ii)[4:6] + "-" +  str(ii)[6:8] + " " +  str(ii)[8:10] + ":" +  str(ii)[10:12] + ":" +  str(ii)[12:14]
	return ss

#################### INPUT TYPE BEFORE SEND DATA ###############################

def input_type(hisData, typ):
	DICT = {'TYPE': '', 'DATA': []}
	DICT['TYPE'] = typ
	DICT['DATA'] = hisData
	DICT_ty = json.dumps(DICT)
	return DICT_ty

#################### START MAIN################################################

if __name__ == "__main__":

##################### I N I T ##################################################
	#######for server
	uuid = "fa87c0d0-afac-11de-8a39-0800200c9a66"
	service_name = "Air Pollution Sensor"
	bt_server = BTServer(uuid, service_name)
	######## db name
        db_name = "sensor.db"
	######## UDOO mac addr
	udoo_mac = '4e:71:9e:8c:3f:ee'
##################### BT SERVER THREAD ##########################################
	bt_server_thread = Thread(target=asyncore.loop, name="BT Server Thread")
	bt_server_thread.daemon = True
	bt_server_thread.start()
###################### SENSOR THREAD ############################################
	sensor_server = SensorServer(db_name)
	sensor_server.daemon = True
	sensor_server.start()
##################### ROAD THE DB ################################################

	try:
		db_conn = sqlite3.connect(db_name)
		db_cur = db_conn.cursor()
	except Exception as e:
		print "ERROR (sensor.py): {}".format(repr(e))
		#print out error using repr(e)
###################### INIT SOMTHING ############################################
	send_status = 0
	reg_s = {'MAC' : ''}
	reg_j = json.dumps(reg_s)
###################### ROOF START ###############################################
	while True:
###################### GET SENSOR DATA ##########################################
		sensor_output = sensor_server.get_sensor_output()
		date = sensor_output.get('date')
#################################################################################
#		temp = sensor_output.get('temp')				#
#		SN1_NO2 = sensor_output.get('NO2')				#
#		SN2_O3 = sensor_output.get('O3')				#
#		SN3_CO = sensor_output.get('CO')				#
#		SN4_SO2 = sensor_output.get('SO2')				#
#		SN5_PM25 = sensor_output.get('PM25')				#
####################### MAKE JSON OUTPUT ########################################
#		output = {"MAC" : udoo_mac, "time": date,			#
#			"TEMP": temp, "NO2": SN1_NO2, "O3": SN2_O3,		#
#			"CO": SN3_CO, "SO2": SN4_SO2, "PM25": SN5_PM25}		#
#		output_json = json.dumps(output)				#
################### LOOP!!!!!!!!!! ##############################################
		for client_handler in bt_server.get_active_client_handlers():
			udoo_mac = sensor_server.get_mac()
			################### SEND MAC TO REG
			app_id = client_handler.get_mac()
			################### UNPACK INPUT
			input_data = client_handler.get_data()
			if input_data.get('REQ') == "M":
				send_status = 1
				print "[{}]:Register board".format(app_id) 
			elif input_data.get('REQ') == "D":
				send_status = 2
				date_start_str = input_data.get("startTime")
				date_start_int = str_to_int(date_start_str) + 3
				print "[{}]:Send DB after {}".format(app_id, date_start_str)
			input_data = {}
################## SEND MAC TO REG #############################################
			if send_status == 1:
				client_handler.send(reg_j)
				print "[BOARD ]:Register done"
				send_status = 0
################## SEND HISTORIC ###############################################
			if send_status == 2:
				date_now_str = date
				date_now_int = str_to_int(date_now_str)
				db_cur.execute("SELECT * FROM history WHERE date BETWEEN ? AND ?", (date_start_int, date_now_int))
				rows = db_cur.fetchall()
				historic_array = []
				array_count = 0
				for row in rows:					
					historic_s = {
						"MAC" : udoo_mac,
						"TIME" : int_to_str(row[0]),
						"TEMP" : row[1],
						"NO2" : row[2],
						"O3" : row[3],
						"CO" : row[4],
						"SO2" : row[5],
						"PM25" : row[6]}
					historic_array.append(historic_s)
					array_count += 1
					if array_count == 9:
						DICT_j = input_type(historic_array, 'H')
						client_handler.send(DICT_j)
						array_acount = 0
						historic_array = []
				if historic_array != []:
					DICT_j = input_type(historic_array, 'H')
					client_handler.send(DICT_j)
				print "[BOARD ]:Send historic data between\n\r                 {} - {}".format(date_start_str, date_now_str)
				send_status += 1
##################### SEND REALTIME ###########################################
			if send_status == 3:
				while True:
					if sensor_server.ready_send() == 1:
						break
					sleep(0.1)
				sensor_output = sensor_server.get_sensor_output()
				date = sensor_output.get('date')
				temp = sensor_output.get('temp')
				SN1_NO2 = sensor_output.get('NO2')
				SN2_O3 = sensor_output.get('O3')
				SN3_CO = sensor_output.get('CO')
				SN4_SO2 = sensor_output.get('SO2')
				SN5_PM25 = sensor_output.get('PM25')
				real_s = {
						"MAC" : udoo_mac,
						"TIME" : date,
						"TEMP" : temp,
						"NO2" : SN1_NO2,
						"O3" : SN2_O3,
						"CO" : SN3_CO,
						"SO2" : SN4_SO2,
						"PM25" : SN5_PM25}
				real_j = input_type(real_s, 'R')
				try:
					client_handler.send(real_j)
					print "[BOARD ]:Send real data({})".format(date)
				except Exception as e:
					print "[BOARD ]:Connection lost.\r\n       stop transport"
					send_status = 0
				sleep(1.2)
#			print time()
#			print date_now_stamp + "  " + date_start_stamp

#################### TEST HISTORY #############################################



#			if client_handler.sending_status.get('history'):
#				print "history ye!!!!!!!!"
#			elif client_handler.sending_status.get('real'):
#				print "real ye!!!!!!!!!!!!!!!"
#		sleep(5)

#			sensor_string = "Date = {}, Temp = {}, NO2 = {}, O3 = {}, CO = {}, SO2= {}, PM = {}".format(date, temp, SN1_NO2, SN2_O3, SN3_CO, SN4_SO2, SN5_PM25)
#			client_handler.send(sensor_string + "\r\n")
#
#			sleep(5)
