import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.core.ConsoleAppender
import ch.qos.logback.core.FileAppender
import grails.util.BuildSettings
import grails.util.Environment
import org.springframework.boot.logging.logback.ColorConverter
import org.springframework.boot.logging.logback.WhitespaceThrowableProxyConverter

import java.nio.charset.Charset

conversionRule 'clr', ColorConverter
conversionRule 'wex', WhitespaceThrowableProxyConverter

// See http://logback.qos.ch/manual/groovy.html for details on configuration
appender('STDOUT', ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        charset = Charset.forName('UTF-8')

        pattern =
                '%clr(%d{yyyy-MM-dd HH:mm:ss.SSS}){faint} ' + // Date
                        '%clr(%5p) ' + // Log level
                        '%clr(---){faint} %clr([%15.15t]){faint} ' + // Thread
                        '%clr(%-40.40logger{39}){cyan} %clr(:){faint} ' + // Logger
                        '%m%n%wex' // Message
    }
}

def targetDir = BuildSettings.TARGET_DIR
if (Environment.isDevelopmentMode() && targetDir != null) {
    appender("FULL_STACKTRACE", FileAppender) {
        file = "${targetDir}/stacktrace.log"
        append = true
        encoder(PatternLayoutEncoder) {
            pattern = "%level %logger - %msg%n"
        }
    }
//    appender("FILE", FileAppender) {
//        file = "${targetDir}/logs/log.log"
//        encoder(PatternLayoutEncoder) {
//            pattern = "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
//        }
//    }
//    logger("org.hibernate.SQL", DEBUG, ["FILE"], false)
//    logger("org.hibernate.type.descriptor.sql.BasicBinder", TRACE, ["FILE"], false)
    logger("StackTrace", ERROR, ['FULL_STACKTRACE'], false)
    logger("grails.app", DEBUG, ['FULL_STACKTRACE', 'STDOUT'], false)
    logger("grails.app1", INFO, ['FULL_STACKTRACE', 'STDOUT'], false)
    root(ERROR, ['STDOUT', 'FULL_STACKTRACE'])
    root( INFO, ['STDOUT'] )
}
else {
    root(ERROR, ['STDOUT'])
}
