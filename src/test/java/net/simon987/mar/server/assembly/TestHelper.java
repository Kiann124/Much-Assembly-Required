package net.simon987.mar.server.assembly;

import net.simon987.mar.server.FakeConfiguration;
import net.simon987.mar.server.FakeHardwareHost;
import net.simon987.mar.server.IServerConfiguration;
import net.simon987.mar.server.TestExecutionResult;
import net.simon987.mar.server.assembly.exception.CancelledException;

public class TestHelper {

    public static Assembler getTestAsm() {
        IServerConfiguration config = getTestConfig();
        CPU cpu = getTestCpu();
        return new Assembler(cpu.getInstructionSet(), cpu.getRegisterSet(), config);
    }

    public static IServerConfiguration getTestConfig() {
        IServerConfiguration configuration = new FakeConfiguration();

        configuration.setInt("memory_size", 65536);
        configuration.setInt("org_offset", 4096);
        configuration.setInt("ivt_offset", 0);
        configuration.setInt("grace_instruction_count", 10000);
        return configuration;
    }

    public static CPU getTestCpu() {

        CPU cpu = null;
        try {
            cpu = new CPU(getTestConfig());
        } catch (CancelledException e) {
            e.printStackTrace();
        }
        return cpu;
    }

    public static TestExecutionResult executeCode(String code) {
        return executeCode(code, 100);
    }

    public static TestExecutionResult executeCode(String code, int timeout) {
        AssemblyResult ar = getTestAsm().parse(code);
        CPU cpu = TestHelper.getTestCpu();

        FakeHardwareHost host = new FakeHardwareHost(cpu);
        cpu.setHardwareHost(host);

        cpu.getMemory().clear();

        char[] assembledCode = ar.getWords();

        cpu.getMemory().write((char) ar.origin, assembledCode, 0, assembledCode.length);
        cpu.setCodeSectionOffset(ar.getCodeSectionOffset());

        cpu.reset();
        cpu.execute(timeout);

        return new TestExecutionResult(cpu.getState(), host.callHistory, ar);
    }
}
