package com.example.duan1.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.duan1.Adapter.BillAdapter;
import com.example.duan1.Adapter.MenuAdapter;
import com.example.duan1.R;
import com.example.duan1.model.HoaDon;
import com.example.duan1.model.SanPham;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class BillActivity extends AppCompatActivity {

    FirebaseFirestore db;
    RecyclerView rcvBill;
    List<HoaDon> listhd;
    LinearLayoutManager layoutManager;
    BillAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill2);
        rcvBill = findViewById(R.id.rcvBill);
        db = FirebaseFirestore.getInstance();
        ReadData();

    }

    public void ReadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("LOGIN_INFO", Context.MODE_PRIVATE);
        if (sharedPreferences.getString("phone", "0912181340").equalsIgnoreCase("+84789867336")) {
            db.collection("BILL")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                listhd = new ArrayList<>();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    String data = "";
                                    int count = 1;
                                    Log.d("====", document.getId() + " => " + document.getData());
                                    Object a = document.get("FOOD");
                                    String c = a.toString().replace("[", "");
                                    String d = c.toString().replace("{", "");
                                    String e = d.toString().replace("]", "");
                                    String f = e.toString().replace("}", "");
                                    String[] token = f.split(",");
                                    for (String b : token) {
                                        data += b.trim() + "                    ";
                                        if (count % 2 == 0){
                                            data += "\n";
                                        }
                                        count ++;

                                    }

                                    String address = document.getString("ADDRESS");
                                    String price = document.getString("PRICE");
                                    String date = document.getString("DATE");
                                    String discountcode = document.getString("DISCOUNTCODE");
                                    HoaDon hoaDon = new HoaDon(data.trim().toUpperCase(), price, address, date, discountcode);
                                    listhd.add(hoaDon);
                                }
                                layoutManager = new LinearLayoutManager(BillActivity.this);
                                rcvBill.setLayoutManager(layoutManager);
                                adapter = new BillAdapter(listhd, BillActivity.this);
                                rcvBill.setAdapter(adapter);
                            } else {
                                Log.w("===", "Error getting documents.", task.getException());
                            }
                        }
                    });
        } else {
            db.collection("BILL")
                    .whereEqualTo("PHONE", sharedPreferences.getString("phone", "0912181340"))
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                listhd = new ArrayList<>();

                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    String data = "";
                                    Log.d("====", document.getId() + " => " + document.getData());
                                    Object a = document.get("FOOD");
                                    String c = a.toString().replace("[{", "");
                                    String d = c.replace("}]", "");
                                    String[] token = d.split(",");
                                    for (String b : token) {
                                        data += b + "\n";
                                    }
                                    String address = document.getString("ADDRESS");
                                    String price = document.getString("PRICE");
                                    String date = document.getString("DATE");
                                    String discountcode = document.getString("DISCOUNTCODE");
                                    HoaDon hoaDon = new HoaDon(data, price, address, date, discountcode);
                                    listhd.add(hoaDon);
                                }
                                layoutManager = new LinearLayoutManager(BillActivity.this);
                                rcvBill.setLayoutManager(layoutManager);
                                adapter = new BillAdapter(listhd, BillActivity.this);
                                rcvBill.setAdapter(adapter);
                            } else {
                                Log.w("===", "Error getting documents.", task.getException());
                            }
                        }
                    });
        }

    }
}