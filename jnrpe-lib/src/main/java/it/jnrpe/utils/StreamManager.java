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
package it.jnrpe.utils;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for handling streams.
 *
 * @author Massimiliano Ziccardi
 *
 */
public final class StreamManager {
    /**
     * Contains all the streams to be managed by the {@link StreamManager}
     * object.
     */
    private final List<Closeable> managedStreamsList = new ArrayList<Closeable>();

    /**
     * Default constructor.
     */
    public StreamManager() {
    }

    /**
     * Handles the received InputStream and returns it.
     *
     * @param in
     *            The stream to be automatically closed when {@link #closeAll()}
     *            is called.
     * @return The passed in stream.
     */
    public InputStream handle(final InputStream in) {
        managedStreamsList.add(in);
        return in;
    }

    /**
     * Handles the received OutputStream and returns it.
     *
     * @param out
     *            The stream to be automatically closed when {@link #closeAll()}
     *            is called.
     * @return The passed in stream.
     */
    public OutputStream handle(final OutputStream out) {
        managedStreamsList.add(out);
        return out;
    }

    /**
     * Handles the received Reader and returns it.
     *
     * @param r
     *            The reader to be automatically closed when {@link #closeAll()}
     *            is called.
     * @return The passed in reader.
     */
    public Reader handle(final Reader r) {
        managedStreamsList.add(r);
        return r;
    }

    /**
     * Handles the received Writer and returns it.
     *
     * @param w
     *            The writer to be automatically closed when {@link #closeAll()}
     *            is called.
     * @return The passed in writer.
     */
    public Writer handle(final Writer w) {
        managedStreamsList.add(w);
        return w;
    }

    /**
     * Returns an InputStream on the given file.
     *
     * @param f
     *            The file attached to the returned stream to be automatically
     *            closed when {@link #closeAll()} is called.
     * @return The stream to the passed in file
     * @throws FileNotFoundException
     *             If the file does not exists
     */
    public InputStream getInputStream(final File f) throws FileNotFoundException {
        return handle(new FileInputStream(f));
    }

    /**
     * Returns an OutputStream on the given file.
     *
     * @param f
     *            The file attached to the returned stream to be automatically
     *            closed when {@link #closeAll()} is called.
     * @return The stream to the passed in file
     * @throws FileNotFoundException
     *             If the file does not exists
     */
    public OutputStream getOutputStream(final File f) throws FileNotFoundException {
        return handle(new FileOutputStream(f));
    }

    /**
     * Closes all handles streams and readers. Non exception is thrown. This.
     * method should be called in the finally block.
     */
    public void closeAll() {
        for (Closeable obj : managedStreamsList) {
            try {
                obj.close();
            } catch (Exception e) {
                // if (m_Logger.isDebugEnabled())
                // m_Logger.debug("EXCEPTION CLOSING STREAM/READER : " +
                // e.getMessage());
            }
        }
    }

}
