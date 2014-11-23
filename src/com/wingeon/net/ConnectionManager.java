package com.wingeon.net;

import java.util.HashMap;
import java.util.Map;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;

public class ConnectionManager {
	
	public static final int MESSAGE_READ = 0;
	public static final int MESSAGE_WRITE = 1;
	public static final int MESSAGE_CONNECTED = 2;
	
	private ConnectionManager() {}
	
	private Handler m_handler;
	private Map<Integer, BluetoothConnection> m_connections = new HashMap<Integer, BluetoothConnection>();
	private static ConnectionManager m_instance = null;
	
	public static ConnectionManager getInstance() {
		if(m_instance == null)
			m_instance = new ConnectionManager();
		return m_instance;
	}
	
	public void broadcast(byte[] buffer) {
		for(Map.Entry<Integer, BluetoothConnection> entry : m_connections.entrySet()) {
			BluetoothConnection connection = entry.getValue();
			connection.write(buffer);
		}
	}
	
	public synchronized void setHandler(Handler handler) {
		m_handler = handler;
	}
	
	public synchronized BluetoothConnection getConnection(int id) {
		return m_connections.get(id);
	}
	
	public synchronized void connectedBluetooth(BluetoothSocket socket, BluetoothDevice device) {
		BluetoothConnection connection = new BluetoothConnection(socket);
		connection.start();
		m_connections.put(connection.getConnectionId(), connection);
		
		if(m_handler != null)
			m_handler.obtainMessage(MESSAGE_CONNECTED, connection.getConnectionId(), -1, connection).sendToTarget();
	}
	
	public synchronized void receivedBytes(int id, int bytes, byte[] buffer) {
		if(m_handler != null)
			m_handler.obtainMessage(MESSAGE_READ, id, bytes, buffer).sendToTarget();
	}
	
	public synchronized void sentBytes(int id, byte[] buffer) {
		if(m_handler != null)
			m_handler.obtainMessage(MESSAGE_WRITE, id, -1, buffer).sendToTarget();
	}
}
