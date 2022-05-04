package cdc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Random;
import java.util.concurrent.Callable;

@Command(name = "datagen")
public class IfxJdbcMain implements Callable<Integer> {

    private static Logger logger = LoggerFactory.getLogger(IfxJdbcMain.class);

    @Parameters(description = "JDBC URI for Informix Database",
            defaultValue = "jdbc:informix-sqli://172.20.3.242:9088/testdb:user=informix;password=in4mix")
    String jdbcUrl;

    @Option(names = {"-s", "--start"}, defaultValue = "0",
            description = "Start index of records")
    Integer start;

    @Option(names = {"-e", "--end"}, defaultValue = "10",
            description = "End index of records")
    Integer end;

    @Option(names = {"--do-commit"}, defaultValue = "true",
            description = "Do commit at end of the batch")
    boolean doCommit;

    @Option(names = {"--do-rollback"}, defaultValue = "false",
            description = "Do rollback at end of the batch")
    boolean doRollback;

    public static String generateRandomString(int length) {
        int leftLimit = 48;   // numeral '0'
        int rightLimit = 122; // letter 'z'
        Random random = new Random();

        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
        return generatedString;
    }

    @Override
    public Integer call() throws Exception {
        logger.info("Run Datagen ...");

        String strPrefix = generateRandomString(8);
        Class.forName("com.informix.jdbc.IfxDriver");

        String SQL = "INSERT INTO testdb:cdctable.hello VALUES(?, ?)";
        try (Connection conn = DriverManager.getConnection(jdbcUrl);
             Statement stmt = conn.createStatement();
             PreparedStatement pstmt = conn.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS)) {

            conn.setAutoCommit(false);

            for (int i = start; i < end; i++) {
                pstmt.setInt(1, i);
                pstmt.setString(2, String.format("%s-%08d", strPrefix, i));
                pstmt.executeUpdate();
            }
            logger.info("Insert Values, from={}, end={}, Prefix Like {}-00000000", start, end, strPrefix);

            if (doCommit) {
                conn.commit();
                logger.info("Connection Commit Done");
            }
            if (doRollback) {
                conn.rollback();
                logger.info("Connection Rollback Done");
            }
        }

        return 0;
    }
}
