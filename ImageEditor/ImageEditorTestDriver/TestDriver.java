package passoff;

import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;
import org.junit.platform.launcher.listeners.TestExecutionSummary.Failure;

import java.util.List;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

public class TestDriver {

    public static final String BLUE = "\033[34m";    // BLUE
    public static final String RED = "\033[31m";    // RED
    public static final String ANSI_RESET = "\u001B[0m";
    public static boolean displayCurrentTest = false;

    public static void main(String[] args) {
        final long startTime = System.currentTimeMillis();
        displayCurrentTest = true;
        final LauncherDiscoveryRequest request =
                LauncherDiscoveryRequestBuilder.request()
                        .selectors(selectClass(ImageEditorTest.class))
                        .build();

        final Launcher launcher = LauncherFactory.create();
        final SummaryGeneratingListener listener = new SummaryGeneratingListener();

        launcher.registerTestExecutionListeners(listener);
        launcher.execute(request);

        TestExecutionSummary summary = listener.getSummary();
        long testFoundCount = summary.getTestsFoundCount();
        List<Failure> failures = summary.getFailures();

        //Print to console
        System.out.println(BLUE + "Passed: " + summary.getTestsSucceededCount() + " of " + testFoundCount + " tests" + ANSI_RESET);
        for(Failure failure : failures){
            StringBuilder exception;
            if(failure.getException().getMessage() != null){
                exception = new StringBuilder(failure.getException().toString());
                exception.delete(0,37);
                exception = new StringBuilder(exception.toString().replace(">", ""));
                exception = new StringBuilder(exception.toString().replace("<", ""));
                exception.insert(0, RED); //make text red
                int index = exception.indexOf(" ==");
                if(index < 0){
                    index = exception.length() - 1;
                }
                exception.insert(index, ANSI_RESET); //end red
                exception = new StringBuilder(exception.toString().replace("==", "-"));
//                System.out.println("failure - " + failure.getTestIdentifier().getDisplayName() + " - " + exception.toString());
            }
            else exception = new StringBuilder();

            int i;
            for(i = 0; i < failure.getException().getStackTrace().length; i++){ //used to find element on stack trace with Test Code
                if(failure.getException().getStackTrace()[i].toString().contains("Test.java")) //Find the line on stack trace that contains test file
                    break;
            }

//            exception.append(" - Test File: " + failure.getException().getStackTrace()[i].getFileName()); //add test file that failure originated from to message
//            exception.append(" - Line: "+ failure.getException().getStackTrace()[i].getLineNumber()); //add line number of failure to message
            System.out.println(RED + "  failure" + ANSI_RESET + " - " + failure.getTestIdentifier().getDisplayName() + " - " + exception.toString()); //print failure report
        }
        final long endTime = System.currentTimeMillis();
        System.out.println("Total execution time: " + (endTime - startTime)/1000.0 + " seconds");
        if(summary.getTestsSucceededCount() != testFoundCount){
            System.exit(1); //for use with driver
        }
    }
}

