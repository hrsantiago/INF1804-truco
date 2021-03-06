package com.wingeon.truco;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.wingeon.net.BluetoothConnect;
import com.wingeon.net.BluetoothConnection;
import com.wingeon.net.BluetoothServer;
import com.wingeon.net.ConnectionManager;

public class RoomActivity extends Activity {
	
	class PlayerSlot {
		PlayerSlot(String name, boolean virtual) {
			this.name = name;
			this.virtual = virtual;
		}
		String name;
		boolean virtual;
		boolean local = false;
		int connectionId = -1;
	}
	
	private BluetoothServer m_server;
	private BluetoothConnect m_connect;
	private BluetoothConnection m_connection;
	
	private Button m_joinButtons[] = new Button[4];
	private TextView m_playerNames[] = new TextView[4];
	private Button m_startButton;
	private TextView m_status;
	private PlayerSlot m_slots[] = new PlayerSlot[4];
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_room);

		m_joinButtons[0] = (Button)findViewById(R.id.join0);
		m_joinButtons[1] = (Button)findViewById(R.id.join1);
		m_joinButtons[2] = (Button)findViewById(R.id.join2);
		m_joinButtons[3] = (Button)findViewById(R.id.join3);
		
		m_playerNames[0] = (TextView)findViewById(R.id.player0);
		m_playerNames[1] = (TextView)findViewById(R.id.player1);
		m_playerNames[2] = (TextView)findViewById(R.id.player2);
		m_playerNames[3] = (TextView)findViewById(R.id.player3);
		
		m_status = (TextView)findViewById(R.id.status);
		m_startButton = (Button)findViewById(R.id.start);
		m_startButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) { sendStartGame(); }
		});
		
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	protected void onStart() {
		for(int i = 0; i < 4; ++i) {
			m_slots[i] = new PlayerSlot(getResources().getString(R.string.player_virtual), true);
			m_joinButtons[i].setVisibility(View.INVISIBLE);
		}
		
		ConnectionManager.getInstance().closeAll();
		ConnectionManager.getInstance().setHandler(m_handler);
		
		Intent intent = getIntent();
		String connectType = intent.getStringExtra("type");
		if(connectType.equals("host")) {
			m_server = new BluetoothServer();
			m_server.start();
			m_startButton.setVisibility(View.VISIBLE);
			m_status.setText(R.string.waiting_players);
			m_slots[0] = new PlayerSlot(getName(), false);
			m_slots[0].local = true;
		}
		else if(connectType.equals("guest")) {
			BluetoothDevice device = intent.getParcelableExtra("device");
			m_connect = new BluetoothConnect(device);
			m_connect.start();
			m_startButton.setVisibility(View.INVISIBLE);
			m_status.setText(R.string.connecting);
		}
		
		updateSlots();
		
		super.onStart();
	}
	
	@Override
	protected void onStop() {
		if(m_server != null) {
			m_server.cancel();
			m_server = null;
		}
		if(m_connect != null) {
			m_connect.cancel();
			m_connect = null;
		}
		if(m_connection != null) {
			m_connection.cancel();
			m_connection = null;
		}
		
		super.onStop();
	}
	
	private final Handler m_handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	switch (msg.what) {
            case ConnectionManager.MESSAGE_CONNECTED:
                processConnected(msg.arg1, (BluetoothConnection)msg.obj);
            	break;
            case ConnectionManager.MESSAGE_READ:
            	processRead(msg.arg1, msg.arg2, (byte[])msg.obj);
            	break;
            case ConnectionManager.MESSAGE_WRITE:
            	break;
            case ConnectionManager.MESSAGE_CONNECT_FAILED:
            	Toast.makeText(getApplicationContext(), getResources().getString(R.string.connect_fail), Toast.LENGTH_SHORT).show();
            	finish();
            	break;
            case ConnectionManager.MESSAGE_CONNECTION_LOST:
            	processConnectionLost(msg.arg1);
            	break;
            default:
            	System.out.println("Unknown handler message: " + msg.what);
            	break;
        	}
        }
	};
	
	private void processConnected(int id, BluetoothConnection connection) {
		if(!isHost()) {
			m_connection = connection;
			m_status.setText(R.string.connected);
			sendName();
		}
	}
	
	private void processConnectionLost(int id) {
		if(isHost())
			processPlayerLeave(id);
		else {
			Toast.makeText(getApplicationContext(), getResources().getString(R.string.connection_lost), Toast.LENGTH_SHORT).show();
			finish();
		}
	}
	
	private void processRead(int id, int bytes, byte[] buffer) {
		int opcode = buffer[0];
		switch(opcode) {
		case 0x00: // name
			byte[] nameByte = Arrays.copyOfRange(buffer, 1, bytes);
			try {
				String name = new String(nameByte, "UTF-8");
				processPlayerJoin(id, name);
			}
			catch (UnsupportedEncodingException e) { e.printStackTrace(); }
			break;
		case 0x01: // slot
			int index = 1;
			for(int i = 0; i < 4; ++i) {
				m_slots[i].local = buffer[index++] != 0 ? true : false;
				int len = buffer[index++];
				byte[] slotNameByte = Arrays.copyOfRange(buffer, index, index+len);
				index += len;
				m_slots[i].virtual = buffer[index++] != 0 ? true : false;;
				try {
					m_slots[i].name = new String(slotNameByte, "UTF-8");
				}
				catch (UnsupportedEncodingException e) { e.printStackTrace(); }
			}
			updateSlots();
			break;
		case 0x02: // start game
			long seed = buffer[1] | (buffer[2] << 8) | (buffer[3] << 16) | (buffer[4] << 24);
			processStartGame(seed);
			break;
		}
	}
	
	private boolean isHost() {
		Intent intent = getIntent();
		String connectType = intent.getStringExtra("type");
		return connectType.equals("host");
	}
	
	private void sendName() {
		String name = getName();
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		stream.write(0);
		try {
			stream.write(name.getBytes());
		} catch (IOException e) { e.printStackTrace(); }
		m_connection.write(stream.toByteArray());
	}
	
	private void sendSlots() {
		for(int j = 0; j < 4; ++j) {
			PlayerSlot slotBase = m_slots[j];
			if(slotBase.virtual || slotBase.local)
				continue;
			
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			stream.write(1);
			for(int i = 0; i < 4; ++i) {
				PlayerSlot slot = m_slots[i];
				stream.write(i == j ? 1 : 0);
				stream.write(slot.name.length());
				try {
					stream.write(slot.name.getBytes());
				} catch (IOException e) { e.printStackTrace(); }
				stream.write(slot.virtual ? 1 : 0);
			}
			BluetoothConnection connection = ConnectionManager.getInstance().getConnection(slotBase.connectionId);
			connection.write(stream.toByteArray());
		}
	}
	
	private void sendStartGame() {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		stream.write(2);
		
		long seed = System.currentTimeMillis();
		byte a = (byte)(seed & 0xff);
		byte b = (byte)((seed >> 8) & 0xff);
		byte c = (byte)((seed >> 16) & 0xff);
		byte d = (byte)((seed >> 24) & 0xff);
		stream.write(a);
		stream.write(b);
		stream.write(c);
		stream.write(d);
		seed = a | b << 8 | c << 16 | d << 24;
		
		ConnectionManager.getInstance().broadcast(stream.toByteArray());
		processStartGame(seed);
	}
	
	private String getName() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(RoomActivity.this);
		return prefs.getString("name", getResources().getString(R.string.player_0));
	}
	
	private void processPlayerJoin(int id, String name) {
		for(int i = 0; i < 4; ++i) {
			PlayerSlot slot = m_slots[i];
			if(slot.virtual) {
				slot.connectionId = id;
				slot.name = name;
				slot.virtual = false;
				slot.local = false;
				break;
			}
		}
		sendSlots();
		updateSlots();
	}
	
	private void processPlayerLeave(int id) {
		for(int i = 0; i < 4; ++i) {
			PlayerSlot slot = m_slots[i];
			if(slot.connectionId == id) {
				slot.name = getResources().getString(R.string.player_virtual);
				slot.virtual = true;
				slot.connectionId = -1;
				break;
			}
		}
		sendSlots();
		updateSlots();
	}
	
	private void processStartGame(long seed) {
		Intent intent = new Intent(RoomActivity.this, GameActivity.class);
		intent.putExtra("online", true);
		intent.putExtra("seed", seed);
		for(int i = 0; i < 4; ++i) {
			int playerId = i;
			if(i == 1)
				++playerId;
			else if(i == 2)
				--playerId;
			
			intent.putExtra("slot_name_" + playerId, m_slots[i].name);
			intent.putExtra("slot_virtual_" + playerId, m_slots[i].virtual);
			intent.putExtra("slot_id_" + playerId, m_slots[i].connectionId);
			
			if(m_slots[i].local)
				intent.putExtra("slot_id_local", playerId);
		}
		m_connect = null;
		m_connection = null;
		startActivity(intent);
		finish();
	}
	
	private void updateSlots() {
		for(int i = 0; i < 4; ++i) {
			PlayerSlot slot = m_slots[i];
			m_playerNames[i].setText(slot.name);
		}
	}
}
