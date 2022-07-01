package cdc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine.Command;

import java.util.concurrent.Callable;

@Command(name = "fun")
public class FunMain implements Callable<Integer>  {

    private static Logger logger = LoggerFactory.getLogger(FunMain.class);

    public void runFun(HelloFunInterface hf) {
        hf.onEvent(1);
    }

    public void runAbsFun(HelloFunAbsClass hfa) {
        hfa.handler(1);
    }

    @Override
    public Integer call() throws Exception {
        runFun(x -> {
            logger.info("runFun in lambda {}", x);
            System.out.println("hello");
        });

        runAbsFun(new HelloFunAbsClass() {
            @Override
            public void handler(int x) {
                System.out.println("hello abstract function: base=" + this.getBase() + " x=" + x);
            }
        });
        return null;
    }

}
