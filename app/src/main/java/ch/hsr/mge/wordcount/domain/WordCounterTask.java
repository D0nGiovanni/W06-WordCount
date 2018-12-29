package ch.hsr.mge.wordcount.domain;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import ch.hsr.mge.wordcount.data.FileHolder;
import ch.hsr.mge.wordcount.data.WordCount;
import ch.hsr.mge.wordcount.data.WordCountResult;
import ch.hsr.mge.wordcount.view.WordListActivity;

public class WordCounterTask extends AsyncTask<Void, Void, WordCountResult> {

    public final static String DEBUG_TAG = "WordApp";
    public final static String KEY_WORD_RESULT = "WordResult";
    private Context context;
    private FileHolder holder;

    public WordCounterTask(Context context, FileHolder holder) {
        this.context = context;
        this.holder = holder;
    }

    @Override
    protected WordCountResult doInBackground(Void... nothing) {
        String text = loadFile(holder.id);
        List<WordCount> counters = analyzeText(text);
        return new WordCountResult(holder, counters);
    }

    @Override
    protected void onPostExecute(WordCountResult result) {
        Intent showResultIntent = new Intent(context, WordListActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_WORD_RESULT, result);
        showResultIntent.putExtras(bundle);
        context.startActivity(showResultIntent);
    }

    /**
     * Laedt die Datei und liefert den Inhalt als String.
     */
    private String loadFile(int id) {
        InputStream in = context.getResources().openRawResource(id);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String readLine;
        StringBuilder out = new StringBuilder();

        try {
            while ((readLine = br.readLine()) != null) {
                out.append(readLine);
            }
            in.close();
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String text = out.toString();

        Log.d(DEBUG_TAG, "File loaded size=" + text.length());

        return text;
    }

    /**
     * Trennt den Text und zaehlt die Anzahl Worte.
     *
     * @param text
     */
    private List<WordCount> analyzeText(String text) {
        List<WordCount> result = new WordCounter().countWords(text);
        Log.d(DEBUG_TAG, "File analyzed");
        return result;
    }

}
