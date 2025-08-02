package org.example.cpu.cpuTypes

import org.example.Emulators.D5700_Emulator
import org.example.cpu.Cpu
import org.example.cpu.instruction.D5700_Instruction
import org.example.cpu.instruction.Instruction
import org.example.cpu.register.Register
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

class D5700_CPU: Cpu(speed = 500,
                     registers = mapOf<String, Register>(
                         "P" to Register(16),
                         "T" to Register(8),
                         "A" to Register(16),
                         "M" to Register(1),
                         "r0" to Register(8),
                         "r1" to Register(8),
                         "r2" to Register(8),
                         "r3" to Register(8),
                         "r4" to Register(8),
                         "r5" to Register(8),
                         "r6" to Register(8),
                         "r7" to Register(8),
                         )
                    ) {

    private val timerExecutor = Executors.newSingleThreadScheduledExecutor()
    private var timerFuture: ScheduledFuture<*>? = null

    init{
        // Initialize registers
        registers.forEach { (name, register) ->
            register.setData(0)
        }
        registers["M"]?.setData(1) // Memory mode default to 1
        registers["P"]?.setData(0) // Program counter starts at 0
    }

    override fun startTimer() {
        if (timerFuture?.isCancelled == false) return // Already running

        val timerTask = Runnable {
            if (regData("T") > 0) {
                val t = regData("T")
                registers["T"]?.setData(t - 1)
            }
        }

        timerFuture = timerExecutor.scheduleAtFixedRate(
            timerTask,
            0,
            1000L / 60L, // Every ~16ms
            TimeUnit.MILLISECONDS
        )
    }

    override fun getInstruction(): Int {
        val pc = regData("P")

        // Fetch 2 bytes from ROM
        val byte1 = D5700_Emulator.getMemory(pc, false)
        val byte2 = D5700_Emulator.getMemory(pc + 1, false)
        val instructionInt = (byte1 shl 8) or byte2
        return instructionInt
    }

    override fun checkForShutdown(instruction: Int) {
        if (instruction == 0x0000) {
            shutdown()
        }
    }

    private fun shutdown() {
        println("Shutdown instruction 0000 received. Stopping CPU...")
        stopExecutionLoop()
        stopTimer()
    }


    override fun parseInstructionParts(instruction: Int): D5700_Instruction {
        val nibble1 = (instruction shr 12) and 0xF
        val nibble2 = (instruction shr 8) and 0xF
        val nibble3 = (instruction shr 4) and 0xF
        val nibble4 = instruction and 0xF

        return D5700_Instruction(arrayOf(nibble1, nibble2, nibble3, nibble4))
    }

    override fun performOperation(instruction: Instruction) {
        val opcode = D5700Opcode.fromCode(instruction.parts[0])
            ?: error("Unknown opcode: ${instruction.parts[0]}")
        opcode.execute(instruction.parts, this)
    }

    override fun updateState(instruction: Instruction) {
        val opcode = instruction.parts[0]
        if (opcode != 0x5) {
            incrementPC()
        }
    }



    private fun stopTimer() {
        timerFuture?.cancel(true)
        timerExecutor.shutdownNow()
    }

    private fun regData(name: String): Int = registers[name]?.data ?: 0

    private fun incrementPC() {
        val current = regData("P")
        registers["P"]?.setData(current + 2)
    }

    enum class D5700Opcode(val code: Int, val execute: (Array<Int>, D5700_CPU) -> Unit) {
        STORE(0x0, { p, cpu ->
            val value = (p[2] shl 4) or p[3]
            cpu.registers["r${p[1]}"]?.setData(value)
        }),

        ADD(0x1, { p, cpu ->
            val x = cpu.regData("r${p[1]}")
            val y = cpu.regData("r${p[2]}")
            cpu.registers["r${p[3]}"]?.setData((x + y) and 0xFF)
        }),

        SUB(0x2, { p, cpu ->
            val x = cpu.regData("r${p[1]}")
            val y = cpu.regData("r${p[2]}")
            cpu.registers["r${p[3]}"]?.setData((x - y) and 0xFF)
        }),

        READ(0x3, { p, cpu ->
            val addr = cpu.regData("A")
            val value = D5700_Emulator.getMemory(addr, cpu.registers["M"]?.data == 1)
            cpu.registers["r${p[1]}"]?.setData(value)
        }),

        WRITE(0x4, { p, cpu ->
            val addr = cpu.regData("A")
            val value = cpu.regData("r${p[1]}")
            D5700_Emulator.setMemory(addr, value, cpu.registers["M"]?.data == 1)
        }),

        JUMP(0x5, { p, cpu ->
            val addr = (p[1] shl 8) or (p[2] shl 4) or p[3]
            require(addr % 2 == 0) { "Program counter must be even" }
            cpu.registers["P"]?.setData(addr)
        }),

        READ_KEYBOARD(0x6, { p, cpu ->
            val input = readln().take(2).ifBlank { "0" }.uppercase()
            val parsed = input.toIntOrNull(16) ?: 0
            cpu.registers["r${p[1]}"]?.setData(parsed and 0xFF)
        }),

        SWITCH_MEMORY(0x7, { _, cpu ->
            val current = cpu.regData("M")
            cpu.registers["M"]?.setData(1 - current)
        }),

        SKIP_EQUAL(0x8, { p, cpu ->
            if (cpu.regData("r${p[1]}") == cpu.regData("r${p[2]}")) {
                cpu.incrementPC()
            }
        }),

        SKIP_NOT_EQUAL(0x9, { p, cpu ->
            if (cpu.regData("r${p[1]}") != cpu.regData("r${p[2]}")) {
                cpu.incrementPC()
            }
        }),

        SET_A(0xA, { p, cpu ->
            val addr = (p[1] shl 8) or (p[2] shl 4) or p[3]
            cpu.registers["A"]?.setData(addr)
        }),

        SET_T(0xB, { p, cpu ->
            val value = (p[1] shl 4) or p[2]
            cpu.registers["T"]?.setData(value)
        }),

        READ_T(0xC, { p, cpu ->
            val t = cpu.regData("T")
            cpu.registers["r${p[1]}"]?.setData(t)
        }),

        CONVERT_TO_BASE_10(0xD, { p, cpu ->
            val value = cpu.regData("r${p[1]}")
            val hundreds = value / 100
            val tens = (value / 10) % 10
            val ones = value % 10
            val base = cpu.regData("A")
            D5700_Emulator.setMemory(base, hundreds, cpu.registers["M"]?.data == 1)
            D5700_Emulator.setMemory(base + 1, tens, cpu.registers["M"]?.data == 1)
            D5700_Emulator.setMemory(base + 2, ones, cpu.registers["M"]?.data == 1)
        }),

        CONVERT_BYTE_TO_ASCII(0xE, { p, cpu ->
            val digit = cpu.regData("r${p[1]}")
            require(digit <= 0xF) { "Invalid hex digit for ASCII conversion" }
            cpu.registers["r${p[2]}"]?.setData(digit + 0x30)
        }),

        DRAW(0xF, { p, cpu ->
            val value = cpu.regData("r${p[1]}")
            val row = p[2]
            val col = p[3]
            require(value <= 0x7F) { "Cannot draw non-ASCII character" }
            D5700_Emulator.setDisplayPixel(row, col, value.toChar())
        });

        companion object {
            fun fromCode(code: Int): D5700Opcode? = entries.find { it.code == code }
        }
    }
}