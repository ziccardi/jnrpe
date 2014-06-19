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
package it.jnrpe.plugin.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Utility class for running shell commands 
 * 
 *
 * @author Frederico Campos
 */
public class ShellUtils {
    
    /**
     * Executes a system command with arguments and returns the output
     * 
     * @param command
     * @param encoding
     * @return command output
     * @throws IOException
     */
    public static String executeSystemCommandAndGetOutput(String[] command, String encoding) throws IOException{
        Process p = Runtime.getRuntime().exec(command);
        InputStream input = p.getInputStream();
        StringBuffer lines = new StringBuffer();
        String line = null;
        BufferedReader in =
                new BufferedReader(new InputStreamReader(input, encoding));
        while ((line = in.readLine()) != null) {
            lines.append(line).append("\n");
        }
        return lines.toString();
    }
 
    /**
     * Check if we are running in a Windows environment
     * 
     * @return true if windows
     */
    public static boolean isWindows() {
        String os = System.getProperty("os.name").toLowerCase();
        return os.contains("windows");
    }
    /**
    
     * Check if name of process is a windows idle process
     * 
     * @param proc
     * @return true if idle process, false otherwise
     */
	public static boolean isWindowsIdleProc(String proc) {
		proc = proc.trim().toLowerCase();
		if (proc.equals("system idle process") ||
				proc.contains("inactiv") ||
				proc.equals("system")) {
			return true;
		}
		return false;
	}

}