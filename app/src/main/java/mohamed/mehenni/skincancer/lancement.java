package mohamed.mehenni.skincancer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class lancement extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lancement);
    }

    public void lancer(View view) {
        Intent it =new Intent(this,MainActivity.class);
        startActivity(it);
    }
}