package webshop.konyv;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class ShoppingActivity extends AppCompatActivity {
    //TODO?: első vásárlásnál kap ajándékot
    private static final String LOG_TAG = ShoppingActivity.class.getName();
    private FirebaseUser user;
    private RecyclerView mRecyclerView;
    private FirebaseFirestore mFirestore;
    private MyAdapter mAdapter;
    private ArrayList<Book> bookList;
    private CollectionReference bookRefs;
    private FrameLayout redCircle;
    private TextView redCircleNum;
    private boolean viewRow = true;
    private int gridNum = 1;
    private int queryLimit = 10;
    private static int cartItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            Log.d(LOG_TAG, "Hitelesített felhasználó");
        } else {
            Log.d(LOG_TAG, "Nem hitelesített felhasználó");
            finish();
        }

        mFirestore = FirebaseFirestore.getInstance();
        bookRefs = mFirestore.collection("Konyvek");
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, gridNum));
        bookList = new ArrayList<>();
        mAdapter = new MyAdapter(this, bookList);
        mRecyclerView.setAdapter(mAdapter);

        queryData();

    }
    private void initializeData() {
        String[] booksTitles = getResources().getStringArray(R.array.titles);
        String[] booksAuthors = getResources().getStringArray(R.array.authors);
        String[] booksInfos = getResources().getStringArray(R.array.infos);
        String[] booksPrices = getResources().getStringArray(R.array.prices);
        TypedArray booksRates = getResources().obtainTypedArray(R.array.rated_infos);
        TypedArray booksImgs = getResources().obtainTypedArray(R.array.img_resources);

        for(int i = 0; i < booksTitles.length; i++){
            bookRefs.add(new Book(
                    booksTitles[i],
                    booksAuthors[i],
                    booksInfos[i],
                    booksPrices[i],
                    booksRates.getFloat(i, 0),
                    booksImgs.getResourceId(i, 0),
                    0));
        }
        booksImgs.recycle();
    }
    private void queryData(){
        // a már meglévő adatok törlése hogy elkerüljük a duplikációt
        bookList.clear();

        bookRefs.orderBy("cartedCount", Query.Direction.DESCENDING).limit(queryLimit).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    cartItems = 0;
                    for(QueryDocumentSnapshot document: queryDocumentSnapshots){
                        Book book = document.toObject(Book.class);
                        book.setId(document.getId());
                        bookList.add(book);
                        cartItems += book.getCartedCount();
                    }

                    // hogy meghívódjon az onPrepareOptionsMenu() és kijelentkezés majd újbóli belépés után is megjelenjen a kosárszám
                    invalidateOptionsMenu();

                    if(bookList.size() == 0){
                        initializeData();
                        queryData();
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.my_menu, menu);

        MenuItem search = menu.findItem(R.id.search_bar);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
        searchView.setOnQueryTextListener( new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {return false;}
            @Override
            public boolean onQueryTextChange(String s) {
                Log.d(LOG_TAG, s);
                mAdapter.getFilter().filter(s);
                return false;
            }
        });
        return true;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.log_out:
                Log.d(LOG_TAG, "Kijelentkezés megnyomva");
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(this,MainActivity.class);
                startActivity(intent);
                return true;
            case R.id.setting:
                Log.d(LOG_TAG, "Beállítások megnyomva");
                return true;
            case R.id.bag:
                Log.d(LOG_TAG, "Kosár megnyomva");
                return true;
            case R.id.diff_view:
                Log.d(LOG_TAG, "Átrendezés megnyomva");
                if(viewRow){
                    changeSpanCount(item, R.drawable.ic_view_row, 2);
                } else {
                    changeSpanCount(item, R.drawable.ic_view_grid, 1);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void changeSpanCount(MenuItem item, int drawableId, int spanCount) {
        viewRow = !viewRow;
        item.setIcon(drawableId);
        // fontos hogy a már meglévőn módosítunk, az android lifecycle miatt
        GridLayoutManager layoutManager = (GridLayoutManager) mRecyclerView.getLayoutManager();
        layoutManager.setSpanCount(spanCount);
        mAdapter.notifyDataSetChanged();
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        final MenuItem bag = menu.findItem(R.id.bag);

        FrameLayout rootView = (FrameLayout) bag.getActionView();
        redCircle = rootView.findViewById(R.id.red_circle);
        redCircleNum = rootView.findViewById(R.id.red_circle_num);

        if (cartItems > 0){
            redCircleNum.setText(String.valueOf(cartItems));
        } else {
            redCircleNum.setText("");
        }
        redCircle.setVisibility((cartItems > 0) ? VISIBLE : GONE);

        rootView.setOnClickListener(view -> onOptionsItemSelected(bag));
        mAdapter.notifyDataSetChanged();
        return super.onPrepareOptionsMenu(menu);
    }

    public void updateBag(Book item) {
        cartItems += 1;
        if (cartItems > 0){
            redCircleNum.setText(String.valueOf(cartItems));
        } else {
            redCircleNum.setText("");
        }

        redCircle.setVisibility((cartItems > 0) ? VISIBLE : GONE);

        bookRefs.document(item._getId()).update("cartedCount", item.getCartedCount() + 1)
                .addOnFailureListener(failure -> {
                    Log.e(LOG_TAG, "Könyv melynek id-ja:" + item._getId() + " nem változott meg (cartedCount)");
                });

        //mNotificationHandler.send(item.getName());
        queryData();
    }

}