package io.github.t45k.feature_flag_remover.core.visitor

import io.github.t45k.feature_flag_remover.core.visitor.RemoveTargetVisitor.RemoveTarget.None
import io.github.t45k.feature_flag_remover.core.visitor.RemoveTargetVisitor.RemoveTarget.OnlyTargetNames
import io.github.t45k.feature_flag_remover.core.visitor.RemoveTargetVisitor.RemoveTarget.WholeElement
import org.jetbrains.kotlin.psi.KtAnnotated
import org.jetbrains.kotlin.psi.KtAnnotatedExpression
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtPrimaryConstructor
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtSecondaryConstructor
import org.jetbrains.kotlin.psi.KtTreeVisitorVoid
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.psi.KtValueArgumentName
import org.jetbrains.kotlin.psi.KtWhenConditionWithExpression
import org.jetbrains.kotlin.psi.KtWhenEntry

class RemoveTargetVisitor(private val targetName: String) : KtTreeVisitorVoid() {
    private val _removeTargetElements: MutableList<KtElement> = mutableListOf()
    val removeTargetElements: List<KtElement> get() = _removeTargetElements

    override fun visitClassOrObject(classOrObject: KtClassOrObject) {
        visitCandidate(classOrObject, { super.visitClassOrObject(it) })
    }

    override fun visitProperty(property: KtProperty) {
        visitCandidate(property, { super.visitProperty(it) })
    }

    override fun visitParameter(parameter: KtParameter) {
        visitCandidate(parameter, { super.visitParameter(it) })
    }

    override fun visitAnnotatedExpression(expression: KtAnnotatedExpression) {
        visitCandidate(expression, { super.visitAnnotatedExpression(it) }) {
            when {
                expression.isNamedArgument() -> expression.parent
                expression.isWhenCondition() -> expression.parent.parent
                expression.isWhenBody() -> expression.parent
                else -> expression
            } as KtElement
        }
    }

    override fun visitNamedFunction(function: KtNamedFunction) {
        visitCandidate(function, { super.visitNamedFunction(it) })
    }

    override fun visitPrimaryConstructor(constructor: KtPrimaryConstructor) {
        visitCandidate(constructor, { super.visitPrimaryConstructor(it) })
    }

    override fun visitSecondaryConstructor(constructor: KtSecondaryConstructor) {
        visitCandidate(constructor, { super.visitSecondaryConstructor(it) })
    }

    private fun <E : KtAnnotated> visitCandidate(element: E, continueVisiting: (E) -> Unit, selectElement: (E) -> KtElement = { it }) {
        when (val removeTarget = element.decideRemoveTargetElements()) {
            WholeElement -> {
                _removeTargetElements += selectElement(element)
            }

            is OnlyTargetNames -> {
                _removeTargetElements += removeTarget.targetNames
                continueVisiting(element)
            }

            None -> {
                continueVisiting(element)
            }
        }
    }

    private fun KtAnnotated.decideRemoveTargetElements(): RemoveTarget {
        val targetAnnotation = annotationEntries.firstOrNull { it.shortName?.asString() == RemoveTargetVisitor::class.simpleName } ?: return None
        val targetFeatureNames = targetAnnotation.valueArguments.filter { it.getArgumentExpression()?.text == "\"$targetName\"" }
        return when {
            targetFeatureNames.isEmpty() -> None
            targetFeatureNames.size == targetAnnotation.valueArguments.size -> WholeElement
            else -> OnlyTargetNames(targetFeatureNames.map { it.asElement() })
        }
    }

    sealed interface RemoveTarget {
        data object WholeElement : RemoveTarget
        data class OnlyTargetNames(val targetNames: List<KtElement>) : RemoveTarget
        data object None : RemoveTarget
    }

    private fun KtAnnotatedExpression.isNamedArgument(): Boolean = this.parent is KtValueArgument && this.parent.firstChild is KtValueArgumentName
    private fun KtAnnotatedExpression.isWhenCondition(): Boolean = this.parent is KtWhenConditionWithExpression
    private fun KtAnnotatedExpression.isWhenBody(): Boolean = this.parent is KtWhenEntry
}