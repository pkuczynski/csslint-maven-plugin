#!/usr/bin/env node
/* Build time: 25-October-2011 09:25:06 */

/*
 * Encapsulates all of the CLI functionality. The api argument simply
 * provides environment-specific functionality.
 */
/*global CSSLint*/
function cli(api){

    //-------------------------------------------------------------------------
    // Helper functions
    //-------------------------------------------------------------------------
    
    /**
     * Returns an array of messages for a particular type.
     * @param messages {Array} Array of CSS Lint messages.
     * @param type {String} The type of message to filter on.
     * @return {Array} An array of matching messages.
     */
    function pluckByType(messages, type){
        return messages.filter(function(message) {
            return message.type === type;
        });        
    }

    /**
     * Returns a ruleset object based on the CLI options.
     * @param options {Object} The CLI options.
     * @return {Object} A ruleset object.
     */
    function gatherRules(options){
        var ruleset,
            warnings = options.rules || options.warnings,
            errors = options.errors;
        
        if (warnings){
            ruleset = ruleset || {};
            warnings.split(",").forEach(function(value){
                ruleset[value] = 1;
            });
        }
        
        if (errors){
            ruleset = ruleset || {};
            errors.split(",").forEach(function(value){
                ruleset[value] = 2;
            });
        }
        
        return ruleset;
    }

    /**
     * Outputs all available rules to the CLI.
     * @return {void}
     */
    function printRules(){
        api.print("");
        var rules = CSSLint.getRules();
        rules.forEach(function(rule){
            api.print(rule.id + "\n" + rule.desc + "\n");
        });
    }

    /**
     * Given a file name and options, run verification and print formatted output.
     * @param {String} relativeFilePath absolute file location
     * @param {Object} options for processing
     * @return {Number} exit code
     */
    function processFile(relativeFilePath, options) {
        var input = api.readFile(relativeFilePath),
            result = CSSLint.verify(input, gatherRules(options)),
            formatter = CSSLint.getFormatter(options.format || "text"),
            messages = result.messages || [],
            exitCode = 0;

        if (!input) {
            api.print("csslint: Could not read file data in " + relativeFilePath + ". Is the file empty?");
            exitCode = 1;
        } else {
            //var relativeFilePath = getRelativePath(api.getWorkingDirectory(), fullFilePath);
            options.fullPath = api.getFullPath(relativeFilePath);
            api.print(formatter.formatResults(result, relativeFilePath, options));

            if (messages.length > 0 && pluckByType(messages, "error").length > 0) {
                exitCode = 1;
            }
        }
        
        return exitCode;
    }


    /**
     * Outputs the help screen to the CLI.
     * @return {void}
     */
    function outputHelp(){
        api.print([
            "\nUsage: csslint-rhino.js [options]* [file|dir]*",
            " ",
            "Global Options",
            "  --help                    Displays this information.",
            "  --format=<format>         Indicate which format to use for output.",
            "  --list-rules              Outputs all of the rules available.",
            "  --quiet                   Only output when errors are present.",
            "  --errors=<rule[,rule]+>   Indicate which rules to include as errors.",
            "  --warnings=<rule[,rule]+> Indicate which rules to include as warnings.",
            "  --version                 Outputs the current version number."
        ].join("\n") + "\n");
    }

    /**
     * Given an Array of filenames, print wrapping output and process them.
     * @param files {Array} filenames list
     * @param options {Object} options object
     * @return {Number} exit code
     */
    function processFiles(files, options){
        var exitCode = 0,
            formatId = options.format || "text",
            formatter,
            output;
            
        if (!files.length) {
            api.print("csslint: No files specified.");
            exitCode = 1;
        } else {
            if (!CSSLint.hasFormat(formatId)){
                api.print("csslint: Unknown format '" + formatId + "'. Cannot proceed.");
                exitCode = 1; 
            } else {
                formatter = CSSLint.getFormatter(formatId);
                
                output = formatter.startFormat();
                if (output){
                    api.print(output);
                }

                files.forEach(function(file){
                    if (exitCode === 0) {
                        exitCode = processFile(file,options);
                    } else {
                        processFile(file,options);
                    }
                });
                
                output = formatter.endFormat();
                if (output){
                    api.print(output);
                }
            }
        }
        return exitCode;
    }    

    //-----------------------------------------------------------------------------
    // Process command line
    //-----------------------------------------------------------------------------

    var args     = api.args,
        argName,
        parts,
        arg      = args.shift(),
        options  = {},
        files    = [];

    while(arg){
        if (arg.indexOf("--") === 0){
            argName = arg.substring(2);
            options[argName] = true;
            
            if (argName.indexOf("=") > -1){
                parts = argName.split("=");
                options[parts[0]] = parts[1];
            } else {
                options[argName] = true;
            }

        } else {
            
            //see if it's a directory or a file
            if (api.isDirectory(arg)){
                files = files.concat(api.getFiles(arg));
            } else {
                files.push(arg);
            }
        }
        arg = args.shift();
    }

    if (options.help || arguments.length === 0){
        outputHelp();
        api.quit(0);
    }

    if (options.version){
        api.print("v" + CSSLint.version);
        api.quit(0);
    }

    if (options["list-rules"]){
        printRules();
        api.quit(0);
    }

    //files = api.fixFilenames(files);

    api.quit(processFiles(files,options));
}
/*
 * CSSLint Node.js Command Line Interface
 */

/*jshint node:true*/
/*global cli*/

var fs      = require("fs"),
    path    = require("path"),
    CSSLint = require("./lib/csslint-node").CSSLint;
    
cli({
    args: process.argv.slice(2),

    print: function(message){
        fs.writeSync(1, message + "\n");
    },
    
    quit: function(code){
    
        //Workaround for https://github.com/joyent/node/issues/1669
        if (!process.stdout.flush || !process.stdout.flush()) {
            process.once("drain", function () {
                process.exit(code || 0);
            });
        } else {
            process.exit(code || 0);
        }
    },
    
    isDirectory: function(name){
        return fs.statSync(name).isDirectory();
    },

    getFiles: function(dir){
        var files = [];

        try {
            fs.statSync(dir);
        } catch (ex){
            return [];
        }

        function traverse(dir, stack){
            stack.push(dir);
            fs.readdirSync(stack.join("/")).forEach(function(file){
                var path = stack.concat([file]).join("/"),
                    stat = fs.statSync(path);
                
                if (file[0] == ".") {
                    return;
                } else if (stat.isFile() && /\.css$/.test(file)){
                    files.push(path);
                } else if (stat.isDirectory()){
                    traverse(file, stack);
                }
            });
            stack.pop();
        }

        traverse(dir, []);

        return files;
    },
    
    fixFilenames: function(files){
        return files.map(function(filename){
            return path.resolve(process.cwd(), filename);
        });
    },

    getWorkingDirectory: function() {
        return process.cwd();
    },
    
    getFullPath: function(filename){
        return path.resolve(process.cwd(), filename);
    },

    readFile: function(filename){
        return fs.readFileSync(filename, "utf-8");    
    }
});



