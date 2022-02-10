package edu.washu.arc.sampleapp;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import edu.wustl.arc.core.ArcAssessmentActivity;
import edu.wustl.arc.navigation.NavigationManager;
import edu.wustl.arc.study.StateMachine;
import edu.wustl.arc.study.Study;
import edu.wustl.arc.study.TestSession;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ArcAssessmentActivity
        implements SampleAppStateMachine.AssessmentCompleteListener {

    RecyclerView recyclerView;
    MyRecyclerViewAdapter adapter;
    FrameLayout contentFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if(getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Important: needed for the NavigationManager to manage showing Arc Assessments
        NavigationManager.initialize(getSupportFragmentManager());

        List<String> rows = new ArrayList<>();
        for (CellRows row : CellRows.values()) {
            rows.add(row.stringValue);
        }

        contentFrame = findViewById(R.id.content_frame);

        // set up the RecyclerView
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyRecyclerViewAdapter(this, rows);
        adapter.setClickListener(this::onItemClick);
        recyclerView.setAdapter(adapter);
    }

    public void onItemClick(View view, int position) {
        CellRows cellRow = CellRows.values()[position];
        NavigationManager.getInstance().removeController();
        Study.setMostRecentTestSession(createTestSession());
        Study.getCurrentTestSession().markStarted();
        Study.getParticipant().save();

        StateMachine stateMachine = Study.getStateMachine();
        if (stateMachine instanceof SampleAppStateMachine) {
            SampleAppStateMachine sampleAppStateMachine = (SampleAppStateMachine)stateMachine;
            sampleAppStateMachine.listener = this;
            sampleAppStateMachine.setPathForCellRow(cellRow);
            sampleAppStateMachine.save();
        }

        recyclerView.setVisibility(View.INVISIBLE);
        contentFrame.setVisibility(View.VISIBLE);
        Study.openNextFragment();
    }

    public TestSession createTestSession() {
        TestSession session = new TestSession(0, 0, 0);
        session.setScheduledDate(LocalDate.now());
        session.setPrescribedTime(DateTime.now());
        return session;
    }

    @Override
    public void onAssessmentComplete(List<Bitmap> signatureList, TestSession session) {
        NavigationManager.getInstance().clearBackStack();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(session);
        Log.d("SampleAppResult", json);

        StateMachine stateMachine = Study.getStateMachine();
        if (stateMachine instanceof SampleAppStateMachine) {
            SampleAppStateMachine sampleAppStateMachine = (SampleAppStateMachine) stateMachine;
            sampleAppStateMachine.listener = null;
        }
        recyclerView.setVisibility(View.VISIBLE);
        contentFrame.setVisibility(View.GONE);
    }

    public enum CellRows {
        WAKE_SURVEY("Wake Survey"),
        CHRONOTYPE_SURVEY("Chronotype Survey"),
        CONTEXT_SURVEY("Context Survey"),
        SYMBOLS_TEST("Symbols Test"),
        GRIDS_TEST("Grids Test"),
        PRICING_TEST("Pricing Test"),
        ALL_TESTS("All Tests");

        public String stringValue;
        CellRows(String rowTitle) {
            this.stringValue = rowTitle;
        }
    }

    public static class MyRecyclerViewAdapter extends
            RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

        private List<String> mData;
        private LayoutInflater mInflater;
        private ItemClickListener mClickListener;

        // data is passed into the constructor
        MyRecyclerViewAdapter(Context context, List<String> data) {
            this.mInflater = LayoutInflater.from(context);
            this.mData = data;
        }

        // inflates the row layout from xml when needed
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.recycler_view_row, parent, false);
            return new ViewHolder(view);
        }

        // binds the data to the TextView in each row
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            String animal = mData.get(position);
            holder.myTextView.setText(animal);
        }

        // total number of rows
        @Override
        public int getItemCount() {
            return mData.size();
        }


        // stores and recycles views as they are scrolled off screen
        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView myTextView;

            ViewHolder(View itemView) {
                super(itemView);
                myTextView = itemView.findViewById(R.id.row_title);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
            }
        }

        // convenience method for getting data at click position
        String getItem(int id) {
            return mData.get(id);
        }

        // allows clicks events to be caught
        void setClickListener(ItemClickListener itemClickListener) {
            this.mClickListener = itemClickListener;
        }

        // parent activity will implement this method to respond to click events
        public interface ItemClickListener {
            void onItemClick(View view, int position);
        }
    }
}