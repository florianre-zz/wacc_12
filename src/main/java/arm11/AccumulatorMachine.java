package arm11;

import org.antlr.v4.runtime.ParserRuleContext;

import java.util.Stack;

public class AccumulatorMachine {

    private static final Register ACCUMULATOR = ARM11Registers.R10;
    private static final Register RESERVED = ARM11Registers.R11;
    
    private int regOverflowCount;
    private Stack<Register> freeRegisters;

    public AccumulatorMachine(){
        this.freeRegisters = new Stack<>();
        this.regOverflowCount = 0;
    }

    public void resetFreeRegisters() {
        freeRegisters.clear();
        pushFreeRegister(ARM11Registers.R12);
        pushFreeRegister(ARM11Registers.R11);
        pushFreeRegister(ARM11Registers.R10);
        pushFreeRegister(ARM11Registers.R9);
        pushFreeRegister(ARM11Registers.R8);
        pushFreeRegister(ARM11Registers.R7);
        pushFreeRegister(ARM11Registers.R6);
        pushFreeRegister(ARM11Registers.R5);
        pushFreeRegister(ARM11Registers.R4);
    }

    public Register peekFreeRegister() {
        Register register = freeRegisters.peek();
        return (register == RESERVED ? ACCUMULATOR : register);
    }

    public Register popFreeRegister() {
        Register register = freeRegisters.peek();
        if (register == RESERVED) {
            register = ACCUMULATOR;
            regOverflowCount++;
        } else {
            register = freeRegisters.pop();
        }
        return register;
    }

    public void pushFreeRegister(Register register) {
        if (regOverflowCount > 0) {
            regOverflowCount--;
        } else {
            freeRegisters.push(register);
        }
    }

    //  private class RegisterPair {
//    Register first;
//    Register second;
//
//    public RegisterPair(Register first, Register second) {
//      this.first = first;
//      this.second = second;
//    }
//  }
//
//  private static void swapRegistersIfAccumulating(RegisterPair regPair, int regOverflowCount){
//    if (regOverflowCount > 0) {
//      Register temp = regPair.second;
//      regPair.second = regPair.first;
//      regPair.first = temp;
//    }
//  }


    public InstructionList getInstructionList (InstructionType inst, Register dst, Register src, Operand op){
        InstructionList result = new InstructionList();
        if (inst.isArithmetic()) {
            return arithmeticInstructions(inst, dst, src, op);
        } else if (inst.isLogical()) {
            return logicalInstructions(inst, dst, src, op);
        } else if (inst.isMove()) {
            return movInstructions(inst, dst, op);
        } else if (inst.isLoad()) {
            return loadInstructions(inst, dst, op);
        }
        return result;
    }

    private InstructionList loadInstructions(InstructionType inst, Register dst, Operand op) {
        switch (inst) {
            case LDR:
                if (inAccumulatorMode()) {

                } else {

                }
        }
        return new InstructionList();
    }

    private InstructionList movInstructions(InstructionType inst, Register dst, Operand op) {
        switch (inst) {
            case MOV:
                if (inAccumulatorMode()) {

                } else {

                }
        }
        return new InstructionList();
    }

    private InstructionList logicalInstructions(InstructionType inst, Register dst, Register src, Operand op) {
        switch (inst) {
            case AND:
                if (inAccumulatorMode()) {

                } else {

                }
        }
        return new InstructionList();
    }

    private InstructionList arithmeticInstructions(InstructionType inst, Register dst, Register src, Operand op) {
        // TODO: see if true for all cases
        assert (op instanceof Register);

        InstructionList result = new InstructionList();
        if (inAccumulatorMode()) {
            result.add(InstructionFactory.createPop(RESERVED));
            src = RESERVED;
        }
        switch (inst) {
            case ADD:
                result.add(InstructionFactory.createAdd(dst, src, op));
                break;
            case ADDS:
                result.add(InstructionFactory.createAdds(dst, src, op));
                break;
            case SUB:
                result.add(InstructionFactory.createSub(dst, src, op));
                break;
            case SUBS:
                result.add(InstructionFactory.createSubs(dst, src, (Register) op));
                break;
            case RSBS:
                // TODO
            case SMULL:
                // TODO
            default:
                break;
        }
        return result;
    }

    private boolean inAccumulatorMode() {
        return regOverflowCount > 0;
    }

    public InstructionList getInstructionList(InstructionType inst, Register dst, Register src, Operand op, Operand offset){
        InstructionList result = new InstructionList();
        if (inst.isStore()) {
            return storeInstructions(inst, dst, src, offset);
        } else if (inst.isLoad()) {
            return loadInstructions(inst, dst, op, offset);
        }
        return result;
    }

    private InstructionList loadInstructions(InstructionType inst, Register dst, Operand op, Operand offset) {
        return null;
    }

    private InstructionList storeInstructions(InstructionType inst, Register dst, Register src, Operand offset) {
        return null;
    }

}
