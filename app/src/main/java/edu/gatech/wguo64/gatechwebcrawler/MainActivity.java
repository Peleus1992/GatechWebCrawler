package edu.gatech.wguo64.gatechwebcrawler;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.LinkedList;

import edu.gatech.wguo64.gatechwebcrawler.database.MyDB;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public final static String TAG = MainActivity.class.getName();
    EditText seedEdit;
    EditText filterEdit;
    EditText limitEdit;
    EditText displayEdit;
    Button startBtn;
    Button stopBtn;
    TextView countTxt;
    TextView timeTxt;

    boolean stop = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inflateViews();
        setActions();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.startBtn:
                if(checkForm()) {
                    new CrawlerTask().execute(seedEdit.getText().toString(),
                            filterEdit.getText().toString(),
                            limitEdit.getText().toString());
                }
                break;
            case R.id.stopBtn:
                stop = true;
                break;
        }
    }

    private void inflateViews() {
        seedEdit = (EditText)findViewById(R.id.seedEdit);
        filterEdit = (EditText)findViewById(R.id.filterEdit);
        limitEdit = (EditText)findViewById(R.id.limitEdit);
        displayEdit = (EditText)findViewById(R.id.displayEdit);
        startBtn = (Button)findViewById(R.id.startBtn);
        stopBtn = (Button)findViewById(R.id.stopBtn);
        countTxt = (TextView)findViewById(R.id.countTxt);
        timeTxt = (TextView)findViewById(R.id.timeTxt);
    }

    private void setActions() {
        startBtn.setOnClickListener(this);
        stopBtn.setOnClickListener(this);
    }

    private boolean checkForm() {
        String seed = seedEdit.getText().toString();
        if(!seed.startsWith("http://") && !seed.startsWith("https://")) {
            Toast.makeText(this, "Please enter correct seed URL.", Toast.LENGTH_SHORT).show();
            return false;
        }
        int limit = 0;
        try {
            limit = Integer.parseInt(limitEdit.getText().toString());
            if(limit > 2000) {
                Toast.makeText(this, "Currently, we do not support limit greater than 2000.", Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter an Integer as limit.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private class CrawlerTask extends AsyncTask<String, Void, Void> {
        long startTime;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            displayEdit.setText("");
            timeTxt.setText("");
            startTime = System.currentTimeMillis();
        }

        @Override
        protected Void doInBackground(String... params) {

            String seedUrl = params[0],
                    filter = params[1];
            int limit = Integer.parseInt(params[2]);

            //Initialize and open database
            MyDB db = new MyDB(MainActivity.this);
            db.open();
            //a queue used to do BFS
            LinkedList<String> queue = new LinkedList<>();
            //clear the database and begin new crawling
            db.clear();
            db.insertURL(seedUrl);
            queue.add(seedUrl);
            int count = 0;
            stop = false;
            Log.d(TAG, "seedUrl: " + seedUrl + ";"
            + "filter: " + filter + ";"
            + "limit: " + limit + ";");
            while(!queue.isEmpty() && count <= limit && !stop) {
                String url = queue.poll();
                //get useful information
                Log.d(TAG, "begin:" + url);
                Document doc = null;
                try {
                    doc = Jsoup.connect(url).get();
                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }
                Log.d(TAG, "end:" + url);
                //get all links and do BFS crawling
                Elements questions = doc.select("a[href]");
                    for(Element link: questions){
                    String newUrl = null;
                    //check if the given URL contains href and is already in database
                    if(count < limit && link.attr("href").contains(filter)
                            && !db.checkExists(newUrl = link.attr("abs:href"))) {
                        //store the URL into database to avoid parsing again
                        db.insertURL(newUrl);
                        queue.add(newUrl);
                        Log.d(TAG, newUrl);
                        final String text = newUrl;
                        final int num = count;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                displayEdit.append(num + ".   " + text + "\n");
                                countTxt.setText("Count: " + num);
                            }
                        });
                        count++;
                    }
                }
            }

            // close database
            db.close();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            long diff = System.currentTimeMillis() - startTime;
            timeTxt.setText("Time: " + diff + " ms");
        }
    }

}
