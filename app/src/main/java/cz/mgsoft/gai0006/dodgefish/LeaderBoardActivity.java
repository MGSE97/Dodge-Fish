package cz.mgsoft.gai0006.dodgefish;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LeaderBoardActivity extends Activity {

    LayoutInflater inflater;
    RelativeLayout head;
    RelativeLayout row;
    TableLayout tableLayout;
    int index = 0;

    FirebaseFirestore db;

    String Id;
    String Name;

    List<Map<String, Object>> First;
    Map<String, Object> UserBest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_board);

        // get user
        Bundle data = getIntent().getExtras();
        Id = data.getString("id");
        Name = data.getString("name");

        // get database data
        db = FirebaseFirestore.getInstance();

        First = new ArrayList<>();

        tableLayout = (TableLayout)findViewById(R.id.table_scoreboard);
        inflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        head = (RelativeLayout) inflater.inflate(R.layout.activity_leader_board_header,null);
        tableLayout.addView(head, index++);

        // Get first 10
        db.collection("scoreboard")
            .orderBy("score", Query.Direction.DESCENDING)
            .limit(10)
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Map<String, Object> item = document.getData();
                            Log.w("DBDATA", document.getId() + " => " + item.get("name"));
                            First.add(item);
                            row = (RelativeLayout) inflater.inflate(R.layout.activity_leader_board_row,null);
                            row.setTag(index);
                            ((TextView)row.findViewById(R.id.index)).setText(Integer.toString(index)+".");
                            ((TextView)row.findViewById(R.id.name)).setText(item.get("name").toString());
                            ((TextView)row.findViewById(R.id.score)).setText(item.get("score").toString());
                            ((TextView)row.findViewById(R.id.speed)).setText(item.get("speed").toString());
                            tableLayout.addView(row, index++);
                        }
                    } else {
                        Log.w("DB", "Error getting documents.", task.getException());
                    }

                    db.collection("scoreboard")
                            .whereEqualTo("id", Id)
                            .orderBy("score", Query.Direction.DESCENDING)
                            .limit(1)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            Map<String, Object> item = document.getData();
                                            Log.w("DBDATA", document.getId() + " => " + item.get("name"));
                                            UserBest = item;
                                            row = (RelativeLayout) inflater.inflate(R.layout.activity_leader_board_row,null);
                                            ((TextView)row.findViewById(R.id.index)).setText("You");
                                            ((TextView)row.findViewById(R.id.name)).setText(">> "+item.get("name").toString()+" <<");
                                            ((TextView)row.findViewById(R.id.score)).setText(item.get("score").toString());
                                            ((TextView)row.findViewById(R.id.speed)).setText(item.get("speed").toString());
                                            tableLayout.addView(row, index++);
                                        }
                                    } else {
                                        Log.w("DB", "Error getting documents.", task.getException());
                                    }
                                }
                            });
                }
            });
    }
}
