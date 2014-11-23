package com.wingeon.net;

import java.io.IOException;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

public class BluetoothServer extends Thread {
	private final BluetoothServerSocket mmServerSocket;
	private BluetoothAdapter m_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

	public static String NAME = "truco";
	public static String Truco_UUID = "0a7268bc-8fd1-4696-9ad9-9954f06c43c9";

	public BluetoothServer() {
		BluetoothServerSocket tmp = null;
		try {
			UUID uuid = UUID.fromString(Truco_UUID);
			tmp = m_bluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, uuid);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		mmServerSocket = tmp;
	}

	public void run() {
		BluetoothSocket socket = null;
		while(true) {
			try {
				socket = mmServerSocket.accept();
			} catch (IOException e) {
				System.out.println("Accept failed: " + e.getMessage());
				break;
			}
			if (socket != null) {
				System.out.println("Someone connected!");
				ConnectionManager.getInstance().connectedBluetooth(socket, socket.getRemoteDevice());
			}
		}
	}

	/** Will cancel the listening socket, and cause the thread to finish */
	public void cancel() {
		try {
			mmServerSocket.close();
		} catch (IOException e) { }
	}
}
