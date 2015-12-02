package arm11;

import java.util.Stack;

public class AccumulatorMachine {

    private static final Register ACCUMULATOR = ARM11Registers.R10;
    
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
        return (register == ARM11Registers.R11 ? ACCUMULATOR : register);
    }

    public Register popFreeRegister() {
        Register register = freeRegisters.peek();
        if (register == ARM11Registers.R11) {
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


}
