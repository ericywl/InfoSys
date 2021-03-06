package MultiUnitCalculator;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Calculator lexical analyzer.
 */
public class Lexer {
    // map for accessing TYPEs
    private static final Map<String, Type> TYPE_MAP = new HashMap<>();
    static {
        TYPE_MAP.put(Type.PLUS.getS(), Type.PLUS);
        TYPE_MAP.put(Type.MINUS.getS(), Type.MINUS);
        TYPE_MAP.put(Type.TIMES.getS(), Type.TIMES);
        TYPE_MAP.put(Type.DIVIDE.getS(), Type.DIVIDE);
        TYPE_MAP.put(Type.L_PAREN.getS(), Type.L_PAREN);
        TYPE_MAP.put(Type.R_PAREN.getS(), Type.R_PAREN);
        TYPE_MAP.put(Type.INCH.getS(), Type.INCH);
        TYPE_MAP.put(Type.POINT.getS(), Type.POINT);
    }

    // individual groups of symbols
    private static final Pattern NUMBER_PATTERN = Pattern.compile("(\\d+(\\.\\d+)?)");
    private static final Pattern PARENS_PATTERN = Pattern.compile("((\\Q(\\E)|(\\Q)\\E))");
    private static final Pattern UNITS_PATTERN = Pattern.compile("(in)|(pt)");
    private static final Pattern OP_PATTERN = Pattern.compile("(\\+)|(\\-)|(\\*)|(\\/)");

    // combination of the above
    private static final Pattern CALC =
            Pattern.compile(NUMBER_PATTERN.toString() +
                    "|" + OP_PATTERN.toString() +
                    "|" + PARENS_PATTERN.toString() +
                    "|" + UNITS_PATTERN.toString());

    // inverse CALC for detecting other tokens
    private static final Pattern INV_CALC = Pattern.compile("[^" + CALC.toString() + "]");

    // list to store Tokens
    private List<Token> tokenList = new LinkedList<>();

	/**
	 * Token in the stream.
	 */
	static class Token {
		final Type type;
		final String text;

		Token(Type type, String text) {
			this.type = type;
			this.text = text;
		}

		Token(Type type) {
			this(type, null);
		}

		@Override
		public String toString() {
            return text + " " + type.toString();
        }
 	}

	@SuppressWarnings("serial")
	static class TokenMismatchException extends Exception {
        TokenMismatchException(String message) {
            super(message);
        }
	}

	/**
	 * Use regex for lexical analysis to keep finding matches in the input
     * and add them to tokenList.
     * If the start of the next match is not in the same position as the end of the previous match,
     * (ie. 3.2a+b : the index of 3.2 is 0:3 and + is index 4)
     * something else not recognized by the matcher is in between,
     * so throw TokenMismatchException.
	 */
	public Lexer(String input) throws TokenMismatchException {
        String input_temp = input.trim().replaceAll(" ", "");
        Matcher matcher = CALC.matcher(input_temp);
        Matcher invMatcher = INV_CALC.matcher(input_temp);

        if (invMatcher.find()) {
            throw new TokenMismatchException("\nUnsupported token: " + invMatcher.group());
        }

        while (matcher.find()) {
            tokenList.add(getToken(matcher.group()));
        }
    }

    public List<Token> getTokenList() {
        return this.tokenList;
    }

    /**
     * The Types are grouped in two primary groups, NUMBER and non-NUMBER.
     * If the Type belongs to non-NUMBER, go to the TYPE_MAP and get the Type.
     * Else the Type is NUMBER.
     *
     * @param s : The string outputted by matcher.group()
     * @return Token
     */
    private Token getToken(String s) {
        if (TYPE_MAP.get(s) != null) {
            return new Token(TYPE_MAP.get(s), s);
        }

        return new Token(Type.NUMBER, s);
    }

    // Test
    public static void main(String[] args) {
        try {
            Lexer lexer = new Lexer("6a");
            for (Token token : lexer.getTokenList()) {
                System.out.println(token);
            }

        } catch (TokenMismatchException ex) {
            System.out.println(ex);
        }
    }
}
