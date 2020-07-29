package net.simon987.mar.server.assembly.instruction;

import net.simon987.mar.server.assembly.Register;
import net.simon987.mar.server.assembly.RegisterSet;
import net.simon987.mar.server.assembly.Status;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class SetnbeInstructionTest {
    private final RegisterSet registers;
    private final Status status;
    private final SetccInstruction instruction;
    private final int SETCCOPCODE = SetccInstruction.SETA;

    public SetnbeInstructionTest() {
        registers = new RegisterSet();
        registers.put(1, new Register("R"));
        registers.clear();

        status = new Status();
        status.clear();

        instruction = new SetnbeInstruction();
    }

    /**
     * SETA, SETNBE       Above, Not Below or Equal            CF=0 AND ZF=0
     */
    @Test
    public void execution() {
        status.setCarryFlag(false);
        status.setZeroFlag(false);
        instruction.execute(registers, 1, SETCCOPCODE, status);
        assertEquals(registers.get(1), 1);

        status.setCarryFlag(true);
        status.setZeroFlag(false);
        instruction.execute(registers, 1, SETCCOPCODE, status);
        assertEquals(registers.get(1), 0);

        status.setCarryFlag(false);
        status.setZeroFlag(true);
        instruction.execute(registers, 1, SETCCOPCODE, status);
        assertEquals(registers.get(1), 0);

        status.setCarryFlag(true);
        status.setZeroFlag(true);
        instruction.execute(registers, 1, SETCCOPCODE, status);
        assertEquals(registers.get(1), 0);
    }
}