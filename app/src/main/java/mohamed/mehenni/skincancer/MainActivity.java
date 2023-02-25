package mohamed.mehenni.skincancer;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;

import mohamed.mehenni.skincancer.ml.ModelTF;

public class MainActivity extends AppCompatActivity {
    private ImageView image;
    private Button select, predict;
    private TextView tv, per;
    private Bitmap bitmap;



    String [] info = {
            "Le carcinome basocellulaire est le cancer de la peau le plus fréquent. Il se développe dans certaines cellules de la couche supérieure de la peau (épiderme). En général, une petite papule brillante apparaît sur la peau et grossit lentement.",
            "Le mélanome est un cancer de la peau qui naît des cellules qui produisent des pigments de la peau. Les mélanomes peuvent se développer sur une peau normale ou dans des grains de beauté",
            "dermatofibrome : Un dermatofibrome est une excroissance du tissu fibreux dans le derme (la couche inférieure de l'épiderme). On l'appelle aussi histiocytome. Ils prennent la forme d'une excroissance de la taille d'un petit pois juste au-dessous des couches supérieures de l'épiderme et sont assez fermes au toucher.\n",
            "lésions bénignes de type kératose : Les kératoses séborrhéiques sont des lésions pigmentées bénignes. La cause est inconnue. Elles ont tendance à se développer chez les personnes âgées et ont un aspect collé avec une surface verruqueuse, veloutée, cireuse, squameuse ou croûteuse.\n",
            "•\tLa kératose actinique : La kératose actinique évolue assez rarement en carcinome épidermoïde et peut régresser. Il s'agit d'un carcinome épidermoïde in situ (intra-épithélial). Il se transforme en carcinome épidermoïde infiltrant dans environ 3 % à 5 % des cas.\n",
            "•\tnaevus mélanocytaires: Les nævus mélanocytaires (NM) sont des tumeurs pigmentées de la peau traduisant une prolifération mélanocytaire bénigne focalisée ou diffuse. Ils peuvent être présents à la naissance (nævus mélanocytaires congénitaux, NMC) ou apparaitre au cours de la vie (nævus mélanocytaires acquis, NMA).",
            "•\tLe granulome pyogénique : est une croissance bénigne courante qui apparaît souvent comme une bosse saignante à croissance rapide sur la peau ou à l'intérieur de la bouche. Il est composé de vaisseaux sanguins et peut survenir au site d'une blessure mineure.",
                    "A : Asymétrie\n" +
                    "la moitié du naevus ne colle pas avec l'autre moitié.\n" +

                    "\nB :  Bords irréguliers\n" +
                    "les bords peuvent être encochés, mal délimités.\n" +

                    "\nC :  Couleur inhomogène\n" +
                    "variant d'une zone à l'autre de la lésion\n" +

                    "\nD :  Diamètre\n" +
                    "qui est souvent supérieur à 6 mm, la taille de section d'un crayon.\n" +

                    "\nE :  Évolution\n" +
                    "l’aspect de la lésion dans sa taille surtout, sa forme, ou sa couleur a changé."

};

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_info,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        AlertDialog.Builder b = new AlertDialog.Builder(MainActivity.this);
        int id = item.getItemId();
        switch (id) {
            case R.id.one:
                b.setMessage(info[0]);
                break;
            case R.id.two:
                b.setMessage(info[1]);
                break;
            case R.id.three:
                b.setMessage(info[2]);
                break;
            case R.id.four:
                b.setMessage(info[3]);
                break;
            case R.id.five:
                b.setMessage(info[4]);
                break;
            case R.id.six:
                b.setMessage(info[5]);
                break;
            case R.id.seven:
                b.setMessage(info[6]);
                break;
            case R.id.eight:
                b.setMessage(info[7]);
                break;

        }

        b.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(MainActivity.this, "useful information ", Toast.LENGTH_SHORT).show();
            }
        }).show();

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        image = findViewById(R.id.imageView);
        per = findViewById(R.id.percentage);
        select = findViewById(R.id.select);
        predict = findViewById(R.id.predict);

        tv = findViewById(R.id.textView);

        select.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                Intent it = new Intent(Intent.ACTION_GET_CONTENT);
                it.setType("image/*");
                startActivityForResult(it,100);

            }
        });


        predict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bitmap = Bitmap.createScaledBitmap(bitmap,28,28, true);

                try {
                    ModelTF model = ModelTF.newInstance(getApplicationContext());

                    // Creates inputs for reference.
                    TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 28, 28, 3}, DataType.FLOAT32);

                    TensorImage tensorImage = new TensorImage(DataType.FLOAT32);
                    tensorImage.load(bitmap);
                    ByteBuffer byteBuffer = tensorImage.getBuffer();

                    inputFeature0.loadBuffer(byteBuffer);

                    // Runs model inference and gets result.
                    ModelTF.Outputs outputs = model.process(inputFeature0);
                    TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();


                    float [] conf = outputFeature0.getFloatArray();

                    int max = 0;
                    float maxConf = 0;

                    for (int i=0; i<conf.length; i++){
                        if (conf[i]> maxConf){
                            maxConf = conf[i];
                            max = i;
                        }
                    }

                    String [] classes={"kératose actinique et carcinome intraépithélial(Cancereuses)",
                            "carcinome basocellulaire (cancereuses)",
                            "lésions bénignes de type kératose (non cancéreuses)",
                            "dermatofibrome (non cancéreuses)",
                            "naevus mélanocytaires (non cancéreuses)",
                            "granulomes pyogéniques et hémorragies (Peut conduire au cancer)",
                            "mélanome ( cancéreuses)"};

                    tv.setText(classes[max]);
                    per.setText(String.format("%.2f", conf[max]*100)  + "%");



                    // Releases model resources if no longer used.
                    model.close();

                    //tv.setText(outputFeature0.getFloatArray()[0] + "\n" + outputFeature0.getFloatArray()[1]+ "\n" +outputFeature0.getFloatArray()[2] + "\n" + outputFeature0.getFloatArray()[3]+ "\n" +outputFeature0.getFloatArray()[4] + "\n" + outputFeature0.getFloatArray()[5]+ "\n" +outputFeature0.getFloatArray()[6]);
                } catch (IOException e) {
                    // TODO Handle the exception
                }

            }
        });







    }





    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100){
            image.setImageURI(data.getData());

            Uri uri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}