package com.khadijael.places;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import 	java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

//socket io
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Button qrCodeFoundButton;
    private Button dateButton;
    private Button addoccupation;
    private String qrCode;
    private Spinner spinner;
    private List<String> categories;
    public static TextView scantext;
    public static TextView datetext;
    public static TextView sallename;
    public static TextView MyLink;
    private Integer salle_id;
    String salle_name = "";
    Boolean salle_found = false;
    String occupation_date;
    List<String> list_hrdbt = new ArrayList<String>();
    List<String> list_hrfin = new ArrayList<String>();
    String hrfin;
    String hrdebut;
    Boolean date_accepted = false;
    Boolean crenau_selected = false;
    Boolean scan = false;
    private Integer myid;

    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("https://oumaima-sallebloc.herokuapp.com");
        } catch (URISyntaxException e) {}
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myid = new Random().nextInt((9999 - 0) + 1) + 0;
        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);
        mSocket.emit("getcrenau", true);




        mSocket.on("crenolist", new Emitter.Listener() {

            @Override
            public void call(final Object... args) {
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("getting crenaux");
                        try {
                            System.out.println(args[0].toString());
                            System.out.println(args[0].getClass().getName());
                            JSONArray list_creno = (JSONArray) args[0];
                            if(list_creno.length() > 0){
                                System.out.println("creno size >>");
                                categories = new ArrayList<>();
                                for(int index = 0 ; index < list_creno.length() ; index ++){
                                    JSONObject obj = list_creno.getJSONObject(index);
                                    list_hrdbt.add((String) obj.get("hrdebut"));
                                    list_hrfin.add((String) obj.get("hrfin"));
                                    categories.add((String) obj.get("hrdebut") + " - " + (String) obj.get("hrfin"));
                                }
                                set_adapters();

                            }
                        } catch (Exception e) {
                        }
                    }
                });
            }
        });


        mSocket.on("information", new Emitter.Listener() {

                    @Override
                    public void call(final Object... args) {
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //MySimpleArrayAdapter mySimpleArrayAdapter = (MySimpleArrayAdapter) listView.getAdapter();
                                //mySimpleArrayAdapter.add(args[0].toString());
                                //mySimpleArrayAdapter.notifyDataSetChanged();
                                System.out.println("new event ");

                                try {
                                    JSONObject json = (JSONObject) args[0];
                                    System.out.println(json.get("id"));
                                    System.out.println(myid);
                                    System.out.println(json.get("message"));
                                    Boolean _me = ((Integer) json.get("id")).equals(myid);
                                    if(_me){
                                        Toast.makeText(MainActivity.this, (String) json.get("message") , Toast.LENGTH_SHORT).show();
                                        if(((String) json.get("message")).equals("Occupation Added :)")){
                                            date_accepted = false;
                                            occupation_date = "";
                                            datetext.setText(occupation_date);


                                        }
                                    }
                                } catch (Exception e) {
                                }
                            }
                        });
                    }
                });

        mSocket.on("salleinfo", new Emitter.Listener() {

            @Override
            public void call(final Object... args) {
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            JSONObject json = (JSONObject) args[0];
                            salle_name = (String) json.get("name");
                            salle_found = true;
                            sallename.setText(salle_name);
                        } catch (Exception e) {
                            salle_found = false;
                            salle_name = "";
                        }
                    }
                });
            }
        });

        mSocket.on("carenoadded", new Emitter.Listener() {

            @Override
            public void call(final Object... args) {
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //MySimpleArrayAdapter mySimpleArrayAdapter = (MySimpleArrayAdapter) listView.getAdapter();
                        //mySimpleArrayAdapter.add(args[0].toString());
                        //mySimpleArrayAdapter.notifyDataSetChanged();
                        System.out.println("new event ");

                        try {
                            System.out.println(args[0].toString());
                            JSONObject json = (JSONObject) args[0];
                            System.out.println((Integer) json.get("complete"));
                            if ((Integer) json.get("complete") == 1) {
                                Toast.makeText(MainActivity.this, "Creno Added", Toast.LENGTH_SHORT).show();
                            }
                            else if((Integer) json.get("complete") == -1){
                                Toast.makeText(MainActivity.this, "Creno Not Added", Toast.LENGTH_SHORT).show();
                            }
                            else if((Integer) json.get("complete") == 0){
                                Toast.makeText(MainActivity.this, "Creno Already available", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                        }
                    }
                });
            }
        });
        mSocket.connect();
       // mSocket.emit("addcrenau", {});

        scantext = (TextView) findViewById(R.id.scantext);
        datetext = (TextView) findViewById(R.id.datetext);
        sallename = (TextView) findViewById(R.id.sallename);

        qrCodeFoundButton = findViewById(R.id.activity_main_qrCodeFoundButton);
        dateButton = findViewById(R.id.choosedate);
        addoccupation = findViewById(R.id.addoccupation);
        //qrCodeFoundButton.setVisibility(View.INVISIBLE);
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                datepicker();
            }
        });

        addoccupation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                add_occupation();
            }
        });


        qrCodeFoundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startActivity(new Intent(getApplicationContext(), ScannerView.class));
                Intent intent= new Intent(getApplicationContext(),ScannerView.class);
                startActivityForResult(intent, 0);
                /*HashMap<String, String> meMap=new HashMap<String, String>();
                meMap.put("hrdebut","10:00");
                meMap.put("hrfin","12:00");
                JSONObject obj = new JSONObject();
               try{
                   obj.put("id",myid);
                   obj.put("hrdebut","10:00");
                   obj.put("hrfin","12:00");
               } catch (JSONException e) {
                   e.printStackTrace();
               }
                mSocket.emit("addcrenau", obj);*/

            }
        });

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == RESULT_OK) {
                //Toast.makeText(this, data.getData().toString(), Toast.LENGTH_SHORT).show();
                get_salle_info(data.getData().toString());
                scan = true;
            }
    }

    public  void get_salle_info(String id){
        JSONObject obj = new JSONObject();
        try{
            System.out.println("getting salle info");
            obj.put("salleid",id);
            obj.put("id",myid);
            mSocket.emit("getsalleinfo", obj);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void set_adapters() {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
    }




    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();
         hrdebut = list_hrdbt.get(position);
        hrfin = list_hrfin.get(position);
        if(hrdebut != null && !hrdebut.isEmpty()){
            crenau_selected = true;
        }
        // Showing selected spinner item
        Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();

    }

    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mSocket.disconnect();
        //mSocket.off("newData", onNewData);
    }

    public void datepicker(){
        final Calendar newCalendar = Calendar.getInstance();
        DatePickerDialog monthDatePickerDialog = new DatePickerDialog(this,
                AlertDialog.THEME_HOLO_LIGHT, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String valid_until = dayOfMonth+"/"+(month + 1)+"/"+year;
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                Date strDate = null;
                try {
                    strDate = sdf.parse(valid_until);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (new Date().after(strDate)) {
                    Toast.makeText(MainActivity.this, "Please choose time in the future", Toast.LENGTH_SHORT).show();
                    occupation_date = "";
                    date_accepted = false;
                }
                else{
                    date_accepted = true;
                    occupation_date = year +"-" + ((month + 1) < 10 && !String.valueOf(month + 1).contains("0") ? "0"+(month + 1) : (month + 1)) + "-" + ((dayOfMonth) < 10 && !String.valueOf(dayOfMonth).contains("0") ? "0"+dayOfMonth : dayOfMonth);
                    datetext.setText(occupation_date);




                }

                //scantext.setText(occupation_date);
            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH)){
            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                //getDatePicker().findViewById(getResources().getIdentifier("year","id","android")).setVisibility(View.GONE); //if you want to hide year ..
            }
        };
        monthDatePickerDialog.setTitle("Select Occupation Date");
        monthDatePickerDialog.show();
    }


    private void add_occupation(){
        if(!scan){
            Toast.makeText(MainActivity.this, "Please scan salle bar code", Toast.LENGTH_LONG).show();
        }
        if(scan && salle_name != null && salle_name != "" && date_accepted && crenau_selected){

            JSONObject obj = new JSONObject();
            try{
                obj.put("id",myid);
                obj.put("sallename",salle_name);
                obj.put("date",occupation_date);
                obj.put("salleid",salle_id);
                obj.put("hrdebut",hrdebut);
                obj.put("hrfin",hrfin);
                mSocket.emit("addoccupation", obj);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }


}