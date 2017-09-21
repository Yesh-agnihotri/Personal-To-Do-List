package yesh_agnihotri.android.personalto_dolist;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
{


    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private RecyclerViewAdapter mRecyclerAdapter;
    private EditText mTaskEditText;
    private DatabaseReference databaseReference;

    private List<Task> listOfTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //Array list of all tasks
        listOfTask = new ArrayList<>();
        //get the database reference of FIREBASE
        databaseReference = FirebaseDatabase.getInstance().getReference();
        //this is where you can enter text of your task
        mTaskEditText = (EditText) findViewById(R.id.add_task_box);

        //Initializing RecyclerView
        mRecyclerView = (RecyclerView) findViewById(R.id.task_list);
        //As we are showing data in Linear form we are using this
        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        //When we add text of task, and press Button "Add task" triggers this
        FloatingActionButton addTaskButton = (FloatingActionButton) findViewById(R.id.fab);

        assert addTaskButton != null;
        addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enteredTask = mTaskEditText.getText().toString();
                if(TextUtils.isEmpty(enteredTask)){
                    Toast.makeText(MainActivity.this, "You must enter a task to Add it",Toast.LENGTH_LONG).show();
                    return;
                }
                else{
                    //If task text is valid Add it in our FIREBASE DATABASE.
                    Task taskObject = new Task(enteredTask);

                    databaseReference.push().setValue(taskObject);
                    mTaskEditText.setText("");
                }

            }
        });

        //Database helper methods
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //if added , show all
                getAllTask(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                //if changed show all
                getAllTask(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                //delete the task
                taskDeletion(dataSnapshot);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    //On delete, remove the task from Firebase database - REAL TIME
    private void taskDeletion(DataSnapshot dataSnapshot) {

        for (DataSnapshot singleDataSnapshot : dataSnapshot.getChildren()) {

            String currentTask = singleDataSnapshot.getValue(String.class);
            for (int i = 0; i < listOfTask.size(); i++) {
                if (listOfTask.get(i).getTask().equals(currentTask)) {
                    listOfTask.remove(i);
                }


            }

            //After removing , send notification to RecyclerView to remove the element
            // AND re adapt it
            mRecyclerAdapter.notifyDataSetChanged();
            mRecyclerAdapter = new RecyclerViewAdapter(MainActivity.this, listOfTask);
            mRecyclerView.setAdapter(mRecyclerAdapter);

        }
    }

    private void getAllTask(DataSnapshot dataSnapshot)
    {

        //get all task when opening the app.
        for(DataSnapshot singleShot : dataSnapshot.getChildren())
        {
            String taskTitle = singleShot.getValue(String.class);
            listOfTask.add(new Task(taskTitle));
            mRecyclerAdapter = new RecyclerViewAdapter(MainActivity.this,listOfTask);
            mRecyclerView.setAdapter(mRecyclerAdapter);
        }

    }

}