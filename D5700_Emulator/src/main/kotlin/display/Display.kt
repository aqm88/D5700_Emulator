package org.example.display

abstract class Display<T>(protected val height: Int, protected val width: Int) {
    protected lateinit var frameBuffer: Array<T>

    abstract fun setPixel(x: Int, y: Int, newValue: T)
    abstract fun render()
}
