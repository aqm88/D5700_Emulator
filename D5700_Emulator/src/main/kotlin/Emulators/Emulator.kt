package org.example.Emulators

import org.example.cpu.Cpu
import org.example.display.Display
import org.example.memory.Memory
import java.io.InputStream

abstract class Emulator<T>(protected val cpu: Cpu, protected val ram: Memory,  protected val display: Display<T>) {

    protected lateinit var rom: Memory

    abstract fun loadProgramFromFile(path: String): Array<Int>

    abstract fun run(path: String? = null, input: InputStream? = null)

    fun setMemory(address: Int, value: Int, isRam: Boolean) {
        if (isRam) {
            ram.write(address, value)
            return
        }else{
            if (!::rom.isInitialized) {
                throw UnsupportedOperationException("Rom must be initialized before writing to it")
            }else{
                rom.write(address, value)
            }
        }
    }

    fun getMemory(address: Int, isRam: Boolean): Int {
        return if(isRam){
            ram.read(address)
        } else if (!::rom.isInitialized) {
            throw UnsupportedOperationException("Rom must be initialized before reading from it")
        } else{
            rom.read(address)
        }

    }

    fun setDisplayPixel(x: Int, y: Int, value: T) {
        display.setPixel(x, y, value)
    }
}