package org.example.Emulators

import org.example.cpu.cpuTypes.D5700_CPU
import org.example.memory.D5700_ROM
import org.example.memory.D5700_RAM
import org.example.display.D5700_Display
import java.io.InputStream

object D5700_Emulator: Emulator<Char>(
    cpu = D5700_CPU(),
    ram = D5700_RAM(),
    display = D5700_Display()
) {
    override fun loadProgramFromFile(path: String): Array<Int> {
        val inputStream = javaClass.getResourceAsStream(path)
            ?: error("Could not find resource at '$path'")

        val bytes = inputStream.readBytes()
        require(bytes.size <= 4096) { "ROM file too large: ${bytes.size} bytes" }

        return Array(4096) { i ->
            if (i < bytes.size) bytes[i].toInt() and 0xFF else 0
        }
    }


    override fun run(path: String?, input: InputStream?) {
        if (input != null) {
            System.setIn(input) // Set fake input for tests
        }

        val filePath = path ?: run {
            println("Enter ROM file path:")
            readln()
        }

        try {
            val programData = loadProgramFromFile(filePath)
            rom = D5700_ROM(programData)
            println("ROM loaded successfully. Ready to start execution.")
            cpu.executeInstructions()
        } catch (e: Exception) {
            println("Error loading ROM: ${e.message}")
        }
    }


}