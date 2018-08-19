import asyncore
import sqlite3
import json
from bterror import BTError

class BTClientHandler(asyncore.dispatcher_with_send):
	"A Bluetooth handler for a client-sided socket"

	def __init__(self, socket, mac_addr, server):
		asyncore.dispatcher_with_send.__init__(self, socket)
		self.mac_addr = mac_addr
		self.server = server 
		self.data =""
		self.app_input = ""

	def handle_read(self):
		try:
			data = self.recv(1024)
			if not data: return
			self.data = data

		except Exception as e:
			BTError.print_error(
				handler = self,
				error = BTError.ERR_READ,
				message = repr(e)
				)
			self.data = ""
			self.handle_close()

	def get_mac(self):
		mac = self.mac_addr
		return mac

	def get_data(self):
		if self.data != '':
			data = self.data
			self.data = ""
			try:
				data_d = json.loads(data)
			except Exception as e:
				print "[ERROR]{} is not a json type".format(data)
				return {}
			return data_d
		else :
			return {}

	def handle_close(self):
		# flush the buffer
		while self.writable():
			self.handle_write()
		self.server.active_client_handlers.remove(self)
		self.close()
