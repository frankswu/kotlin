/*
 * Copyright 2010-2015 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.kotlin.idea.completion

import com.intellij.codeInsight.completion.InsertionContext
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementDecorator
import com.intellij.codeInsight.lookup.LookupElementPresentation
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.builtins.KotlinBuiltIns
import org.jetbrains.kotlin.idea.completion.handlers.*
import com.intellij.codeInsight.completion.PrefixMatcher
import java.util.ArrayList
import com.intellij.codeInsight.completion.CompletionResultSet
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import com.intellij.openapi.util.TextRange
import com.intellij.codeInsight.completion.CompletionParameters
import org.jetbrains.kotlin.idea.caches.resolve.ResolutionFacade

class LookupElementsCollector(
        private val prefixMatcher: PrefixMatcher,
        private val completionParameters: CompletionParameters,
        private val resolutionFacade: ResolutionFacade,
        private val lookupElementFactory: LookupElementFactory
) {
    private val elements = ArrayList<LookupElement>()

    public fun flushToResultSet(resultSet: CompletionResultSet) {
        if (!elements.isEmpty()) {
            resultSet.addAllElements(elements)
            elements.clear()
            isResultEmpty = false
        }
    }

    public var isResultEmpty: Boolean = true
        private set

    public fun addDescriptorElements(descriptors: Iterable<DeclarationDescriptor>,
                                     suppressAutoInsertion: Boolean, // auto-insertion suppression is used for elements that require adding an import
                                     shouldCastToRuntimeType: Boolean = false
    ) {
        for (descriptor in descriptors) {
            addDescriptorElements(descriptor, suppressAutoInsertion, shouldCastToRuntimeType)
        }
    }

    public fun addDescriptorElements(descriptor: DeclarationDescriptor, suppressAutoInsertion: Boolean, shouldCastToRuntimeType: Boolean) {
        run {
            var lookupElement = lookupElementFactory.createLookupElement(resolutionFacade, descriptor, true)
            if (shouldCastToRuntimeType) {
                lookupElement = lookupElement.shouldCastReceiver()
            }
            if (suppressAutoInsertion) {
                addElementWithAutoInsertionSuppressed(lookupElement)
            }
            else {
                addElement(lookupElement)
            }
        }

        // add special item for function with one argument of function type with more than one parameter
        if (descriptor is FunctionDescriptor) {
            val parameters = descriptor.getValueParameters()
            if (parameters.size() == 1) {
                val parameterType = parameters.get(0).getType()
                if (KotlinBuiltIns.isFunctionOrExtensionFunctionType(parameterType)) {
                    val parameterCount = KotlinBuiltIns.getParameterTypeProjectionsFromFunctionType(parameterType).size()
                    if (parameterCount > 1) {
                        val lookupElement = lookupElementFactory.createLookupElement(resolutionFacade, descriptor, true)
                        addElement(object : LookupElementDecorator<LookupElement>(lookupElement) {
                            override fun renderElement(presentation: LookupElementPresentation) {
                                super.renderElement(presentation)

                                val tails = presentation.getTailFragments()
                                presentation.clearTail()
                                presentation.appendTailText(" " + buildLambdaPresentation(parameterType), false)
                                tails.drop(1)/*drop old function signature*/.forEach { presentation.appendTailText(it.text, it.isGrayed()) }
                            }

                            override fun handleInsert(context: InsertionContext) {
                                KotlinFunctionInsertHandler(CaretPosition.IN_BRACKETS, GenerateLambdaInfo(parameterType, true)).handleInsert(context, this)
                            }
                        })
                    }
                }
            }
        }
    }

    public fun addElement(element: LookupElement) {
        if (prefixMatcher.prefixMatches(element)) {
            elements.add(object: LookupElementDecorator<LookupElement>(element) {
                override fun handleInsert(context: InsertionContext) {
                    getDelegate().handleInsert(context)

                    if (context.shouldAddCompletionChar() && !isJustTyping(context, this)) {
                        val handler = when (context.getCompletionChar()) {
                            ',' -> WithTailInsertHandler.commaTail()
                            '=' -> WithTailInsertHandler.eqTail()
                            else -> null
                        }
                        handler?.postHandleInsert(context, getDelegate())
                    }
                }
            })
        }
    }

    // used to avoid insertion of spaces before/after ',', '=' on just typing
    private fun isJustTyping(context: InsertionContext, element: LookupElement): Boolean {
        if (!completionParameters.isAutoPopup()) return false
        val insertedText = context.getDocument().getText(TextRange(context.getStartOffset(), context.getTailOffset()))
        return insertedText == element.getUserData(KotlinCompletionCharFilter.JUST_TYPING_PREFIX)
    }

    public fun addElementWithAutoInsertionSuppressed(element: LookupElement) {
        if (isResultEmpty && elements.isEmpty()) { /* without these checks we may get duplicated items */
            addElement(element.suppressAutoInsertion())
        }
        else {
            addElement(element)
        }
    }

    public fun addElements(elements: Iterable<LookupElement>) {
        elements.forEach { addElement(it) }
    }

    public fun addElementsWithReceiverCast(elements: Iterable<LookupElement>) {
        elements.forEach { addElement(it.shouldCastReceiver()) }
    }
}
