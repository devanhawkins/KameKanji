package com.hawkins.devan.kamekanji;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity {

    //Initialize word banks as arrays of Strings.
    //*CHANGE TO A VARIABLE-DECLADED SIZE!*
    //*READ FROM A FILE!*
    //*MAKE A WIDGET FOR CURRENT/LAST CARD!*
    //randomKinji(), makeFlashcard(), nopeAction() and KnowAction() have file calls.
    String[] notifInfo;
    String filename;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Load deck. Only one, default deck for now.
        //Check internal storage for save file. If one isn't found, initialize one from the assets.

        String filename_assets = "joyo_example.txt"; //Change to _internal after.
        String filename_internal = "statistics";
        File savFile = new File(getApplicationContext().getFilesDir(), filename_internal);



        String text = new String();
        text = loadFromInternalStorage(filename_internal);

        //If there is no internal data, load from txt file in assets.
        if(text.equals("")){
            Toast.makeText(MainActivity.this, "Loading for the first time (Assets)",
                    Toast.LENGTH_SHORT).show();


            //Save the txt file data to internal storage if no internal storage data.
            FileOutputStream outputStream;


            try{
                //text = loadFromTxtFile(filename_assets);
                BufferedReader br = new BufferedReader(new InputStreamReader(openFileInput(filename_assets)));
                String line;
                while((line = br.readLine()) != null){
                    text = text + line + "\n";
                }

                outputStream = openFileOutput(filename_internal, Context.MODE_PRIVATE);
                outputStream.write(text.toString().getBytes());
                outputStream.close();
            } catch(Exception e){
                e.printStackTrace();
            }


        }else{
            Toast.makeText(MainActivity.this, "Loading from internal storage",
                    Toast.LENGTH_SHORT).show();
            filename = filename_internal;
        }

        //filename = filename_assets;
          filename = filename_internal;

        //String testDeck = new String(readFromFile(this, "N3.txt"));

        //set up Notifications
        //setUp();
    }

    public void knowAction(View view, String kanji){
        //Used when User responds with the "Know" button; they
        //already recognize and understand the kanji.
        // Writes to the deck's text file, increasing the value of the weight.
        // Stat weight moves towards 5.

        //File fileToBeModified = this.getFileStreamPath(filename);
        String oldContent = "";
        String newContent = "";
        BufferedReader reader = null;
        FileWriter writer = null;
        String[] parts;
        boolean updateTheFile = false;

        try {
            //Access the file (read)
            //reader = new BufferedReader(new InputStreamReader(getAssets().open(filename)));
            reader = new BufferedReader(new InputStreamReader(openFileInput(filename)));

            //Read all lines in input file, save into 'oldContent' variable
            String line = "";
            while ((line = reader.readLine()) != null) {


                //Check if this is the right kanji character.
                parts = line.split("\\t");
                System.out.println(line);
                if (parts[1].equals(kanji)) {

                    updateTheFile = true;

                    //If the right kanji, but the weight is already capped out at max (5), keep
                    //the 'newContent' string an empty string.
                    if (parts[0].equals("5")) {
                        //Maxed out, pass the info back as is.
                        newContent = parts[0] + "\t" + parts[1] + "\t" + parts[2] + "\t" + parts[3]
                                + "\r\n";
                    } else {
                        //If not maxed out, increase weight by 1, making it LESS likely to appear
                        //Sorry for the mixing of terminology!
                        int new_tier = Integer.parseInt(parts[0]);
                        new_tier = new_tier + 1;
                        parts[0] = Integer.toString(new_tier);
                        newContent = parts[0] + "\t" + parts[1] + "\t" + parts[2] + "\t" + parts[3]
                                + "\r\n";


                    }

                }

                //Add line to old content string.

                if(updateTheFile==true){
                    oldContent = oldContent + newContent;
                }else{
                    oldContent = oldContent + line + '\n';  //Is a new line needed here?
                }

                updateTheFile = false;

            }

            //Write to the same file, using 'oldContent' string.
            FileOutputStream outStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outStream.write(oldContent.getBytes());
            outStream.close();
            //writer = new FileWriter(this.getFileStreamPath(filename));
            //writer.write(oldContent);

        }catch(IOException e) {
            e.printStackTrace();
        }finally {
            try {
                //Closing everything
                reader.close();
                //writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch(NullPointerException n){
                n.printStackTrace();
            }
        }
    }

    public void nopeAction(View view, String kanji){

        //Used when User responds with the "Nope" button; they
        //don't recognize or understand the kanji.
        // Writes to the deck's text file, decreasing the value of the weight.
        // Stat weight moves towards 1.

        //File fileToBeModified = this.getFileStreamPath(filename);
        String oldContent = "";
        String newContent = "";
        BufferedReader reader = null;
        FileWriter writer = null;
        String[] parts;
        boolean updateTheFile = false;

        try {
            //Access the file (read)
            //reader = new BufferedReader(new InputStreamReader(getAssets().open(filename)));
            reader = new BufferedReader(new InputStreamReader(openFileInput(filename)));

            //Read all lines in input file, save into 'oldContent' variable
            String line = "";
            while ((line = reader.readLine()) != null) {


                //Check if this is the right kanji character.
                parts = line.split("\\t");
                System.out.println(line);
                if (parts[1].equals(kanji)) {

                    updateTheFile = true;

                    //If the right kanji, but the weight is already capped out at max (1), keep
                    //the 'newContent' string an empty string.
                    if (parts[0].equals("1")) {
                        //Maxed out, pass the info back as is.
                        newContent = parts[0] + "\t" + parts[1] + "\t" + parts[2] + "\t" + parts[3]
                                + "\r\n";
                    } else {
                        //If not maxed out, increase weight by 1, making it LESS likely to appear
                        //Sorry for the mixing of terminology!
                        int new_tier = Integer.parseInt(parts[0]);
                        new_tier = new_tier - 1;
                        parts[0] = Integer.toString(new_tier);
                        newContent = parts[0] + "\t" + parts[1] + "\t" + parts[2] + "\t" + parts[3]
                                + "\r\n";

                    }

                }

                //Add line to old content string.

                if(updateTheFile==true){
                    oldContent = oldContent + newContent;
                }else{
                    oldContent = oldContent + line + '\n';  //Is a new line needed here?
                }

                updateTheFile = false;

            }

            //Write to the same file, using 'oldContent' string.
            FileOutputStream outStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outStream.write(oldContent.getBytes());
            outStream.close();
            //writer = new FileWriter(this.getFileStreamPath(filename));
            //writer.write(oldContent);

        }catch(IOException e) {
            e.printStackTrace();
        }finally {
            try {
                //Closing everything
                reader.close();
                //writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch(NullPointerException n){
                n.printStackTrace();
            }
        }
    }

    /** Creates and sends out the notification **/
    public void setUp(){
        //Create Notification Channel

        //Channel ID
        String id = "main_channel_01";

        /** Create a notification **/

        //The notification properties
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, id);
        mBuilder.setSmallIcon(R.drawable.kamekanji_icon);


        //Build the flash card, containing kanji, tier, etc.
        notifInfo = makeFlashcard();

        //Set the kanji on the MainActivity
        setContentView(R.layout.activity_main);
        TextView textView = (TextView) findViewById(R.id.textView2);
        textView.setText(notifInfo[0]);

        //Create the action button intent
        Intent intentAction = new Intent(this, MyBroadcastReceiver.class);
        Intent intentAction2 = new Intent(this, MyBroadcastReceiver.class);
        intentAction.putExtra("answer","know");
        intentAction.putExtra("answer", "nope");
        PendingIntent pIntentlogin = PendingIntent.getBroadcast(this.getBaseContext(), 0, intentAction,PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pIntentlogin2 = PendingIntent.getBroadcast(this.getBaseContext(), 1, intentAction2,PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentTitle(notifInfo[0]);
        mBuilder.setContentText(notifInfo[1]);
        mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(notifInfo[1] + "\n\n\n" + notifInfo[2] + "\n" + notifInfo[3]));
        mBuilder.setPriority(-1); //Set as low priority to stop auto-expending and showing answer.
        mBuilder.setAutoCancel(true);
        mBuilder.addAction(R.drawable.kamekanji_icon, getString(R.string.new_know), pIntentlogin);
        mBuilder.addAction(R.drawable.kamekanji_icon, getString(R.string.new_nope), pIntentlogin2);

        mBuilder.build();

        //Create an intent for the notification.
        Intent notifIntent = new Intent(this, MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntent(notifIntent);
        //PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pIntentlogin);
        mBuilder.setContentIntent(pIntentlogin2);

        NotificationManager mNotifMan = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotifMan.notify(mBuilder.mNumber ,mBuilder.build()); //This makes the notification appear.
    }

    /** Toggle on and off */
    public void beginLearning(View view) {
        // Check if turning on or off.

        TextView tv = (TextView)findViewById(R.id.button_id);
        Button b = (Button) view;
        String buttonText = b.getText().toString();
        if (tv.getText().equals(getString(R.string.app_off))) {
            //TURN ON
            //Currently sends a new card every time it's turned on.
            //Should allow app to run in background sending notifications.
            tv.setText(R.string.app_on);
            setUp();
        } else {
            //TURN OFF
            //Currently does nothing. Should pause notifications.
            tv.setText(R.string.app_off);
        }

    }

    public void newCard(View view){
        //Pumps out a new card to practice at will.
        setUp();
    }

    public void newCardKnow(View view){
        knowAction(view, notifInfo[0]);
    }

    public void newCardNope(View view){
        nopeAction(view, notifInfo[0]);
    }

    /** Make a flashcard to be turned into a notification**/
    public String[] makeFlashcard() {
        /** Use weighted tiers to choose a tier, then a random kanji,
         * then make a card. **/

        //The card to be sent.
        String[] card = new String[5];
        int total_kanji = 0;
        int[] tiers = new int[5];

        try{
            AssetManager assetManager = getBaseContext().getAssets();
            //InputStream in = assetManager.open(filename);
            InputStream in = openFileInput(filename);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            //LineNumberReader lnr = new LineNumberReader(br);
            String line;

            while((line = br.readLine()) != null){
                String[] parts = line.split("\\t");
                int next_int = Integer.parseInt(parts[0]);
                switch (next_int){
                    case 1: tiers[0]++;
                    break;
                    case 2: tiers[1]++;
                    break;
                    case 3: tiers[2]++;
                    break;
                    case 4: tiers[3]++;
                    break;
                    case 5: tiers[4]++;
                    break;
                }
                total_kanji++;

            }

            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String chosen_kanji = "";
        int tier = 0;

        tier = randomTier(total_kanji, tiers);
        chosen_kanji = randomKanji(tier, tiers[tier - 1]);
        //Parts anatomy:
        //TIER - KANJI - MEANING - READINGS
        String[] parts = chosen_kanji.split("\\t");

        //Fill out card info in following format:
        //0: kanji, 1: flavour text, 2: reading, 3: meaning, 4:tier
        card[0] = parts[1];
        switch (tier){
            case 1:
                card[1] = getResources().getString(R.string.tier1);
                break;
            case 2:
                card[1] = getResources().getString(R.string.tier2);
                break;
            case 3:
                card[1] = getResources().getString(R.string.tier3);
                break;
            case 4:
                card[1] = getResources().getString(R.string.tier4);
                break;
            case 5:
                card[1] = getResources().getString(R.string.tier5);
                break;
        }
        card[2] = parts[3];
        card[3] = parts[2];
        card[4] = Integer.toString(tier);

        return card;

    }

    public int randomTier(int total, int[] tiers){
        //Choosing the tier.
        //Eq: {tier = (kanji-in-tier / total-kanji) + tier bonus}
        int tier = 0;
        int[] weighted_tiers = new int[5];
        weighted_tiers[0] = tiers[0] * 110; //10% more likely
        weighted_tiers[1] = tiers[1] * 105; //5% more likely
        weighted_tiers[2] = tiers[2] * 100; //Even chance
        weighted_tiers[3] = tiers[3] * 95; //5% less likely
        weighted_tiers[4] = tiers[4] * 90; //10% less likely
        int weighted_total = weighted_tiers[0] + weighted_tiers[1] +
                weighted_tiers[2] + weighted_tiers[3] + weighted_tiers[4];

        //Pick a number out of the weighted total.
        int ans = (int) ((Math.random() * weighted_total) + 1);

        //Find which tier this number belongs to.
        if(ans < weighted_tiers[0]){
            tier = 1;
        }else if(ans < (weighted_tiers[0] + weighted_tiers[1])){
            tier = 2;
        }else if(ans < (weighted_tiers[0] + weighted_tiers[1]
                + weighted_tiers[2])){
            tier = 3;
        }else if(ans < (weighted_tiers[0] + weighted_tiers[1]
                + weighted_tiers[2] + weighted_tiers[3])){
            tier = 4;
        }else{
            tier = 5;
        }

        return tier;
    }

    public String randomKanji(int tier, int numOfKanji){
        //Select the kanji using the tier and number-of-kanji-in-said-tier info

        int i = (int) (Math.random() * numOfKanji);
        String ans = "";

        //Search through dictionary to the i-th element of the tier.
        try {
            AssetManager assetManager = getBaseContext().getAssets();
            //InputStream in = assetManager.open(filename);
            InputStream in = openFileInput(filename);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            //LineNumberReader lnr = new LineNumberReader(br);
            String line;
            int count = 0;

            while((line = br.readLine()) != null){
                String[] parts = line.split("\\t");
                int next_int = Integer.parseInt(parts[0]);

                if (next_int == tier){

                    if(count == i){ //Found the answer.
                        ans = line;
                        count++;
                        break;
                    }
                    count++;
                }
            }
            br.close();
        } catch (IOException e) {
        e.printStackTrace();
    }
        return ans;
    }

    public String loadFromInternalStorage(String txtfile){

        //Load all info from file as a single String and return it.
        String allData = new String();

        try{
            File file = new File(getApplicationContext().getFilesDir(), txtfile);
            allData = loadGeneral(file);
        }catch (IOError e){

        }
        return allData;
    }

    public String loadFromTxtFile(String txtfile){

        //Load all of the info from the file as a single String and return it.
        String allData = new String();

        try{
            File file = this.getFileStreamPath(txtfile);
            allData = loadGeneral(file);
        }catch(IOError e){

        }

        return allData;
    }

    public String loadGeneral(File file){
        String text = "";

        try{
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null){
                text += line;
            }
            br.close();
        } catch (IOException e){

        }
        return text;
    }

}

