package project

import org.example.Emulators.D5700_Emulator
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.assertThrows
import java.io.ByteArrayInputStream
import kotlin.test.*
import java.io.FileNotFoundException

class D5700EmulatorUnitTest {

    @Test
    fun `a_test memory set and get throws without ROM initialized`() {

        val exceptionSet = assertFailsWith<UnsupportedOperationException> {
            D5700_Emulator.setMemory(0x00, 0xAA, isRam = false)
        }
    }

    @Test
    fun `test ROM loading with valid file`() {
        val program = D5700_Emulator.loadProgramFromFile("/roms/keyboard.out")
        assertEquals(4096, program.size)
        assertTrue(program.any { it != 0 }) // Ensure some data loaded
    }

    @Test
    fun `test ROM loading with invalid file path`() {
        val exception = assertFailsWith<IllegalStateException> {
            D5700_Emulator.loadProgramFromFile("/roms/nonexistent.out")
        }
        assertTrue(exception.message!!.contains("Could not find resource"))
    }

    @Test
    fun `test emulator run with fake input`() {
        val input = "1\n2\n" // Simulated user input
        val inputStream = ByteArrayInputStream(input.toByteArray())

        // Should not throw any exceptions
        assertDoesNotThrow {
            D5700_Emulator.run("/roms/addition.out", inputStream)
            Thread.sleep(500) // Give time to process (if needed)
        }
    }

    @Test
    fun `test emulator run with null path and input`() {
        val inputStream = ByteArrayInputStream("\n".toByteArray())
        System.setIn(inputStream)
        // This will prompt for user input which we simulate with newline
        assertDoesNotThrow {
            D5700_Emulator.run(null, inputStream)
        }
    }

    @Test
    fun `test memory set and get (RAM)`() {
        D5700_Emulator.setMemory(0x10, 0x42, isRam = true)
        val value = D5700_Emulator.getMemory(0x10, isRam = true)
        assertEquals(0x42, value)
    }

    @Test
    fun `test memory get after ROM initialized`() {
        val program = D5700_Emulator.loadProgramFromFile("/roms/keyboard.out")
        assertDoesNotThrow {
            val value = D5700_Emulator.getMemory(0x00, isRam = false)
        }

    }

    @Test
    fun `test display pixel setting`() {
        D5700_Emulator.setDisplayPixel(5, 7, 'X')
        // We can't verify result without exposing Display, but this ensures no crash
        assertTrue(true)
    }
}
