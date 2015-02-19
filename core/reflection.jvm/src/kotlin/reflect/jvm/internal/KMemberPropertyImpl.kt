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

package kotlin.reflect.jvm.internal

import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.renderer.DescriptorRenderer
import kotlin.reflect.KMemberProperty
import kotlin.reflect.KMutableMemberProperty

// TODO: properties of built-in classes

open class KMemberPropertyImpl<T : Any, out R>(
        override val container: KClassImpl<T>,
        computeDescriptor: () -> PropertyDescriptor
) : DescriptorBasedProperty(computeDescriptor), KMemberProperty<T, R>, KPropertyImpl<R> {
    override val name: String get() = descriptor.getName().asString()

    override fun get(receiver: T): R {
        try {
            val getter = getter
            [suppress("UNCHECKED_CAST")]
            return if (getter != null) getter(receiver) as R else field!!.get(receiver) as R
        }
        catch (e: java.lang.IllegalAccessException) {
            throw kotlin.reflect.IllegalAccessException(e)
        }
    }

    override fun equals(other: Any?): Boolean =
            other is KMemberPropertyImpl<*, *> && descriptor == other.descriptor

    override fun hashCode(): Int =
            descriptor.hashCode()

    // TODO: include visibility, return type
    override fun toString(): String {
        val descriptor = descriptor
        val renderer = DescriptorRenderer.FQ_NAMES_IN_TYPES
        val valVar = if (descriptor.isVar()) "var" else "val"
        val receiverType = descriptor.getDispatchReceiverParameter()!!.getType()
        return "$valVar ${renderer.renderType(receiverType)}.${renderer.renderName(descriptor.getName())}"
    }
}


class KMutableMemberPropertyImpl<T : Any, R>(
        container: KClassImpl<T>,
        computeDescriptor: () -> PropertyDescriptor
) : KMemberPropertyImpl<T, R>(container, computeDescriptor), KMutableMemberProperty<T, R>, KMutablePropertyImpl<R> {
    override fun set(receiver: T, value: R) {
        try {
            val setter = setter
            if (setter != null) setter(receiver, value) else field!!.set(receiver, value)
        }
        catch (e: java.lang.IllegalAccessException) {
            throw kotlin.reflect.IllegalAccessException(e)
        }
    }
}
