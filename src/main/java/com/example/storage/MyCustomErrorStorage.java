package com.example.storage;

import org.kie.internal.runtime.error.ExecutionError;
import org.kie.internal.runtime.error.ExecutionErrorStorage;

import java.util.ArrayList;
import java.util.List;

public class MyCustomErrorStorage implements ExecutionErrorStorage {

  List<ExecutionError> list = new ArrayList<ExecutionError>();

  public ExecutionError store(ExecutionError error) {
    list.add(error);
    return error;
  }

  public ExecutionError get(String errorId) {
    return null;
  }

  public void acknowledge(String user, String... errorId) {}

  public List<ExecutionError> list(Integer page, Integer pageSize) {
    return list;
  }

  public List<ExecutionError> listByProcessInstance(
      Long processInstanceId, Integer page, Integer pageSize) {
    return null;
  }

  public List<ExecutionError> listByActivity(String activityName, Integer page, Integer pageSize) {
    return null;
  }

  public List<ExecutionError> listByDeployment(
      String deploymentId, Integer page, Integer pageSize) {
    return null;
  }
}
