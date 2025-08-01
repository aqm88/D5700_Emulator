package org.example.memory

abstract class Memory(private val sizeInBytes: Int) {
    protected open val data = Array<Int>(sizeInBytes) { 0 }

    abstract fun read(address: Int): Int
    abstract fun write(address: Int, value: Int)
}