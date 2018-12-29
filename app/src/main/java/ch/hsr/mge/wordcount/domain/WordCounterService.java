package ch.hsr.mge.wordcount.domain;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
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

import static ch.hsr.mge.wordcount.view.FileActivity.KEY_WORD_RESULT;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class WordCounterService extends IntentService {
    public static final String ACTION_COUNT_WORDS = "ch.hsr.mge.wordcount.domain.action.COUNT_WORDS";
    private static final String RESULT_COUNT_WORDS = "ch.hsr.mge.wordcount.domain.result.COUNT_WORDS";

    public static final String EXTRA_HOLDER = "ch.hsr.mge.wordcount.domain.extra.HOLDER";
    public static final String EXTRA_RESULT = "ch.hsr.mge.wordcount.domain.extra.RESULT";

    public final static String DEBUG_TAG = "WordApp";

    public WordCounterService() {
        super("WordCounterService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionCountWords(Context context, FileHolder holder) {
        Intent intent = new Intent(context, WordCounterService.class)
                .setAction(ACTION_COUNT_WORDS)
                .putExtra(EXTRA_HOLDER, holder);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_COUNT_WORDS.equals(action)) {
                FileHolder holder = (FileHolder) intent.getSerializableExtra(EXTRA_HOLDER);
                handleActionCountWords(holder);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionCountWords(FileHolder holder) {
        String text = loadFile(holder.id);
        List<WordCount> counters = analyzeText(text);
        WordCountResult result = new WordCountResult(holder, counters);

        Intent intent = new Intent();
        intent.setAction(RESULT_COUNT_WORDS);
        intent.putExtra(EXTRA_RESULT, result);
//        sendBroadcast(intent);
        Intent showResultIntent = new Intent(this, WordListActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_WORD_RESULT, intent.getSerializableExtra(EXTRA_RESULT));
        showResultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        showResultIntent.putExtras(bundle);

        startActivity(showResultIntent);
    }
    /**
     * Laedt die Datei und liefert den Inhalt als String.
     */
    private String loadFile(int id) {
        InputStream in = getResources().openRawResource(id);
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
