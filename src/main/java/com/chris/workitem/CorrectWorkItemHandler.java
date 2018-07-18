package com.chris.workitem;

import lombok.extern.slf4j.Slf4j;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;


@Slf4j
public class CorrectWorkItemHandler implements WorkItemHandler {
    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
        log.debug("Execute id=[]", workItem.getId());
        manager.completeWorkItem(workItem.getId(), null);
    }

    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {

    }
}
