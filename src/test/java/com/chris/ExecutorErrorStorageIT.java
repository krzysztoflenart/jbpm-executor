package com.chris;

import com.chris.storage.MyCustomErrorStorage;
import com.chris.workitem.CorrectWorkItemHandler;
import com.chris.workitem.IncorrectWorkItemHandler;
import lombok.extern.slf4j.Slf4j;
import org.jbpm.runtime.manager.impl.AbstractRuntimeManager;
import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.jbpm.workflow.instance.WorkflowRuntimeException;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.internal.runtime.error.ExecutionError;
import org.kie.internal.runtime.error.ExecutionErrorManager;
import org.kie.internal.runtime.error.ExecutionErrorStorage;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;

import java.util.List;

import static org.hamcrest.core.Is.is;

@Slf4j
public class ExecutorErrorStorageIT extends JbpmJUnitBaseTestCase {

  private static final String TEST_BPM_RUNTIME_IDENTIFIER = "123456789";
  private static final String MAIN_PROCESS_WITH_SUBPROCESS_BPMN_FILE = "MainProcess.bpmn2";
  private static final String SUB_PROCESS_BPM_FILE = "SubProcess.bpmn2";
  private static final String MAIN_PROCESS_ID = "com.example.MainProcess";
  private static final String MAIN_PROCESS_WITHOUT_SUBPROCESS_BPMN_FILE = "MainProcess2.bpmn2";
  private static final String MAIN_PROCESS_WITHOUT_SUBPROCESS_BPMN_ID = "com.example.MainProcess2";

  public ExecutorErrorStorageIT() {
    super(true, true);
  }

  @Test
  public void shouldStoreExceptionFromSubProcess() {

    // given
    addEnvironmentEntry("ExecutionErrorStorage", new MyCustomErrorStorage());
    RuntimeManager manager =
        createRuntimeManager(
            Strategy.PROCESS_INSTANCE,
            TEST_BPM_RUNTIME_IDENTIFIER,
            MAIN_PROCESS_WITH_SUBPROCESS_BPMN_FILE,
            SUB_PROCESS_BPM_FILE);

    RuntimeEngine runtimeEngine = getRuntimeEngine(ProcessInstanceIdContext.get());

    KieSession kieSession = runtimeEngine.getKieSession();

    // register work item
    CorrectWorkItemHandler correctWorkItemHandler = new CorrectWorkItemHandler();
    kieSession
        .getWorkItemManager()
        .registerWorkItemHandler("CorrectWorkItemHandler", correctWorkItemHandler);

    IncorrectWorkItemHandler incorrectWorkItemHandler = new IncorrectWorkItemHandler();
    super.addWorkItemHandler("IncorrectWorkItemHandler", incorrectWorkItemHandler);

    // when
    try {
      kieSession.startProcess(MAIN_PROCESS_ID);
    } catch (WorkflowRuntimeException ex) {
      log.debug("Expected", ex);
    }

    // then
    ExecutionErrorManager errorManager =
        ((AbstractRuntimeManager) manager).getExecutionErrorManager();
    ExecutionErrorStorage storage = errorManager.getStorage();
    List<ExecutionError> errors = storage.list(0, 10);
    assertThat(errors.size(), is(1));

    manager.disposeRuntimeEngine(runtimeEngine);
    manager.close();
  }

  @Test
  public void shouldStoreExceptionFromMainProcess() {

    // given
    addEnvironmentEntry("ExecutionErrorStorage", new MyCustomErrorStorage());

    RuntimeManager manager =
        createRuntimeManager(
            Strategy.PROCESS_INSTANCE,
            TEST_BPM_RUNTIME_IDENTIFIER,
            MAIN_PROCESS_WITHOUT_SUBPROCESS_BPMN_FILE);

    RuntimeEngine runtimeEngine = getRuntimeEngine(ProcessInstanceIdContext.get());

    KieSession kieSession = runtimeEngine.getKieSession();
    CorrectWorkItemHandler correctWorkItemHandler = new CorrectWorkItemHandler();
    kieSession
        .getWorkItemManager()
        .registerWorkItemHandler("CorrectWorkItemHandler", correctWorkItemHandler);

    IncorrectWorkItemHandler incorrectWorkItemHandler = new IncorrectWorkItemHandler();
    kieSession
        .getWorkItemManager()
        .registerWorkItemHandler("IncorrectWorkItemHandler", incorrectWorkItemHandler);

    // when
    try {
      kieSession.startProcess(MAIN_PROCESS_WITHOUT_SUBPROCESS_BPMN_ID);
    } catch (WorkflowRuntimeException ex) {
      log.debug("Expected", ex);
    }

    // then
    ExecutionErrorManager errorManager =
        ((AbstractRuntimeManager) manager).getExecutionErrorManager();
    ExecutionErrorStorage storage = errorManager.getStorage();
    List<ExecutionError> errors = storage.list(0, 10);
    assertThat(errors.size(), is(1));

    manager.disposeRuntimeEngine(runtimeEngine);
    manager.close();
  }
}
