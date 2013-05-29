package com.zibea.parser.core.workers;

import com.zibea.parser.core.task.Task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author: Mikhail Bragin
 */
public abstract class Worker {

    protected ExecutorService pool;

    protected AtomicLong tasksProduced = new AtomicLong();

    protected LinkedBlockingQueue<Task> tasks;

    protected Worker(int workersAmount, String workersPrefix) {
        this.pool = Executors.newFixedThreadPool(workersAmount, new CustomThreadFactory(workersPrefix));
        this.tasks = new LinkedBlockingQueue<>();
        start();
    }

    public void addTask(Task task) {
        this.tasks.add(task);
    }

    public long getTasksProduced() {
        return tasksProduced.get();
    }

    public int getQueueSize() {
        return tasks.size();
    }

    public abstract void start();
}
