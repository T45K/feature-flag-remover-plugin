package io.github.t45k.feature_flag_remover.core.visitor

import io.github.t45k.feature_flag_remover.api.RemoveElseClauseAfterRelease
import org.jetbrains.kotlin.psi.KtIfExpression
import org.jetbrains.kotlin.psi.KtTreeVisitorVoid
import org.jetbrains.kotlin.psi.psiUtil.getAnnotationEntries

class RemoveElseClauseTargetVisitor(private val targetName: String) : KtTreeVisitorVoid() {
    val removeTargetElements: List<KtIfExpression>
        field: MutableList<KtIfExpression> = []

    override fun visitIfExpression(expression: KtIfExpression) {
        val isAnnotatedAsRemoveTarget = expression.getAnnotationEntries().any { entry ->
            entry.shortName?.asString() == RemoveElseClauseAfterRelease::class.simpleName &&
                entry.valueArguments.any { arg -> arg.getArgumentExpression()?.text == "\"$targetName\"" }
        }

        if (isAnnotatedAsRemoveTarget) {
            removeTargetElements.add(expression)
        } else {
            super.visitIfExpression(expression)
        }
    }
}
