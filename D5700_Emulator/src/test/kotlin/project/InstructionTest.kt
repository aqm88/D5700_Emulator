package project

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.example.cpu.instruction.Instruction
import org.example.cpu.instruction.D5700_Instruction

class InstructionTest {
    @Test
    fun `constructor accepts array of exactly 4 elements`() {
        val input = arrayOf(1, 2, 3, 4)
        val instruction = D5700_Instruction(input)
        assertArrayEquals(input, instruction.parts)
    }

    @Test
    fun `constructor throws when fewer than 4 elements`() {
        val input = arrayOf(1, 2, 3)
        val exception = assertThrows<IllegalArgumentException> {
            D5700_Instruction(input)
        }
        assertEquals("D5700_Instruction must be initialized with exactly 4 parts, but got 3", exception.message)
    }

    @Test
    fun `constructor throws when more than 4 elements`() {
        val input = arrayOf(1, 2, 3, 4, 5)
        val exception = assertThrows<IllegalArgumentException> {
            D5700_Instruction(input)
        }
        assertEquals("D5700_Instruction must be initialized with exactly 4 parts, but got 5", exception.message)
    }

    @Test
    fun `Instruction base class stores reference to parts array`() {
        // Anonymous subclass just for testing abstract class
        val input = arrayOf(10, 20, 30, 40)
        val instruction = object : Instruction(input) {}
        assertArrayEquals(input, instruction.parts)
    }
}