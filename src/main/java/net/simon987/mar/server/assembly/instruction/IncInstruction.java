package net.simon987.mar.server.assembly.instruction;

import net.simon987.mar.server.assembly.Instruction;
import net.simon987.mar.server.assembly.Status;
import net.simon987.mar.server.assembly.Target;
import net.simon987.mar.server.assembly.Util;

public class IncInstruction extends Instruction {

    public static final int OPCODE = 42;

    public IncInstruction() {
        super("inc", OPCODE);
    }

    @Override
    public Status execute(Target dst, int dstIndex, Status status) {
        char a = (char) dst.get(dstIndex);
        int result = a + 1;

        // Like x86 Carry flag is preserved during INC/DEC
        // (Use ADD x, 1 to have carry flag change)
        // Other flags set according to result
        status.setSignFlag(Util.checkSign16(result));
        status.setZeroFlag((char) result == 0);
        status.setOverflowFlag(Util.checkOverFlowAdd16(a, 1));

        dst.set(dstIndex, result);
        return status;
    }
}
