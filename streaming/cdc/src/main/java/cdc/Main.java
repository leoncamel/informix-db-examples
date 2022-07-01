package cdc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Spec;
import picocli.CommandLine.Model.CommandSpec;
import java.util.concurrent.Callable;

@Command(name = "Main", mixinStandardHelpOptions = true, version = "CDC-Tools 1.0",
        description = "A set of tools for develop/testing Informix CDC.",
        subcommands = {
                cdc.CdcMain.class,
                cdc.IfxJdbcMain.class,
                cdc.FunMain.class
})
public class Main implements Callable<Integer> {

    private static Logger logger = LoggerFactory.getLogger(IfxJdbcMain.class);

    @Spec CommandSpec spec;

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Main()).execute(args);
        logger.info("Command Exit with code = {}", exitCode);
    }

    @Override
    public Integer call() throws Exception {
        throw new ParameterException(spec.commandLine(), "Specify a subcommand");
    }
}
