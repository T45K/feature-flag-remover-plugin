package io.github.t45k.feature_flag_remover.removal

import io.github.t45k.feature_flag_remover.api.RemoveAfterRelease
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
        visitEachElement(classOrObject, { super.visitClassOrObject(it) })
    }

    override fun visitProperty(property: KtProperty) {
        visitEachElement(property, { super.visitProperty(it) })
    }

    override fun visitParameter(parameter: KtParameter) {
        visitEachElement(parameter, { super.visitParameter(it) })
    }

    override fun visitAnnotatedExpression(expression: KtAnnotatedExpression) {
        visitEachElement(expression, { super.visitAnnotatedExpression(it) }) {
            when {
                expression.isNamedArgument() -> expression.parent
                expression.isWhenCondition() -> expression.parent.parent
                expression.isWhenBody() -> expression.parent
                else -> expression
            } as KtElement
        }
    }

    override fun visitNamedFunction(function: KtNamedFunction) {
        visitEachElement(function, { super.visitNamedFunction(it) })
    }

    override fun visitPrimaryConstructor(constructor: KtPrimaryConstructor) {
        visitEachElement(constructor, { super.visitPrimaryConstructor(it) })
    }

    override fun visitSecondaryConstructor(constructor: KtSecondaryConstructor) {
        visitEachElement(constructor, { super.visitSecondaryConstructor(it) })
    }

    private fun <E : KtAnnotated> visitEachElement(element: E, continueVisiting: (E) -> Unit, selectElement: (E) -> KtElement = { it }) {
        if (element.isAnnotatedAsRemoveTarget()) {
            _removeTargetElements += selectElement(element)
        } else {
            continueVisiting(element)
        }
    }

    private fun KtAnnotated.isAnnotatedAsRemoveTarget(): Boolean =
        this.annotationEntries.any { entry ->
            entry.shortName?.asString() == RemoveAfterRelease::class.simpleName &&
                entry.valueArguments.any { arg -> arg.getArgumentExpression()?.text == "\"$targetName\"" }
        }

    private fun KtAnnotatedExpression.isNamedArgument(): Boolean = this.parent is KtValueArgument && this.parent.firstChild is KtValueArgumentName
    private fun KtAnnotatedExpression.isWhenCondition(): Boolean = this.parent is KtWhenConditionWithExpression
    private fun KtAnnotatedExpression.isWhenBody(): Boolean = this.parent is KtWhenEntry
}
