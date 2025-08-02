package project

import org.example.Emulators.D5700_Emulator
import java.io.ByteArrayInputStream
import java.io.File
import kotlin.test.Test

class D5700CPUTest {

    @Test
    fun `run all test ROMs with proper input`() {
        val romDir = File("src/test/resources/roms")

        romDir.listFiles { f -> f.extension == "out" }?.forEach { file ->
            val fakeInput = when (file.name) {
                "addition.out" -> "1\n2\n"
                "keyboard.out" -> "a\n"
                "subtraction.out" -> "5\n3\n"
                else -> ""
            }

            val inputStream = ByteArrayInputStream(fakeInput.toByteArray())
            D5700_Emulator.run(file.absolutePath, inputStream)

            // Optional: wait and then assert emulator state
            Thread.sleep(500)
        }
    }
}