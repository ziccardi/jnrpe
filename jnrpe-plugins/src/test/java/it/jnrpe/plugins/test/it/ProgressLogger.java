/*******************************************************************************
 * Copyright (c) 2007, 2014 Massimiliano Ziccardi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package it.jnrpe.plugins.test.it;

import org.testng.IClass;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;

/**
 * This class is used to log test method invocations.
 * 
 * @author Massimiliano Ziccardi
 * 
 */
public class ProgressLogger implements IInvokedMethodListener {

    private String getMethodName(final IInvokedMethod m) {
        if (!m.isTestMethod())
            return null;

        ITestNGMethod method = m.getTestMethod();

        IClass klass = method.getTestClass();

        return klass.getName() + "." + method.getMethodName();
    }

    public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {
        String methodName = getMethodName(method);
        if (methodName == null) {
            return;
        }
        System.out.print(methodName + ": ");
        System.out.flush();
    }

    public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
        String methodName = getMethodName(method);

        if (methodName == null) {
            return;
        }

        switch (testResult.getStatus()) {
        case ITestResult.SUCCESS:
            System.out.println("OK");
            break;
        case ITestResult.FAILURE:
            System.out.println("*** FAILURE ***");
            break;
        case ITestResult.SKIP:
            System.out.println("*** SKIPPED ***");
            break;
        case ITestResult.SUCCESS_PERCENTAGE_FAILURE:
            System.out.println("*** PARTIALLY FAILED ***");
            break;
        case ITestResult.STARTED:
        default:
            break;
        }

        if (testResult.getThrowable() != null) {
            testResult.getThrowable().printStackTrace();
        }

        // if (testResult.isSuccess()) {
        // System.out.println("OK");
        // } else {
        // System.out.println("*** " + testResult.getStatus());
        // if (testResult.getThrowable() != null) {
        // testResult.getThrowable().printStackTrace();
        // }
        // }
    }
}
