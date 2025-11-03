package io.github.t45k.feature_flag_remover.core.visitor

import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtElement

internal inline fun <reified T : Annotation> List<KtAnnotationEntry>.decideRemoveTargetElements(targetName: String): RemoveTarget {
    val targetAnnotation = firstOrNull { it.shortName?.asString() == T::class.simpleName } ?: return RemoveTarget.None
    val targetFeatureNames = targetAnnotation.valueArguments.filter { it.getArgumentExpression()?.text == "\"$targetName\"" }
    return when {
        targetFeatureNames.isEmpty() -> RemoveTarget.None
        targetFeatureNames.size == targetAnnotation.valueArguments.size -> RemoveTarget.WholeElement
        else -> RemoveTarget.OnlyTargetNames(targetFeatureNames.map { it.asElement() })
    }
}

sealed interface RemoveTarget {
    data object WholeElement : RemoveTarget
    data class OnlyTargetNames(val targetNames: List<KtElement>) : RemoveTarget
    data object None : RemoveTarget
}
