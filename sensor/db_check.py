from time import sleep, time

import sqlite3
import asyncore
import json
import datetime

def str_to_int(ss):
	ii = int(ss[0:4] + ss[5:7] + ss[8:10] + ss[11:13] + ss[14:16] + ss[17:19] + ss[20:22])
	return ii

def int_to_str(ii):
	ss = str(ii)[0:4] + "-" +  str(ii)[4:6] + "-" +  str(ii)[6:8] + " " +  str(ii)[8:10] + ":" +  str(ii)[10:12] + ":" +  str(ii)[12:14]
	return ss

if __name__ == "__main__":
		try:
			db_conn = sqlite3.connect(sensor.db)
			db_cur = db_conn.cursor()
		except Exception as e:
			print "ERROR (sensor.py): {}".format(repr(e))
		while True:
			date_now_str = datetime.datetime.now().strftime("%Y-%m-%d %H:%M:%S")
			date_now_int = str_to_int(date_now_str)
			db_cur.execute("SELECT * FROM history WHERE date ?", (date_now_int))
			rows = db_cur.fetchall()
			print "   TIME     TEMP      NO2     O3     CO     SO2     PM25"
			for row in rows:
				print "[{}] : {} | {} | {} | {} | {} | {} |".format(row[0], row[1], row[2], row[3], row[4], row[5], row[6])
			sleep(5.5)
