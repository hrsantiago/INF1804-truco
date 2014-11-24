package com.wingeon.net;

import java.io.IOException;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

public class BluetoothConnect extends Thread {
	private final BluetoothSocket mmSocket;
	private final BluetoothDevice mmDevice;
	private BluetoothAdapter m_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

	public BluetoothConnect(BluetoothDevice device) {
		// Use a temporary object that is later assigned to mmSocket,
		// because mmSocket is final
		BluetoothSocket tmp = null;
		mmDevice = device;

		// Get a BluetoothSocket to connect with the given BluetoothDevice
		try {
			UUID uuid = UUID.fromString(BluetoothServer.Truco_UUID);
			tmp = device.createRfcommSocketToServiceRecord(uuid);
		} catch (IOException e) { }
		mmSocket = tmp;
	}

	public void run() {
		// Cancel discovery because it will slow down the connection
		m_bluetoothAdapter.cancelDiscovery();

		try {
			// Connect the device through the socket. This will block
			// until it succeeds or throws an exception
			mmSocket.connect();
		} catch (IOException connectException) {
			
			System.out.println("Could not connect: " + connectException.getMessage());
			ConnectionManager.getInstance().connectFailed();
			// Unable to connect; close the socket and get out
			try {
				mmSocket.close();
			} catch (IOException closeException) { }
			return;
		}

		System.out.println("Connected!");
		ConnectionManager.getInstance().connectedBluetooth(mmSocket, mmDevice);
	}

	/** Will cancel an in-progress connection, and close the socket */
	public void cancel() {
		try {
			mmSocket.close();
		} catch (IOException e) { }
	}
}
