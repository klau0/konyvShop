package webshop.konyv;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final String LOG_TAG = RegisterActivity.class.getName();
    EditText nameEditText;
    EditText emailEditText;
    EditText pswdEditText;
    EditText pswdAgainEditText;
    EditText addressEditText;
    Spinner houseSpinner;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private CollectionReference userRefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        pswdEditText = findViewById(R.id.pswdEditText);
        pswdAgainEditText = findViewById(R.id.pswdAgainEditText);
        addressEditText = findViewById(R.id.addressEditText);
        houseSpinner = findViewById(R.id.houseSpinner);

        houseSpinner.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.houses, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        houseSpinner.setAdapter(adapter);

        mFirestore = FirebaseFirestore.getInstance();
        userRefs = mFirestore.collection("Users");

    }

    public void register(View view) {
        String name = nameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String pswd = pswdEditText.getText().toString();
        String pswdAgain = pswdAgainEditText.getText().toString();
        String address = addressEditText.getText().toString();
        String house = houseSpinner.getSelectedItem().toString();

        if (!pswd.equals(pswdAgain)){
            Log.e(LOG_TAG ,"Helytelen jelszó megerősítés!");
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, pswd).addOnCompleteListener(this, task -> {
            if(task.isSuccessful()){
                Log.d(LOG_TAG, "Felhasználó sikeresen létrehozva");
                userRefs.add(new User(mAuth.getCurrentUser().getUid(), 0));
                startShopping();
            } else {
                Log.e(LOG_TAG, "Sikertelen felhasználó létrehozás");
                Toast.makeText(RegisterActivity.this, "Sikertelen felhasználó létrehozás: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void startShopping(/* registered user data */){
        Intent intent = new Intent(this, ShoppingActivity.class);
        startActivity(intent);
    }

    public void cancel(View view) {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selectedItem = parent.getItemAtPosition(position).toString();
        Log.i(LOG_TAG, selectedItem);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}