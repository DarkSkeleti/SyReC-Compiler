package AbstractSyntaxTree;

import CodeGen.Code;
import CodeGen.ExpressionResult;
import SymTable.Mod;

import java.util.HashMap;

public class ShiftExpression extends Expression {

    private final Expression expression;
    private final NumberExpression number;

    public enum Kind {
        LEFT, RIGHT
    }

    private Kind kind;

    public ShiftExpression(Expression expression, NumberExpression number, Kind kind) {
        this.expression = expression;
        this.number = number;
        this.kind = kind;
        containedSignals.addAll(expression.containedSignals);
    }

    @Override
    public ExpressionResult generate(CodeMod module) {
        ExpressionResult res = expression.generate(module);
        usedLines.addAll(expression.usedLines);
        int numberRes = number.generate(module).number;
        switch (kind) {
            case LEFT:
                if (res.isNumber) {
                    return new ExpressionResult(res.number << numberRes);
                } else {
                    SignalExpression shiftLine = module.getAdditionalLines(expression.getWidth());
                    usedLines.addAll(shiftLine.getLines());
                    ExpressionResult shiftRes = new ExpressionResult(shiftLine);
                    shiftRes.gates.addAll(res.gates);
                    shiftRes.gates.addAll(Code.leftShift(res, numberRes, shiftLine));
                    return shiftRes;
                }
            case RIGHT:
                if (res.isNumber) {
                    return new ExpressionResult(res.number >> numberRes);
                } else {
                    SignalExpression shiftLine = module.getAdditionalLines(expression.getWidth());
                    usedLines.addAll(shiftLine.getLines());
                    ExpressionResult shiftRes = new ExpressionResult(shiftLine);
                    shiftRes.gates.addAll(res.gates);
                    shiftRes.gates.addAll(Code.rightShift(res, numberRes, shiftLine));
                    return shiftRes;
                }
        }
        return new ExpressionResult(0);
    }

    @Override
    public int getWidth() {
        return expression.getWidth();
    }

    @Override
    public ShiftExpression replaceSignals(HashMap<String, String> replace, Mod currentModule) {
        return new ShiftExpression(expression.replaceSignals(replace, currentModule), number.replaceSignals(replace, currentModule), kind);
    }
}
