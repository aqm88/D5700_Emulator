package org.example.cpu

import org.example.cpu.instruction.Instruction
import org.example.cpu.register.Register
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

abstract class Cpu(val speed: Int, protected val registers: Map<String, Register>) {

    private val cpuExecutor = Executors.newSingleThreadScheduledExecutor()
    private var cpuFuture: ScheduledFuture<*>? = null

    fun executeInstructions(){
        startTimer()
        val task = Runnable {
            try {
        val instruction = getInstruction()
        checkForShutdown(instruction)
        val parsedInstruction = parseInstructionParts(instruction)
        performOperation(parsedInstruction)
        updateState(parsedInstruction)
            } catch (e: Exception) {
                println("CPU halted with error: ${e.message}")
                stopExecutionLoop()
            }
        }

            cpuFuture = cpuExecutor.scheduleAtFixedRate(
            task,
            0,
            1000L / speed.toLong(),
            TimeUnit.MILLISECONDS
        )
    }

    protected abstract fun startTimer()
    protected abstract fun getInstruction(): Int
    protected abstract fun checkForShutdown(instruction: Int)
    protected abstract fun parseInstructionParts(instruction: Int): Instruction
    protected abstract fun performOperation(instruction: Instruction)
    protected abstract fun updateState(instruction: Instruction)

    protected fun stopExecutionLoop() {
        cpuFuture?.cancel(true)
        cpuExecutor.shutdownNow()
    }
}