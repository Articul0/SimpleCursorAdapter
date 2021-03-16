package com.samsung.itschool.adapterexample;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    ListView lv; // не забудьте привязать переменную (findViewById)
    TextView goodsName, price, goodsCount, totalPrice;
    SimpleCursorAdapter adapter; // объявлен в классе, чтобы был доступен вл всех методах
    Button addGood, sortByPrice, sortByName, sortByType;
    DBHelperWithLoader helper;
    SQLiteDatabase goodsDB;
    Cursor goods;
    Spinner categories;

    Boolean sortPrice = true, sortName = false, sortType = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lv = findViewById(R.id.goodslist);
        goodsName = findViewById(R.id.goods_name);
        price = findViewById(R.id.price);
        addGood = findViewById(R.id.addGood);
        goodsCount = findViewById(R.id.goodsCount);
        totalPrice = findViewById(R.id.totalPrice);
        sortByPrice = findViewById(R.id.sortByPrice);
        sortByName = findViewById(R.id.sortByName);
        sortByType = findViewById(R.id.sortByType);
        categories = findViewById(R.id.categories);

        helper = new DBHelperWithLoader(this);
        goodsDB = helper.getWritableDatabase();

        int total = 0;
        Cursor cursor = goodsDB.rawQuery("SELECT SUM(price) as Total FROM databasa", null);
        if (cursor.moveToFirst()) {
            total = cursor.getInt(cursor.getColumnIndex("Total"));
        }
        totalPrice.setText("Сумма: " + total);

        goods = goodsDB.rawQuery("SELECT * FROM databasa", null);
        goodsCount.setText("Всего товаров: " + (goods.getCount() - 1));
        String[] databasa_fields = goods.getColumnNames();

        // int[] - ссылки на id элементов разметки playlist_item
        int[] views = { R.id.id, R.id.price, R.id.goods_name, R.id.type};

        // этот адаптер отображает в ListView перечень полей (столбцов)
        adapter = new SimpleCursorAdapter(this, R.layout.good_item, goods, databasa_fields, views, 0 );
        lv.setAdapter(adapter);

        addGood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentValues values = new ContentValues();
                values.put("goods_name", goodsName.getText().toString());
                values.put("price", price.getText().toString());
                values.put("type", categories.getSelectedItem().toString());

                goodsDB.insert("databasa", null, values);
                goods = goodsDB.rawQuery("SELECT * FROM databasa", null);

                adapter.swapCursor(goods);
                adapter.notifyDataSetChanged();

                goodsCount.setText("Всего товаров: " + goods.getCount());

                int total = 0;
                Cursor cursor = goodsDB.rawQuery("SELECT SUM(price) as Total FROM databasa", null);
                if (cursor.moveToFirst()) {
                    total = cursor.getInt(cursor.getColumnIndex("Total"));
                }
                totalPrice.setText("Сумма : " + total);
            }
        });

        sortByPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sortPrice) {
                    goods = goodsDB.rawQuery("SELECT * FROM databasa ORDER BY price DESC", null);
                } else {
                    goods = goodsDB.rawQuery("SELECT * FROM databasa ORDER BY price ASC", null);
                }
                adapter.swapCursor(goods);
                sortPrice = !sortPrice;
            }
        });

        sortByName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sortName) {
                    goods = goodsDB.rawQuery("SELECT * FROM databasa ORDER BY goods_name DESC", null);
                } else {
                    goods = goodsDB.rawQuery("SELECT * FROM databasa ORDER BY goods_name ASC", null);
                }
                adapter.swapCursor(goods);
                sortName = !sortName;
            }
        });

        sortByType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sortType) {
                    goods = goodsDB.rawQuery("SELECT * FROM databasa ORDER BY type DESC", null);
                } else {
                    goods = goodsDB.rawQuery("SELECT * FROM databasa ORDER BY type ASC", null);
                }
                adapter.swapCursor(goods);
                sortType = !sortType;
            }
        });
    }
}
