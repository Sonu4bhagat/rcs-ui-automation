package utils;

import org.testng.IAlterSuiteListener;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

import java.util.ArrayList;
import java.util.List;

/**
 * Lets the role suites of a multi-suite run (e.g. the Jenkins-generated selected-suites.xml) run at the same
 * time without logging each other out.
 *
 * Logging out ends every session of that user, in every browser. So suites of different roles (separate
 * accounts) may overlap, but the Common suite - which logs in and out as every role - must not overlap
 * any of them. TestNG runs child suites before their parent, so this nests the role suites under the Common
 * suite: the role suites run together (up to the Surefire "suitethreadpoolsize"), then Common runs alone.
 *
 * Register it in the parent suite file's &lt;listeners&gt;; a suite without child suites is left unchanged.
 */
public class RoleSuitesInParallel implements IAlterSuiteListener {

    private static final String COMMON_PACKAGE = "tests.common.";

    @Override
    public void alter(List<XmlSuite> suites) {
        for (XmlSuite parent : suites) {
            List<XmlSuite> common = new ArrayList<>();
            List<XmlSuite> roles = new ArrayList<>();
            for (XmlSuite child : parent.getChildSuites()) {
                (isCommonSuite(child) ? common : roles).add(child);
            }
            if (common.isEmpty() || roles.isEmpty()) {
                continue; // nothing that must be kept apart - TestNG already runs sibling suites in parallel
            }

            XmlSuite runsLast = common.get(0);
            for (XmlSuite role : roles) {
                role.setParentSuite(runsLast);
                runsLast.getChildSuites().add(role);
            }
            parent.getChildSuites().clear();
            parent.getChildSuites().addAll(common);
            System.out.println("[RoleSuitesInParallel] Running " + names(roles) + " in parallel, then "
                    + names(common));
        }
    }

    /** A suite whose classes are all in tests.common (shared across roles). */
    private static boolean isCommonSuite(XmlSuite suite) {
        boolean any = false;
        for (XmlTest test : suite.getTests()) {
            for (XmlClass cls : test.getXmlClasses()) {
                if (!cls.getName().startsWith(COMMON_PACKAGE)) {
                    return false;
                }
                any = true;
            }
        }
        return any;
    }

    private static String names(List<XmlSuite> suites) {
        List<String> names = new ArrayList<>();
        for (XmlSuite suite : suites) {
            names.add(suite.getName());
        }
        return String.join(", ", names);
    }
}
