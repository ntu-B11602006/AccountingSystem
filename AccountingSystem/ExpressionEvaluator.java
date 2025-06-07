import java.math.BigDecimal;
import java.util.*;

public class ExpressionEvaluator {

    /**
     * 支援 + - * / 括號 與 運算順序的數學表達式計算器
     * @param expression 數學算式（例如："3 + 5 * (2 - 1)"）
     * @return 結果（BigDecimal）
     */
    public static BigDecimal evaluate(String expression) {
        try {
            List<String> postfix = infixToPostfix(expression);
            return evaluatePostfix(postfix);
        } catch (Exception e) {
            throw new IllegalArgumentException("無效的算式: " + expression, e);
        }
    }

    // 將中序表達式轉換為後序表達式（RPN）
    private static List<String> infixToPostfix(String expression) {
        List<String> output = new ArrayList<>();
        Stack<String> operators = new Stack<>();
        StringTokenizer tokenizer = new StringTokenizer(expression, "+-*/() ", true);

        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken().trim();
            if (token.isEmpty()) continue;

            if (isNumber(token)) {
                output.add(token);
            } else if (isOperator(token)) {
                while (!operators.isEmpty() &&
                        isOperator(operators.peek()) &&
                        precedence(token) <= precedence(operators.peek())) {
                    output.add(operators.pop());
                }
                operators.push(token);
            } else if (token.equals("(")) {
                operators.push(token);
            } else if (token.equals(")")) {
                while (!operators.isEmpty() && !operators.peek().equals("(")) {
                    output.add(operators.pop());
                }
                if (operators.isEmpty() || !operators.peek().equals("(")) {
                    throw new IllegalArgumentException("括號不匹配");
                }
                operators.pop(); // 移除左括號
            } else {
                throw new IllegalArgumentException("未知符號: " + token);
            }
        }

        while (!operators.isEmpty()) {
            String op = operators.pop();
            if (op.equals("(")) {
                throw new IllegalArgumentException("括號不匹配");
            }
            output.add(op);
        }

        return output;
    }

    // 評估後序表達式
    private static BigDecimal evaluatePostfix(List<String> postfix) {
        Stack<BigDecimal> stack = new Stack<>();
        for (String token : postfix) {
            if (isNumber(token)) {
                stack.push(new BigDecimal(token));
            } else if (isOperator(token)) {
                if (stack.size() < 2) {
                    throw new IllegalArgumentException("算式格式錯誤");
                }
                BigDecimal b = stack.pop();
                BigDecimal a = stack.pop();
                switch (token) {
                    case "+" -> stack.push(a.add(b));
                    case "-" -> stack.push(a.subtract(b));
                    case "*" -> stack.push(a.multiply(b));
                    case "/" -> {
                        if (b.compareTo(BigDecimal.ZERO) == 0) {
                            throw new ArithmeticException("除以零");
                        }
                        stack.push(a.divide(b, 10, BigDecimal.ROUND_HALF_UP));
                    }
                }
            } else {
                throw new IllegalArgumentException("無效 token: " + token);
            }
        }
        if (stack.size() != 1) {
            throw new IllegalArgumentException("算式格式錯誤");
        }
        return stack.pop();
    }

    private static boolean isNumber(String token) {
        try {
            new BigDecimal(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean isOperator(String token) {
        return "+-*/".contains(token);
    }

    private static int precedence(String op) {
        return switch (op) {
            case "+", "-" -> 1;
            case "*", "/" -> 2;
            default -> -1;
        };
    }
}
