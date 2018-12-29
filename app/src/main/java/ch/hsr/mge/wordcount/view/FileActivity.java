package ch.hsr.mge.wordcount.view;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import ch.hsr.mge.wordcount.R;
import ch.hsr.mge.wordcount.data.FileHolder;
import ch.hsr.mge.wordcount.data.FileProvider;
import ch.hsr.mge.wordcount.domain.WordCounterService;

public class FileActivity extends AppCompatActivity {

    public final static String DEBUG_TAG = "WordApp";
    public final static String KEY_WORD_RESULT = "WordResult";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);

        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Liste erstellen
        ListView listView = findViewById(R.id.listView);

        // Liste mit Daten fuellen
        List<FileHolder> files = FileProvider.getFiles();
        ArrayAdapter<FileHolder> adapter = new FileHolderArrayAdapter(this, files);
        listView.setAdapter(adapter);

        // Listener fuer ListView
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                fileSelected(FileProvider.getFiles().get(position));
            }
        });

    }

    private void fileSelected(FileHolder holder) {
        Toast.makeText(getApplicationContext(),
                "File selected: " + holder.filename, Toast.LENGTH_SHORT)
                .show();

        WordCounterService.startActionCountWords(this, holder);
    }

    /**
     * Adapter, um die ListView mit Daten zu bestuecken.
     *
     * @author Peter Buehler
     */
    public static class FileHolderArrayAdapter extends ArrayAdapter<FileHolder> {

        private final Context context;
        private final List<FileHolder> values;

        public FileHolderArrayAdapter(Context context, List<FileHolder> values) {
            super(context, R.layout.file_item, values);
            this.context = context;
            this.values = values;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View rowView = inflater.inflate(R.layout.file_item, parent, false);

            FileHolder fh = values.get(position);

            ((TextView) rowView.findViewById(R.id.fileNameTextView)).setText("" + fh.filename);
            ((TextView) rowView.findViewById(R.id.fileSizeTextView)).setText("" + fh.size + "kByte");

            return rowView;
        }
    }
}
