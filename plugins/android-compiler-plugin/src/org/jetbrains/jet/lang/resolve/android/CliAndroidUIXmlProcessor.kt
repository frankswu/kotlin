/*
 * Copyright 2010-2014 JetBrains s.r.o.
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

package org.jetbrains.jet.lang.resolve.android

import java.util.ArrayList
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import java.io.ByteArrayInputStream

public class CliAndroidUIXmlProcessor(project: Project, override val searchPath: String?, val manifestPath: String?) : org.jetbrains.jet.lang.resolve.android.AndroidUIXmlProcessor(project) {

    override var androidAppPackage: String = ""
        get() = resourceManager.readManifest()._package

    override val resourceManager = org.jetbrains.jet.lang.resolve.android.CliAndroidResourceManager(project, searchPath, manifestPath)

    override fun parseSingleFileImpl(file: PsiFile): String {
        val ids: MutableCollection<AndroidWidget> = ArrayList()
        val handler = org.jetbrains.jet.lang.resolve.android.AndroidXmlHandler(resourceManager, { id, wClass -> ids.add(AndroidWidget(id, wClass)) })
        try {
            val inputStream = ByteArrayInputStream(file.getVirtualFile().contentsToByteArray())
            resourceManager.saxParser.parse(inputStream, handler)
            return produceKotlinProperties(KotlinStringWriter(), ids).toString()
        }
        catch (e: Throwable) {
            LOG.error(e)
            return ""
        }
    }
}

