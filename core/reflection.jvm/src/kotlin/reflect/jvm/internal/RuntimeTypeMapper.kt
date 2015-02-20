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

import org.jetbrains.kotlin.builtins.KotlinBuiltIns
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.TypeParameterDescriptor
import org.jetbrains.kotlin.load.java.structure.reflect.classId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.platform.JavaToKotlinClassMapBuilder
import org.jetbrains.kotlin.resolve.DescriptorUtils
import org.jetbrains.kotlin.resolve.descriptorUtil.classId
import org.jetbrains.kotlin.resolve.jvm.JvmClassName
import org.jetbrains.kotlin.resolve.jvm.JvmPrimitiveType
import org.jetbrains.kotlin.types.JetType
import org.jetbrains.kotlin.types.TypeUtils

object RuntimeTypeMapper : JavaToKotlinClassMapBuilder() {
    val knownTypes = linkedMapOf<FqName, String>()
    val knownNullableTypes = linkedMapOf<FqName, String>();

    {
        for (type in JvmPrimitiveType.values()) {
            val primitiveType = type.getPrimitiveType()
            val fqName = KotlinBuiltIns.BUILT_INS_PACKAGE_FQ_NAME.child(primitiveType.getTypeName())

            knownTypes[fqName] = type.getDesc()
            knownTypes[KotlinBuiltIns.BUILT_INS_PACKAGE_FQ_NAME.child(primitiveType.getArrayTypeName())] = "[" + type.getDesc()
            knownNullableTypes[fqName] = ClassId.topLevel(type.getWrapperFqName()).desc
        }

        init()
    }

    override fun register(javaClass: Class<*>, kotlinDescriptor: ClassDescriptor, direction: JavaToKotlinClassMapBuilder.Direction) {
        knownTypes[DescriptorUtils.getFqNameSafe(kotlinDescriptor)] = javaClass.classId.desc
    }

    override fun register(javaClass: Class<*>, kotlinDescriptor: ClassDescriptor, kotlinMutableDescriptor: ClassDescriptor) {
        register(javaClass, kotlinDescriptor, JavaToKotlinClassMapBuilder.Direction.BOTH)
        register(javaClass, kotlinMutableDescriptor, JavaToKotlinClassMapBuilder.Direction.BOTH)
    }

    fun mapType(type: JetType): String {
        val classifier = type.getConstructor().getDeclarationDescriptor()
        if (classifier is TypeParameterDescriptor) return mapType(classifier.getUpperBounds().first())

        val classDescriptor = classifier as ClassDescriptor
        val fqNameUnsafe = DescriptorUtils.getFqName(classDescriptor)
        if (fqNameUnsafe.isSafe()) {
            val fqName = fqNameUnsafe.toSafe()
            if (TypeUtils.isNullableType(type)) {
                val cached = knownNullableTypes[fqName]
                if (cached != null) return cached
            }
            val cached = knownTypes[fqName]
            if (cached != null) return cached
        }

        if (KotlinBuiltIns.isArray(type)) {
            var dimension = 0
            var elementType = type

            while (KotlinBuiltIns.isArray(elementType)) {
                elementType = KotlinBuiltIns.getInstance().getArrayElementType(elementType)
                dimension++
            }

            return "[".repeat(dimension) + mapType(TypeUtils.makeNullable(elementType))
        }

        return classDescriptor.classId.desc
    }

    private val ClassId.desc: String
        get() = "L${JvmClassName.byClassId(this).getInternalName()};"
}
