package com.chris.workitem;

import com.chris.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

@Slf4j
public class IncorrectWorkItemHandler implements WorkItemHandler {
  public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
    log.debug("Throw Exception =[]", workItem.getId());

    throw new BusinessException("On purpose");
  }

  public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {}
}
