package fileexplorer;

import java.io.File;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import si.krivec.tracker.R;

import android.os.Bundle;
import android.os.Environment;
import android.app.ListActivity;
import android.content.Intent;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

public class FileChooser extends ListActivity {

    private File currentDir;
    private FileArrayAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentDir = Environment.getExternalStorageDirectory(); // /sdcard/

        if(getIntent().getExtras().getString("currentDir") != null)
            currentDir = new File(getIntent().getExtras().getString("currentDir"));

        fill(currentDir);
    }

    private void fill(File f)
    {
        File[] dirs = f.listFiles();
        this.setTitle(getResources().getString(R.string.current_directory) + ": " + f.getName());

        List<Item>dir = new ArrayList<>();
        List<Item>fls = new ArrayList<>();
        try{
            for(File ff: dirs)
            {
                Date lastModDate = new Date(ff.lastModified());
                String date_modify_formatted = new SimpleDateFormat("d.M.yyyy HH:mm:ss").format(lastModDate);

                if(ff.isDirectory()){
                    File[] fbuf = ff.listFiles();
                    int buf;

                    if(fbuf != null){
                        buf = fbuf.length;
                    }
                    else buf = 0;

                    String num_item = String.valueOf(buf) + " " + getResources().getString(R.string.files);

                    dir.add(new Item(ff.getName(),num_item,date_modify_formatted,ff.getAbsolutePath(),"directory_icon"));
                }
                else
                {
                    fls.add(new Item(ff.getName(),ff.length() + " Byte", date_modify_formatted, ff.getAbsolutePath(),"file_icon"));
                }
            }
        }catch(Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }

        Collections.sort(dir);
        Collections.sort(fls);
        dir.addAll(fls);

        if( f.getName().length() > 0 && !f.getName().equalsIgnoreCase("storage") )
            dir.add(0,new Item("..",getResources().getString(R.string.parent_directory),"",f.getParent(),"directory_up"));
        adapter = new FileArrayAdapter(FileChooser.this,R.layout.file_view,dir);
        this.setListAdapter(adapter);
    }
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Item o = adapter.getItem(position);

        if(o.getImage().equalsIgnoreCase("directory_icon") || o.getImage().equalsIgnoreCase("directory_up")){
            currentDir = new File(o.getPath());
            fill(currentDir);
        }
        else
        {
            onFileClick(o);
        }
    }
    private void onFileClick(Item o)
    {
        //Toast.makeText(this, "Folder Clicked: "+ currentDir, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent();
        intent.putExtra("GetPath",currentDir.toString());
        intent.putExtra("GetFileName",o.getName());
        setResult(RESULT_OK, intent);
        finish();
    }
}
