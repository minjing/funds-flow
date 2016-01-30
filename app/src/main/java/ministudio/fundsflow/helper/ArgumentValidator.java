package ministudio.fundsflow.helper;

import com.google.common.base.Strings;

/**
 * Created by min on 16/1/23.
 */
public final class ArgumentValidator {

    private ArgumentValidator() { }

    public static void checkNull(Object argument, String argumentName) {
        if (argument == null) {
            throw new IllegalArgumentException("The argument is required - " + argumentName);
        }
    }

    public static void checkBlank(String argument, String argumentName) {
        if (Strings.isNullOrEmpty(argument)) {
            throw new IllegalArgumentException("The argument is required - " + argumentName);
        }
    }
}
