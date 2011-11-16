/*
Copyright (c) 2011, Tomasz Oponowicz
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

- Redistributions of source code must retain the above copyright notice, this
  list of conditions and the following disclaimer.
- Redistributions in binary form must reproduce the above copyright notice,
  this list of conditions and the following disclaimer in the documentation
  and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package net.csslint.plugins.maven;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.io.RawInputStreamFacade;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.tools.shell.Global;
import org.mozilla.javascript.tools.shell.Main;
import org.mozilla.javascript.tools.shell.QuitAction;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * A Maven plugin for CSS Lint tool.
 *
 * @goal check
 * @phase verify
 */
public class LintMojo extends AbstractMojo {
    private static final String SCRIPT_FILE = "csslint-rhino.js";
    private static final String OUTPUT_FILE = "csslint.xml";

    private int scriptExitCode;

    /**
     * TODO Update rule list
     *
     * List of rules that the tool uses.
     * <p/>
     * If the rule is matched, an error message is produced. Errors cause build failure.
     * <p/>
     * Available rules:
     * <ul>
     * <li>adjoining-classes : Don't use adjoining classes</li>
     * <li>box-model : Beware of broken box model</li>
     * <li>compatible-vendor-prefixes : Use compatible vendor prefixes</li>
     * <li>display-property-grouping : Use properties appropriate for 'display'</li>
     * <li>duplicate-properties : Avoid duplicate properties</li>
     * <li>empty-rules : Disallow empty rules</li>
     * <li>floats : Don't use too many floats</li>
     * <li>font-faces : Don't use too many web fonts</li>
     * <li>font-sizes : Don't use too many font sizes</li>
     * <li>gradients : Include all gradient definitions</li>
     * <li>ids : Don't use IDs</li>
     * <li>import : Avoid '@import'</li>
     * <li>important : Disallow '!important'</li>
     * <li>overqualified-elements : Don't use overqualified elements</li>
     * <li>qualified-headings : Don't qualify headings</li>
     * <li>regex-selectors : Don't use selectors that look like regexs</li>
     * <li>text-indent : Don't use negative text-indent'</li>
     * <li>unique-headings : Heading should only be defined once</li>
     * <li>vendor-prefix : Use vendor prefix properties correctly</li>
     * <li>zero-units : Don't use units for 0 values</li>
     * </ul>
     *
     * @parameter
     * @optional
     */
    private List<String> errors;

    /**
     * TODO Update rule list
     *
     * List of rules that the tool uses (if not specified all available rules are applied).
     * <p/>
     * If the rule is matched, a warning message is produced. Warnings don't cause build failure.
     * <p/>
     * Available rules:
     * <ul>
     * <li>adjoining-classes : Don't use adjoining classes</li>
     * <li>box-model : Beware of broken box model</li>
     * <li>compatible-vendor-prefixes : Use compatible vendor prefixes</li>
     * <li>display-property-grouping : Use properties appropriate for 'display'</li>
     * <li>duplicate-properties : Avoid duplicate properties</li>
     * <li>empty-rules : Disallow empty rules</li>
     * <li>floats : Don't use too many floats</li>
     * <li>font-faces : Don't use too many web fonts</li>
     * <li>font-sizes : Don't use too many font sizes</li>
     * <li>gradients : Include all gradient definitions</li>
     * <li>ids : Don't use IDs</li>
     * <li>import : Avoid '@import'</li>
     * <li>important : Disallow '!important'</li>
     * <li>overqualified-elements : Don't use overqualified elements</li>
     * <li>qualified-headings : Don't qualify headings</li>
     * <li>regex-selectors : Don't use selectors that look like regexs</li>
     * <li>text-indent : Don't use negative text-indent'</li>
     * <li>unique-headings : Heading should only be defined once</li>
     * <li>vendor-prefix : Use vendor prefix properties correctly</li>
     * <li>zero-units : Don't use units for 0 values</li>
     * </ul>
     *
     * @parameter
     * @optional
     */
    private List<String> warnings;

    /**
     * The directory to scan.
     * <p/>
     * The directory used by 'includes' and 'excludes' options.
     *
     * @parameter expression="${project.basedir}"
     * @optional
     */
    private File baseDirectory;

    /**
     * List of includes patterns (Ant patterns).
     *
     * @parameter
     * @required
     */
    private List<String> includes;

    /**
     * List of excludes patterns (Ant patterns).
     *
     * @parameter
     * @optional
     */
    private List<String> excludes;

    /**
     * Output format.
     * <p/>
     * Console formats:
     * <ul>
     * <li>"text" - the default format</li>
     * <li>"compact" - a more condensed output where each warning takes only one line of output</li>
     * </ul>
     * <p/>
     * External file formats (the location of the file is "${project.build.directory}/csslint.xml"):
     * <ul>
     * <li>"lint-xml" - an XML format that can be consumed by other utilities</li>
     * <li>"csslint-xml" - same as lint-xml except the document element is &lt;csslint&gt;</li>
     * <li>"checkstyle-xml" -  a format appropriate for consumption by Checkstyle</li>
     * </ul>

     * @parameter expression="text"
     * @optional
     */
    private String format;

    /**
     * Absolute path to project build directory.
     *
     * @parameter expression="${project.build.directory}"
     * @readonly
     * @required
     */
    private File projectBuildDirectory;

    /**
     * Validate inputs, build arguments and execute CSS Lint tool.
     *
     * @throws MojoExecutionException if input validation fails
     */
    @Override
    public void execute() throws MojoExecutionException {
        List<String> arguments = new ArrayList<String>();

        validateInput();

        setSoftQuit();

        // Build arguments list
        addScriptArgument(arguments);
        addErrorsAndWarningsArguments(arguments);
        addOutputArgument(arguments);
        addIncludesArgument(arguments);

        // Invoke script with specified arguments by means Rhino
        int exitCode = 0;

        try {
            exitCode =
                    Main.exec(arguments.toArray(new String[arguments.size()]));
        } catch (ExitScript e) {
            if (scriptExitCode != 0) {
                throw new IllegalStateException("There are errors in " +
                        "your CSS files");
            }
        }

        if (exitCode != 0) {
            throw new IllegalStateException("Error occurs when " +
                    "executing CSS Lint tool");
        }
    }

    private void validateInput() throws MojoExecutionException {
        if (includes == null || includes.isEmpty()) {
            throw new MojoExecutionException("You have to define " +
                    "file to process");
        }
    }

    private void addIncludesArgument(List<String> arguments) {
        List<File> files = getFiles();
        for (File file : files) {
            arguments.add(file.getAbsolutePath());
        }
    }

    private void addOutputArgument(List<String> arguments) throws MojoExecutionException {
        assert arguments != null;

        if ("lint-xml".equals(format)
                || "csslint-xml".equals(format)
                || "checkstyle-xml".equals(format)) {

            getLog().debug(String.format("Use file logger, format='%s'", format));

            arguments.add("--format=" + format);

            try {
                setOutputFile();
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }

            return;
        }

        if ("text".equals(format)
                || "compact".equals(format)) {

            getLog().debug(String.format("Use console logger, format='%s'", format));

            arguments.add("--format=" + format);

            return;
        }

        throw new MojoExecutionException("Unknown output format");
    }

    private void addErrorsAndWarningsArguments(List<String> arguments) {
        assert  arguments != null;

        if (errors != null && !errors.isEmpty()) {
            getLog().debug(String.format("Apply errors rules set, which was specified by user, errors='%s'", errors));
            addRulesToCustomArgument("--errors=", errors, arguments);
        }

        if (warnings != null && !warnings.isEmpty()) {
            getLog().debug(String.format("Apply warnings rules set, which was specified by user, warnings='%s'", warnings));
            addRulesToCustomArgument("--warnings=", warnings, arguments);
        }

        if ((errors == null || errors.isEmpty())
                && (warnings == null || warnings.isEmpty())) {
            getLog().debug("Apply all available rules, because user didn't specify any custom set");
        }
    }

    private void addRulesToCustomArgument(String argumentName , List<String> rules, List<String> arguments) {
        StringBuilder sb = new StringBuilder();

        sb.append(argumentName);

        for (String rule : rules) {
            sb.append(rule);
            sb.append(',');
        }

        // Remove last ',' character
        sb.deleteCharAt(sb.length() - 1);

        arguments.add(sb.toString());
    }

    private void addScriptArgument(List<String> arguments) {
        File script = getFileFromBuildDirectory(SCRIPT_FILE);

        /*
            'csslint-rhino.js' script exists in classpath. The Rhino accepts
            only file's path or URL. Thus loading script from classpath
            is impossible. Extract script to temporary directory and use.

            There is workaround, which enables defining custom protocol,
            but it isn't total valid in this situation (overriding global
            @link(URLStreamHandlerFactory} doesn't sound elegant):

            http://stackoverflow.com/questions/861500/
            url-to-load-resources-from-the-classpath-in-java
         */

        /*
            FIXME
            File's name should contain version to avoid unexpected
            behaviour when switching between different version of plugin
            without cleaning workspace
         */
        if (!script.exists()) {

            getLog().debug(String.format("Extract script to build " +
                    "directory, script='%s'", script.getAbsolutePath()));

            try {
                extractFileFromClasspath(script);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            getLog().debug(String.format("Script already exists in build " +
                    "directory, thus skip extracting, script='%s'",
                    script.getAbsolutePath()));
        }

        arguments.add(script.getAbsolutePath());
    }

    private void setSoftQuit() {

        /*
            By default Main class use System.exit() method to quit from script.
            In our case this is incorrect action because the whole Maven process
            is terminated. Thus we simulate "soft quit". This initialization
            has to be invoked before any interaction with Main class.

            This piece of code can be confused because it modifies static
            fields so other plugins can be affected. However this is
            impossible because for each plugin used by a project, a plugin
            class loader is created. For more information:

            https://cwiki.apache.org/MAVEN/maven-3x-class-loading.html
         */

        Main.global = new Global();
        Main.getGlobal().initQuitAction(new QuitAction() {

            @Override
            public void quit(Context cx, int exitCode) {

                // Pass exit code from "csslint-rhino.js" script
                scriptExitCode = exitCode;

                // Prevent script from running (comes from Rhino examples)
                throw new ExitScript();
            }
        });
    }

    private File getFileFromBuildDirectory(String path) {
        return new File(projectBuildDirectory, path);
    }

    private void setOutputFile() throws FileNotFoundException {
        Main.setOut(new PrintStream(getFileFromBuildDirectory(OUTPUT_FILE)));
    }

    private void extractFileFromClasspath(File script) throws IOException {
        FileUtils.copyStreamToFile(getScriptAsInputStream(), script);
    }

    private RawInputStreamFacade getScriptAsInputStream() throws IOException {
        return new RawInputStreamFacade(
                LintMojo.class.getClassLoader()
                        .getResource(SCRIPT_FILE).openStream());
    }

    @SuppressWarnings("unchecked")
    private List<File> getFiles() {
        String includesAsString = StringUtils.join(includes, ",");
        String excludesAsString = StringUtils.join(excludes, ",");

        try {
            return (List<File>) FileUtils.getFiles(
                    baseDirectory,
                    includesAsString,
                    excludesAsString,
                    true
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * The main purpose is to mark that script wants exit with specified code.
     */
    static class ExitScript extends Error {

    }
}
