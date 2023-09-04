package io.github.gunkim.domain.game

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.DisplayName
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

@DisplayName("셀은")
class CellTests : StringSpec({
    "최고 레벨이 레벨업을 할 경우 예외가 발생한다" {
        val cell = Cell.ELEVEN

        shouldThrow<IllegalStateException> { cell.levelUp() }.apply {
            message shouldBe "이미 최고 레벨이므로 레벨업이 불가능합니다."
        }
    }
})
