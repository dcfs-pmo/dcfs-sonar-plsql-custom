/*
 * Sonar PL/SQL Plugin (Community)
 * Copyright (C) 2015-2017 Felipe Zorzo
 * mailto:felipebzorzo AT gmail DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.plsqlopen.checks;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Locale;

import org.junit.Before;
import org.sonar.api.batch.fs.internal.DefaultFileSystem;
import org.sonar.api.batch.fs.internal.DefaultInputFile;
import org.sonar.api.batch.fs.internal.TestInputFileBuilder;
import org.sonar.api.batch.sensor.internal.SensorContextTester;
import org.sonar.api.issue.NoSonarFilter;
import org.sonar.plsqlopen.AnalyzerMessage;
import org.sonar.plsqlopen.squid.PlSqlAstScanner;

import com.google.common.collect.ImmutableList;

public class BaseCheckTest {

    private static final String defaultResourceFolder = "src/test/resources/checks/";
    private DefaultFileSystem fs = new DefaultFileSystem(new File("."));
    
    @Before
    public void setUp() {
        Locale.setDefault(Locale.ENGLISH);
    }
    
    protected String getPath(String filename) {
        return defaultResourceFolder + filename;
    }
    
    protected Collection<AnalyzerMessage> scanFile(String filename, PlSqlCheck check) {
        TestInputFileBuilder inputFile = new TestInputFileBuilder("key", filename)
                .setLanguage("plsqlopen")
                .setCharset(Charset.forName("UTF-8"))
                .setModuleBaseDir(Paths.get(defaultResourceFolder));
        DefaultInputFile file = inputFile.build();
        fs.add(file);
        
        SensorContextTester context = SensorContextTester.create(new File("."));
        context.setFileSystem(fs);
        
        PlSqlAstScanner scanner = new PlSqlAstScanner(context, ImmutableList.of(check), new NoSonarFilter(), null, false);
        return scanner.scanFile(file);
    }
    
}
