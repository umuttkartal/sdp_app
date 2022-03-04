package com.example.circulatio;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Set;

public class SplashScreen extends AppCompatActivity {
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        startActivity(new Intent(SplashScreen.this, MainActivity.class));
//        finish();
//    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                //This method will be executed once the timer is over
//                // Start your app main activity
//                Intent i = new Intent(SplashScreen.this, MainActivity.class);
//                startActivity(i);
//                // close this activity
//                finish();
//            }
//        }, 3000);
    }
    @Override
    protected void onResume(){
        super.onResume();


        Intent i = new Intent(SplashScreen.this, MainActivity.class);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //This method will be executed once the timer is over
                // Start your app main activity
                System.out.println("CIGUBUGU");
                startActivity(i);
                // close this activity
                finish();
            }
        }, 3000);


    }
}































//package com.example.circulatio;
//
//        import androidx.appcompat.app.AppCompatActivity;
//
//        import android.bluetooth.BluetoothAdapter;
//        import android.bluetooth.BluetoothDevice;
//        import android.content.Intent;
//        import android.os.Bundle;
//        import android.os.Handler;
//        import android.view.View;
//        import android.widget.AdapterView;
//        import android.widget.ArrayAdapter;
//        import android.widget.Button;
//        import android.widget.ListView;
//        import android.widget.TextView;
//        import android.widget.Toast;
//
//        import java.util.ArrayList;
//        import java.util.Set;
//
//public class MainActivity extends AppCompatActivity {
//    private BluetoothAdapter myBluetooth = null;
//    private Set<BluetoothDevice> pairedDevices;
//    public static String EXTRA_ADDRESS = "98:D3:32:31:30:0F";
////    ListView devicelist;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
////        Button btnPaired;
//
////        btnPaired = (Button) findViewById(R.id.button);
////        devicelist = (ListView) findViewById(R.id.listView);
//
//        myBluetooth = BluetoothAdapter.getDefaultAdapter();
//        if ( myBluetooth==null ) {
//            Toast.makeText(getApplicationContext(), "Bluetooth device not available", Toast.LENGTH_LONG).show();
//            finish();
//        }
//        else if ( !myBluetooth.isEnabled() ) {
//            Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(turnBTon, 1);
//        }
//
////        btnPaired.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                pairedDevicesList();
////            }
////        });
//
//    }
//
////    private void pairedDevicesList () {
////        pairedDevices = myBluetooth.getBondedDevices();
////        ArrayList list = new ArrayList();
////
////        if ( pairedDevices.size() > 0 ) {
////            for ( BluetoothDevice bt : pairedDevices ) {
////                list.add(bt.getName().toString() + "\n" + bt.getAddress().toString());
////            }
////        } else {
////            Toast.makeText(getApplicationContext(), "No Paired Bluetooth Devices Found.", Toast.LENGTH_LONG).show();
////        }
////
////        final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
////        devicelist.setAdapter(adapter);
////        devicelist.setOnItemClickListener(myListClickListener);
////    }
//
////    private AdapterView.OnItemClickListener myListClickListener = new AdapterView.OnItemClickListener() {
////        @Override
////        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
////            String info = ((TextView) view).getText().toString();
////            String address = info.substring(info.length()-17);
////
////            Intent i = new Intent(MainActivity.this, BLE.class);
////            i.putExtra(EXTRA_ADDRESS, address);
////            startActivity(i);
////        }
////    };
//
//    @Override
//    protected void onResume() {
//
//        super.onResume();
//
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                //This method will be executed once the timer is over
//                // Start your app main activity
//                Intent i = new Intent(MainActivity.this, BLE.class);
//                pairedDevices = myBluetooth.getBondedDevices();
//                String address = "";
//
//                if ( pairedDevices.size() > 0 ) {
//                    for ( BluetoothDevice bt : pairedDevices ) {
//                        address = bt.getAddress().toString();
//                    }
//                } else {
//                    Toast.makeText(getApplicationContext(), "No Paired Bluetooth Devices Found.", Toast.LENGTH_LONG).show();
//                }
//                i.putExtra(EXTRA_ADDRESS, "98:D3:32:31:30:0F");
//                startActivity(i);
//                // close this activity
//                finish();
//            }
//        }, 3000);
//
////        startActivity(i);
//
//    }
//}