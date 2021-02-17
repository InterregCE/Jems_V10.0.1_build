package io.cloudflight.jems.server

import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import java.util.stream.Collectors

@ExtendWith(MockKExtension::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class UnitTest {

    protected fun getStringOfLength(length: Int): String =
        IntArray(length).map { "x" }.stream().collect(Collectors.joining())

}
