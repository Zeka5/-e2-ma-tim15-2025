package com.example.ma_mobile.repository;

import android.content.Context;
import android.util.Log;

import com.example.ma_mobile.models.CreateTaskRequest;
import com.example.ma_mobile.models.Task;
import com.example.ma_mobile.models.TaskInstance;
import com.example.ma_mobile.network.ApiService;
import com.example.ma_mobile.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TaskRepository {
    private static final String TAG = "TaskRepository";
    private ApiService apiService;
    private Context context;

    public TaskRepository(Context context) {
        this.context = context;
        this.apiService = RetrofitClient.getApiService();
    }

    public interface TaskListCallback {
        void onSuccess(List<Task> tasks);
        void onError(String error);
    }

    public interface TaskCallback {
        void onSuccess(Task task);
        void onError(String error);
    }

    public interface DeleteCallback {
        void onSuccess();
        void onError(String error);
    }

    public interface TaskInstanceListCallback {
        void onSuccess(List<TaskInstance> instances);
        void onError(String error);
    }

    public interface TaskInstanceCallback {
        void onSuccess(TaskInstance instance);
        void onError(String error);
    }

    public void getAllTasks(TaskListCallback callback) {
        Call<List<Task>> call = apiService.getAllTasks();

        call.enqueue(new Callback<List<Task>>() {
            @Override
            public void onResponse(Call<List<Task>> call, Response<List<Task>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Tasks fetched successfully: " + response.body().size());
                    callback.onSuccess(response.body());
                } else {
                    String errorMessage = "Failed to fetch tasks: " + response.code();
                    Log.e(TAG, errorMessage);
                    callback.onError(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<List<Task>> call, Throwable t) {
                String errorMessage = "Network error: " + t.getMessage();
                Log.e(TAG, errorMessage, t);
                callback.onError(errorMessage);
            }
        });
    }

    public void getTasksByStatus(String status, TaskListCallback callback) {
        Call<List<Task>> call = apiService.getTasksByStatus(status);

        call.enqueue(new Callback<List<Task>>() {
            @Override
            public void onResponse(Call<List<Task>> call, Response<List<Task>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Tasks fetched successfully");
                    callback.onSuccess(response.body());
                } else {
                    String errorMessage = "Failed to fetch tasks: " + response.code();
                    Log.e(TAG, errorMessage);
                    callback.onError(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<List<Task>> call, Throwable t) {
                String errorMessage = "Network error: " + t.getMessage();
                Log.e(TAG, errorMessage, t);
                callback.onError(errorMessage);
            }
        });
    }

    public void getTaskById(Long id, TaskCallback callback) {
        Call<Task> call = apiService.getTaskById(id);

        call.enqueue(new Callback<Task>() {
            @Override
            public void onResponse(Call<Task> call, Response<Task> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Task fetched successfully");
                    callback.onSuccess(response.body());
                } else {
                    String errorMessage = "Failed to fetch task: " + response.code();
                    Log.e(TAG, errorMessage);
                    callback.onError(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<Task> call, Throwable t) {
                String errorMessage = "Network error: " + t.getMessage();
                Log.e(TAG, errorMessage, t);
                callback.onError(errorMessage);
            }
        });
    }

    public void createTask(CreateTaskRequest request, TaskCallback callback) {
        Call<Task> call = apiService.createTask(request);

        call.enqueue(new Callback<Task>() {
            @Override
            public void onResponse(Call<Task> call, Response<Task> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Task created successfully");
                    callback.onSuccess(response.body());
                } else {
                    String errorMessage = "Failed to create task: " + response.code();
                    Log.e(TAG, errorMessage);
                    callback.onError(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<Task> call, Throwable t) {
                String errorMessage = "Network error: " + t.getMessage();
                Log.e(TAG, errorMessage, t);
                callback.onError(errorMessage);
            }
        });
    }

    public void updateTask(Long id, CreateTaskRequest request, TaskCallback callback) {
        Call<Task> call = apiService.updateTask(id, request);

        call.enqueue(new Callback<Task>() {
            @Override
            public void onResponse(Call<Task> call, Response<Task> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Task updated successfully");
                    callback.onSuccess(response.body());
                } else {
                    String errorMessage = "Failed to update task: " + response.code();
                    Log.e(TAG, errorMessage);
                    callback.onError(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<Task> call, Throwable t) {
                String errorMessage = "Network error: " + t.getMessage();
                Log.e(TAG, errorMessage, t);
                callback.onError(errorMessage);
            }
        });
    }

    public void completeTask(Long id, TaskCallback callback) {
        Call<Task> call = apiService.completeTask(id);

        call.enqueue(new Callback<Task>() {
            @Override
            public void onResponse(Call<Task> call, Response<Task> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Task completed successfully");
                    callback.onSuccess(response.body());
                } else {
                    String errorMessage = "Failed to complete task: " + response.code();
                    Log.e(TAG, errorMessage);
                    callback.onError(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<Task> call, Throwable t) {
                String errorMessage = "Network error: " + t.getMessage();
                Log.e(TAG, errorMessage, t);
                callback.onError(errorMessage);
            }
        });
    }

    public void deleteTask(Long id, DeleteCallback callback) {
        Call<Void> call = apiService.deleteTask(id);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Task deleted successfully");
                    callback.onSuccess();
                } else {
                    String errorMessage = "Failed to delete task: " + response.code();
                    Log.e(TAG, errorMessage);
                    callback.onError(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                String errorMessage = "Network error: " + t.getMessage();
                Log.e(TAG, errorMessage, t);
                callback.onError(errorMessage);
            }
        });
    }

    public void getTaskInstances(Long taskId, TaskInstanceListCallback callback) {
        Call<List<TaskInstance>> call = apiService.getTaskInstances(taskId);

        call.enqueue(new Callback<List<TaskInstance>>() {
            @Override
            public void onResponse(Call<List<TaskInstance>> call, Response<List<TaskInstance>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Task instances fetched successfully: " + response.body().size());
                    callback.onSuccess(response.body());
                } else {
                    String errorMessage = "Failed to fetch task instances: " + response.code();
                    Log.e(TAG, errorMessage);
                    callback.onError(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<List<TaskInstance>> call, Throwable t) {
                String errorMessage = "Network error: " + t.getMessage();
                Log.e(TAG, errorMessage, t);
                callback.onError(errorMessage);
            }
        });
    }

    public void completeTaskInstance(Long instanceId, TaskInstanceCallback callback) {
        Call<TaskInstance> call = apiService.completeTaskInstance(instanceId);

        call.enqueue(new Callback<TaskInstance>() {
            @Override
            public void onResponse(Call<TaskInstance> call, Response<TaskInstance> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Task instance completed successfully");
                    callback.onSuccess(response.body());
                } else {
                    String errorMessage = "Failed to complete task instance: " + response.code();
                    Log.e(TAG, errorMessage);
                    callback.onError(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<TaskInstance> call, Throwable t) {
                String errorMessage = "Network error: " + t.getMessage();
                Log.e(TAG, errorMessage, t);
                callback.onError(errorMessage);
            }
        });
    }

    public void deleteTaskInstance(Long instanceId, DeleteCallback callback) {
        Call<Void> call = apiService.deleteTaskInstance(instanceId);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Task instance deleted successfully");
                    callback.onSuccess();
                } else {
                    String errorMessage = "Failed to delete task instance: " + response.code();
                    Log.e(TAG, errorMessage);
                    callback.onError(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                String errorMessage = "Network error: " + t.getMessage();
                Log.e(TAG, errorMessage, t);
                callback.onError(errorMessage);
            }
        });
    }
}
