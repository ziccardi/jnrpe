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
 * This class is used to log test method invocations
 * @author ziccardi
 *
 */
public class ProgressLogger implements IInvokedMethodListener {

	private String getMethodName(IInvokedMethod m) {
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
		System.out.print (methodName + ": ");
		System.out.flush();
	}

	public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
		String methodName = getMethodName(method);
		
		if (methodName == null) {
			return;
		}
		if (testResult.isSuccess()) {
			System.out.println ("OK");
		} else {
			System.out.println ("*** FAIL ***");
			if (testResult.getThrowable() != null) {
				testResult.getThrowable().printStackTrace();
			}
		}
	}
}
