package com.example.googletaskproject.ui.components.monthview;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.googletaskproject.R;
import com.example.googletaskproject.utils.Const;

import java.util.Calendar;


public class MonthViewFragment extends Fragment {


    public MonthAdapter monthAdapter;
    RecyclerView monthListView;
    int mYear, mMonth;
    LinearLayoutManager layoutManager;
    int currentPos;
    TextView day1,day2,day3,day4,day5,day6,day7;
    TextView[] textViewArray = new TextView[7];
    private  String[] DAY_NAMES;




    public MonthViewFragment() {
        // Required empty public constructor
    }

    public static MonthViewFragment newInstance(int Year, int Month) {
        MonthViewFragment fragment = new MonthViewFragment();
        Bundle args = new Bundle();
        args.putInt("Year", Year);
        args.putInt("Month", Month);
        fragment.setArguments(args);
        return fragment;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);




        if (getArguments() != null) {
            mYear = getArguments().getInt("Year");
            mMonth = getArguments().getInt("Month");
        }

        MonthAdapter monthAdapter2 = new MonthAdapter( );
        this.monthAdapter = monthAdapter2;
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View v = layoutInflater.inflate(R.layout.fragment_month, viewGroup, false);
        monthListView = v.findViewById(R.id.monthListView);
        day1 = v.findViewById(R.id.day1);
        day2 = v.findViewById(R.id.day2);
        day3 = v.findViewById(R.id.day3);
        day4 = v.findViewById(R.id.day4);
        day5 = v.findViewById(R.id.day5);
        day6 = v.findViewById(R.id.day6);
        day7 = v.findViewById(R.id.day7);


        textViewArray[0] = day1;
        textViewArray[1] = day2;
        textViewArray[2] = day3;
        textViewArray[3] = day4;
        textViewArray[4] = day5;
        textViewArray[5] = day6;
        textViewArray[6] = day7;

        layoutManager = new LinearLayoutManager(requireActivity());
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        monthListView.setLayoutManager(layoutManager);

        return v;
    }

    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, mYear);
        calendar.set(Calendar.MONTH, mMonth);

        initView();

    }

    public void initView() {
        monthListView.setAdapter(this.monthAdapter);
        selectFirstDisplayedYear();

        DAY_NAMES = new String[]{"S", "M", "T", "W", "T", "F", "S"};

        /*if (SharedPreferenceUtils.getString(ConstantField.WEEK_START_ON,R.string.strSystemDefault,requireContext()).equals(getString(R.string.strSystemDefault)))
        {
            DAY_NAMES = new String[]{"S", "M", "T", "W", "T", "F", "S"};
        }
        else  if (SharedPreferenceUtils.getString(ConstantField.WEEK_START_ON,R.string.strSystemDefault,requireContext()).equals(getString(R.string.strSunday)))
        {
            DAY_NAMES = new String[]{"S", "M", "T", "W", "T", "F", "S"};
        }
        else if (SharedPreferenceUtils.getString(ConstantField.WEEK_START_ON, R.string.strSystemDefault, requireContext()).equals(getString(R.string.strMonday)))
        {
            DAY_NAMES = new String[]{"M", "T", "W", "T", "F", "S","S"};
        }
        else if (SharedPreferenceUtils.getString(ConstantField.WEEK_START_ON, R.string.strSystemDefault, requireContext()).equals(getString(R.string.strSaturday)))
        {
            DAY_NAMES = new String[]{"S","S","M", "T", "W", "T", "F"};
        }*/


        for (int i=0;i< textViewArray.length;i++)
        {
            if (DAY_NAMES.length>0)
            {
                textViewArray[i].setText(DAY_NAMES[i]);
            }
        }


        LinearSnapHelper snapHelper = new LinearSnapHelper() {
            @Override
            public int findTargetSnapPosition(RecyclerView.LayoutManager layoutManager, int velocityX, int velocityY) {
                if (!(layoutManager instanceof LinearLayoutManager)) {
                    return RecyclerView.NO_POSITION;
                }

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;

                int position = linearLayoutManager.findFirstVisibleItemPosition();
                if (position == RecyclerView.NO_POSITION) {
                    return RecyclerView.NO_POSITION;
                }

                int targetPosition = -1;

                if (layoutManager.canScrollHorizontally()) {
                    if (velocityX < 0) {
                        targetPosition = position;
                    } else if (velocityX > 0) {
                        targetPosition = position + 1;
                    }
                } else if (layoutManager.canScrollVertically()) {
                    if (velocityY < 0) {
                        targetPosition = position - 1;
                    } else if (velocityY > 0) {
                        targetPosition = position + 1;
                    }
                }

                final int firstItem = 0;
                final int lastItem = layoutManager.getItemCount() - 1;
                targetPosition = Math.min(lastItem, Math.max(targetPosition, firstItem));

                return targetPosition;
            }
        };
        snapHelper.attachToRecyclerView(monthListView);


        monthListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {

                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                View centerView = snapHelper.findSnapView(layoutManager);
                currentPos = layoutManager.getPosition(centerView);

                String CurrentMonth = monthAdapter.getTittle(currentPos);

                Intent intent = new Intent(Const.TITLE_EVENT);
                intent.putExtra(Const.TITLE, CurrentMonth);
                LocalBroadcastManager.getInstance(requireActivity()).sendBroadcast(intent);

            }
        });

    }

    @SuppressLint("NotifyDataSetChanged")
    public void notifyMonthView() {
         monthAdapter.notifyDataSetChanged();
     }

    public void selectFirstDisplayedYear() {
        int yearPosition;
        if (this.monthAdapter != null && (yearPosition = this.monthAdapter.getMonthPosition(mYear, mMonth)) > -1) {
            try {
                monthListView.scrollToPosition(yearPosition - 1);
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            try {
                monthListView.scrollToPosition(yearPosition);
            } catch (Exception e3) {
                e3.printStackTrace();
            }
        }
    }

}
