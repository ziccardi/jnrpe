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

import it.jnrpe.utils.StreamManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Utility class for running shell commands.
 * 
 *
 * @author Frederico Campos
 */
public final class ShellUtils {

    /**
     * Avoid instantiations.
     */
    private ShellUtils() {
        
    }
    
    /**
     * Executes a system command with arguments and returns the output.
     * 
     * @param command command to be executed
     * @param encoding encoding to be used
     * @return command output
     * @throws IOException on any error
     */
    public static String executeSystemCommandAndGetOutput(final String[] command, final String encoding) throws IOException {
        Process p = Runtime.getRuntime().exec(command);
        
        StreamManager sm = new StreamManager();
        
        try {
            InputStream input = sm.handle(p.getInputStream());
            StringBuilder lines = new StringBuilder();
            String line = null;
            BufferedReader in = (BufferedReader) sm.handle(new BufferedReader(new InputStreamReader(input, encoding)));
            while ((line = in.readLine()) != null) {
                lines.append(line).append('\n');
            }
            return lines.toString();
        } finally {
            sm.closeAll();
        }
    }

    /**
     * Check if we are running in a Windows environment.
     * 
     * @return true if windows
     */
    public static boolean isWindows() {
        String os = System.getProperty("os.name").toLowerCase();
        return os.contains("windows");
    }

    public static boolean isMac() {
        String OS = System.getProperty("os.name").toLowerCase();
        return OS.contains("mac");
    }

    /**
     * 
     * Check if name of process is a windows idle process.
     * 
     * @param proc process to be checked
     * @return true if idle process, false otherwise
     */
    public static boolean isWindowsIdleProc(final String proc) {
        String process = proc.trim().toLowerCase();
        if ("system idle process".equals(process) || process.contains("inactiv") || "system".equals(process)) {
            return true;
        }
        return false;
    }

}