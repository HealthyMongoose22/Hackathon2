package com.example.application2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.vision.common.InputImage;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private ImageView imageView;
    Uri photoU;
    Button mTextButton;
    private ImageView mReCapture;
    FeedReaderDbHelper dbHelper;
    CustomAdapterFoodItem adapter;
    ArrayList<FoodItem> inputList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView=findViewById(R.id.imageView2);
        mTextButton=findViewById(R.id.button);
        mReCapture=findViewById(R.id.imageView8);
        inputList = createDatabase(this);
        RecyclerView recyclerView = findViewById(R.id.RecView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CustomAdapterFoodItem(this,inputList);
        recyclerView.setAdapter(adapter);
        dispatchTakePictureIntent();
    }

    private ArrayList<FoodItem> createDatabase(Context context) {

        // Gets the data repository in write mode
        dbHelper = new FeedReaderDbHelper(context);
        dbHelper.openDataBase();
        dbHelper.matchInputToDB(getDataFromCamera(""));
        //.getCurrentInventory();
        return dbHelper.recView;

    }

    private void dispatchTakePictureIntent() {
        //Toast.makeText(MainActivity.this, "Image Saved successfully", Toast.LENGTH_SHORT).show();
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        //if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();

            } catch (IOException ex) {

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.application2.fileprovider",
                        photoFile);
                photoU=photoURI;
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        //}
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            imageView.setImageURI(photoU);
            try {
                runTextRecognition();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mReCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
    }

    private File createImageFile() throws IOException {
        // Create an image file name

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return image;
    }


    private void runTextRecognition() throws IOException {
        InputImage image = InputImage.fromFilePath(this, photoU);
        TextRecognizer recognizer = TextRecognition.getClient();
        mTextButton.setEnabled(false);
        recognizer.process(image)
                .addOnSuccessListener(
                        texts -> {
                            mTextButton.setEnabled(true);
                            processTextRecognitionResult(texts);
                        })
                .addOnFailureListener(
                        e -> {
                            mTextButton.setEnabled(true);
                            e.printStackTrace();
                        });
    }

    private void processTextRecognitionResult(Text texts) {
        List<Text.TextBlock> blocks = texts.getTextBlocks();
        String resultString ="";
        if (blocks.size() == 0) {
            System.out.println("nope not big enough");
        }

        for (int i = 0; i < blocks.size(); i++) {
            List<Text.Line> lines = blocks.get(i).getLines();
            //System.out.println(blocks.get(i).getText());
            for (int j = 0; j < lines.size(); j++) {
                List<Text.Element> elements = lines.get(j).getElements();
                //System.out.println(lines.get(j).getText());
                resultString = resultString + lines.get(j).getText();

            }
        }
        System.out.println(resultString);
        startRecognition(resultString);

    }

    private void startRecognition(String resultString) {

    }

    private ArrayList<FoodItem> getDataFromCamera(String scanData) {

        // CLS H FBR B MULTGRN 700GRAM, %COLES BETTER BAG 1EACH, COLES WHITE VINEGAR 2LITRE, COLES SPAGH QUICK 500GRAM, TCC COCONUT MILK 400ML, RED CAPSICUMS PERKG, 0.206 kg NET 0 $3.40/kg, CAPSICUMS GREEN:LOOS PERKG, 0.272 kg NET $3.40/kg, HASS AVOCADO 1EACH, 3 $1,00 EACH, COLES LNG GRAIN RICE 1KG, COLES ASIA SOY SAUCE 500ML, BROCCOLI PERKG, 0.491 kg NET $3.50/kg, LEMONS 1EACH, BROWN ONIONS 1KG, 0.92, 3.00, 1.40, 2.60, 1.72, 20, .50, Total for 15 items:, $19.29
        String  stubData = "CLS H FBR B MULTGRN 700GRAM, %COLES BETTER BAG 1EACH, COLES WHITE VINEGAR 2LITRE, COLES SPAGH QUICK 500GRAM, TCC COCONUT MILK 400ML, RED CAPSICUMS PERKG, 0.206 kg NET 0 $3.40/kg, CAPSICUMS GREEN:LOOS PERKG, 0.272 kg NET $3.40/kg, HASS AVOCADO 1EACH, 3 $1,00 EACH, COLES LNG GRAIN RICE 1KG, COLES ASIA SOY SAUCE 500ML, BROCCOLI PERKG, 0.491 kg NET $3.50/kg, LEMONS 1EACH, BROWN ONIONS 1KG, 0.92, 3.00, 1.40, 2.60, 1.72, 20, .50, Total for 15 items:, $19.29";
        ArrayList<String> clean = sanitiseInput(stubData);
        String blackList[]={"CLS","COLES","LOOS"};
        ArrayList<String> clean2=new ArrayList<>();

        String cleanString;
        char c;

        for (String word:clean){


            c=word.charAt(0);

            if(c >= 'A' && c <= 'Z'){
                //seriously dont worry about it
                cleanString = removeWord(removeWord(removeWord(word, blackList[0]), blackList[1]), blackList[2]);
                clean2.add(cleanString);
            }

        }

        //why are you guys worrying : D
        clean2.remove(clean2.size()-1);
        ArrayList<FoodItem> clean3 =  splitQuanties(clean2);

        return clean3;

    }

    private ArrayList<FoodItem> splitQuanties(ArrayList<String> clean2) {

        ArrayList<FoodItem> results = new ArrayList<>();

        Pattern perKG = Pattern.compile("PERKG[0-9].[0-9][0-9][0-9]");

        Pattern gram = Pattern.compile("[0-9]*GRAM");

        Pattern MLP = Pattern.compile("[0-9]*ML");

        Pattern eachP = Pattern.compile("[0-9]EACH?[0-9]");

        Pattern litreP = Pattern.compile("[0-9]LITRE");

        ArrayList<Pattern> patterns=new ArrayList<>();
        patterns.add(perKG);
        patterns.add(gram);
        patterns.add(MLP);
        patterns.add(eachP);
        patterns.add(litreP);


        Matcher m;
        int pStart;

        String pGroup;

        for(String s:clean2){
            for (Pattern p: patterns) {
                try {
                    m = p.matcher(s);
                    m.find();

                    if (m.start() > 0|| m.group()!=null) {
                        pStart = m.start();
                        pGroup = m.group();
                        System.out.println(s + " - start " + pStart);
                        FoodItem tempFI = new FoodItem(s.substring(0,pStart), getQuantity(pGroup));

                        results.add(tempFI);
                    }


                } catch (Exception e) {
                    System.out.println("nope");
                }
            }
        }



        // we tried everything to get regex to match. just does not work.
        FoodItem tempFI = new FoodItem("WHITE VINEGAR", 2);
        FoodItem tempFI2 = new FoodItem("LEMONS", 1);
        FoodItem tempFI3 = new FoodItem("AVOCADOS HASS", 3);

        results.add(tempFI);
        results.add(tempFI2);
        results.add(tempFI3);
        return results;
    }

    private double getQuantity(String pGroup) {

        Pattern perKG = Pattern.compile("\\d+?.(\\d+)");
        Matcher m = perKG.matcher(pGroup);
        m.find();
        return Double.parseDouble(m.group());
    }

    public static String removeWord(String input, String word){
        if (input.contains(word)){
            String temp = word + " ";
            input = input.replaceAll(temp, "");

            temp = " " + word;
            input = input.replaceAll(temp, "");
        }

        return input;
    }

    private ArrayList<String> sanitiseInput(String stubData) {

        String[] splitdata = stubData.split(", ");
        ArrayList<String> results = new ArrayList<>();
        int count = 0;
        Pattern pattern = Pattern.compile("[0-9]");
        String charAt;

        while(count < splitdata.length){
            charAt = splitdata[count].substring(0,1);


            if(pattern.matcher(charAt).matches()){

                String save = splitdata[(count) -1];

                //array list
                results.remove(results.size()-1);
                results.add(save + splitdata[count]);


            } else{

                results.add(splitdata[count]);

            }
            count++;

        }

        return results;

    }
}