package com.example.application2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.intuit.fuzzymatcher.component.MatchService;
import com.intuit.fuzzymatcher.domain.Document;
import com.intuit.fuzzymatcher.domain.Element;
import com.intuit.fuzzymatcher.domain.Match;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.intuit.fuzzymatcher.domain.ElementType.NAME;

class FeedReaderDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "ffood.sqlite";
    private static final String DB_PATH_SUFFIX = "/databases/";
    static Context ctx;
    public SQLiteDatabase myDataBase;
    public ArrayList<FoodItem>  recView;

    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + FeedReaderContract.FeedEntry.TABLE_NAME;


    public FeedReaderDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        ctx = context;

    }

    @Override
    public void onCreate(SQLiteDatabase db) {


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);

    }

    private static String getDatabasePath() {
        return ctx.getApplicationInfo().dataDir + DB_PATH_SUFFIX
                + DATABASE_NAME;
    }


    @SuppressLint("NewApi")
    public void matchInputToDB(ArrayList<FoodItem> foodItems) {

        Cursor cursor = myDataBase.rawQuery("SELECT * FROM fridgly", null);
        recView=new ArrayList<>();

        int len= 53;
        String[][] databaseStr=new String[len][6];
        int i =0;

        while (cursor.moveToNext()) {
            databaseStr[i][0]=Integer.toString(i);
            databaseStr[i][1]=cursor.getString(3);
            databaseStr[i][2]=cursor.getString(0);
            databaseStr[i][3]=cursor.getString(1);
            databaseStr[i][4]=cursor.getString(2);
            databaseStr[i][5]=cursor.getString(4);
            i++;
        }
        cursor.close();
        i=0;
        String[][] input = new String[foodItems.size()][2];

        for (FoodItem f:foodItems){
            input[i][0]=Integer.toString(i);
            input[i][1]=f.getShortDesc();
            i++;
        }

        // input from


        List<Document> documentList = new ArrayList<>();
        for (String[] contact : Arrays.asList(input)) {
            Object document = new Document.Builder(contact[0])
                    .addElement(new Element.Builder<String>().setValue(contact[1]).setType(NAME).setThreshold(0.2).createElement())
                    .createDocument();
            documentList.add((Document) document);
        }

        List<Document> matchWith = new ArrayList<>();
        for (String[] contact2 : Arrays.asList(databaseStr)) {
            Object document = new Document.Builder(contact2[0])
                    .addElement(new Element.Builder<String>().setValue(contact2[1]).setThreshold(0.2).setType(NAME).createElement())
                    .createDocument();
            matchWith.add((Document) document);
        }

        //Match a list of Documents with an Existing List: This is useful for matching a new list of documents with an existing list in your system. For example, if you're performing a bulk import and want to find out if any of them match with existing data
        // matchService.applyMatchByDocId(List<Document> documents, List<Document> matchWith)

        MatchService matchService = new MatchService();
        Map<Document, List<Match<Document>>> result = matchService.applyMatch(documentList,matchWith);
        ArrayList<Match> bestMatches= new ArrayList<>();


        result.entrySet().forEach(entry -> {

            Match maxMatch = entry.getValue().get(0);

            for (Match match : entry.getValue()){

                if (match.getScore().getResult() > maxMatch.getScore().getResult()) {

                    maxMatch=match;

                }

            }
            bestMatches.add(maxMatch);

        });

        int count=0;

        for(Match m:bestMatches){

            System.out.println(m.getData().toString().substring(4,m.getData().toString().length()-4) +"|||" + foodItems.get(count).getShortDesc());
            if(m.getData().toString().substring(4,m.getData().toString().length()-4).equals(foodItems.get(count).getShortDesc())){

                for(int j=0;j<len;j++){
                    System.out.println(databaseStr[j][1] + " | " + m.getMatchedWith().toString().substring(4,m.getMatchedWith().toString().length()-4));
                    if(databaseStr[j][1].equals(m.getMatchedWith().toString().substring(4,m.getMatchedWith().toString().length()-4))){
                        foodItems.get(count).setInv_ID(databaseStr[j][4]);
                        foodItems.get(count).setNBD_No(Integer.parseInt(databaseStr[j][2]));
                        foodItems.get(count).setDisplayName(databaseStr[j][1]);
                        foodItems.get(count).setUnits(databaseStr[j][5]);

                        recView.add(foodItems.get(count));

                    }

                }
                count++;
            }


        }


    }

    private void addToDB(FoodItem fi) {

        myDataBase.execSQL("insert into InvDB (Inv_ID,NDB_No,DisplayName,ShortDesc,amount,Unit) " +
                "values (" + fi.getInv_ID() + ","+ fi.getNBD_No() + ","+fi.getDisplayName()+","+fi.getShortDesc()+fi.getAmount()+"," +
                fi.getUnits());

    }

    public ArrayList<FoodItem> getCurrentInventory(){
        ArrayList<FoodItem> foodItems = new ArrayList<>();
        Cursor cursor =  myDataBase.rawQuery("Select * from InvDB" ,null);
        FoodItem tempFI;
        if (cursor!=null) {
            while (cursor.moveToNext()) {
                System.out.println(cursor.getString(2));
            }
        }
        return null;

    }
    //.import C:/Users/61415/Desktop/FOODSTUB.csv foodstub

    public SQLiteDatabase openDataBase() throws SQLException {
        File dbFile = ctx.getDatabasePath(DATABASE_NAME);

        if (!dbFile.exists()) {
            try {
                CopyDataBaseFromAsset();
                System.out.println("Copying success from Assets folder");
            } catch (IOException e) {
                throw new RuntimeException("Error creating source database", e);
            }
        }
        myDataBase = this.getReadableDatabase();
        return SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.CREATE_IF_NECESSARY);
    }

    public void CopyDataBaseFromAsset() throws IOException {
        InputStream myInput = ctx.getAssets().open(DATABASE_NAME);
        // Path to the just created empty db
        String outFileName = getDatabasePath();

        // if the path doesn't exist first, create it
        File f = new File(ctx.getApplicationInfo().dataDir + DB_PATH_SUFFIX);
        if (!f.exists())
            f.mkdir();

        // Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        // transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }
        // Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }


}
