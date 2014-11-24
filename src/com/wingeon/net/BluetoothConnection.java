package com.wingeon.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.bluetooth.BluetoothSocket;

public class BluetoothConnection extends Thread {
	private final BluetoothSocket mmSocket;
	private final InputStream mmInStream;
	private final OutputStream mmOutStream;
	private int m_id;
	static int idGenerator = 0;

	public BluetoothConnection(BluetoothSocket socket) {
		m_id = idGenerator++;

		mmSocket = socket;
		InputStream tmpIn = null;
		OutputStream tmpOut = null;

		// Get the input and output streams, using temp objects because
		// member streams are final
		try {
			tmpIn = socket.getInputStream();
			tmpOut = socket.getOutputStream();
		} catch (IOException e) { }

		mmInStream = tmpIn;
		mmOutStream = tmpOut;
	}

	public void run() {
		byte[] buffer = new byte[1024];  // buffer store for the stream
		int bytes; // bytes returned from read()

		// Keep listening to the InputStream until an exception occurs
		while (true) {
			try {
				// Read from the InputStream
				bytes = mmInStream.read(buffer);
				// Send the obtained bytes to the UI activity

				ConnectionManager.getInstance().receivedBytes(m_id, bytes, buffer);
			} catch (IOException e) {
				ConnectionManager.getInstance().connectionLost(m_id);
				break;
			}
		}
	}

	/* Call this from the main activity to send data to the remote device */
	public void write(byte[] buffer) {
		try {
			mmOutStream.write(buffer);
			ConnectionManager.getInstance().sentBytes(m_id, buffer);
		} catch (IOException e) { }
	}

	/* Call this from the main activity to shutdown the connection */
	public void cancel() {
		try {
			mmSocket.close();
		} catch (IOException e) { }
	}
	
	public int getConnectionId() {
		return m_id;
	}
}