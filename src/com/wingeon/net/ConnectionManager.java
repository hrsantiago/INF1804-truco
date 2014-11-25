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
	public static final int MESSAGE_CONNECT_FAILED = 3;
	public static final int MESSAGE_CONNECTION_LOST = 4;
	
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
	
	public void broadcastExcept(int id, byte[] buffer) {
		for(Map.Entry<Integer, BluetoothConnection> entry : m_connections.entrySet()) {
			BluetoothConnection connection = entry.getValue();
			if(connection.getConnectionId() != id)
				connection.write(buffer);
		}
	}
	
	public void closeAll() {
		for(Map.Entry<Integer, BluetoothConnection> entry : m_connections.entrySet()) {
			BluetoothConnection connection = entry.getValue();
			connection.cancel();
		}
		m_connections.clear();
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
	
	public synchronized void connectFailed() {
		if(m_handler != null)
			m_handler.obtainMessage(MESSAGE_CONNECT_FAILED, -1, -1, null).sendToTarget();
	}
	
	public synchronized void connectionLost(int id) {
		m_connections.remove(id);
		if(m_handler != null)
			m_handler.obtainMessage(MESSAGE_CONNECTION_LOST, id, -1, null).sendToTarget();
	}
}
