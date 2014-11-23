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
import android.widget.Button;
import android.widget.TextView;

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
		boolean local = false;;
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
				try {
					m_slots[i].name = new String(slotNameByte, "UTF-8");
				}
				catch (UnsupportedEncodingException e) { e.printStackTrace(); }
			}
			updateSlots();
			break;
		case 0x02: // start game
			processStartGame();
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
			}
			BluetoothConnection connection = ConnectionManager.getInstance().getConnection(slotBase.connectionId);
			connection.write(stream.toByteArray());
		}
	}
	
	private void sendStartGame() {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		stream.write(2);
		ConnectionManager.getInstance().broadcast(stream.toByteArray());
		processStartGame();
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
	
	private void processStartGame() {
		Intent intent = new Intent(RoomActivity.this, GameActivity.class);
		intent.putExtra("online", true);
		for(int i = 0; i < 4; ++i) {
			intent.putExtra("slot_name_" + i, m_slots[i].name);
			intent.putExtra("slot_local_" + i, m_slots[i].local);
			intent.putExtra("slot_id_" + i, m_slots[i].connectionId);
		}
		m_connection = null;
		startActivity(intent);
	}
	
	private void updateSlots() {
		for(int i = 0; i < 4; ++i) {
			PlayerSlot slot = m_slots[i];
			m_playerNames[i].setText(slot.name);
		}
	}
}
