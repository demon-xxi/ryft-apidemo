package com.metasys.ryft.program;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.metasys.ryft.RyftException;

/**
 * Wrapper around a {@link FileWriter} conveniently taking care of tabs and new lines.
 *
 * @author Sylvain Crozon
 *
 */
public class ProgramWriter extends FileWriter {

    private static final Logger LOG = LogManager.getLogger(ProgramManager.class);

    public ProgramWriter(File file) throws IOException {
        super(file);
    }

    public ProgramWriter append(String str) throws RyftException {
        return append(str, 0);
    }

    public ProgramWriter append(String str, int tabs) throws RyftException {
        try {
            for (int i = 0; i < tabs; i++) {
                write("    ");
            }
            super.write(str);
        } catch (IOException e) {
            LOG.error("Error writing program", e);
            throw RyftException.GENERATE_PROGRAM;
        }
        return newLine();
    }

    public ProgramWriter newLine() throws RyftException {
        try {
            write('\n');
        } catch (IOException e) {
            LOG.error("Error writing program", e);
            throw RyftException.GENERATE_PROGRAM;
        }
        return this;
    }
}
