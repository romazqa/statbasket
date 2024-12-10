package com.example.mycoach;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mycoach.ui.calendar.CalendarFragment;

import java.util.List;

public class CalendarTasksAdapter extends RecyclerView.Adapter<CalendarTasksAdapter.TaskViewHolder> {

    private List<CalendarFragment.Task> tasks;

    public CalendarTasksAdapter(List<CalendarFragment.Task> tasks) {
        this.tasks = tasks;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_calendar_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        CalendarFragment.Task task = tasks.get(position);
        holder.taskTitle.setText(task.getTitle());
        holder.taskTime.setText(task.getTime());
        // ... (установите другие данные задачи в элементы макета)
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView taskTitle;
        TextView taskTime;
        // ... (другие элементы макета)

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            taskTitle = itemView.findViewById(R.id.task_title);
            taskTime = itemView.findViewById(R.id.task_time);
            // ... (инициализация других элементов макета)
        }
    }
}