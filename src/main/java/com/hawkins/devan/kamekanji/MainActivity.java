package com.hawkins.devan.kamekanji;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Create default deck if no other decks
        //String testDeck = new String(readFromFile(this, "N3.txt"));

        //set up Notifications
        //setUp();
    }


    private void knowAction(View view, String kanji){
        //Read file to search for kanji



        try{
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(getApplicationContext().openFileOutput("joyo_example.txt", Context.MODE_PRIVATE));
            //File file = this.getFileStreamPath("joyo_example.txt");
            //FileWriter fw = new FileWriter(file);

            AssetManager assetManager = getBaseContext().getAssets();
            InputStream in = assetManager.open("joyo_example.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            String temp_txt = "";
            String[] parts;

            while ((line = br.readLine()) != null){

                parts = line.split("\\t");
                if(parts[1].equals(kanji)){
                    //If already max (5), then finish
                    if(parts[0].equals("5")){
                        //Do nothing
                    }
                    //When we find the right line, increase the tier
                    int new_tier = Integer.parseInt(parts[0]);
                    new_tier++;
                    parts[0] = Integer.toString(new_tier);
                    String newline = parts[0] + "\t" + parts[1] + "\t" + parts[2] + "\t" + parts[3]
                            + "\r\n";
                    temp_txt += newline;
                    outputStreamWriter.write(temp_txt);
                    //test
                    Context context = getApplicationContext();
                    String txt = newline;
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, txt, duration);
                    toast.show();
                }else{
                    //copy as is.
                    temp_txt += line + "\r\n";
                    outputStreamWriter.write(temp_txt);
                }
            }
            //outputStreamWriter.write(temp_txt);

            outputStreamWriter.close();
            in.close();
            br.close();


        }catch(FileNotFoundException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    private  void nopeAction(View view){

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

        notifInfo = makeFlashcard();
        mBuilder.setContentTitle(notifInfo[0]);
        mBuilder.setContentText(notifInfo[1]);
        mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(notifInfo[1] + "\n\n\n" + notifInfo[2] + "\n" + notifInfo[3]));
        mBuilder.setPriority(-1); //Set as low priority to stop auto-expending and showing answer.
        mBuilder.build();

        //Create an intent for the notification.
        Intent notifIntent = new Intent(this, MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntent(notifIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

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
        knowAction(view, notifInfo[0]);
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
            InputStream in = assetManager.open("joyo_example.txt");
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
            InputStream in = assetManager.open("joyo_example.txt");
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


}
